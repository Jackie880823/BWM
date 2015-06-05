package com.madx.bwm.entity;

import java.io.Serializable;

/**
 * Created by liangzemian on 15/4/12.
 */
public class BigDayEntity implements Serializable {
    private String action_user_id;
    private String receiver_user_id;
    private String reference_id;
    private String module_name;
    private String module_action;
    private String message_variable;
    private String status;
    private String creation_date;
    private String action_username;
    private String postowner_username;
    private String message;

    /*new*/
    private String user_id;
    private String bondwithme_id;
    private String dis_bondwithme_id;
    private String user_surname;
    private String user_given_name;
    private String user_dob;
    private String birthday_date;
    private String day;
    private String day_left;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBondwithme_id() {
        return bondwithme_id;
    }

    public void setBondwithme_id(String bondwithme_id) {
        this.bondwithme_id = bondwithme_id;
    }

    public String getDis_bondwithme_id() {
        return dis_bondwithme_id;
    }

    public void setDis_bondwithme_id(String dis_bondwithme_id) {
        this.dis_bondwithme_id = dis_bondwithme_id;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
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

    public String getBirthday_date() {
        return birthday_date;
    }

    public void setBirthday_date(String birthday_date) {
        this.birthday_date = birthday_date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay_left() {
        return day_left;
    }

    public void setDay_left(String day_left) {
        this.day_left = day_left;
    }

    public String getAction_user_id() {
        return action_user_id;
    }

    public void setAction_user_id(String action_user_id) {
        this.action_user_id = action_user_id;
    }

    public String getReceiver_user_id() {
        return receiver_user_id;
    }

    public void setReceiver_user_id(String receiver_user_id) {
        this.receiver_user_id = receiver_user_id;
    }

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getModule_action() {
        return module_action;
    }

    public void setModule_action(String module_action) {
        this.module_action = module_action;
    }

    public String getMessage_variable() {
        return message_variable;
    }

    public void setMessage_variable(String message_variable) {
        this.message_variable = message_variable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getAction_username() {
        return action_username;
    }

    public void setAction_username(String action_username) {
        this.action_username = action_username;
    }

    public String getPostowner_username() {
        return postowner_username;
    }

    public void setPostowner_username(String postowner_username) {
        this.postowner_username = postowner_username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
