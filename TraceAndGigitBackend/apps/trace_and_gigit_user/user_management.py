import datetime
import logging

from django import forms
from apps.trace_and_gigit_user.models import ClientSecret, User, UserEmail
from libs.utils import strip_unsafe_html
from libs.forms import InitialDefaultForm
from django.core.validators import MinLengthValidator,MaxLengthValidator

LOGGER = logging.getLogger("trace_and_gigit_user.user_management")


class BaseForm(InitialDefaultForm):
    pretty = forms.IntegerField(required=False, initial=0)


class DeviceRegistrationForm(BaseForm):
    serialNo = forms.CharField(max_length=64)
    os = forms.CharField(max_length=64)
    osVersion = forms.CharField(max_length=64)
    make = forms.CharField(max_length=64)
    model = forms.CharField(max_length=64)
    mobile = forms.CharField(max_length=20, required=False)
    resolution = forms.RegexField(max_length=12, regex=r'^\d{2,4}x\d{2,4}$',
            help_text='Should be widthxheight, e.g. 200x400')
    profile = forms.CharField(max_length=32)
    clientSecret = forms.CharField(max_length=32)
    serviceId = forms.CharField(max_length=255, required=False)
    ''' ==============Modifications for cabletv start=============================== '''
    friendlyName = forms.CharField(max_length=64, required=False)
    status = forms.CharField(max_length=32, required=False)
    msoSubscriberId = forms.CharField(max_length=12, required=False)
    ''' ==============Modifications for cabletv end=============================== '''

    def clean_clientSecret(self):
        os = self.cleaned_data['os']
        clientSecret = self.cleaned_data['clientSecret']
        try:
            secret = ClientSecret.objects.get(os__iexact=os, secret__iexact=clientSecret)
            self.cleaned_data['serviceId'] = secret.service_id
        except ClientSecret.DoesNotExist:
            raise forms.ValidationError("Invalid client secret")
        return self.cleaned_data['clientSecret']



class resetForm(BaseForm):
    email = forms.CharField()
    password = forms.CharField(min_length=6, max_length=32)
    conform_password = forms.CharField(min_length=6, max_length=32)
    otp = forms.IntegerField()
    def clean(self):
        cleaned_data = super(resetForm, self).clean()
        password = cleaned_data.get('password')
        
        # clean up char fields
        for field in ('first', 'last'):
            if cleaned_data.get(field):
                cleaned_data[field] = strip_unsafe_html(cleaned_data[field])
        return cleaned_data

class SignUpForm(BaseForm):
    email = forms.CharField()
    password = forms.CharField(min_length=6, max_length=32)
    first = forms.CharField(max_length=32)
    autoPassword = forms.BooleanField(required=False)
    last = forms.CharField(max_length=32, required=False)
    dob = forms.DateField(required=False)
    gender = forms.CharField(max_length=1, required=False)

    def clean_gender(self):
        gender = self.cleaned_data.get('gender')
        if not gender:
            self.cleaned_data['gender'] = 'N'    # Not specified
        elif not gender in ('M', 'F'):
            raise forms.ValidationError("Invalid Gender")
        return self.cleaned_data['gender']

    def clean_dob(self):
        dob = self.cleaned_data.get('dob')
        # ensure that dob doesn't point to future date
        
        if dob and dob > datetime.date.today():
            raise forms.ValidationError("Invalid date of birth, should not be a future value")
        return dob
    
    def clean(self):
        cleaned_data = super(SignUpForm, self).clean()
        password = cleaned_data.get('password')
        
        # clean up char fields
        for field in ('first', 'last'):
            if cleaned_data.get(field):
                cleaned_data[field] = strip_unsafe_html(cleaned_data[field])
        return cleaned_data



