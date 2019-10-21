import datetime
import exceptions
import json
import logging
import re
import urllib
import urllib2

from django.core.cache import cache
import pytz
import requests
import base64



from apps.common.error_utils import InvalidDeviceSession
from apps.common.models import IpCountryMapping
from apps.config.models import Config
from apps.trace_and_gigit_user.models import Session

from TraceAndGigitBackend.settings import JSON_SETTINGS


LOGGER = logging.getLogger("TraceAndGigitBackend.apps.common.utils")

CONTENT_STORE_SETTINGS = JSON_SETTINGS["content_store"]
SUPPORTED_COUNTRIES = CONTENT_STORE_SETTINGS["supported_countries"].split(",")
X_FORWARD_HEADER = CONTENT_STORE_SETTINGS["forward_header"]
REMOTE_ADDRESS_HEADER = CONTENT_STORE_SETTINGS["address_header"]

MAXMIND_SERVICE = 'maxmind'
FREE_GEO_IP_SERVICE = 'free-geo-ip'
GEO_SETTINGS = JSON_SETTINGS.get('geo', {})
GEO_SERVICE = GEO_SETTINGS.get('service', MAXMIND_SERVICE)
WHITELISTED_IPS = GEO_SETTINGS.get('whitelisted_ips', [])

EST_NAME = 'Buy'

TIME_OUT = 30

def get_ntp_time():
    """convert a system time to a NTP time"""
    diff = datetime.datetime.utcnow() - datetime.datetime(1900, 1, 1, 0, 0, 0)
    return diff.days*24*60*60+diff.seconds

def get_device_session(request):
    client_key = request.META.get('HTTP_CLIENTKEY')
    if not client_key:
        LOGGER.debug("Client key not found in headers, looking in query parameters & body")
        client_key = request.GET.get('clientKey')
    if not client_key:
        client_key = request.POST.get('clientKey')
    if client_key:
        try:
            session = Session.get_device_session(client_key)
            return session
        except Session.DoesNotExist:
            LOGGER.warning("URL '%s': invalid clientKey %s", request.path, client_key)
            raise InvalidDeviceSession("Invalid device session")

def get_browser_session(request):
    # check for browser session, if browser session doesn't exist we need to
    # create it
    session_id = request.session.get('api_session_id')
    if session_id:
        try:
            session = Session.get_browser_session(session_id)
            if session:
                return session
        except Session.DoesNotExist:
            # We'll create below
            pass

    # Create a session, browsers should always have a session
    regex1 = r"(?P<br_name>[\w.\-_]+)/(?P<br_version>[\w.\-_]+) \((?P<system>[\w.\-_; ]+)"
    regex2 = r"(?P<br_name>[\w.\-_]+)/(?P<br_version>[\w.\-_]+)"
    user_agent = request.META.get("HTTP_USER_AGENT")
    browser_params = {"os": "unknown", "os_version": "unknown", "browser": "unknown",
                      "browser_version": "unknown"}
    if user_agent:
        m = re.match(regex1, user_agent)
        if not m:
            m = re.match(regex2, user_agent)
        if m:
            LOGGER.debug("m: %s", m.groupdict())
            browser_params.update({
                "browser": m.group('br_name')[:30],
                "browser_version": m.group('br_version')[:30],
            })
            if 'system' in m.groupdict():
                browser_params['os'] = m.group('system')[:30]
    LOGGER.debug("User agent: %s", user_agent)
    LOGGER.info("Browser params: %s", browser_params)
    
    remote_addr = request.META.get(REMOTE_ADDRESS_HEADER, None)
    
    request.session["api_session_id"] = session.id
    return session

def get_api_session(request):
    # check for device API session
    try:
        session = get_device_session(request)
        if session:
            return session
    except InvalidDeviceSession:
        return None

    LOGGER.debug("URL '%s': clientKey not provided", request.path)
    if not JSON_SETTINGS['web']:
        return None
    return get_browser_session(request)

def is_logged_in(request):
    session = get_api_session(request)
    if session.user:
        return True
    return False

