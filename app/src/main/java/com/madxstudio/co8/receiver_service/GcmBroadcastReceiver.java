package com.madxstudio.co8.receiver_service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.madxstudio.co8.App;

/**
 * GCM推送接收器
 * Created by wing on 15/4/22.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (App.getLoginedUser()!=null&& com.madxstudio.co8.util.LocationUtil.isGoogleAvailable()) {
            // Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),
                    com.madxstudio.co8.receiver_service.GcmIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }

}
