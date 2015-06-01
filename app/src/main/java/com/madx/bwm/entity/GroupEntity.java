package com.madx.bwm.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by christepherzhang on 15/1/30.
 */
public class GroupEntity implements Serializable{
    private String group_id;            //"1319",
    private String group_name;          //"今天真好啊 :)",
    private String group_active_date;   //"2015-01-21 12:59:21",
    private String unread;              //"2",
    private String group_flag;

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    private String user_given_name;              //"2",



    private List<UserEntity> member;

    public List<UserEntity> getMember() {
        return member;
    }

    public void setMember(List<UserEntity> member) {
        this.member = member;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
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

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getGroup_flag() {
        return group_flag;
    }

    public void setGroup_flag(String group_flag) {
        this.group_flag = group_flag;
    }
}
