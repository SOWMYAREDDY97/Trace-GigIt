'''
Created on Nov 10, 2019

@author: S534596
'''



# from textmagic.rest import TextmagicRestClient
#   
# username = "subrahmanyasaibharad"
# token = "yIjw84khgP51gLpOzSjUByfo4NWHg0"
# client = TextmagicRestClient(username, token)
# 
# 
# Yn4QaRCAng
#   
# message = client.messages.create(phones="9294149700", text="Hello TextMagic")



import textmagic.client 
client = textmagic.client.TextMagicClient('subrahmanyasaibharad', 'Yn4QaRCAng') 
result = client.send("Hello, World!", "+19294149700") 
message_id = result['message_id'].keys()[0]


