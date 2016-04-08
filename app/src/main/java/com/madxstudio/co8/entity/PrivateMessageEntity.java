package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by quankun on 15/5/8.
 */
public class PrivateMessageEntity implements Serializable {
    public static final String POST_TEXT = "postText";
    public static final String POST_STICKER = "postSticker";
    public static final String POST_LOCATION = "postLocation";
    public static final String POST_PHOTO = "postPhoto";
    public static final String POST_AUDIO = "postAudio";
    public static final String POST_VIDEO = "postVideo";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_DE_ACTIVE = "deactive";


    private String message_type; // 群组的聊天室
    private String group_name; // 群组名
    private String group_id; // 群组ID
    private String group_owner_id; // 群组创建人ID
    private String group_active_date;
    private String group_active_timestamp; // 聊天室最后活跃时间
    private String unread; // 未读讯息总数
    private String member_name;// 最后讯息的创建人，没有是空
    private String type; // 最后讯息的格式，没有是空( postText - 文字 , postPhoto - 照片 , postVideo - 视频, postAudio - 语音, postSticker- 大表情 )
    private String message; // 文字讯息，没有是空

    private String user_id; // 会员ID
    private String user_given_name; // 会员名
    private String status;// active – 还未离开公司, deactive – 已离开公司

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_owner_id() {
        return group_owner_id;
    }

    public void setGroup_owner_id(String group_owner_id) {
        this.group_owner_id = group_owner_id;
    }

    public String getGroup_active_date() {
        return group_active_date;
    }

    public void setGroup_active_date(String group_active_date) {
        this.group_active_date = group_active_date;
    }

    public String getGroup_active_timestamp() {
        return group_active_timestamp;
    }

    public void setGroup_active_timestamp(String group_active_timestamp) {
        this.group_active_timestamp = group_active_timestamp;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
