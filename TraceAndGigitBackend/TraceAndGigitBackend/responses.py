from django.utils.translation import ugettext as _
from TraceAndGigitBackend.common_responses import Result
from TraceAndGigitBackend.libs_utils import datetime_str

STATUS_SUCCESS = "SUCCESS"
STATUS_INVALID_SESSION_ID = "ERR_INVALID_SESSION_ID"
STATUS_NON_LOGGED_USER = "ERR_NON_LOGGED_USER"
STATUS_INVALID_PARAMETER = "ERR_INVALID_PARAMETER"
STATUS_USER_EXISTS = "ERR_USER_EXISTS"
STATUS_EMAIL_IN_USE = "ERR_EMAIL_IN_USE"
STATUS_USER_UPDATE = "ERR_USER_UPDATE"

class DeviceRegistrationResult(Result):      
    def __init__(self, session, created, extra_fields=None):
        if created:
            code = 201
            message = _("Device successfully registered")
        else:
            code = 200
            message = _("Device already registered")
        status = "SUCCESS"
        super(DeviceRegistrationResult, self).__init__(code, status, message, extra_fields=extra_fields)
        self.session = session
        
    def to_json_dict(self):
        result = {
            "deviceId": self.session.device.device_id,
            "clientKey": self.session.client_key,
            "expiresAt": datetime_str(self.session.expires_at),
        }
        result.update(super(DeviceRegistrationResult, self).to_json_dict())
        return result

class GenerateKeyResult(Result):
    def __init__(self, session):
        super(GenerateKeyResult, self).__init__(200, "SUCCESS", _("OK"))
        self.session = session

    def to_json_dict(self):
        result = {
            "clientKey": self.session.client_key,
            "expiresAt": datetime_str(self.session.expires_at),
        }
        result.update(super(GenerateKeyResult, self).to_json_dict())
        return result

class ProfileStatus(Result):
    def __init__(self, result):
        super(ProfileStatus, self).__init__(200, STATUS_SUCCESS, _("OK"))
        self.result = result

class Profile(object):
    def __init__(self, u_id, first, last, gender, mobile, emails):
        self.id = u_id
        self.first = first
        self.last = last
        self.gender = gender
        self.mobile_no = mobile
        self.emails = emails

class Email(object):
    def __init__(self, email, is_primary, is_verified):
        self.email = email
        self.is_primary = is_primary
        self.is_verified = is_verified

class ProfileResult(Result):
    def __init__(self, profile):
        result = {'result': {'profile': profile}}
        super(ProfileResult, self).__init__(200, "SUCCESS", _("OK"), extra_fields=result)

