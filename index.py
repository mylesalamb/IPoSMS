from flask import Flask, request, redirect
from twilio.twiml.messaging_response import MessagingResponse
import json


import requests

app = Flask(__name__)

@app.route("/",methods=["get"])
def index():
	return "I am alive"



@app.route("/sms", methods=['GET', 'POST'])
def sms_reply():
    """Respond to incoming json formatted texts, requesting resources
        {
            "type" : ... , 
            "resource" : ...,
        }

    """
    #Start our TwiML response
    resp = MessagingResponse()
    arg = request.values.get("Body",None)

    arg = json.loads(arg)

    #check if the text recieved was good
    if not arg or "type" not in arg:
        resp.message("Invalid message body")
        return str(resp)

    if arg["type"] == "get":
        extern_resp = requests.get(arg["resource"]) 
    
    #we couldnt get the resource
    if extern_resp.status_code != 200:
        resp.message("Couldnt get resource soz")
    resp.message(extern_resp.text)
    return str(resp)

if __name__ == "__main__":
    app.run(debug=True)