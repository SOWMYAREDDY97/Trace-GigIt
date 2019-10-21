import logging

from django.http import HttpResponse
from apps.common.responses import InvalidParameterResult, Result, get_json_data

LOGGER = logging.getLogger("TraceAndGigitBackend.apps.common.error_utils")

def log_form_errors(form, data, path):
    LOGGER.warning("%s: Missing or invalid parameter(s): %s", path, data)
    LOGGER.warning("Form errors:")
    for field, errors in form.errors.items():
        LOGGER.warning("%s: %s", field, errors.as_text())
 
def invalid_form_error(form, data, path):
    log_form_errors(form, data, path)
    errors = dict([(f, e.as_text()) for f, e in form.errors.items()])
    return InvalidParameterResult(errors=errors).http_response()


class InvalidDeviceSession(Exception):
    pass

class DuplicateRequest(Exception):
    pass
