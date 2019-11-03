import hashlib
import datetime
import logging
import pytz

from apps.trace_and_gigit_user import constants
from TraceAndGigitBackend import settings
from libs.utils import datetime_str

from django.db import models
from django.core.cache import cache

LOGGER = logging.getLogger("TraceAndGigitBackend.apps.trace_and_gigit_user.models")

GENDER_CHOICES = (
    ('M', 'Male'),
    ('F', 'Female'),
    ('O', 'Other'),
    ('N', 'Not Specified'),
)

class ClientSecret(models.Model):
    os = models.CharField(max_length=32)
    secret = models.CharField(max_length=32)
    service_id = models.CharField(max_length=255, default='traceandgigit')

    class Meta:
        unique_together = [('os', 'secret')]

    def __unicode__(self):
        return (self.os)

class User(models.Model):
    # profile information
    first = models.CharField(max_length=64, blank=True, db_index=True)
    last = models.CharField(max_length=64, blank=True, db_index=True)
    # password hash is sha256 hash digest of user password plus salt
    password_hash = models.CharField(max_length=128, null=True)
    mobile_no = models.CharField(max_length=32, null=True)
    mobile_no_verified = models.BooleanField(default=False)
    dob = models.DateField(null=True)
    gender = models.CharField(max_length=2, choices=GENDER_CHOICES, default='N')
    # created and modified timestamps
    created_on = models.DateTimeField(auto_now_add=True)
    modified_on = models.DateTimeField(auto_now=True)
    # associated device if this is guest account
    guest_device = models.ForeignKey("Device", db_index=True, unique=True,
            null=True, default=None, related_name='guest_user')

    @staticmethod
    def make_password(raw_password):
        return hashlib.sha256(raw_password + constants.PASSWORD_SALT).hexdigest()
    
    def set_password(self, raw_password):
        self.password_hash = self.make_password(raw_password)
        self.save()
        
    def check_password(self, raw_password):
        return self.password_hash == self.make_password(raw_password)

    def full_name(self):
        return ' '.join([self.first, self.last])

    def is_guest(self):
        return self.guest_device is not None

    def display_name(self):
        name = self.full_name().strip()
        if name:
            return name
        # return email address without domain
        try:
            user_email = self.useremail_set.all()[0]
            tokens =  user_email.email.split('@')
            return tokens[0] if tokens else ''
        except:
            return ""
    
    def __unicode__(self):
        fields = [self.id, self.first, self.last]
        return " ".join([unicode(f) for f in fields if f])




class UserMobile(models.Model):
    mobile = models.CharField(unique=True, max_length=32, null=False, db_index=True)
    user = models.ForeignKey(User)
    is_verified = models.BooleanField(default=False)
    network = models.CharField(max_length = 12,null=True)
    imsi = models.CharField(max_length = 64,null=True)
    # created and modified timestamps
    created_on = models.DateTimeField(auto_now_add=True)
    modified_on = models.DateTimeField(auto_now=True)
    
    @classmethod
    def get_network(cls, user):
        #Getting circle based on mcc and mnc values
        network = "3G"
        try:
            circles = cls.objects.get(user = user)
            if circles.network:
                network = circles.network
            else: network = "3G"
        except:
            LOGGER.info("No entry for this user in UserMobile")
        return network






class Device(models.Model):
    os = models.CharField(max_length=64, db_index=True)
    os_version = models.CharField(max_length=64)
    make = models.CharField(max_length=64, db_index=True)
    model = models.CharField(max_length=128)
    # resolution in widthxheight e.g. 800x1200
    resolution = models.CharField(max_length=12)
    # device serial number, can be something unique e.g. Mac id.
    serial_number = models.CharField(max_length=255, db_index=True)
    # profile, allows more than one device ids to be associated with a device
    profile = models.CharField(max_length=32)
    device_id = models.CharField(unique=True, db_index=True, max_length=255)
    service_id = models.CharField(max_length=255, db_index=True, default='traceandgigit')
    user = models.ForeignKey(User, null=True, blank=True, default=None)
    
    # created and modified timestamps
    created_on = models.DateTimeField(auto_now_add=True)
    modified_on = models.DateTimeField(auto_now=True)

    class Meta:
        unique_together = [('os', 'make', 'model', 'serial_number', 'profile'),]
    
    def __unicode__(self):
        return (self.os+self.os_version+self.serial_number+self.profile)

    def user_email(self):
        if not self.user:
            return ''
        return self.user.useremail_set.all()[0].email







class Session(models.Model):
    """Class representing an API Session."""
    DEVICE_CACHE_PREFIX = "apps.trace_and_gigit_user.models.session.device.%s"
    
    device = models.ForeignKey(Device, db_index=True, null=True)
    client_key = models.CharField(max_length=128, null=True, unique=True, db_index=True)
    expires_at = models.DateTimeField(null=True, default=None)
    user = models.ForeignKey(User, null=True)
    ip_address = models.IPAddressField(null=True)

    # created timestamp
    created_on = models.DateTimeField(auto_now_add=True)
    
    def session_type(self):
        if self.device:
            return "device"
        return "browser"

    @classmethod
    def get_device_session(cls, client_key):
        #cache_key = cls.DEVICE_CACHE_PREFIX % client_key
        #session = cache.get(cache_key)
        #if session:
        #    LOGGER.debug("Got device session from cache, key: %s", cache_key)
        #    return session
        session = cls.objects.get(client_key=client_key, expires_at__gt=datetime.datetime.today())
        #now = pytz.UTC.localize(datetime.datetime.now())
        #cache_time = (session.expires_at - now).seconds
        #cache.set(cache_key, session, cache_time)
        return session

    

    def save(self, force_insert=False, force_update=False, using=None,
             update_fields=None):
        super(Session, self).save(force_insert=force_insert, force_update=force_update,
                using=using, update_fields=update_fields)
        # save to cache
        if self.client_key:
            now = datetime.datetime.now()
            cache_key = self.DEVICE_CACHE_PREFIX % self.client_key
            cache_time = (self.expires_at - now).seconds
            LOGGER.debug("Saved device session to cache, key: %s", cache_key)
        else:
            cache_key = self.BROWSER_CACHE_PREFIX % self.id
            cache_time = 7*24*60*60
            LOGGER.debug("Saved browser session to cache, key: %s", cache_key)
        cache.set(cache_key, self, cache_time)



class UserEmail(models.Model):
    email = models.EmailField(unique=True, db_index=True)
    user = models.ForeignKey(User)
    is_verified = models.BooleanField(default=False)
    is_primary = models.BooleanField(default=False)

    # created and modified timestamps
    created_on = models.DateTimeField(auto_now_add=True)
    modified_on = models.DateTimeField(auto_now=True)
    
    @classmethod
    def get_email(cls, user):
        #Getting circle based on mcc and mnc values
        email = ""
        try:
            useremail = cls.objects.get(user = user)
            if useremail.email:
                email = useremail.email
            else: email = ""
        except:
            LOGGER.info("No email for this user")
        return email


