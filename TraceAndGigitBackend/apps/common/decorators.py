import logging
import hashlib

from django.http import HttpResponse
from django.core.cache import cache

from apps.common.utils import get_api_session
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






