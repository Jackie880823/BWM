package com.bondwithme.BondWithMe.receiver_service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.bondwithme.BondWithMe.util.NotificationUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;

public class GcmIntentService extends IntentService {
    private final static String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                NotificationUtil.sendNotification(this, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                NotificationUtil.sendNotification(this, "Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
//                for (int i=0; i<5; i++) {
//                    Log.i(TAG, "Working... " + (i + 1)
//                            + "/5 @ " + SystemClock.elapsedRealtime());
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                    }
//                }
//                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                try {
//                    if (App.getLoginedUser() == null) {
//                        Log.d("","nonononono");
//                        return;
//                    }

                    String from = extras.getString("from");
                    if (from.equals("google.com/iid")) {
                        //related to google ... DO NOT PERFORM ANY ACTION
                    } else {
                        //HANDLE THE RECEIVED NOTIFICATION
                        NotificationUtil.sendNotification(this, extras, true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


}