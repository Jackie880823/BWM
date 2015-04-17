package com.madx.bwm.entity;

/**
 * Created by wing on 15/1/23.
 */
public class WallCommentEntity {


    private String group_name;
    private String love_count;
    private String user_id;
    private String comment_count;
    private String text_description;
    private String loc_latitude;
    private String loc_longitude;
    private String loc_name;
    private String user_given_name;
    private String file_id;
    private String love_id;
    private String comment_id;
    private String comment_content;
    private String comment_creation_date;



    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
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

    public String getLove_count() {
        return love_count;
    }

    public void setLove_count(String love_count) {
        this.love_count = love_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {

        this.comment_count = comment_count;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {

        this.file_id = file_id;
    }

    public String getLove_id() {
        return love_id;
    }

    public void setLove_id(String love_id) {
        this.love_id = love_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_creation_date() {
        return comment_creation_date;
    }

    public void setComment_creation_date(String comment_creation_date) {
        this.comment_creation_date = comment_creation_date;
    }
}
