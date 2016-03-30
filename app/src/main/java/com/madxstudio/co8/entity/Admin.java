package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created 16/3/29.
 *
 * @author Jackie
 * @version 1.0
 */
public class Admin implements Serializable{
    private String user_id;
    private String user_given_name;

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
}
