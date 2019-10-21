import logging

from django.db import models

LOGGER = logging.getLogger("common.models")

class IpCountryMapping(models.Model):
    begin_ip = models.IPAddressField()
    end_ip = models.IPAddressField()
    begin_number = models.BigIntegerField()
    end_number = models.BigIntegerField()
    country_code = models.CharField(max_length=8, blank=True, db_index=True)
    country_name = models.CharField(max_length=50, blank=True, db_index=True)
    
    @classmethod
    def get_country_by_ip(cls, ip_address):
        #Converting ip address to long integer
        octates = [v for v in ip_address.split('.') if v]
        number = 0
        for i, value in enumerate(octates):
            power = 3-i
            number = number + long((int(value) % 256) * pow(256, power))
        LOGGER.debug("IP: %s Converted to the long: %s", ip_address, number)
        try:
            country = cls.objects.filter(begin_number__lte=number, end_number__gte=number)[0]
            return country.country_code
        except IndexError:
            LOGGER.debug("Information for ip %s doesn't exist in db", ip_address)
            return None
class CircleMapping(models.Model):
    mcc = models.IntegerField()
    mnc = models.IntegerField()
    operator = models.CharField(max_length=32, blank=True)
    region = models.CharField(max_length=32, blank=True)
    language = models.CharField(max_length = 32)
    score = models.IntegerField()
    created_on = models.DateTimeField(auto_now_add=True)
    modified_on = models.DateTimeField(auto_now=True)
    
    
    def __unicode__(self):
        return unicode(self.operator) + ": " + unicode(self.region) + ": " + unicode(self.language) + ": " + unicode(self.mcc) + ": " + unicode(self.mnc)
    
    
    @classmethod
    def get_circle(cls, mcc, mnc):
        #Getting circle based on mcc and mnc values
        circles = cls.objects.filter(mcc = mcc, mnc = mnc)
        return circles