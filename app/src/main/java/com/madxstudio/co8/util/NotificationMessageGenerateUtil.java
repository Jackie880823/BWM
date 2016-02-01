package com.madxstudio.co8.util;

import android.content.Context;
import android.text.TextUtils;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.MessageChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wing on 15/8/20.
 */
public class NotificationMessageGenerateUtil {

    /**
     * 1) module - 消息模块
     * 2) action - 消息类型
     * 3) action_owner – 推送发表成员的名字
     * 4) action_owner_id –推送发表成员的user_id
     * 5) item_id– 依据不同模块的id
     * 6) item_type – item_id 的类型 -关系Id, 群组Id, 事件 Id, BondAlert (news)消息Id
     * 7) item_name –依据不同模块的名字 － 关系／群组名字／事件名称／BondAlert (news) 消息标题
     * 8) item_owner_name – item 的原文发表者
     * 9) sub_item_id – 日记的content_group_id
     * 10) snippet – 聊天的内容， 给module=message 和 action=postText 用的数据
     */


    public static String getWallMessage(Context context, String action, String action_owner, String item_owner_name) {

        boolean isOwner = TextUtils.isEmpty(item_owner_name);
        String msg = null;
        if ("comment".equals(action)) {
            if (isOwner) {
                msg = context.getString(R.string.notification_action_diary_comment_owner, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_diary_comment_not_owner, action_owner, item_owner_name);
            }
        } else if ("love".equals(action)) {
            if (isOwner) {
                msg = context.getString(R.string.notification_action_diary_love_owner, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_diary_love_not_owner, action_owner, item_owner_name);
            }
        } else if ("loveComment".equals(action)) {
            msg = context.getString(R.string.notification_action_diary_loveComment, action_owner);
        } else if ("tag".equals(action)) {
            msg = context.getString(R.string.notification_action_diary_tag, action_owner);
        } else if ("postPhoto".equals(action)) {
            msg = context.getString(R.string.notification_action_diary_add_photo, action_owner);
        } else if ("postVideo".equals(action)) {
            msg = context.getString(R.string.notification_action_diary_add_video, action_owner);
        }

        return msg;
    }

    public static String getEventMessage(Context context, String action, String action_owner, String item_name, String item_owner_name) {

        boolean isOwner = TextUtils.isEmpty(item_owner_name);
        String msg = null;
        if ("invite".equals(action)) {
            msg = context.getString(R.string.notification_action_event_invited, action_owner, item_name);
        } else if ("update".equals(action)) {
            msg = context.getString(R.string.notification_action_event_update, action_owner, item_name);
        } else if ("text_dialog_cancel".equals(action)) {
            msg = context.getString(R.string.notification_action_event_cancel, action_owner, item_name);
        } else if ("resp".equals(action)) {
            msg = context.getString(R.string.notification_action_event_resp, action_owner, item_name);
        } else if ("comment".equals(action)) {
            if (isOwner) {
                if (action_owner != null && action_owner.equals(item_owner_name)) {
                    msg = context.getString(R.string.notification_action_event_comment_owner_self, action_owner, item_name);
                } else {
                    msg = context.getString(R.string.notification_action_event_comment_owner, action_owner, item_name);
                }
            } else {
                msg = context.getString(R.string.notification_action_event_comment_not_owner, action_owner, item_owner_name, item_name);
            }
        } else if ("loveComment".equals(action)) {
            msg = context.getString(R.string.notification_action_event_loveComment, action_owner);
        } else if ("reminder".equals(action)) {
            msg = context.getString(R.string.notification_action_event_reminder, item_name);
        }
        return msg;
    }

    public static String getMemberMessage(Context context, String action, String action_owner, String item_name) {

        String msg = null;
        if ("add".equals(action)) {
            msg = context.getString(R.string.notification_action_member_add, action_owner, RelationshipUtil.getRelationshipName(context, item_name));
        } else if ("autoAcp".equals(action)) {
            msg = context.getString(R.string.notification_action_member_autoAcp, action_owner, RelationshipUtil.getRelationshipName(context, item_name));
        } else if ("accept".equals(action)) {
            msg = context.getString(R.string.notification_action_member_accept, action_owner);
        } else if ("updateRel".equals(action)) {
            msg = context.getString(R.string.notification_action_member_updateRel, action_owner, RelationshipUtil.getRelationshipName(context, item_name));
        }
        return msg;
    }

