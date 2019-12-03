from django.conf.urls import patterns, url

urlpatterns = patterns('apps.trace_and_gigit_user.views',
    url(r'^registerDevice/?$', 'register_device', name='register_device'),
    url(r'^generateKey/?$', 'generate_key', name='generate_key'),
    url(r'^signUp/?$', 'sign_up', name='sign_up'),
    url(r'^signIn/?$', 'sign_in', name='sign_in'),
    url(r'^signOut/?$', 'sign_out', name='sign_out'),
    url(r'^forgotPassword/?$', 'forgot_password', name='forgot_password'),
    url(r'^resetPassword/?$', 'reset_password', name='reset_password'),
    url(r'^unregisterDevice/?$', 'unregister_device', name='unregister_device'),
    
)
