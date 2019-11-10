'''
Created on Nov 10, 2019

@author: S534596
'''

import urllib
import urllib.parse
import urllib.request
import ssl

# Proovl SMS API settings www.proovl.com / for Python 3 send SMS
user = "ZXh0zTG"   # change ***** to your Proovl user ID
token = "A2yL8KqCnzhzNJq6G7hN5hCAYBqP8g9R"  # change ***** to your Proovl token
from_n = "9294149700"  # change ***** to your Proovl SMS number
to = "9294149700"    # change ***** to receiver number
text = "testing"   # Text message


url = "https://www.proovl.com/api/send.php?"   
params = {       
"user": user,       
"token": token,       
"from": from_n,
"to": to,
"text": text} 
try:
    _create_unverified_https_context = ssl._create_unverified_context
except AttributeError:
    pass
else:
    ssl._create_default_https_context = _create_unverified_https_context
query_string = urllib.parse.urlencode(params)      
http_req = url + query_string 
f = urllib.request.urlopen(http_req)
txt = (f.read().decode('utf-8'))
x = txt.split(";")
if x[0] == "Error":
    print "Error message:",x[1]
else:
    print "Message has been sent! ID:" + x[1]
f.close()

