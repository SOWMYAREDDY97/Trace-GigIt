from django.conf.urls import patterns, url

urlpatterns = patterns('login.views',
    url(r'^signUp/?$', 'sign_up', name='sign_up'),
    #url(r'^signIn/?$', 'sign_in', name='sign_in'),
    
)