import datetime
import hashlib
import random
import uuid
import logging

import binascii

import constants

from TraceAndGigitBackend.settings import JSON_SETTINGS
from django.utils import timezone



LOGGER = logging.getLogger("traceandgigit_user.traceandgigit_utils")

def get_tzaware_now():
    now = timezone.now()
    # drop sub-second precision
    now = datetime.datetime(now.year, now.month, now.day, now.hour, now.minute, now.second,
        tzinfo=now.tzinfo)
    return now
    
def get_client_key_expiry_time():
    """
    Get time at which client key expires.
    """
    expires_at = get_tzaware_now() + constants.CLIENT_KEY_DURATION
    expires_at = expires_at.replace(tzinfo=None)
    return expires_at

def generate_client_key(device_id):
    """
    Generate client key for the given device id.
    The key generated is random for all practical purposes.
    """
    key = hashlib.sha256(device_id + datetime.datetime.now().isoformat() + str(random.random()))
    return key.hexdigest()

def generate_device_id(os, make, model, serial_no, profile):
    """
    Generate device id based on given parameters. For the same parameters, the
    device id generated will be same.
    """
    fields = [os, make, model, serial_no, profile]
    name = ':'.join([str(f) for f in fields])
    
    key_input = name + datetime.datetime.now().isoformat() + str(random.random())
    LOGGER.info("Generating device id for %s", name)
    return str(uuid.uuid5(constants.DEVICE_NAMESPACE_UUID, key_input))
    
def create_unique(fields):
    return hashlib.sha224(fields).hexdigest()

def get_validto_date(days):
    now = datetime.datetime.today()
    delta = datetime.timedelta(days=2)
    valid_to = now + delta
    return valid_to.strftime('%Y%m%d-%H%M%S')

def get_validto_datetime(days):
    now = get_tzaware_now()
    delta = datetime.timedelta(days=2)
    valid_to = now + delta
    return valid_to

def sign_out(session, unregister_device=False):
    if not session.user:
        return
    LOGGER.info("Signing out, user=%s", session.user.id)
    session.user = None
    session.save()
    device = session.device
    if not device or not device.user:
        return
    if unregister_device or device.user.is_guest():
        device.user = None
        device.save()
        LOGGER.info("Unregistered user=%s from device=%s", device.user, device)

def generate_tzaware_now(date):
    now = timezone.now()
    return date.replace(tzinfo=now.tzinfo)
