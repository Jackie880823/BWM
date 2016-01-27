package com.bondwithme.BondCorp.receiver_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bondwithme.BondCorp.util.NotificationUtil;
import com.bondwithme.BondCorp.util.NotificationUtil.MessageType;

import org.json.JSONException;

/**
 * Created by wing on 16/1/6.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        MessageType msgTytpe = null;
        switch (action) {
            case AlarmControler.FIVE_MINUTE_ACTION:
                msgTytpe = MessageType.LOCAL_PRIVACY_SETTINGS;
                break;
            case AlarmControler.TWO_DAY_ACTION:
                msgTytpe = MessageType.LOCAL_NEW_DIARY;
                break;
            case AlarmControler.THREE_DAY_ACTION:
                msgTytpe = MessageType.LOCAL_STICKIES_STORE;
                break;
            case AlarmControler.FIVE_DAY_ACTION:
                msgTytpe = MessageType.LOCAL_FAMILY_PAGE;
                break;
            default:
                break;

        }
        if (msgTytpe != null) {
            try {
                NotificationUtil.sendLocalNotification(context, msgTytpe);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
