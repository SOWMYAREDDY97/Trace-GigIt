from django.contrib import admin

from apps.common.models import CircleMapping

class CircleMappingnAdmin(admin.ModelAdmin):
    fields = ('operator', 'region', 'language','mcc','mnc')

# Register your models here.
admin.site.register(CircleMapping)
