import datetime
import uuid
from TraceAndGigitBackend.settings import JSON_SETTINGS

CLIENT_KEY_EXPIRY = JSON_SETTINGS["trace_and_gigit_user"]["client_key_duration"]

CLIENT_KEY_DURATION = datetime.timedelta(days=CLIENT_KEY_EXPIRY)
PASSWORD_SALT = "adf0qf-92%2f$245oi@twgwt024#$s^%#^esf"
DEVICE_NAMESPACE_UUID = uuid.UUID(''.join([str(ord(c)) for c in "apalyadevice"]))

#send_html_mail('Reset password', text_mail, html_mail, settings.EMAIL_HOST_USER, [email])



