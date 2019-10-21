import logging
import hashlib

from django.http import HttpResponse
from django.core.cache import cache

from apps.common.utils import get_api_session, is_ip_location_allowed, get_cache_timeout
from apps.common.responses import InvalidSessionResult, NonLoggedUserResult, ServerErrorResult, CountryNotAllowedResult

from TraceAndGigitBackend.settings import JSON_SETTINGS




LOGGER = logging.getLogger("TraceAndGigitBackend.apps.common.decorators")

def json_response(view_func):
    """
    Decorator for returning JSON response.
    """
    def decorated(request, *args, **kwargs):
        try:
            result = view_func(request, *args, **kwargs)
        except Exception, e:
            LOGGER.error("Path: %s, Exception: %s", request.path, e)
            LOGGER.exception(e)
            result = ServerErrorResult()
        try:
            pretty = int(request.GET.get('pretty', 0))
        except:
            pretty = 0
        return result.http_response(pretty=pretty)
    return decorated


def run_once(f):
    def wrapper(*args, **kwargs):
        if not wrapper.has_run:
            wrapper.has_run = True
            return f(*args, **kwargs)
    wrapper.has_run = False
    return wrapper


def require_valid_session(view_func):
    """
    Decorator ensuring that a valid session is present. The session object is
    passed to the view function.
    """
    def decorated(request, *args, **kwargs):
        session = get_api_session(request)
        if not session:
            result = InvalidSessionResult()
            LOGGER.info("Invalied Session Request for %s and ip : %s"%(request.path,request.META.get('HTTP_X_FORWARDED_FOR')))
            LOGGER.warning("%s: No valid session", request.path)
            return HttpResponse(result.to_json(), mimetype='application/json')
        try:
            if session and session.user:
                mobile = session.user.mobile_no
            else:
                mobile = 'Guest'
            LOGGER.info("Request for %s from user :%s and ip is :%s"%(request.path,mobile,request.META.get('HTTP_X_FORWARDED_FOR')))
            return view_func(request, session, *args, **kwargs)
        except Exception, e:
            LOGGER.error("Path: %s, Exception: %s", request.path, e)
            LOGGER.exception(e)
            
            return ServerErrorResult().http_response()
    return decorated

def require_logged_in_user(view_func):
    """
    Decorator ensuring that a valid session is present. The session object is
    passed to the view function.
    """
    def decorated(request, *args, **kwargs):
        session = get_api_session(request)
        if not session:
            LOGGER.warning("%s: No valid session", request.path)
            return InvalidSessionResult().http_response()
        if not session.user:
            LOGGER.warning("%s: No logged in user", request.path)
            return NonLoggedUserResult().http_response()
        try:
            LOGGER.info("Request for %s from user :%s and ip is :%s"%(request.path,session.user.mobile_no,request.META.get('HTTP_X_FORWARDED_FOR')))
            return view_func(request, session, *args, **kwargs)
        except Exception, e:
            LOGGER.error("Path: %s, Exception: %s", request.path, e)
            LOGGER.exception(e)
            
            return ServerErrorResult().http_response()
    return decorated

def require_country_allowed(view_func):
    """
    Decorator ensuring that the request coming from the country is allowed or not based on IP address.
    """
    def decorated(request, *args, **kwargs):
        if not is_ip_location_allowed(request):
            return CountryNotAllowedResult().http_response()
        try:
            return view_func(request, *args, **kwargs)
        except Exception, e:
            LOGGER.error("Path: %s, Exception: %s", request.path, e)
            LOGGER.exception(e)
            return ServerErrorResult().http_response()
    return decorated

def require_country_allowed_for_videos(view_func):
    """
    Decorator ensuring that the request coming from the country is allowed or not based on IP address.
    """
    def decorated(request, *args, **kwargs):
        fields = request.GET.get('fields')
        if fields and "videos" in fields and not is_ip_location_allowed(request):
            return CountryNotAllowedResult().http_response()
        try:
            return view_func(request, *args, **kwargs)
        except Exception, e:
            LOGGER.error("Path: %s, Exception: %s", request.path, e)
            LOGGER.exception(e)
            return ServerErrorResult().http_response()
    return decorated

def cache_wrapper(key, timeout, func, *args, **kwargs):
    """
    Get data from cache if available otherwise get it from the function.
    """
    LOGGER.debug("Cache key: %s", key)
    value = cache.get(key)
    if value is not None:
        LOGGER.debug("Value found in cache for key: %s", key)
        return value

    LOGGER.debug("Value not found in cache for key: %s", key)
    value = func(*args, **kwargs)
    # store value in cache only if it is not False
    if value:
        duration = get_cache_timeout(timeout)
        LOGGER.debug("Setting value in cache for key: %s, duration: %s", key, duration)
        cache.set(key, value, duration)
    return value

def cache_result(timeout=5*60, key_prefix=None):
    def decorator_wrapper(func):
        def decorated(*args, **kwargs):
            arg_key = hashlib.sha1(unicode(args)+unicode(kwargs)).hexdigest()
            if key_prefix:
                key = "%s.%s:%s" % (key_prefix, func.__name__, arg_key)
            else:
                key = "%s.%s:%s:%s" % (func.__module__, func.__name__, repr(func), arg_key)
                key = key.replace(' ', '')
            return cache_wrapper(key, timeout, func, *args, **kwargs)
        return decorated
    return decorator_wrapper

