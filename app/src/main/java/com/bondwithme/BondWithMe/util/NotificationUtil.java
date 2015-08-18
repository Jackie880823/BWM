package com.bondwithme.BondWithMe.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.AlertEventActivity;
import com.bondwithme.BondWithMe.ui.AlertGroupActivity;
import com.bondwithme.BondWithMe.ui.AlertWallActivity;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.BondAlertActivity;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.MemberActivity;
import com.bondwithme.BondWithMe.ui.MessageChatActivity;
import com.bondwithme.BondWithMe.ui.MissListActivity;
import com.bondwithme.BondWithMe.ui.NewsActivity;
import com.bondwithme.BondWithMe.ui.RecommendActivity;
import com.bondwithme.BondWithMe.ui.more.BondAlert.BigDayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wing on 15/5/8.
 */
public class NotificationUtil {

    private static final String TAG = "NotificationUtil";

    /**
     * 通知类型
     */
    private enum MessageType {
        BONDALERT_WALL("wall"), BONDALERT_EVENT("event"), BONDALERT_BIGDAY("bigday"), BONDALERT_MISS("miss"), BONDALERT_NEWS("news"), BONDALERT_MEMBER("member"), BONDALERT_RECOMMENDED("recommended"), BONDALERT_MESSAGE("message"), BONDALERT_GROUP("group");
        private String typeName;

        MessageType(String typeName) {
            this.typeName = typeName;
        }

        String getTypeName() {
            return typeName;
        }

