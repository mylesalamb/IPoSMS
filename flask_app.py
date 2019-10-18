from flask import Flask, request, redirect
from twilio import twiml
import requests

app = Flask(__name__)

@app.route("/",methods=["get"])
def index():
	return "I am alive"

@app.route("/sms", methods=['GET', 'POST'])
def sms_reply():
    """Respond to incoming calls with a simple text message."""
    # Start our TwiML response
    resp = twiml.Response()

    # Add a message
    resp.message("The Robots are coming! Head for the hills!")

    return str(resp)

if __name__ == "__main__":
    app.run(debug=True)