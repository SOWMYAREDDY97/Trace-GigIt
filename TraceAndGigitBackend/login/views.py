
import logging
from django.http import HttpResponse
from django.http.response import HttpResponseNotFound, HttpResponseRedirect
from django.views.decorators.http import require_http_methods
import requests
from django.db import transaction
from TraceAndGigitBackend import responses
from django.utils.translation import ugettext as _
from login.request_form import SignUpForm
from TraceAndGigitBackend.common_responses import InvalidParameterResult
from login.models import customer



LOGGER = logging.getLogger("login.views")

@require_http_methods(['POST'])
@transaction.commit_on_success
def sign_up(request):
    
    form = SignUpForm(request.POST)
    
    form = SignUpForm(request.POST)
    errors = dict()
    if not form.is_valid():
        LOGGER.warning("Missing or invalid parameter(s): %s", request.POST)
        LOGGER.warning("Form errors:")
        for field, error in form.errors.items():
            LOGGER.warning("%s: %s", field, error.as_text())
            errors[field] = error.as_text()

        
    if errors:
        result = InvalidParameterResult(errors=errors)
        return result.http_response(int(request.POST.get("pretty", 0)))

    
    data = form.cleaned_data
    mobile = data['mobile']
    
    if customer.objects.filter(mobile=mobile).exists():
        LOGGER.info("customer with mobile %s exists", mobile)
        result = responses.Result(409, responses.STATUS_USER_EXISTS,
            _("customer with mobile %(mobile)s exists" % {'mobile':mobile}))
        
        return result.http_response(data.get("pretty"))
    
    customer = customer(first=data.get('first'), last=data.get('last'), mobile=mobile,
        gender=data.get('gender'))
    customer.password_hash = customer.make_password(data['password'])
    customer.save() 
    LOGGER.info("Created customer account with mobilr: %s", mobile)
    
    
    
    result = responses.Result(201, responses.STATUS_SUCCESS, _("Successfully created user account")%{"email":"xxxxxxx"},
        extra_fields={"userid": "1234", "mobile":mobile})
    return result.http_response()
    