package com.ei.kalavarafoods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ei.kalavarafoods.utils.Constants;

public class SmsReceiver extends BroadcastReceiver{
    private static final String TAG = SmsReceiver.class.getSimpleName();
    private static Context _context;

    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;
        final Bundle bundle = intent.getExtras();
        if (bundle!=null){
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object aPdusObj : pdusObj) {
//                SmsMessage smsMessage;
//                if (Build.VERSION.SDK_INT >= 19) { //KITKAT
//                    SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
//                    smsMessage = msgs[0];
//                } else {
//                    Object pdus[] = (Object[]) bundle.get("pdus");
//                    smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
//                }
//                String senderAddress = smsMessage.getDisplayOriginatingAddress();
//                String message = smsMessage.getDisplayMessageBody();
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                String senderAddress = currentMessage.getDisplayOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();

                Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                // if the SMS is not from our gateway, ignore the message
                if (!senderAddress.toLowerCase().contains(Constants.SMS_ORIGIN.toLowerCase())) {
                    return;
                }

                // verification code from sms
                String verificationCode = getVerificationCode(message);

                Log.e(TAG, "OTP received: " + verificationCode);

                Intent hhtpIntent = new Intent(context, HttpService.class);
                hhtpIntent.putExtra("otp", verificationCode);
                context.startService(hhtpIntent);
            }
        }
    }

    private String getVerificationCode(String message) {
//        String code = null;
//        int index = message.indexOf(Constants.OTP_DELIMITER);
//        if (index != -1) {
//            int start = index + 2;
//            int length = Constants.OTP_LENGTH;
//            code = message.substring(start, start + length);
//            return code;
//        }
        return message.substring(0,6);
    }

    public static void stopService() {
        Intent hhtpIntent = new Intent(_context, HttpService.class).putExtra("otp","123456");
        _context.stopService(hhtpIntent);
    }
}
