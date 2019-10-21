from django.contrib import admin

from apps.trace_and_gigit_user.models import ClientSecret, Device

class DeviceAdmin(admin.ModelAdmin):
    list_display = ('os', 'make', 'model', 'serial_number', 'user_email','service_id')
    fields = ('os', 'os_version', 'make', 'model', 'serial_number', 'user','service_id')
    search_fields = ['make', 'model', 'serial_number', 'user__first', 'user__useremail__email','service_id']

    def get_readonly_fields(self, request, obj=None):
        if obj:
            return ('os', 'os_version', 'make', 'model', 'serial_number','service_id')
        return []


class HdWhiteListedDevicesAdmin(admin.ModelAdmin):
    list_display = ('os', 'make', 'model', 'os_version')

"""
class PromotionCodesAdmin(admin.ModelAdmin):
    list_display = ('promo_name', 'promo_code', 'description')


class DeviceToPromocodeAdmin(admin.ModelAdmin):
    list_display = ('white_listed_device', 'promo_code')
"""

class UserCommentAdmin(admin.ModelAdmin):
    list_display = ('content_id', 'comment')
    fields = ('content_id', 'comment')
    search_fields = ['content_id', 'comment']

    def get_readonly_fields(self, request, obj=None):
        if obj:
            return ('content_id', 'comment')
        return []

class UserContentRatingAdmin(admin.ModelAdmin):
    list_display = ('content_id', 'content_rating', 'review')
    fields = ('content_id', 'content_rating', 'review')
    search_fields = ['content_id', 'content_rating', 'review']

    def get_readonly_fields(self, request, obj=None):
        if obj:
            return ('content_id', 'content_rating', 'review')
        return []

#admin.site.register(DeviceToPromocode, DeviceToPromocodeAdmin)
#admin.site.register(PromotionCodes, PromotionCodesAdmin)

admin.site.register(ClientSecret)
admin.site.register(Device, DeviceAdmin)



