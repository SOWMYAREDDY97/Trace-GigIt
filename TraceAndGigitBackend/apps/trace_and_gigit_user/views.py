import exceptions
import json
import logging
import smtplib
import urllib
import datetime
import random
import string

from apps.common import responses as common_responses


from django.db import transaction
from django.http import HttpResponse
from django.http.response import HttpResponseNotFound, HttpResponseRedirect
from django.shortcuts import render
from django.template.context import Context
from django.template.loader import get_template
from django.utils import timezone
from django.utils.translation import ugettext as _
from django.views.decorators.http import require_http_methods
import requests


from apps.common.decorators import json_response, require_valid_session, require_logged_in_user
from apps.common.responses import InvalidParameterResult, Result, get_json_data, \
    NotFoundResult, ServerErrorResult
from apps.common.utils import make_request, normalize_india_mobile_no, \
     make_request_requests
    

from apps.trace_and_gigit_user import constants
from apps.trace_and_gigit_user import model_utils
from apps.trace_and_gigit_user import trace_and_gigit_utils
from apps.trace_and_gigit_user import responses
from apps.trace_and_gigit_user.models import Device, Session, User, ClientSecret, UserMobile,UserEmail, EmailToken

from apps.trace_and_gigit_user.trace_and_gigit_utils import get_tzaware_now


from libs import utils

from libs.utils import ISO_FORMAT, strip_unsafe_html
from TraceAndGigitBackend import settings
from TraceAndGigitBackend.settings import JSON_SETTINGS

from user_management import DeviceRegistrationForm, SignUpForm, resetForm


from django.db.models.query_utils import Q

from apps.common.models import CircleMapping


import time
from apps.common.send_grid_sender import send_email



REG_DEVICE_LIMIT = JSON_SETTINGS["trace_and_gigit_user"]["reg_device_limit"]


LOGGER = logging.getLogger("TraceAndGigitBackend.apps.trace_and_gigit_user.views")




def log_form_errors(form, data, path):
    LOGGER.warning("%s: Missing or invalid parameter(s): %s", path, data)
    LOGGER.warning("Form errors:")
    for field, errors in form.errors.items():
        LOGGER.warning("%s: %s", field, errors.as_text())

def invalid_form_error(form, data, path):
    log_form_errors(form, data, path)
    errors = dict([(f, e.as_text()) for f, e in form.errors.items()])
    result = InvalidParameterResult(errors=errors)
    return HttpResponse(result.to_json(), mimetype='application/json')

@require_http_methods(['POST'])
@json_response
@transaction.commit_on_success
def register_device(request):
    if request.GET.get('clientKey') or request.POST.get('clientKey'):
        LOGGER.warning("Got unexpected clientKey")
        result = InvalidParameterResult(errors={'clientKey': _('Unexpected parameter')})
        return result

    form = DeviceRegistrationForm(request.POST)
    
    if form.is_valid():
        data = form.cleaned_data
        serviceid_tracker = ClientSecret.objects.get(os = data['os'],secret = data['clientSecret'])
        data['serviceId'] = serviceid_tracker.service_id
        if data['serviceId'] != 'traceandgigit':
            data['os'] = data['os'] +'_'+ data['clientSecret']
            data['make'] = data['make'] + '_' + data['clientSecret']
            data['model'] = data['model'] + '_' + data['clientSecret']
            data['profile'] = data['profile'] + '_' + data['clientSecret']
            
        
        
        device_id = trace_and_gigit_utils.generate_device_id(data['os'], data['make'], data['model'],
            data['serialNo'], data['profile'])
        
        
        
        
        
        
        creation_params = {'os_version': data['osVersion'], 'resolution': data['resolution'],
        'device_id': device_id, 'service_id': data['serviceId']}
    
    
    
        device, created = Device.objects.get_or_create(os=data['os'], make=data['make'], model=data['model'],
                    serial_number=data['serialNo'], profile=data['profile'], defaults=creation_params)
        
        
        if created:
            LOGGER.info("Added device. Parameters: %s, device id: %s", data, device_id)
        else:
            LOGGER.info("Device already added. Parameters: %s, device id: %s", data, device_id)

        client_key = trace_and_gigit_utils.generate_client_key(device.device_id)
        expires_at = trace_and_gigit_utils.get_client_key_expiry_time()
        extra_fields = None
        try:
            sessions = Session.objects.filter(device=device)
            if sessions.exists():
                session = sessions[0]
                acc_types = []
                if session.user:
                    if session.user.password_hash:
                        acc_types.append("traceandgigit")
                    user_email = UserEmail.objects.get(user=session.user)
                    
                # Delete existing session for the device
                sessions.delete()
        except (Session.DoesNotExist, UserEmail.DoesNotExist):
            pass
        session_params = {
            "device": device,
            "client_key": client_key,
            "expires_at": expires_at,
            "user": None,
            "ip_address": request.META.get("REMOTE_ADDR", None),
        }
        
        if data.get('mobile'):
            mobile = data.get('mobile')
            if len(mobile) > 10: mobile = mobile[2:]
            msisdn = mobile
            LOGGER.info("Msisdn is :%s",msisdn)
            LOGGER.debug("New user with out subscriptions")
        LOGGER.info("Device registration success for : %s",session_params)
        try:
            session = Session.objects.create(**session_params)
        except Exception,e:
            print e
            
        result = responses.DeviceRegistrationResult(session, created, extra_fields=extra_fields)
    else:
        # form not valid
        LOGGER.warning("POST data: %s", request.POST)
        LOGGER.warning("Form errors:")
        for field, errors in form.errors.items():
            LOGGER.warning("%s: %s", field, errors.as_text())
        errors = dict([(f, e.as_text()) for f, e in form.errors.items()])
        result = InvalidParameterResult(errors=errors)
    return result

