package com.madx.bwm.entity;

import android.net.Uri;

/**
 * Created by wing on 15/1/23.
 */
public class EventCommentEntity {

    private String comment_content;
    private String user_given_name;
    private String comment_creation_date;
    private String comment_date_info;
    private String comment_id;
    private String content_group_id;
    private String owner_user_id;
    private String love_count;
    private String love_id;
    private String user_id;
    private String sticker_group_path;
    private String sticker_name;
    private String sticker_type;
    private String file_id;

    private Uri uri;
    private String isNate;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getIsNate() {
        return isNate;
    }

    public void setIsNate(String isNate) {
        this.isNate = isNate;
    }



    public String getSticker_group_path() {
        return sticker_group_path;
    }

    public void setSticker_group_path(String sticker_group_path) {
        this.sticker_group_path = sticker_group_path;
    }

    public String getSticker_name() {
        return sticker_name;
    }

    public void setSticker_name(String sticker_name) {
        this.sticker_name = sticker_name;
    }

    public String getSticker_type() {
        return sticker_type;
    }

    public void setSticker_type(String sticker_type) {
        this.sticker_type = sticker_type;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getComment_creation_date() {
        return comment_creation_date;
    }

    public void setComment_creation_date(String comment_creation_date) {
        this.comment_creation_date = comment_creation_date;
    }

    public String getComment_date_info() {
        return comment_date_info;
    }

    public void setComment_date_info(String comment_date_info) {
        this.comment_date_info = comment_date_info;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }


    public String getContent_group_id() {
        return content_group_id;
    }

    public void setContent_group_id(String content_group_id) {
        this.content_group_id = content_group_id;
    }

    public String getOwner_user_id() {
        return owner_user_id;
    }

    public void setOwner_user_id(String owner_user_id) {
        this.owner_user_id = owner_user_id;
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

    public String getLove_id() {
        return love_id;
    }

    public void setLove_id(String love_id) {
        this.love_id = love_id;
    }
}