    public static String getChatMessage(Context context, String action, String item_type, String action_owner, String item_name, String snippet) {

//        boolean isPersonnal = !("group".equals(item_type));
        boolean isPersonnal = ("" + MessageChatActivity.CHAT_TYPE_PERSONAL).equals(item_type);
        String msg = null;
        if ("postText".equals(action)) {
            if (isPersonnal) {
                msg = action_owner + ":" + snippet;
//                msg = context.getString(R.string.notification_action_message_postText_personal, action_owner);
            } else {
                msg = action_owner + "@" + item_name + ":" + snippet;
//                msg = context.getString(R.string.notification_action_message_postText_group, action_owner, item_name);
            }
        } else if ("postSticker".equals(action)) {
            if (isPersonnal) {
                msg = context.getString(R.string.notification_action_message_postSticker_personal, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_message_postSticker_group, action_owner, item_name);
            }
        } else if ("postPhoto".equals(action)) {
            if (isPersonnal) {
                msg = context.getString(R.string.notification_action_message_postPhoto_personal, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_message_postPhoto_group, action_owner, item_name);
            }
        } else if ("postAudio".equals(action)) {
            if (isPersonnal) {
                msg = context.getString(R.string.notification_action_message_postAudio_personal, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_message_postAudio_group, action_owner, item_name);
            }
        } else if ("postVideo".equals(action)) {
            if (isPersonnal) {
                msg = context.getString(R.string.notification_action_message_postVideo_personal, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_message_postVideo_group, action_owner, item_name);
            }
        }
        return msg;
    }

    public static String getBirthdayMessage(Context context, String action, String item_type, String action_owner) {

        String msg = null;
        if ("birthday".equals(action)) {
            boolean isSelf = "self".equals(item_type);
            if (isSelf) {
                msg = context.getString(R.string.notification_action_bigday_birthday_self, action_owner);
            } else {
                msg = context.getString(R.string.notification_action_bigday_birthday_other, action_owner);
            }
        } else if ("prebirthday".equals(action)) {
            if ("1day".equals(item_type)) {
                msg = context.getString(R.string.notification_action_bigday_birthday_prebirthday_1, action_owner);
            } else if ("3day".equals(item_type)) {
                msg = context.getString(R.string.notification_action_bigday_birthday_prebirthday_3, action_owner);
            } else if ("7day".equals(item_type)) {
                msg = context.getString(R.string.notification_action_bigday_birthday_prebirthday_3, action_owner);
            } else if ("30day".equals(item_type)) {
                msg = context.getString(R.string.notification_action_bigday_birthday_prebirthday_30, action_owner);
            }
        }
        return msg;
    }

    public static String getMissMessage(Context context, String action, String action_owner, int memberCount, int msgCount) {

        String msg = null;
        if ("missyou".equals(action)) {
            /**
             you hui sent you a Miss
             you hui sent you 2 Miss
             然后 jennifer 又 miss 我，就需要改成
             2 members sent you 3 miss
             */

            //只有一个人
            if (memberCount == 1) {
                msg = context.getString(R.string.notification_action_miss_missyou, "", action_owner, msgCount);
            } else {
                msg = context.getString(R.string.notification_action_miss_missyou, "" + memberCount, context.getString(R.string.text_member_unit), msgCount);
            }
        }
        return msg;
    }

    /**
     * 用于可以直接使用的msg(不需要拼装)
     *
     * @param context
     * @param action
     * @param jsonObjectExtras
     * @return
     * @throws JSONException
     */
    public static String getOtherMessage(Context context, String action, JSONObject jsonObjectExtras) throws JSONException {

        String msg = null;
        if ("news".equals(action)) {
            msg = jsonObjectExtras.getString("item_name");
        }
        return msg;
    }

    public static String getGroupAlertMessage(Context context, String action, String action_owner, String item_name) throws JSONException {
        String msg = null;
        if ("add".equals(action)) {
            msg = context.getString(R.string.notification_action_message_add, action_owner, item_name);
        } else if ("pending".equals(action)) {
            msg = context.getString(R.string.notification_action_message_pending, action_owner, item_name);
        } else if ("reject".equals(action)) {
            msg = context.getString(R.string.notification_action_message_reject, action_owner, item_name);
        }
        return msg;
    }

    public static String getInactiveMessage(Context context, String action, String action_owner) throws JSONException {
        String msg = null;
        if ("recommendMem".equals(action)) {
            msg = context.getString(R.string.notification_action_message_inactive_recommend, action_owner);
        }
        return msg;
    }

}