@require_http_methods(['POST'])
@json_response
def generate_key(request):
    device_id = request.POST.get('deviceId')
    if device_id:
        try:
            device = Device.objects.get(device_id=device_id)
            try:
                session = Session.objects.get(device=device)
                LOGGER.info("Renewing client key for device: %s", device.device_id)
            except Session.DoesNotExist:
                session = Session(device=device, browser=None, user=None)
                LOGGER.info("Creating client key for device: %s", device.device_id)
            session.client_key = trace_and_gigit_utils.generate_client_key(device.device_id)
            session.expires_at = trace_and_gigit_utils.get_client_key_expiry_time()
            session.save()
            result = responses.GenerateKeyResult(session)
        except Device.DoesNotExist:
            LOGGER.warning("Invalid device_id: %s", device_id)
            result = InvalidParameterResult(_("Missing or invalid device id"))
    else:
        LOGGER.warning("Missing parameter device_id")
        result = InvalidParameterResult(_("Missing or invalid device id"))
    return result

@require_http_methods(['POST'])
@require_valid_session
@transaction.commit_on_success
def sign_up(request, session):
    if session.user:
        if session.user.is_guest():
            LOGGER.info("Signup, signing out guest user %s", session.user.id)
            trace_and_gigit_utils.sign_out(session, True)
        else:
            LOGGER.info("Signup, user already logged in. user=%s", session.user.id)
            result = responses.Result(401, common_responses.STATUS_LOGGED_USER,
                _("A user is already logged in"))
            return result.http_response(int(request.POST.get("pretty", 0)))

    form = SignUpForm(request.POST)
    
    print (request.POST)
    
    errors = dict()
    if not form.is_valid():
        LOGGER.warning("Missing or invalid parameter(s): %s", request.POST)
        LOGGER.warning("Form errors:")
        for field, error in form.errors.items():
            LOGGER.warning("%s: %s", field, error.as_text())
            errors[field] = error.as_text()

    errors = dict([(f, e.as_text()) for f, e in form.errors.items()])
    
    
    if errors:
        result = InvalidParameterResult(errors=errors)
        print (result)
        return result.http_response(int(request.POST.get("pretty", 0)))

    #auto_password = request.POST.get('autoPassword', 'false') == 'true'
    data = form.cleaned_data
    email = data['email']
    
    
    if UserEmail.objects.filter(email=email).exists():
        LOGGER.info("User with email %s exists", email)
        result = responses.Result(409, responses.STATUS_USER_EXISTS,
            _("User with email %(email)s exists" % {'email':email}))
        #return HttpResponse(result.to_json(), mimetype='application/json')
        return result.http_response(data.get("pretty"))
    
#     mobile = data.get('mobile')
#     if mobile and UserMobile.objects.filter(mobile=mobile).exists():
#         LOGGER.info("User with mobile %s exists", mobile)
#         result = responses.Result(409, responses.STATUS_USER_EXISTS,
#             _("User with mobile %(mobile)s exists" % {'mobile':mobile}))
#         #return HttpResponse(result.to_json(), mimetype='application/json')
#         return result.http_response(data.get("pretty"))
    
