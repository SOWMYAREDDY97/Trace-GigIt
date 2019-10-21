from django.db import models

class Config(models.Model):
    key = models.CharField(max_length=255, db_index=True)
    value_type = models.CharField(max_length=32, blank=True, default='')
    value = models.CharField(max_length=255)

    @staticmethod
    def get(key, default=None):
        records = Config.objects.filter(key=key)
        if records.exists():
            return records[0]
        return default

    def __unicode__(self):
        return self.key
