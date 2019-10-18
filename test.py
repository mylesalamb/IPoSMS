from twilio.rest import Client
from base64 import b64encode
import json
ACCOUNT_SID = "AC1ff67468e34c89848511de573a0e5056"
AUTH = "bfa2d58a73b19954934dbd5038ff7230"



def send_text(encoded_string, filename = "test" ,recip='+447758928837'):
	'''  sends json literal over sms with encoded string base64 with optional filename '''

	client = Client(ACCOUNT_SID,AUTH)
	message = client.messages.create(
	                              body=  json.dumps({"name":filename,"data":encoded_string}),
	                              from_='+12162798288',
	                              to='+447758928837'
	                          )

	return message.sid

def get_64image(filename):
	''' returns a base64 encoded file with of teh given filename '''
	with open(filename,"rb") as f:
		file = f.read()
		return b64encode(file).decode("UTF-8")


if __name__ == "__main__":
	test_file = "test.png"
	response = send_text(get_64image(test_file),filename=test_file)
	print(response)