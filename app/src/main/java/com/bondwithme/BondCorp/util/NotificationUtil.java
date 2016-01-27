package com.bondwithme.BondCorp.util;

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
import com.bondwithme.BondCorp.App;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.ui.AlertEventActivity;
import com.bondwithme.BondCorp.ui.AlertGroupActivity;
import com.bondwithme.BondCorp.ui.AlertWallActivity;
import com.bondwithme.BondCorp.ui.BaseActivity;
import com.bondwithme.BondCorp.ui.EventDetailActivity;
import com.bondwithme.BondCorp.ui.FamilyViewProfileActivity;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.ui.MemberActivity;
import com.bondwithme.BondCorp.ui.MessageChatActivity;
import com.bondwithme.BondCorp.ui.MissListActivity;
import com.bondwithme.BondCorp.ui.NewsActivity;
import com.bondwithme.BondCorp.ui.more.BondAlert.BigDayActivity;
import com.bondwithme.BondCorp.ui.more.GroupPrivacyActivity;
import com.bondwithme.BondCorp.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondCorp.ui.wall.DiaryInformationActivity;
import com.bondwithme.BondCorp.ui.wall.NewDiaryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wing on 15/5/8.
 */
public class NotificationUtil {

    private static final String TAG = "NotificationUtil";
    public static final String MSG_TYPE = "msg_type";

    /**
     * 通知类型
     */
    public enum MessageType {
        BONDALERT_WALL("wall"), BONDALERT_EVENT("event"), BONDALERT_BIGDAY("bigday"), BONDALERT_MISS("miss"), BONDALERT_NEWS("news"),
        BONDALERT_MEMBER("member"), BONDALERT_MESSAGE("message"), BONDALERT_GROUP("group"), BONDALERT_INACTIVE("inactive"),
        LOCAL_PRIVACY_SETTINGS("privacy_settings"), LOCAL_NEW_DIARY("new_diary"), LOCAL_STICKIES_STORE("stickies_store"),
        LOCAL_FAMILY_PAGE("family_page");
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
    public static void sendLocalNotification(Context context, MessageType messageType) throws JSONException {
        Notification notification = getLocalNotification(context, messageType);
        if (notification != null) {
            getNotivficationManager(context).notify(messageType.ordinal(), notification);
        }
    }

