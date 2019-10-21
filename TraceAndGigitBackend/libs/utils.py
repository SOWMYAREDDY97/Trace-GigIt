import random
import string
import pytz

ISO_FORMAT = "%Y-%m-%dT%H:%M:%SZ"
UTC = pytz.timezone('UTC')

def datetime_str(value):
    if not value: return ''
    if value.tzinfo:
        value = value.astimezone(UTC)
    return value.strftime(ISO_FORMAT)

def create_email_token():
    return ''.join(random.sample(string.ascii_lowercase + string.digits, 8))

def create_mobile_token():
    return ''.join(random.sample(string.digits, 6))

def strip_unsafe_html(text):
    if not text:
        return text
    unsafe_chars = ('<', '>', "'", '"', '&')
    for c in unsafe_chars:
        text = text.replace(c, ' ')
    return text

def convert_to_iso_date_format(value):
    if not value: return ''
    return value + "T" + "00:00:00Z"

