from flask import Flask, request, redirect
from twilio.twiml.messaging_response import MessagingResponse
import json
import zlib
import requests
import base64
from bs4 import BeautifulSoup

app = Flask(__name__)

@app.route("/sms", methods=['GET', 'POST'])
def sms_reply():
    """Respond to incoming http requests, proxying them through
    """
    #Start our TwiML response
    resp = MessagingResponse()
    body = request.values.get("Body", None)

    r = requests.get(body)

    # Strip out script and style tags
    soup = BeautifulSoup(r.text, 'lxml')
    [s.extract() for s in soup('script')]
    [s.extract() for s in soup('link')]
    [s.extract() for s in soup('meta')]
    [s.extract() for s in soup('style')]

    # Compress the result
    processed = bytearray(str(soup), 'utf-8')
    processed = zlib.compress(processed)
    processed = base64.b64encode(processed)

    n = 1000 # Chunk the string into groups of 1000 characters to stay well below the 1600 character limit
    parts = [processed[i:i+n] for i in range(0, len(processed), n)]
    for chunk in parts:
        resp.message(str(chunk)[2:-1]) # Python appends some garbage to the result
    resp.message('=====') # This is a good way to declare the end
    return str(resp)

if __name__ == "__main__":
    app.run(debug=False)
