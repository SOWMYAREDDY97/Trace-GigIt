import json
import logging

from django.http import HttpResponse
from django.http.response import HttpResponseRedirect
from django.utils.translation import ugettext as _


STATUS_SUCCESS = "SUCCESS"
STATUS_INVALID_SESSION_ID = "ERR_INVALID_SESSION_ID"
STATUS_NON_LOGGED_USER = "ERR_NON_LOGGED_USER"
STATUS_LOGGED_USER = "ERR_LOGGED_USER"
STATUS_INVALID_PARAMETER = "ERR_INVALID_PARAMETER"
STATUS_EXPIRED_OTP = "OTP_VALIDITY_TIMEDOUT"
STATUS_USER_EXISTS = "ERR_USER_EXISTS"
STATUS_EMAIL_IN_USE = "ERR_EMAIL_IN_USE"
STATUS_USER_UPDATE = "ERR_USER_UPDATE"
STATUS_NOT_FOUND = "ERR_NOT_FOUND"
STATUS_SERVER_ERROR = "ERR_INTERNAL_SERVER_ERROR"
STATUS_UPSTREAM_SERVER_ERROR = "ERR_UPSTREAM_SERVER_ERROR"
STATUS_COUNTRY_NOT_ALLOWED = "ERR_COUNTRY_NOT_ALLOWED"
STATUS_CLIENT_NOT_ALLOWED = "ERR_CLIENT_NOT_ALLOWED"
STATUS_ERR_DEVICE_DIFF_USR = "ERR_DEVICE_DIFF_USR"
STATUS_ERR_DEVICE_LIMIT_EXCEEDED = "ERR_DEVICE_LIMIT_EXCEEDED"
STATUS_ERR_USER_NOT_SUBSCRIBED = "ERR_USER_NOT_SUBSCRIBED"
STATUS_ERR_PACKAGES_NOT_DEFINED = "ERR_PACKAGES_NOT_DEFINED"
STATUS_ERR_ALREADY_SUBSCRIBED = "ERR_ALREADY_SUBSCRIBED"
STATUS_ERR_IN_PROGRESS = "ERR_IN_PROGRESS"
STATUS_ERR_NO_INFO = "ERR_NO_INFO"
STATUS_MATCH_FINISHED = "ERR_MATCH_FINISHED"
STATUS_MATCH_NOT_STARTED = "ERR_MATCH_NOT_STARTED"
STATUS_DEVICE_NOT_SUPPORTED_FOR_HD = "ERR_DEVICE_NOT_SUPPORTED_FOR_HD"
STATUS_NO_MSISDN = "ERR_NO_MSISDN"
DITTO_SUBCRRIPTION_FAILUER="DITTO NOT SUBSCRIBED"
PARTNER_SUBCRRIPTION_FAILUER="PARTNER NOT SUBSCRIBED"
UNSUPPORTED_ACTIVATION_TYPE = "UNSUPPORTED ACTIVATION TYPE"
USER_WITH_NO_SUBSCRIPTIONS = "USER_HAS_NO_SUPERPACK_SUBSCRIPTION"


MODULE_NAME = "TraceAndGigitBackend.apps.common.responses"
LOGGER = logging.getLogger(MODULE_NAME)

class Result(object):
    def __init__(self, code, status, message, extra_fields=None):
        """
        Base Result class for API responses.
        code - numeric status code, similar to HTTP status code
        status - short status code
        message - descriptive message.
        """
        self.code = code
        self.status = status
        self.message = message
        self.extra_fields = extra_fields
    
    def to_json_dict(self):
        """Convert to a dict for serializing to JSON."""
        result = {"code": self.code, "status": self.status, "message": self.message}
        if self.extra_fields:
            result.update(self.extra_fields)
        return result
    
    def to_json(self, pretty=0):
        """Serialize to JSON."""
        if pretty == 1:
            return json.dumps(self.to_json_dict(), indent=2)
        return json.dumps(self.to_json_dict())

    def http_response(self, pretty=0, status_code=200):
        response = HttpResponse(self.to_json(pretty), mimetype='application/json')
        response.status_code = status_code
        response['Access-Control-Allow-Origin'] = '*'
        return response

class ENCRYPT(object):
    def __init__(self, payload, extra_fields=None):
        """
        Base Result class for API responses.
        code - numeric status code, similar to HTTP status code
        status - short status code
        message - descriptive message.
        """
        self.payload = payload
        self.extra_fields = extra_fields

    def to_json_dict(self):
        """Convert to a dict for serializing to JSON."""
        result = {"response": self.payload}
        if self.extra_fields:
            result.update(self.extra_fields)
        return result

    def to_json(self, pretty=0):
        """Serialize to JSON."""
        if pretty == 1:
            return json.dumps(self.to_json_dict(), indent=2)
        return json.dumps(self.to_json_dict())

    def http_response(self, pretty=0, status_code=200):
        response = HttpResponse(self.to_json(pretty), mimetype='application/json')
        response.status_code = status_code
        response['Access-Control-Allow-Origin'] = '*'
        return response


class InvalidParameterResult(Result):
    def __init__(self, message=None, errors=None):
        if not message:
            message = _("Invalid or missing parameter(s)")
        extra_fields = {'errors': errors} if errors else None
        super(InvalidParameterResult, self).__init__(400, STATUS_INVALID_PARAMETER, message,
            extra_fields=extra_fields)

class OtpExpiryResult(Result):
    def __init__(self, message=None, errors=None):
        if not message:
            message = _("Invalid Otp or Otp Had Expired")
        extra_fields = {'errors': errors} if errors else None
        super(OtpExpiryResult, self).__init__(402, STATUS_EXPIRED_OTP, message,
            extra_fields=extra_fields)
        
class InvalidSessionResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Invalid or missing session id")
        super(InvalidSessionResult, self).__init__(401, STATUS_INVALID_SESSION_ID, message)

class InvalidClientResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Client not allowed")
        super(InvalidClientResult, self).__init__(401, STATUS_CLIENT_NOT_ALLOWED, message)

class BadRequestResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Bad Request")
        super(BadRequestResult, self).__init__(400, STATUS_CLIENT_NOT_ALLOWED, message)

class NonLoggedUserResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Need logged in user")
        super(NonLoggedUserResult, self).__init__(401, STATUS_INVALID_SESSION_ID, message)

def get_json_data(data, pretty=False):
    if pretty:
        return json.dumps(data, default=lambda o: o.__dict__, sort_keys=False, indent=4)
    return json.dumps(data, default=lambda o: o.__dict__, sort_keys=False)

class NotFoundResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Not found")
        super(NotFoundResult, self).__init__(404, STATUS_NOT_FOUND, message)

class ServerErrorResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Internal Server Error")
        super(ServerErrorResult, self).__init__(500, STATUS_SERVER_ERROR, message)

class UpstreamServerErrorResult(Result):
    def __init__(self, message=None, errors=None):
        if not message:
            message = _("No/invalid data received from upstream server")
        extra_fields = {'errors': errors} if errors else None
        super(UpstreamServerErrorResult, self).__init__(500, STATUS_UPSTREAM_SERVER_ERROR, message,
                extra_fields=extra_fields)

class CountryNotAllowedResult(Result):
    def __init__(self, message=None):
        if not message:
            message = _("Your country is not allowed for this request")
        super(CountryNotAllowedResult, self).__init__(401, STATUS_COUNTRY_NOT_ALLOWED, message)

 