    /**
     * @param msg
     */
    public static void sendNotification(Context context, Bundle msg, boolean isGCM) throws JSONException {

//        if (App.getLoginedUser() == null) {
//            return;
//        }

        Notification notification = getNotification(context, isGCM, msg);
        if (notification != null) {

            getNotivficationManager(context).notify(msgType.ordinal(), notification);
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


    static String newMsg = null;

    private static Intent getFowwordIntent(Context context, Bundle bundle, boolean isGCM) throws JSONException {

        List<String> msgs = null;
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
            msgString = bundle.getString("extras");
        } else {
            msgString = bundle.getString(JPushInterface.EXTRA_EXTRA);
        }

        String item_id = null;
        String sub_item_id = null;
        String action_owner_id = null;
        String action = null;
        String action_owner = null;
        String item_owner_name = null;
        String item_name = null;
        String module = null;
        String item_type = null;
        String snippet = null;

        if (msgString != null) {
            jsonObjectExtras = new JSONObject(msgString);
            module = jsonObjectExtras.getString("module");
            item_id = jsonObjectExtras.getString("item_id");
            sub_item_id = jsonObjectExtras.getString("sub_item_id");
            action_owner_id = jsonObjectExtras.getString("action_owner_id");
            action = jsonObjectExtras.getString("action");
            action_owner = jsonObjectExtras.getString("action_owner");
            item_owner_name = jsonObjectExtras.getString("item_owner_name");
            item_name = jsonObjectExtras.getString("item_name");
            item_type = jsonObjectExtras.getString("item_type");
            snippet = jsonObjectExtras.getString("snippet");

            msgType = MessageType.getMessageTypeByName(module);
        }
        if (msgType == null) {
            return null;
        }

        switch (msgType) {
            case BONDALERT_WALL:
                smallIcon = R.drawable.bondalert_wall_icon;
                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_WALL);
                if (msgs.size() == 0) {
//                    intent = new Intent(context, AlertWallActivity.class);
                    intent = goWallDetailIntent(context, sub_item_id, item_id);
                } else {
                    intent = new Intent(context, AlertWallActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_WALL);
                doNotificationHandle(MainActivity.TabEnum.wall);
                newMsg = NotificationMessageGenerateUtil.getWallMessage(context, action, action_owner, item_owner_name);
                break;
            case BONDALERT_EVENT:
                smallIcon = R.drawable.bondalert_event_icon;
                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_EVENT);
                if (msgs.size() == 0) {
                    intent = goEventDetailIntent(context, item_id);
//                    intent = new Intent(context, AlertEventActivity.class);
                } else {
                    intent = new Intent(context, AlertEventActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_EVENT);
                doNotificationHandle(MainActivity.TabEnum.event);
                newMsg = NotificationMessageGenerateUtil.getEventMessage(context, action, action_owner, item_name, item_owner_name);
                break;
            case BONDALERT_MEMBER:
                smallIcon = R.drawable.bondalert_member_icon;
                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_MEMBER);
                if (msgs.size() == 0) {
                    intent = new Intent(context, MemberActivity.class);
                } else {
                    //TODO
                    intent = new Intent(context, MemberActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_MEMBER);
                newMsg = NotificationMessageGenerateUtil.getMemberMessage(context, action, action_owner, item_name);
                doNotificationHandle(MainActivity.TabEnum.more);
                break;
            case BONDALERT_MESSAGE:
                smallIcon = R.drawable.bondalert_message_icon;

                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_MESSAGE);
                if (msgs.size() == 0) {
                    intent = new Intent(context, MessageChatActivity.class);
                    int type = 0;
                    try {
                        type = Integer.valueOf(jsonObjectExtras.getString("item_type"));
                    } catch (Exception e) {

                    }
                    intent.putExtra("type", type);
                    intent.putExtra("groupId", jsonObjectExtras.getString("item_id"));
                    intent.putExtra("titleName", jsonObjectExtras.getString("item_name"));
                } else {
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra("jumpIndex", 1);//到信息列表
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_MESSAGE);
                newMsg = NotificationMessageGenerateUtil.getChatMessage(context, action, item_type, action_owner, item_name, snippet);
                doNotificationHandle(MainActivity.TabEnum.chat);
                break;
            case BONDALERT_MISS:
                smallIcon = R.drawable.bondalert_miss_icon;

                HashMap<String, String> missInfos = App.getContextInstance().getMissNotificationInfos();
                if (missInfos.size() == 0) {
                    intent = new Intent(context, MissListActivity.class);
                } else {
                    //TODO
                    intent = new Intent(context, MissListActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_MISS);
                String memberMissCount = missInfos.get(action_owner);
                int msgCount = 0;
                int memberCount;
                if (memberMissCount != null) {
                    //已经有该用户的通知+1
                    missInfos.put(action_owner, String.valueOf(Integer.valueOf(memberMissCount) + 1));
                } else {
                    missInfos.put(action_owner, "1");
                }
                memberCount = missInfos.size();
                Iterator<String> members = missInfos.keySet().iterator();
                while (members.hasNext()) {
                    msgCount += Integer.valueOf(missInfos.get(members.next()));
                }

                newMsg = NotificationMessageGenerateUtil.getMissMessage(context, action, action_owner, memberCount, msgCount);
                doNotificationHandle(MainActivity.TabEnum.more);
                doNotificationHandle(MainActivity.TabEnum.family);
                break;
            case BONDALERT_BIGDAY:
                smallIcon = R.drawable.bondalert_bigday_icon;

                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_BIGDAY);
                if (msgs.size() == 0) {
                    intent = new Intent(context, BigDayActivity.class);
                } else {
                    //TODO
                    intent = new Intent(context, BigDayActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_BIGDAY);
                newMsg = NotificationMessageGenerateUtil.getBirthdayMessage(context, action, item_type, action_owner);
                doNotificationHandle(MainActivity.TabEnum.more);
                break;
            case BONDALERT_NEWS:
                smallIcon = R.drawable.bondalert_news_icon;
                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_NEWS);
                if (msgs.size() == 0) {
                    intent = new Intent(context, NewsActivity.class);
                } else {
                    //TODO
                    intent = new Intent(context, NewsActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_NEWS);
                newMsg = NotificationMessageGenerateUtil.getOtherMessage(context, action, jsonObjectExtras);
                doNotificationHandle(MainActivity.TabEnum.more);
                break;
            case BONDALERT_GROUP:
                smallIcon = R.drawable.bondalert_group_icon;

                msgs = App.getContextInstance().getNotificationMsgsByType(MessageType.BONDALERT_GROUP);
                if (msgs.size() == 0) {
                    intent = new Intent(context, AlertGroupActivity.class);
                } else {
                    //TODO
                    intent = new Intent(context, AlertGroupActivity.class);
                }
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_GROUP);
                newMsg = NotificationMessageGenerateUtil.getGroupAlertMessage(context, action, action_owner, item_name);
                doNotificationHandle(MainActivity.TabEnum.more);
                break;
            case BONDALERT_INACTIVE:
                smallIcon = R.drawable.bondalert_recommended_icon;

                intent = new Intent(context, FamilyViewProfileActivity.class);
                intent.putExtra(MSG_TYPE, MessageType.BONDALERT_INACTIVE);
                intent.putExtra("member_id", action_owner_id);
                newMsg = NotificationMessageGenerateUtil.getInactiveMessage(context, action, action_owner);
                doNotificationHandle(MainActivity.TabEnum.more);
                break;

        }

        if (msgs != null) {
            msgs.add(newMsg);
            isOnlyOneModel = false;
        } else {
            isOnlyOneModel = true;
        }

        return intent;
    }