#     if session.device and session.device.user and not session.device.user.guest_device:
#         user_email = UserEmail.objects.filter(user=session.device.user)[0]
#         result = responses.Result(403, common_responses.STATUS_ERR_DEVICE_DIFF_USR, _("The device is registered to user with email:%(email)s" %{'email':user_email.email}))
#         #return HttpResponse(result.to_json(), mimetype='application/json')
#         return result.http_response(data.get("pretty"))

    user = User(first=data.get('first'), last=data.get('last'), email=email,
        dob=data.get('dob'), gender=data.get('gender'))
    user.password_hash = User.make_password(data['password'])
    user.save()
    
    
    
     
    # log the user in
    try:
        session.user = user
        session.save()
        if session.device:
            device = session.device
            device.user = user
            device.save()
    
    except Exception,e:
        print e
        
    print "created user"
    UserEmail.objects.create(email=email, user=user, is_primary=True)
#     if mobile:
#         UserMobile.objects.create(mobile=mobile, is_verified=False, user=user)
    
    print "insereted data"
    
    LOGGER.info("Created user account with email: %s", email)
    
    client_secret = 'traceandgigit'
    if session.device:
        client_secret = session.device.service_id
    LOGGER.info("Client Secret is :%s",client_secret)
    
    
    

    result = responses.Result(201, responses.STATUS_SUCCESS, _("Successfully created user account")%{"email":email},
        extra_fields={"userid": user.id, "email":email})
    return result.http_response(data.get("pretty"))


@require_http_methods(['POST'])
@require_valid_session
def forgot_password(request, session):
    token = ''.join(random.sample(string.digits, 6))
    print "the otp is " + token
    email = request.POST['email']
    try:
        result = send_email(email.encode('ascii','replace'), token)
    except Exception,e:
        print e
    print result
    if result:
        now = datetime.datetime.now()
        exipires__at = now + datetime.timedelta(minutes = 15)
        EmailToken.objects.create(email= email, token = token, expires_at = exipires__at)
        result = responses.Result(200, "SUCCESS", _("Succefullly sent otp to your mail please use it for reset your account"))
    else:
        result = responses.Result(200, "SUCCESS", _("unable to send otp please try after some time"))
    return result.http_response(int(request.POST.get("pretty", 0)))


@require_http_methods(['POST'])
@require_valid_session
def reset_password(request, session):
    form = resetForm(request.POST)
    errors = dict()
    if not form.is_valid():
        LOGGER.warning("Missing or invalid parameter(s): %s", request.POST)
        LOGGER.warning("Form errors:")
        for field, error in form.errors.items():
            LOGGER.warning("%s: %s", field, error.as_text())
            errors[field] = error.as_text()

    errors = dict([(f, e.as_text()) for f, e in form.errors.items()])
    
    
    if errors:
        result = InvalidParameterResult(errors=errors)
        print (result)
        return result.http_response(int(request.POST.get("pretty", 0)))
    
    data = form.cleaned_data
    email = data['email']
    password = data['password']
    conform_password = data['conform_password']
    otp = data['otp']
    
    if password!= conform_password:
        result = responses.Result(403, "FAIL", _("Password dosent match"))
        return result.http_response(int(request.POST.get("pretty", 0)))
    try:
        if EmailToken.objects.filter(email=email).exists():
            userdata=EmailToken.objects.get(email=email)
            actualotp=userdata.token
            try:
                now = datetime.datetime.now().replace(tzinfo=None)
            except Exception,e:
                print e
#             oto_expire_time = now - datetime.timedelta(minutes = 15)
#             if userdata.expires_at < now:
#                 result = responses.Result(403, "FAIL", _("token time expired"))
#                 return result.http_response(int(request.POST.get("pretty", 0)))
#             else :
            print "time not expired"
            
            if int(actualotp) != otp:
                result = responses.Result(403, "FAIL", _("token dosent match"))
                return result.http_response(int(request.POST.get("pretty", 0)))
            password_hash = User.make_password(password)
            email_record = User.objects.get(email=email)
            email_record.password=password_hash
            email_record.save()
        else:
            result = responses.Result(403, "SUCCESS", _("user wiith email doesnt exists"))
            return result.http_response(int(request.POST.get("pretty", 0)))
        result = responses.Result(200, "SUCCESS", _("succesfully updated user password"))
        return result.http_response(int(request.POST.get("pretty", 0)))
    except Exception,e:
        print e
            
    
            


