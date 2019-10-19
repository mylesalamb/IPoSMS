from flask import Flask, request, redirect
from twilio.twiml.messaging_response import MessagingResponse

import requests

app = Flask(__name__)

@app.route("/",methods=["get"])
def index():
	return "I am alive"

@app.route("/sms", methods=['GET', 'POST'])
def sms_reply():
    """Respond to incoming calls with a simple text message."""
    #Start our TwiML response
    resp = MessagingResponse()
    arg = request.values.get("Body",None)

    #there was no message body so send something silly
    if not arg:
    	resp.message("The Robots are coming! Head for the hills!")
    	return str(resp)
    extern_resp = requests.get(arg)
    #we couldnt get the resource
    if extern_resp.status_code != 200:
        resp.message("Couldnt get resource soz")
    resp.message(extern_resp.text)
    return str(resp)

if __name__ == "__main__":
    app.run(debug=True)