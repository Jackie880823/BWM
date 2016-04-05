package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by quankun on 15/5/12.
 */
public class OrgGroupEntity implements Serializable {
    private String group_id;//群组id
    private String group_owner_id;//群主用户id
    private String group_name;//群组名字
    private String group_photo;//群组图片
    private String group_default;
    private String friend_flag;//1- 成员都是好友，0-有成员不是好友
    private String total_member;

    public String getTotal_member() {
        return total_member;
    }

    public void setTotal_member(String total_member) {
        this.total_member = total_member;
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

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_photo() {
        return group_photo;
    }

    public void setGroup_photo(String group_photo) {
        this.group_photo = group_photo;
    }

    public String getGroup_default() {
        return group_default;
    }

    public void setGroup_default(String group_default) {
        this.group_default = group_default;
    }

    public String getFriend_flag() {
        return friend_flag;
    }

    public void setFriend_flag(String friend_flag) {
        this.friend_flag = friend_flag;
    }
}
