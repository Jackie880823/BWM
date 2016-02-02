package com.madxstudio.co8.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wing on 15/1/23.
 */
public class EventEntity implements Serializable {

    private String group_id;
    private String group_owner_id;
    private String group_name;
    private String content_group_id;
    private String group_creation_date;
    private String group_event_date;
    private String group_event_status;
    private String total_yes;
    private String total_no;
    private String total_maybe;
    private String text_description;
    private String loc_latitude;
    private String loc_longitude;
    /**
     * 坐标类型
     */
    private String loc_type;
    private String loc_name;
    private String user_given_name;
    private String group_event_timestamp;
    private String content_creation_timestamp;
    /**
     * intent
     */
    private String group_member_response;
    private String group_new_post;
    private List<String> event_member;
    private String group_end_date;

    public String getGroup_end_date() {
        return group_end_date;
    }

    public void setGroup_end_date(String group_end_date) {
        this.group_end_date = group_end_date;
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

    public String getGroup_creation_date() {
        return group_creation_date;
    }

    public void setGroup_creation_date(String group_creation_date) {
        this.group_creation_date = group_creation_date;
    }

    public String getGroup_owner_id() {
        return group_owner_id;
    }

    public void setGroup_owner_id(String group_owner_id) {
        this.group_owner_id = group_owner_id;
    }

    public String getGroup_event_date() {
        return group_event_date;
    }

    public void setGroup_event_date(String group_event_date) {
        this.group_event_date = group_event_date;
    }

    public String getGroup_event_status() {
        return group_event_status;
    }

    public void setGroup_event_status(String group_event_status) {
        this.group_event_status = group_event_status;
    }

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
    }

    public String getLoc_latitude() {
        return loc_latitude;
    }

    public void setLoc_latitude(String loc_latitude) {
        this.loc_latitude = loc_latitude;
    }

    public String getLoc_longitude() {
        return loc_longitude;
    }

    public void setLoc_longitude(String loc_longitude) {
        this.loc_longitude = loc_longitude;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getGroup_event_timestamp() {
        return group_event_timestamp;
    }

    public void setGroup_event_timestamp(String group_event_timestamp) {
        this.group_event_timestamp = group_event_timestamp;
    }

    public String getContent_creation_timestamp() {
        return content_creation_timestamp;
    }

    public void setContent_creation_timestamp(String content_creation_timestamp) {
        this.content_creation_timestamp = content_creation_timestamp;
    }

    public List<String> getEvent_member() {
        return event_member;
    }

    public void setEvent_member(List<String> event_member) {
        this.event_member = event_member;
    }

    public String getContent_group_id() {
        return content_group_id;
    }

    public void setContent_group_id(String content_group_id) {
        this.content_group_id = content_group_id;
    }

    public String getTotal_yes() {
        return total_yes;
    }

    public void setTotal_yes(String total_yes) {
        this.total_yes = total_yes;
    }

    public String getTotal_no() {
        return total_no;
    }

    public void setTotal_no(String total_no) {
        this.total_no = total_no;
    }

    public String getTotal_maybe() {
        return total_maybe;
    }

    public void setTotal_maybe(String total_maybe) {
        this.total_maybe = total_maybe;
    }

    public String getGroup_member_response() {
        return group_member_response;
    }

    public void setGroup_member_response(String group_member_response) {
        this.group_member_response = group_member_response;
    }

    public String getGroup_new_post() {
        return group_new_post;
    }

    public void setgetGroup_new_post(String group_new_post) {
        this.group_new_post = group_new_post;
    }

    public String getLoc_type() {
        return loc_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }

    public void setGroup_new_post(String group_new_post) {
        this.group_new_post = group_new_post;
    }
}
