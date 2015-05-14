package com.madx.bwm.entity;

import java.io.Serializable;

/**
 * Created by quankun on 15/5/8.
 */
public class PrivateMessageEntity implements Serializable {
    public static final String POST_TEXT = "postText";
    public static final String POST_STICKER = "postSticker";
    public static final String POST_LOCATION = "postLocation";
    public static final String POST_PHOTO = "postPhoto";

    private String user_id;// member id
    private String user_given_name; // member name
    private String group_id; // group id
    private String group_owner_id;// group owner id
    private String group_active_date;// group last active datetime
    private String group_active_timestamp; // group last active timestamp
    private String unread; // total unread
    private String type; // last action in message group
    private String message; // last message in message group

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
