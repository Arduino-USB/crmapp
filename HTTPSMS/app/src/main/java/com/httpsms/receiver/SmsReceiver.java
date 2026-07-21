package com.httpsms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.httpsms.database.AppDatabase;
import com.httpsms.database.MessageLog;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "HTTPSMS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            if (messages != null && messages.length > 0) {
                // Handle multi-part or concatenated messages cleanly
                String sender = messages[0].getOriginatingAddress();
                StringBuilder bodyBuilder = new StringBuilder();
                
                for (SmsMessage msg : messages) {
                    if (msg != null && msg.getMessageBody() != null) {
                        bodyBuilder.append(msg.getMessageBody());
                    }
                }

                String messageText = bodyBuilder.toString();
                Log.i(TAG, "Incoming text intercept! From: " + sender + " | Content: " + messageText);

                // Run database write asynchronously off the main thread
                new Thread(() -> {
                    try {
                        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
                        MessageLog incomingLog = new MessageLog();
                        incomingLog.message = messageText;
                        incomingLog.type = "sms";
                        incomingLog.direction = "incoming"; // <--- Crucial tag modification
                        incomingLog.time = System.currentTimeMillis();
                        incomingLog.number = sender;
                        incomingLog.groupId = 0; // Default to individual chat lane context

                        db.logDao().insert(incomingLog);
                        Log.d(TAG, "Successfully committed incoming text message log record to database.");
                    } catch (Exception e) {
                        Log.e(TAG, "Failed writing incoming text to DB: " + e.getMessage(), e);
                    }
                }).start();
            }
        }
    }
}
