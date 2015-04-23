package com.madx.bwm.receiver_service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.madx.bwm.R;
import com.madx.bwm.ui.MissListActivity;

public class GcmIntentService extends IntentService {
    /**通知类型,以后会以这个决定跳转页面*/
    public static final int BONDALERT = 1;
    /**GCM异常信息*/
    public static final int GCM_MESSAGE = 2;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
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
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
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
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setContentText(msg);

//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
        mBuilder.setTicker("测试通知来啦");
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mNotificationManager.notify(GCM_MESSAGE, mBuilder.build());
    }

    /**
     *
     * @param msg
     */
    private void sendNotification(Bundle msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MissListActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.tab_message)
                        .setContentTitle(msg.getString("title"))
                        .setContentText(msg.getString("message"));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
//        mBuilder.setNumber(12);
        mBuilder.setTicker("测试通知来啦");
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(BONDALERT, mBuilder.build());
    }
}