@require_http_methods(['POST'])
@require_valid_session
def sign_in(request, session):
    
#     if session.user:
#         if session.user.is_guest():
#             LOGGER.info("Signin, signing out guest user %s", session.user.id)
#             trace_and_gigit_utils.sign_out(session, True)
#         else:
#             LOGGER.info("Signin, user=%s already logged in", session.user.id)
#             result = responses.Result(200,"SUCCESS", _("OK"))
#             return result.http_response(int(request.POST.get("pretty", 0)))
    try:
        print request.POST
        userid = request.POST['email']
        password = request.POST['password']
        user = model_utils.get_user(userid)
        
        password_hash = User.make_password(password)
        paswrd_check = model_utils.verify_passowrd(password_hash)
        if not paswrd_check:
            raise exceptions.InvalidParameterError("password")
        
        
        print "over here "

        if not user:
            raise exceptions.InvalidParameterError("userid")
        print "user came"
        LOGGER.info("Login request: user=%s", user)
        if session.device:
            if session.device.user and not session.device.user.guest_device and session.device.user != user:
                # device is registered to another user account
                user_email = UserEmail.objects.filter(user=session.device.user)[0]
                result = responses.Result(403, common_responses.STATUS_ERR_DEVICE_DIFF_USR,
                        _("The device is already registered to user with email: %(email)s" %{'email':user_email.email}))
                return result.http_response(int(request.POST.get("pretty", 0)))
            if not session.device.user and Device.objects.filter(user=user).count() >= REG_DEVICE_LIMIT:
                # User exceeds maximum number of devices registered to him
                result = responses.Result(403, common_responses.STATUS_ERR_DEVICE_LIMIT_EXCEEDED,
                        _("Number of devices registered for the user are exceeded")%{"email":userid})
                return result.http_response(int(request.POST.get("pretty", 0)))
        
#         if not user.check_password(password):
#             raise exceptions.InvalidParameterError("password")
        
        # sign in the user
        session.user = user
        try:
            session.save()
        except Exception,e:
            print e
        
        if session.device and not session.device.user:
            session.device.user = user
            session.device.save()
        print "about to success"
        LOGGER.info("Signed in user: %s", user)
        result = responses.Result(200, "SUCCESS", _("OK"))
    except KeyError, key:
        print key
        LOGGER.warning("Missing parameter: %s", key)
        result = InvalidParameterResult(_("Missing or invalid userid/password"))
    except exceptions.InvalidParameterError, e:
        print e
        LOGGER.warning("Missing parameter: %s", e.parameter_name)
        result = InvalidParameterResult(_("Missing or invalid userid/password"))
    except Exception,e:
        print e
        result = InvalidParameterResult(_("Missing or invalid userid/password"))
    return result.http_response(int(request.POST.get("pretty", 0)))


@require_http_methods(['POST'])
@require_valid_session
def sign_out(request, session):
    trace_and_gigit_utils.sign_out(session)
    result = responses.Result(200, "SUCCESS", _("OK"))
    return result.http_response(int(request.POST.get("pretty", 0)))

@require_http_methods(['POST'])
@require_valid_session
def unregister_device(request, session):
    device = session.device
    if device and device.user:
        device.user = None
        device.save()
        LOGGER.info("Unregistered user=%s from device=%s", device.user, device)
    session.user = None
    session.save()
    #session.delete()
    result = responses.Result(200, "SUCCESS", _("Device unRegistered successfully"))
    return result.http_response(int(request.POST.get("pretty", 0)))