def get_values_json_dict(records, total_count=None):
    values = [r.to_json_dict() for r in records]
    result = {'results': {'values': values}}
    if total_count is not None:
        result['results']['totalCount'] = total_count
    return result

def get_ip_country(ip_address,user_id=None):
    key = 'ip.country.%s' % ip_address
    value = cache.get(key)
    if value is not None:
        LOGGER.info("Found country %s for ip address %s in cache", value, ip_address)
        return value

    try:
        # try geolite2 first
        

        country = IpCountryMapping.get_country_by_ip(ip_address)
        if country:
            LOGGER.info("Database: Found country %s for ip %s for user : %s", country, ip_address, user_id)
            cache.set(key, country, 30*24*60*60)
            LOGGER.info("Set country %s for ip address %s in cache", country, ip_address)
            return country
        if GEO_SERVICE == MAXMIND_SERVICE:
            maximind_url = CONTENT_STORE_SETTINGS["maximind_url"]
            params = {'l': CONTENT_STORE_SETTINGS["maximind_key"], 'i': ip_address}
            response = requests.get(maximind_url, params=params)
            if response.status_code != 200:
                LOGGER.warning("Couldn't get country information from %s for %s, status code: %s",
                    GEO_SERVICE, ip_address, response.status_code)
                return None

            country = response.text.strip().upper()
            LOGGER.info("MAXMIND_SERVICE: Found country %s for ip %s for user : %s", country, ip_address, user_id)
        else:
            url = 'http://freegeoip.net/json/%s' % ip_address
            resp = requests.get(url).json()
            LOGGER.info("Freegeoip response is :%s",resp)
            country = resp.get('country_code', '').strip().upper()
        if country:
            cache.set(key, country, 30*24*60*60)
            LOGGER.info("Setting country %s for ip address %s in cache", country, ip_address)
        else:
            LOGGER.warn("Couldn't determine country for ip address %s", ip_address)
        return country
    except Exception, e:
        LOGGER.error("Exception: %s", e)
        LOGGER.exception(e)
    return None

def get_ip_from_request(request):
    if request is None:
        return ''
    ip_address = request.META.get(X_FORWARD_HEADER, request.META.get(REMOTE_ADDRESS_HEADER))
    ip_address = ip_address.split(",")[-1]
    return ip_address.strip()


def is_operator_subscription_allowed(ip, client_name):
    configs = Config.objects.filter(key=client_name, value_type="IP")
    if not configs.exists():
        LOGGER.warn("IP white listing not exist, access given for every body")
        return True
    else:
        for config in configs:
            if config.value == ip:
                return True
    return False

def is_location_allowed_by_ip(ip_address, country=None, allowed_countries=None):
    if country is None:
        country = get_ip_country(ip_address)
    if not allowed_countries:
        allowed_countries = SUPPORTED_COUNTRIES

    # Explicitly allowed?
    if 'ALL' in allowed_countries:
        return True
    if ip_address in WHITELISTED_IPS:
        LOGGER.info("Allowing whitelisted IP %s", ip_address)
        return True

    if not ip_address:
        LOGGER.warn("No ip address for location, returning False")
        return False
    if not country or not country in allowed_countries:
        LOGGER.info("Request from address %s, country %s is not allowed", ip_address, country)
        return False
    return True

def is_location_allowed_by_ip_corosel(ip_address, country=None, allowed_countries=None):
    if country is None:
        country = get_ip_country(ip_address)
    if not allowed_countries:
        allowed_countries = SUPPORTED_COUNTRIES

    # Explicitly allowed?
    if 'ALL' in allowed_countries:
        return (True,country)
    if ip_address in WHITELISTED_IPS:
        LOGGER.info("Allowing whitelisted IP %s", ip_address)
        return (True,country)

    if not ip_address:
        LOGGER.warn("No ip address for location, returning False")
        return (False,country)
    if not country or not country in allowed_countries:
        LOGGER.info("Request from address %s, country %s is not allowed", ip_address, country)
        return (False,country)
    return (True,country)

