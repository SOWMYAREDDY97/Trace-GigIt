"""
Django settings for TraceAndGigitBackend project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
import json
import sys
from django.utils.translation import ugettext_lazy as _

SETTINGS_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.dirname(SETTINGS_DIR)
MAX_INCORRECT_PASSWORD_ATTEMPT = 5

# import settings from JSON config
PROJECT_SETTINGS_FILE = os.path.join(PROJECT_DIR, "conf", "trace_and_gigit.json")
CUSTOM_SETTINGS_FILE = os.path.join("/etc/TraceAndGigitBackend/TraceAndGigitBackend", "trace_and_gigit.json")
if os.path.exists(CUSTOM_SETTINGS_FILE):
    JSON_SETTINGS_FILE = CUSTOM_SETTINGS_FILE
else:
    JSON_SETTINGS_FILE = PROJECT_SETTINGS_FILE
JSON_SETTINGS = json.loads(open(JSON_SETTINGS_FILE).read())


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'd$((me&auw+0^p7wrq((bj5wm5qj@#%^xqpnp%z8a+l*y@zl0='

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.sites',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'django_cron',
    'south',
    'apps.common',
    'apps.config',
    'apps.trace_and_gigit_user',
    'django.contrib.admin',
    'django.contrib.admindocs',
    
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'TraceAndGigitBackend.urls'

WSGI_APPLICATION = 'TraceAndGigitBackend.wsgi.application'


# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql', 
        'NAME': 'traceandgigit',
        'USER': 'root', 
        'PASSWORD': 'lolrajahira',
        'HOST': 'localhost',   # Or an IP Address that your DB is hosted on
        'PORT': '3306',
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'
