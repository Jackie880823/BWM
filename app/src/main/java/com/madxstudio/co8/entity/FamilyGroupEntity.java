package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by quankun on 15/5/12.
 */
public class FamilyGroupEntity implements Serializable {
    private String group_id;
    private String group_name; // group name
    private String friend_flag;
    private String group_default;

    public String getGroup_default() {
        return group_default;
    }

    public void setGroup_default(String group_default) {
        this.group_default = group_default;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getFriend_flag() {
        return friend_flag;
    }

    public void setFriend_flag(String friend_flag) {
        this.friend_flag = friend_flag;
    }


}