        static MessageType getMessageTypeByName(String name) {
            for (MessageType type : MessageType.values()) {
                if (type.getTypeName().equals(name)) {
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
    public static void sendNotification(Context context, Bundle msg, boolean isGCM) throws JSONException {

//        if (App.getLoginedUser() == null) {
//            return;
//        }

//        PendingIntent contentIntent = getFowwordIntent(context, msg, isGCM);

//        String title = null;
//        String message = null;
//        if (isGCM) {
//            title = msg.getString("title");
//            message = msg.getString("message");
//        } else {
//            title = msg.getString(JPushInterface.EXTRA_TITLE);
//            message = msg.getString(JPushInterface.EXTRA_MESSAGE);
//        }
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context)
//                        .setContentTitle(title)
//                        .setContentText(message);
//        if (smallIcon != -1) {
//            mBuilder.setSmallIcon(smallIcon);
//        }
//        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
//        //暂时不启用
////        if(msgCount>1) {
////            mBuilder.setNumber(msgCount);
////        }
//        mBuilder.setTicker(msg.getString(JPushInterface.EXTRA_TITLE));
//        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
//
//        if (contentIntent != null) {
//            mBuilder.setContentIntent(contentIntent);
//        }
//        mBuilder.setAutoCancel(true);
//        Notification notification = mBuilder.build();
//        notification.priority = Notification.PRIORITY_HIGH;
//        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
////        notification.defaults=Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
//
////        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
////        String[] events = new String[5];
////        // Sets a title for the Inbox style big view
////        inboxStyle.setBigContentTitle("大视图内容:");
////        // Moves events into the big view
////        for (int i=0; i < events.length; i++) {
////            inboxStyle.addLine(events[i]);
////        }
////        mBuilder.setContentTitle("测试标题")
////                .setContentText("测试内容")
//////				.setNumber(number)//显示数量
////                .setStyle(inboxStyle)//设置风格
////                .setTicker("测试通知来啦");
////
////        //自定义RemoteViews
////        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
////        notification.contentView = contentView;
//        getNotivficationManager(context).notify(msgType.ordinal(), notification);

        Notification notification = getNotification(context, isGCM, msg);
        if (notification != null) {
//            if(msgs.size()==1) {
//                getNotivficationManager(context).notify(0, notification);
//            }else{
//                getNotivficationManager(context).cancel(0);
            getNotivficationManager(context).notify(1, notification);
//            }
//            getNotivficationManager(context).notify(msgType.ordinal(), notification);
        }

    }


    static MessageType msgType;
    //    static int msgCount;
    static int smallIcon = -1;
    private static Intent intentWall;
    private static Intent intentEvent;
    private static Intent intentMember;
    private static Intent intentMessage;
    private static Intent intentMiss;
    private static Intent intentBigday;
    private static Intent intentNews;
    private static Intent intentRecommend;
    private static Intent intentGroup;


    private static Intent getFowwordIntent(Context context, Bundle msg, boolean isGCM,List msgs) throws JSONException {
        if (msg == null) {
            return null;
        }
        Intent intent = null;
        JSONObject jsonObjectExtras = null;
        String msgString = null;

        if (isGCM) {
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
        } else {
            msgString = msg.getString(JPushInterface.EXTRA_EXTRA);
        }
        if (msgString != null) {
            jsonObjectExtras = new JSONObject(msgString);
            String type = jsonObjectExtras.getString("module");
//            msgCount = jsonObjectExtras.getInt("count");
            msgType = MessageType.getMessageTypeByName(type);
        }
        if (msgType == null) {
            return null;
        }

        if (msgs.size() == 0) {
//        Intent intent = null;
            switch (msgType) {
                case BONDALERT_WALL:
                    smallIcon = R.drawable.bondalert_wall_icon;
//                    if (intentWall == null) {
                    intent = new Intent(context, AlertWallActivity.class);
//                        intent.putExtra(FLAG_SINGLE_MSG, true);
//                    } else {
//                        //跳转到详情
//                        intent = new Intent(context, AlertWallActivity.class);
//                        intent.putExtra(FLAG_SINGLE_MSG, false);
//                    }
                    doNotificationHandle(MainActivity.TabEnum.wall);
                    break;
                case BONDALERT_EVENT:
                    smallIcon = R.drawable.bondalert_event_icon;
                    intent = new Intent(context, AlertEventActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.event);
                    break;
                case BONDALERT_MEMBER:
                    smallIcon = R.drawable.bondalert_member_icon;
                    intent = new Intent(context, MemberActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.more);
                    break;
                case BONDALERT_MESSAGE:
                    smallIcon = R.drawable.bondalert_message_icon;
                    intent = new Intent(context, MessageChatActivity.class);
                    int type = 0;
                    try {
                        type = Integer.valueOf(jsonObjectExtras.getString("group_type"));
                    } catch (Exception e) {

                    }
                    intent.putExtra("type", type);
                    intent.putExtra("groupId", jsonObjectExtras.getString("group_id"));
                    intent.putExtra("titleName", jsonObjectExtras.getString("group_name"));
                    doNotificationHandle(MainActivity.TabEnum.chat);
                    break;
                case BONDALERT_MISS:
                    smallIcon = R.drawable.bondalert_miss_icon;
                    intent = new Intent(context, MissListActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.more);
                    break;
                case BONDALERT_BIGDAY:
                    smallIcon = R.drawable.bondalert_bigday_icon;
                    intent = new Intent(context, BigDayActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.more);
                    break;
                case BONDALERT_NEWS:
                    smallIcon = R.drawable.bondalert_news_icon;
                    intent = new Intent(context, NewsActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.more);
                    break;
                case BONDALERT_RECOMMENDED:
                    smallIcon = R.drawable.bondalert_recommended_icon;
                    intent = new Intent(context, RecommendActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.more);
                    break;
                case BONDALERT_GROUP:
                    smallIcon = R.drawable.bondalert_group_icon;
                    intent = new Intent(context, AlertGroupActivity.class);
                    doNotificationHandle(MainActivity.TabEnum.more);
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
        } else {
            intent = new Intent(context, BondAlertActivity.class);
        }


        return intent;
    }

    private static Notification getNotification(Context context, boolean isGCM, Bundle msg) throws JSONException {
        List<String> msgs = App.getContextInstance().getNotificaationList();
        PendingIntent contentIntent = null;
        Intent intent = getFowwordIntent(context, msg, isGCM, msgs);
        if (intent != null) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(BaseActivity.IS_OUTSIDE_INTENT, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return null;
        }

        Notification notification;
        String title = null;
        String message = null;
        if (isGCM) {
            title = msg.getString("title");
            message = msg.getString("message");
        } else {
            title = msg.getString(JPushInterface.EXTRA_TITLE);
            message = msg.getString(JPushInterface.EXTRA_MESSAGE);
        }

        msgs.add(message);
//        if (msgs.size() == 1) {
//            notification = getSingleNotification(context, contentIntent, title);
//        } else {
        notification = getInboxStyleNotification(context, contentIntent, title,msgs);
//        }
        if (notification != null) {
            notification.priority = Notification.PRIORITY_HIGH;
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
        }
        return notification;
    }

    public static void clearNotification(Context context) {
        getNotivficationManager(context).cancelAll();
        JPushInterface.clearAllNotifications(context);
    }

    private static void doNotificationHandle(MainActivity.TabEnum tab) {
        if (mNotificationOtherHandle != null) {
            mNotificationOtherHandle.doSomething(tab);
        }
    }

    private static NotificationOtherHandle mNotificationOtherHandle;

    public static void setNotificationOtherHandle(NotificationOtherHandle notificationOtherHandle) {
        mNotificationOtherHandle = notificationOtherHandle;
    }

    public interface NotificationOtherHandle {
        public void doSomething(MainActivity.TabEnum tab);
    }

    public static void unRegisterPush(Context context, final String userId) {
        if (App.getLoginedUser() != null) {
            RequestInfo requestInfo = new RequestInfo(String.format(Constant.UN_REGISTER_URL, userId), null);
            new HttpTools(context).put(requestInfo, TAG, new HttpCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }

                @Override
                public void onResult(String response) {
                }

                @Override
                public void onError(Exception e) {
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
    }

    /**
     * 最大显示消息数
     */
    private static final int MAX_SHOW = 3;
    private static final String FLAG_SINGLE_MSG = "single_msg";

    private static Notification getSingleNotification(Context context, PendingIntent contentIntent, String title,List<String> msgs) {
        if (msgs.size() < 1) {
            return null;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setContentText(msgs.get(0));
        if (smallIcon != -1) {
            mBuilder.setSmallIcon(smallIcon);
        }
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        //暂时不启用
//        if(msgCount>1) {
//            mBuilder.setNumber(msgCount);
//        }
        mBuilder.setTicker(title);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        if (contentIntent != null) {
            mBuilder.setContentIntent(contentIntent);
        }
        mBuilder.setAutoCancel(true);

        return mBuilder.build();
    }

    private static Notification getInboxStyleNotification(Context context, PendingIntent resultPendingIntent, String title,List<String> msgs) {

        // Create the style object with InboxStyle subclass.
        NotificationCompat.InboxStyle notiStyle = new NotificationCompat.InboxStyle();
        notiStyle.setBigContentTitle(context.getString(R.string.notification_title_expand));


        // Add the multiple lines to the style.
        // This is strictly for providing an example of multiple lines.
        int msgCount = msgs.size();
        for (int i = 0; i < msgCount; i++) {
            if (i == MAX_SHOW) {
                break;
            }
            notiStyle.addLine(msgs.get(i));
        }
        if (msgCount > MAX_SHOW) {
            notiStyle.setSummaryText("+" + (msgCount - MAX_SHOW) + " more line Messages");
        }


//        // Creates an explicit intent for an ResultActivity to receive.
//
//        // This ensures that the back button follows the recommended convention for the back key.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//
//        // Adds the back stack for the Intent (but not the Intent itself).
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack.
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        if (Build.VERSION.SDK_INT >= 16) {
//            // Inflate and set the layout for the expanded notification view
//            RemoteViews expandedView =
//                    new RemoteViews(getPackageName(), R.layout.notification_expanded);
//            notification.bigContentView = expandedView;
//        }

        return new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setContentText(msgs.get(msgs.size() - 1))
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setStyle(notiStyle).build();
//                .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
//                .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
//                .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
    }
}