def is_location_allowed_by_ip_and_device(ip_address, is_client, ip_location=None, 
                              device_location=None, allowed_countries=None):
    
    LOGGER.info("Allowed location check for ip address: %s for client: %s", ip_address, is_client)
    
    if not ip_address:
        LOGGER.warn("No ip address for location, returning False")
        return False
    
    if ip_address in WHITELISTED_IPS:
        LOGGER.info("Allowing whitelisted IP %s", ip_address)
        return True
    
    if not allowed_countries:
        return False
        
    if 'ALL' in allowed_countries:
        LOGGER.info("Allowing all IPs, with ALL")
        return True
    
    if not ip_location or 'NULL' in ip_location:
        ip_location = None
    if not ip_location and not device_location:
        LOGGER.info("No ip_location or device_location is available")
        return False
    
    d_flag = False
    ip_flag = False
    
    if ip_location and ip_location in allowed_countries:
        ip_flag = True
        LOGGER.info("Request from address %s and its location %s is allowed", ip_address, ip_location)
    
    if is_client == 'device':
        if device_location and device_location in allowed_countries:
            d_flag = True
            LOGGER.info("Request from address %s and device location %s is allowed", ip_address, device_location)
    
        if d_flag and ip_flag:
            LOGGER.info("Request from address %s and its location %s is same as device location %s", 
                        ip_address, ip_location, device_location)
            return True
        
        

    return ip_flag

def is_ip_location_allowed(request):
    ip_address = get_ip_from_request(request)
    return is_location_allowed_by_ip(ip_address)

def get_cache_timeout(timeout=None):
    if timeout:
        cache_duration = datetime.timedelta(seconds=timeout)
    else:
        cache_duration = datetime.timedelta(hours=int(CONTENT_STORE_SETTINGS['max_cache_age_hours']))
    ist_tz = pytz.timezone(pytz.country_timezones['IN'][0])
    now = datetime.datetime.now(ist_tz)
    # All cache should expire at mid-night
    max_cache_time = now.replace(hour=23, minute=59, second=59)
    if now + cache_duration > max_cache_time:
        cache_duration = max_cache_time - now
    return int(cache_duration.total_seconds())

def make_request(url=None, data=None):
    LOGGER.info("In make_request")
    data = json.dumps(data)
    data = urllib.urlencode({'param': data})
    LOGGER.debug("Data sending: %s", data)
    resp = urllib2.urlopen(url, data)
    return json.loads(resp.read())

def make_request_requests(method, url, params=None, data=None, headers=None):
    method = method.upper().strip()
    if not method in ('GET', 'POST'):
        raise exceptions.NotImplementedError("Method %s not implemented" % method)
    if not params: params = {}
    LOGGER.info("Making request to %s, params: %s and data is : %s", url, params,data)
    if method == 'GET':
        resp = requests.get(url, params=params, timeout=TIME_OUT)
    else:
        resp = requests.post(url, params=params, data=data, timeout=TIME_OUT, headers=headers)
    try:
        return resp.json()
    except ValueError, e:
        LOGGER.error("Error in %s %s, params: %s", method, url, params)
        LOGGER.error("Exception: %s", e)
        LOGGER.exception(e)
        LOGGER.error("Error decoding JSON for content: \n%s", resp.content)
        return {}

def normalize_india_mobile_no(mobile_no):
    if mobile_no.find( ',')!=-1:
        mobiles=mobile_no.split(',')
        mobile_no = mobiles[len(mobiles) -1].strip(" ")
    if mobile_no.startswith('+91'):
        mobile_no = mobile_no[3:]
    elif mobile_no.startswith('0091'):
        mobile_no = mobile_no[4:]
    elif mobile_no.startswith('0'):
        mobile_no = mobile_no[1:]
    elif mobile_no.startswith('91') and len(mobile_no) == 12:
        mobile_no = mobile_no[2:]
    elif mobile_no.startswith('92') and len(mobile_no) == 12:
        mobile_no = mobile_no[2:]
    return mobile_no



