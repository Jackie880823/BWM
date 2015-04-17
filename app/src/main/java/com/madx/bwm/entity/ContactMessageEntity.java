package com.madx.bwm.entity;

import java.util.List;

/**
 * Created by wing on 15/4/9.
 */
public class ContactMessageEntity {

    //app owner
    private String user_id;
    private String user_given_name;
    private String user_country_code;
    private List<ContactDetailEntity> contact_list;
    private String personal_msg;

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

    public String getUser_country_code() {
        return user_country_code;
    }

    public void setUser_country_code(String user_country_code) {
        this.user_country_code = user_country_code;
    }

    public List<ContactDetailEntity> getContact_list() {
        return contact_list;
    }

    public void setContact_list(List<ContactDetailEntity> contact_list) {
        this.contact_list = contact_list;
    }

    public String getPersonal_msg() {
        return personal_msg;
    }

    public void setPersonal_msg(String personal_msg) {
        this.personal_msg = personal_msg;
    }
}
