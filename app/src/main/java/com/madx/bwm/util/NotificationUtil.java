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
    public static void sendNotification(Context context, Bundle msg,boolean isGCM) throws JSONException {

        PendingIntent contentIntent = getFowwordIntent(context, msg,isGCM);

        String title = null;
        String message = null;
        if(isGCM){
            title = msg.getString("title");
            message = msg.getString("message");
        }else{
            title = msg.getString(JPushInterface.EXTRA_TITLE);
            message = msg.getString(JPushInterface.EXTRA_MESSAGE);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.tab_message)
                        .setContentTitle(title)
                        .setContentText(message);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        if(msgCount>1) {
            mBuilder.setNumber(msgCount);
        }
        mBuilder.setTicker(msg.getString(JPushInterface.EXTRA_TITLE));
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        if (contentIntent != null) {
         mBuilder.setContentIntent(contentIntent);
        }
        mBuilder.setAutoCancel(true);
        getNotivficationManager(context).notify(msgType.ordinal(), mBuilder.build());
    }


    static MessageType msgType ;
    static int msgCount;
    private static PendingIntent getFowwordIntent(Context context, Bundle msg,boolean isGCM) throws JSONException {
        if (msg == null) {
            return null;
        }
        JSONObject jsonObjectExtras = null;
        String msgString = null;

        if(isGCM){
//            JSONObject json = new JSONObject();
//            Set<String> keys = msg.keySet();
//            for (String key : keys) {
//                try {
//                    // json.put(key, bundle.get(key)); see edit below
//                    json.put(key, JSONObject.wrap(msg.get(key)));
//                } catch(JSONException e) {
//                    //Handle exception here
//                }
//            }
            msgString = msg.getString("extras");
        }else {
            msgString = msg.getString(JPushInterface.EXTRA_EXTRA);
        }
        if(msgString!=null) {
            jsonObjectExtras = new JSONObject(msgString);
            String type = jsonObjectExtras.getString("module");
            msgCount = jsonObjectExtras.getInt("count");
            msgType = MessageType.getMessageTypeByName(type);
        }
        if(msgType==null){
            return null;
        }
        PendingIntent contentIntent = null;
        Intent intent = null;
        switch (msgType){
            case BONDALERT_WALL:
                intent = new Intent(context, AlertWallActivity.class);
                break;
            case BONDALERT_EVENT:
                intent = new Intent(context, AlertEventActivity.class);
                break;
            case BONDALERT_MEMBER:
                intent = new Intent(context, MemberActivity.class);
                break;
            case BONDALERT_MESSAGE:
                intent = new Intent(context, MessageChatActivity.class);
                intent.putExtra("type", jsonObjectExtras.getString("group_type"));
                intent.putExtra("groupId", jsonObjectExtras.getString("group_id"));
                intent.putExtra("titleName", jsonObjectExtras.getString("group_name"));
                break;
            case BONDALERT_MISS:
                intent = new Intent(context, MissListActivity.class);
                break;
            case BONDALERT_BIGDAY:
                intent = new Intent(context, BigDayActivity.class);
                break;
            case BONDALERT_NEWS:
                intent = new Intent(context, NewsActivity.class);
                break;
            case BONDALERT_RECOMMENDED:
                intent = new Intent(context, RecommendActivity.class);
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
//           jsonObjecttent.putExtra("type", jsonObjectExtras.getString("group_type"));
//            injsonObjectt.putExtra("groupId", jsonObjectExtras.getString("group_id"));
//            intejsonObjectputExtra("titleName", jsonObjectExtras.getString("group_name"));
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
        if(intent!=null) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }

    public static void clearNotification(Context context){
        getNotivficationManager(context).cancelAll();
        JPushInterface.clearAllNotifications(context);
    }
}
