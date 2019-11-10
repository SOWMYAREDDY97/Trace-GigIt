'''
Created on Nov 10, 2019

@author: S534596
'''

import os
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail


message = Mail(
    from_email='S534596@NWMISSOURI.EDU',
    to_emails='gandrakotassbharadwaj@gmail.com',
    subject='Sending with Twilio SendGrid is Fun',
    html_content='<strong>and easy to do anywhere, even with Python</strong>')


sg = SendGridAPIClient('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')

response = sg.send(message)
print response
print response.status_code
print response.body
print response.headers
