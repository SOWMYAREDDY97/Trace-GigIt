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



def get_api_session(request):
    # check for device API session
    try:
        session = get_device_session(request)
        if session:
            return session
        else: 
            return None
    except InvalidDeviceSession:
        return None

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



