'''
Created on Oct 8, 2019

@author: S534596
'''


from twilio.rest import Client

# the following line needs your Twilio Account SID and Auth Token
client = Client("AC6dfba637c35dcf7d37b3aafe4f75fc30", "3cc6d7a738355523db07a7130234203a")

# change the "from_" number to your Twilio number and the "to" number
# to the phone number you signed up for Twilio with, or upgrade your
# account to send SMS to any phone number
client.messages.create(to="+19294149700", 
                       from_="+12015813836", 
                       body="lolraja")
