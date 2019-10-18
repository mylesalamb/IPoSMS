from twilio.rest import Client

ACCOUNT_SID = "AC1ff67468e34c89848511de573a0e5056"
AUTH = "bfa2d58a73b19954934dbd5038ff7230"



def sent_text(encoded_string, recip='+447758928837'):


	client = Client(ACCOUNT_SID,AUTH)
	message = client.messages.create(
	                              body='Hi there!',
	                              from_='+12162798288',
	                              to='+447758928837'
	                          )

	return message.sid


