/** A broadcast receiver who listens for incoming SMS */
package beriain.atob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = "SmsBroadcastReceiver";

  private final String serviceProviderNumber;
  // private final String serviceProviderSmsCondition;

  private Listener listener;

  public SmsBroadcastReceiver(String serviceProviderNumber) {
    Log.w(TAG, "we are receiving!!!");
    this.serviceProviderNumber = serviceProviderNumber;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.w(TAG, "HELLLLOOOOO from onReceive");
    if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
      String smsSender = "";
      String smsBody = "";
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
          smsSender = smsMessage.getDisplayOriginatingAddress();
          smsBody += smsMessage.getMessageBody();
        }
      } else {
        Bundle smsBundle = intent.getExtras();
        if (smsBundle != null) {
          Object[] pdus = (Object[]) smsBundle.get("pdus");
          if (pdus == null) {
            // Display some error to the user
            Log.e(TAG, "SmsBundle had no pdus key");
            return;
          }
          SmsMessage[] messages = new SmsMessage[pdus.length];
          for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            smsBody += messages[i].getMessageBody();
          }
          smsSender = messages[0].getOriginatingAddress();
        }
      }

      if (smsSender.equals(serviceProviderNumber)) {
        if (listener != null) {
          listener.onTextReceived(smsBody);
        }
      }
    }
  }

  void setListener(Listener listener) {
    this.listener = listener;
  }

  abstract static class Listener {

    protected StringBuilder collector;

    public Listener() {

      collector = new StringBuilder();
    }

    abstract void onTextReceived(String text);
  }
}
