package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created 16/4/5.
 *
 * @author Jackie
 * @version 1.0
 */
public class AdminPendingRequest implements Serializable {
    /**
     * 成员user_id
     */
    private String action_user_id;
    private String receiver_user_id;
    private String reference_id;
    private String module_name;

    /**
     * 申请离开公司
     */
    private String module_action;

    /**
     * 公司名字
     */
    private String message_variable;

    /**
     * "pending"
     */
    private String status;

    /**
     * 创建时间
     */
    private String creation_date;

    /**
     * 时间截
     */
    private String creation_timestamp;

    /**
     * 用户名字
     */
    private String action_username;
    private String postowner_username;
    private String message;
    private String relationship;

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

    public String getCreation_timestamp() {
        return creation_timestamp;
    }

    public void setCreation_timestamp(String creation_timestamp) {
        this.creation_timestamp = creation_timestamp;
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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}


