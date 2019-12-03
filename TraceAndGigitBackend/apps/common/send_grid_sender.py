'''
Created on Nov 10, 2019

@author: S534596
'''

import os
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail



def send_email(to_email, token):

    message = Mail(
        from_email='S534596@NWMISSOURI.EDU',
        to_emails = to_email,
        subject='Reset Password',
        html_content='<strong>please use this token to reset your password ' + token +' </strong>')
    
    
    sg = SendGridAPIClient('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')

    response = sg.send(message)
    if response.status_code == 200:
        return True
    else:
        return False
# print response
# print response.status_code
# print response.body
# print response.headers
