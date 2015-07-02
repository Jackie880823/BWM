package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/4/9.
 */
public class MissEntity implements Serializable {
    private String bond_alert_id;
    private String action_user_id;
    private String receiver_user_id;
    private String reference_id;
    private String module;
    private String module_id;
    private String module_name;
    private String module_action;
    private String message_variable;
    private String creation_date;
    private String last_update_date;
    private String push_flag;
    private String push_date;
    private String alert_date_info;

    public String getBond_alert_id() {
        return bond_alert_id;
    }

    public void setBond_alert_id(String bond_alert_id) {
        this.bond_alert_id = bond_alert_id;
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

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
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

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getLast_update_date() {
        return last_update_date;
    }

    public void setLast_update_date(String last_update_date) {
        this.last_update_date = last_update_date;
    }

    public String getPush_flag() {
        return push_flag;
    }

    public void setPush_flag(String push_flag) {
        this.push_flag = push_flag;
    }

    public String getPush_date() {
        return push_date;
    }

    public void setPush_date(String push_date) {
        this.push_date = push_date;
    }

    public String getAlert_date_info() {
        return alert_date_info;
    }

    public void setAlert_date_info(String alert_date_info) {
        this.alert_date_info = alert_date_info;
    }
}
