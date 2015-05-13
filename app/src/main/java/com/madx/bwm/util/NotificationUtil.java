package com.madx.bwm.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.madx.bwm.R;
import com.madx.bwm.ui.AlertEventActivity;
import com.madx.bwm.ui.AlertWallActivity;
import com.madx.bwm.ui.BigDayActivity;
import com.madx.bwm.ui.MemberActivity;
import com.madx.bwm.ui.MessageChatActivity;
import com.madx.bwm.ui.MissListActivity;
import com.madx.bwm.ui.NewsActivity;
import com.madx.bwm.ui.RecommendActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wing on 15/5/8.
 */
public class NotificationUtil {

    /**
     * 通知类型
     */
    public static final String BONDALERT_WALL = "wall";
    public static final String BONDALERT_EVENT = "event";
    public static final String BONDALERT_BIGDAY = "bigday";
    public static final String BONDALERT_MISS = "miss";
    public static final String BONDALERT_NEWS = "news";
    public static final String BONDALERT_MEMBER = "member";
    public static final String BONDALERT_RECOMMENDED = "recommended";//TODO
    public static final String BONDALERT_MESSAGE = "message";

    private enum MessageType {
        BONDALERT_WALL("wall"), BONDALERT_EVENT("event"), BONDALERT_BIGDAY("bigday"), BONDALERT_MISS("miss"), BONDALERT_NEWS("news"), BONDALERT_MEMBER("member"), BONDALERT_RECOMMENDED("recommended"), BONDALERT_MESSAGE("message");
        private String typeName;

        MessageType(String typeName) {
            this.typeName = typeName;
        }

        String getTypeName() {
            return typeName;
        }

        static MessageType getMessageTypeByName(String name){
            for (MessageType type:MessageType.values()){
                if(type.getTypeName().equals(name)){
                    return type;
                }
            }
            return null;
        }

    }


    /**
     * GCM异常信息
     */
    public static final int GCM_MESSAGE = 2;
    private static NotificationManager mNotificationManager;

    private static NotificationManager getNotivficationManager(Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    public static void sendNotification(Context context, String msg) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setContentText(msg);

//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        mBuilder.setTicker("测试通知来啦");
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        getNotivficationManager(context).notify(GCM_MESSAGE, mBuilder.build());
    }

    /**
     * @param msg
     */
    public static void sendNotification(Context context, Bundle msg) throws JSONException {

        PendingIntent contentIntent = getFowwordIntent(context, msg);
        if (contentIntent == null) {
            return;
        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.tab_message)
                        .setContentTitle(msg.getString(JPushInterface.EXTRA_TITLE))
                        .setContentText(msg.getString(JPushInterface.EXTRA_MESSAGE));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
//        mBuilder.setNumber(12);
        mBuilder.setTicker(msg.getString(JPushInterface.EXTRA_TITLE));
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        getNotivficationManager(context).notify(msgType.ordinal(), mBuilder.build());
    }


    static MessageType msgType ;
    private static PendingIntent getFowwordIntent(Context context, Bundle msg) throws JSONException {
        if (msg == null) {
            return null;
        }
//        String msgString = msg.getString(JPushInterface.EXTRA_MESSAGE);
        String msgString = msg.getString(JPushInterface.EXTRA_EXTRA);
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(msgString);
        String type = jsonObject.getString("module");
        PendingIntent contentIntent = null;
        msgType = MessageType.getMessageTypeByName(type);

        switch (msgType){
            case BONDALERT_WALL:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, AlertWallActivity.class), 0);
                break;
            case BONDALERT_EVENT:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, AlertEventActivity.class), 0);
                break;
            case BONDALERT_MEMBER:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, MemberActivity.class), 0);
                break;
            case BONDALERT_MESSAGE:
                Intent intent = new Intent(context, MessageChatActivity.class);
                intent.putExtra("type", msg.getString("group_type"));
                intent.putExtra("groupId", msg.getString("group_id"));
                intent.putExtra("titleName", msg.getString("group_name"));
                contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
                break;
            case BONDALERT_MISS:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, MissListActivity.class), 0);
                break;
            case BONDALERT_BIGDAY:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, BigDayActivity.class), 0);
                break;
            case BONDALERT_NEWS:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, NewsActivity.class), 0);
                break;
            case BONDALERT_RECOMMENDED:
                contentIntent = PendingIntent.getActivity(context, 0,
                        new Intent(context, RecommendActivity.class), 0);
                break;

        }

//        if (MessageType.BONDALERT_WALL.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, AlertWallActivity.class), 0);
//        } else if (MessageType.BONDALERT_EVENT.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, AlertEventActivity.class), 0);
//        } else if (MessageType.BONDALERT_MEMBER.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, MemberActivity.class), 0);
//        } else if (MessageType.BONDALERT_MESSAGE.getTypeName().equals(type)) {
//            Intent intent = new Intent(context, MessageChatActivity.class);
//           jsonObjecttent.putExtra("type", msg.getString("group_type"));
//            injsonObjectt.putExtra("groupId", msg.getString("group_id"));
//            intejsonObjectputExtra("titleName", msg.getString("group_name"));
//            contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        } else if (MessageType.BONDALERT_MISS.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, MissListActivity.class), 0);
//        } else if (MessageType.BONDALERT_BIGDAY.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, BigDayActivity.class), 0);
//        } else if (MessageType.BONDALERT_NEWS.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, NewsActivity.class), 0);
//        } else if (MessageType.BONDALERT_RECOMMENDED.getTypeName().equals(type)) {
//            contentIntent = PendingIntent.getActivity(context, 0,
//                    new Intent(context, RecommendActivity.class), 0);
//        }
        return contentIntent;
    }
}