def email_mobile_sign_in_impl(request,session,mobile,email,network,imsi,user,data):
    
    if email:
        if mobile and UserMobile.objects.filter(mobile=mobile).exists():
            usermobile = UserMobile.objects.get(mobile=mobile)
            if email and UserEmail.objects.filter(email=email,user_id=usermobile.user_id).exists():
                LOGGER.info("User with mobile %s exists", mobile)
                usermobile.network = network
                usermobile.imsi = imsi
                usermobile.save()
                useremail = UserEmail.objects.get(email=email)
                useremail.email=email
                useremail.user_id=user
                useremail.is_verified=True
                useremail.is_primary=True
                useremail.save()
                session.user = user
                session.save()
                if session.device:
                    session.device.user = user
                    session.device.save()
                result = responses.Result(200, "SUCCESS", _("OK"),extra_fields={"userid": user.id,"mobile":mobile,"email":email})
                return result.http_response(data.get("pretty"))
            
            
            #user mobile exits but with another email id
            if email and UserEmail.objects.filter(email=email).exists():
                result = responses.Result(500, "FAIL", _("email is registered with another mobile"),extra_fields={"userid": user.id,"mobile":mobile,"email":email})
                return result.http_response(data.get("pretty"))
            #user mobile exits but no emaild id i.e old user
            if email:
                if UserEmail.objects.filter(user_id=usermobile.user_id).exists():
                    useremail = UserEmail.objects.get(user_id=usermobile.user_id)
                    result = responses.Result(500, "FAIL", _("user is already registered with another email id"),extra_fields={"userid": user.id,"mobile":mobile,"email":useremail.email})
                    #return result.http_response(data.get("pretty"))
                    email=useremail.email
                    usermobile.network = network
                    usermobile.imsi = imsi
                    usermobile.save()
                    session.user = user
                    session.save()
                    if session.device:
                        session.device.user = user
                        session.device.save()
                    result = responses.Result(201, responses.STATUS_SUCCESS, _("user data updated successfully")%{"user":mobile},
                                          extra_fields={"userid": user.id, "mobile":mobile,"email":email})
                    return result.http_response(int(request.POST.get("pretty", 0))) 
                UserEmail.objects.create(email=email, is_verified=False, user_id=str(user), is_primary=False)
                usermobile.network = network
                usermobile.imsi = imsi
                usermobile.save()
                session.user = user
                session.save()
                if session.device:
                    session.device.user = user
                    session.device.save()
                result = responses.Result(201, responses.STATUS_SUCCESS, _("user data updated successfully")%{"user":mobile},
                                          extra_fields={"userid": user.id, "mobile":mobile,"email":email})
                return result.http_response(int(request.POST.get("pretty", 0))) 
        
        LOGGER.info("New user with mobile %s",mobile)
        user = User(first=data.get('first'), last=data.get('last'), mobile_no=mobile,
            dob=data.get('dob'), gender=data.get('gender'))
        user.save()
        # log the user in
        session.user = user
        session.save()
        if session.device:
            device = session.device
            device.user = user
            device.save()
        #creating user mobiletable entry
        if mobile:
            UserMobile.objects.create(mobile=mobile, is_verified=False, user=user, network=network, imsi = imsi)
        #creating user email table entry
        try:
            if email:
                UserEmail.objects.create(email=email, is_verified=False, user_id=str(user), is_primary=False)
        except:
            result = responses.Result(500, "fail", _("email already mapped to another user"),
                                          extra_fields={"userid": user.id, "mobile":mobile})
            return result.http_response(int(request.POST.get("pretty", 0)))
    else:
        
        if mobile and UserMobile.objects.filter(mobile=mobile).exists():
            LOGGER.info("User with mobile %s exists", mobile)
            usermobile = UserMobile.objects.get(mobile=mobile)
            usermobile.network = network
            usermobile.imsi = imsi
            usermobile.save()
            session.user = user
            session.save()
            if session.device:
                session.device.user = user
                session.device.save()
            
            if email:
                result = responses.Result(200, "SUCCESS", _("OK"),extra_fields={"userid": user.id,"mobile":mobile,"email":email})
            else:
                result = responses.Result(200, "SUCCESS", _("OK"),extra_fields={"userid": user.id,"mobile":mobile})
            #result = responses.Result(200, "SUCCESS", _("OK"),extra_fields={"userid": user.id,"mobile":mobile})
            return result.http_response(data.get("pretty"))
        LOGGER.info("New user with mobile %s",mobile)
        user = User(first=data.get('first'), last=data.get('last'), mobile_no=mobile,
            dob=data.get('dob'), gender=data.get('gender'))
        user.save()
        # log the user in
        session.user = user
        session.save()
        if session.device:
            device = session.device
            device.user = user
            device.save()
        if mobile:
            UserMobile.objects.create(mobile=mobile, is_verified=False, user=user, network=network, imsi = imsi)
        
   
    client_secret = 'traceandgigit'
    if session.device:
        client_secret = session.device.service_id
    LOGGER.debug("Client Secret is :%s",client_secret)
    
    
    result = responses.Result(201, responses.STATUS_SUCCESS, _("Successfully created user account for traceandgigit")%{"user":mobile},
        extra_fields={"userid": user.id, "mobile":mobile,"email":email})
    return result.http_response(int(request.POST.get("pretty", 0)))        