    /**
     * 是否为单模式(只有一条)，双模式为(可显示为一条和多条list)
     */
    private static boolean isOnlyOneModel = true;

    private static Intent goWallDetailIntent(Context mContext, String content_group_id, String group_id) {
        Intent intent = new Intent(mContext, DiaryInformationActivity.class);
        intent.putExtra("content_group_id", content_group_id);
        intent.putExtra("group_id", group_id);
        return intent;
    }

    private static Intent goEventDetailIntent(Context mContext, String group_id) {
        Intent intent = new Intent(mContext, EventDetailActivity.class);
        intent.putExtra("group_id", group_id);
        return intent;
    }

    private static Notification getLocalNotification(Context context, MessageType messageType) throws JSONException {
        Notification notification = null;
        notification = getSingleNotification(context, getLocalFowwordIntent(context,messageType), context.getString(R.string.app_name), newMsg, 1);
        return notification;
    }

    private static PendingIntent getLocalFowwordIntent(Context context, MessageType messageType) {
        PendingIntent contentIntent = null;
        Intent intent = null;
        switch (messageType) {
            case LOCAL_PRIVACY_SETTINGS:
                smallIcon = R.drawable.ic_launcher;
                intent = new Intent(context, GroupPrivacyActivity.class);
                newMsg = context.getString(R.string.local_notification_text_for_5_minute);
                break;
            case LOCAL_NEW_DIARY:
                smallIcon = R.drawable.bondalert_wall_icon;
                intent = new Intent(context, NewDiaryActivity.class);
                newMsg = context.getString(R.string.local_notification_text_for_2_day);
                break;
            case LOCAL_STICKIES_STORE:
                smallIcon = R.drawable.more_sticket_store;
                intent = new Intent(context, StickerStoreActivity.class);
                newMsg = context.getString(R.string.local_notification_text_for_3_day);

                break;
            case LOCAL_FAMILY_PAGE:
                smallIcon = R.drawable.bondalert_member_icon;
                intent = new Intent(context, MainActivity.class);
                intent.putExtra(MainActivity.JUMP_INDEX,0);
                newMsg = context.getString(R.string.local_notification_text_for_5_day);
                break;
        }

        //应用在前台不发通知,不写在最前面是因为需要刷新红点(getFowwordIntent里)
        if (App.isForeground()) {
            return null;
        }

        if (intent != null) {
            intent.putExtra(BaseActivity.IS_OUTSIDE_INTENT, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            contentIntent = PendingIntent.getActivity(context, messageType.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }

    private static Notification getNotification(Context context, boolean isGCM, Bundle msg) throws JSONException {

        PendingIntent contentIntent = null;
        Intent intent = getFowwordIntent(context, msg, isGCM);
        //应用在前台不发通知,不写在最前面是因为需要刷新红点(getFowwordIntent里)
        if (App.isForeground()) {
            return null;
        }

        if (intent != null) {
            intent.putExtra(BaseActivity.IS_OUTSIDE_INTENT, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            contentIntent = PendingIntent.getActivity(context, msgType.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return null;
        }

        Notification notification = null;
        String title = null;
        if (isGCM) {
            title = msg.getString("title");
        } else {
            title = msg.getString(JPushInterface.EXTRA_TITLE);
        }
        if (isOnlyOneModel) {
            notification = getSingleNotification(context, contentIntent, title, newMsg, 1);
        } else {
            //        if (msgs.size() == 1) {
            notification = getSingleNotification(context, contentIntent, title);
            //        } else {
            //        notification = getInboxStyleNotification(context, contentIntent, title);
            //        }
        }
        if (notification != null) {
            notification.priority = Notification.PRIORITY_HIGH;
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
        }
        return notification;
    }

    /**
     * 清除手机上的通知
     *
     * @param context
     */
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

    private static Notification getSingleNotification(Context context, PendingIntent contentIntent, String title, String msg, int msgCount) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setContentText(msg);
        if (smallIcon != -1) {
            mBuilder.setSmallIcon(smallIcon);
        }
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        //暂时不启用
        if (msgCount > 1) {
            mBuilder.setNumber(msgCount);
        }
        mBuilder.setTicker(title);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        if (contentIntent != null) {
            mBuilder.setContentIntent(contentIntent);
        }
        mBuilder.setAutoCancel(true);

        return mBuilder.build();
    }

    private static Notification getSingleNotification(Context context, PendingIntent contentIntent, String title) {

        List<String> currentMsgs = App.getContextInstance().getNotificationMsgsByType(msgType);
        if (currentMsgs == null || currentMsgs.size() == 0) {
            return null;
        }

        String msg = currentMsgs.get(currentMsgs.size() - 1);

        return getSingleNotification(context, contentIntent, title, msg, currentMsgs.size());
    }

    private static Notification getInboxStyleNotification(Context context, PendingIntent resultPendingIntent, String title) {
        List<String> currentMsgs = App.getContextInstance().getNotificationMsgsByType(msgType);
        if (currentMsgs == null || currentMsgs.size() == 0) {
            return null;
        }

        // Create the style object with InboxStyle subclass.
        NotificationCompat.InboxStyle notiStyle = new NotificationCompat.InboxStyle();
        notiStyle.setBigContentTitle(context.getString(R.string.notification_title_expand));


        // Add the multiple lines to the style.
        // This is strictly for providing an example of multiple lines.
        int msgCount = currentMsgs.size();
        for (int i = 0; i < msgCount; i++) {
            if (i == MAX_SHOW) {
                break;
            }
            notiStyle.addLine(currentMsgs.get(i));
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        if (smallIcon != -1) {
            mBuilder.setSmallIcon(smallIcon);
        }
//        mBuilder.setDeleteIntent(PendingIntent.getBroadcast(context, 0, new Intent(NOTIFICATION_DELETED_ACTION), 0));

        return mBuilder.setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setGroupSummary(true)
                .setContentText(currentMsgs.get(currentMsgs.size() - 1))
                .setStyle(notiStyle).build();

//        return new NotificationCompat.Builder(context)
//                .setAutoCancel(true)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
//                .setContentIntent(resultPendingIntent)
//                .setContentTitle(title)
//                .setContentText(currentMsgs.get(currentMsgs.size() - 1))
//                .setGroupSummary(true)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setStyle(notiStyle).build();
//                .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
//                .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
//                .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
    }

    public static final String NOTIFICATION_DELETED_ACTION = "notification_deleted_action";
}
