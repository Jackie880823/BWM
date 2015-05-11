package com.madx.bwm.entity;

import java.io.Serializable;

/**
 * Created by quankun on 15/5/8.
 */
public class GroupMessageEntity implements Serializable {
    private String group_id; // message group id
    private String group_owner_id; // message group owner id
    private String user_given_name; // message group owner username
    private String group_name; // message group name
    private String group_active_date;  // last activity date
    private String group_active_timestamp; // last activity timestamp
    private String unread;  // unread message
    private String member_name; // last member post username
    private String type; // last action in group
    private String message;  // last post message

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

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
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

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
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
