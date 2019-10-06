from django.db import models
import hashlib

PASSWORD_SALT = "adf0qf-92%2f$245oi@twgwt024#$s^%#^esf"

GENDER_CHOICES = (
    ('M', 'Male'),
    ('F', 'Female'),
    ('O', 'Other'),
    ('N', 'Not Specified'),
)

class customer(models.Model):
    # profile information
    first = models.CharField(max_length=64, blank=True, db_index=True)
    last = models.CharField(max_length=64, blank=True, db_index=True)
    # password hash is sha256 hash digest of user password plus salt
    password_hash = models.CharField(max_length=128, null=True)
    mobile = models.CharField(max_length=32, null=True)
    gender = models.CharField(max_length=2, choices=GENDER_CHOICES, default='N')
    # created and modified timestamps
    created_on = models.DateTimeField(auto_now_add=True)
    modified_on = models.DateTimeField(auto_now=True)
    # associated device if this is guest account

    @staticmethod
    def make_password(raw_password):
        return hashlib.sha256(raw_password + PASSWORD_SALT).hexdigest()
    
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
    
    
