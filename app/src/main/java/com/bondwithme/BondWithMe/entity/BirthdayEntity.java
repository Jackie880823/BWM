package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/1/28.
 */
public class BirthdayEntity implements Serializable{

    private String user_id;
    private String user_given_name;
    private String user_dob;
    private String day_count;

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

    public String getUser_dob() {
        return user_dob;
    }

    public void setUser_dob(String user_dob) {
        this.user_dob = user_dob;
    }

    public String getDay_count() {
        return day_count;
    }

    public void setDay_count(String day_count) {
        this.day_count = day_count;
    }
}
