package com.bondwithme.BondCorp.entity;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by wing on 15/1/23.
 */
public class EventCommentEntity implements Serializable {


    /**
     *
     "comment_id": "966", // Comment ID
     "content_group_id": "1410", // Post content group ID
     "comment_owner_id": "175",  // Comment creator ID
     "comment_content": "vvaaaa",  // Comment content
     "comment_creation_date": "2014-06-06 11:16:57", //Comment create date
     "comment_creation_time": "140208359", //Comment creation time
     "group_id": "142", // Group ID
     "content_id": "1410", //  Post content ID
     "sticker_group_path": null, // Sticker path
     "sticker_name": null,  // Sticker name
     "sticker_type": null, // Sticker type (.gif , .jpg, .png)
     "user_id": "175", // Comment creator ID
     "user_given_name": "Christ Ng", //Comment creator name
     "love_id": null, // Viewer love ID, if null means viewer havent love the comment
     "love_count": "0", // Number of love
     "file_id": "1430750914", // 照片file_id，若没有是null
     "comment_date_info":
     */

    private String comment_id;
    private String content_group_id;
    private String comment_owner_id;
    private String comment_content;
    private String comment_creation_date;
    private String comment_creation_time;
    private String group_id;
    private String content_id;
    private String sticker_group_path;
    private String sticker_name;
    private String sticker_type;
    private String user_id;
    private String user_given_name;
    private String love_id;
    private String love_count;
    private String file_id;
    private String comment_date_info;

    private Uri uri;
    private String isNate;

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

    public String getComment_owner_id() {
        return comment_owner_id;
    }

    public void setComment_owner_id(String comment_owner_id) {
        this.comment_owner_id = comment_owner_id;
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

    public String getComment_creation_time() {
        return comment_creation_time;
    }

    public void setComment_creation_time(String comment_creation_time) {
        this.comment_creation_time = comment_creation_time;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
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

    public String getLove_id() {
        return love_id;
    }

    public void setLove_id(String love_id) {
        this.love_id = love_id;
    }

    public String getLove_count() {
        return love_count;
    }

    public void setLove_count(String love_count) {
        this.love_count = love_count;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getComment_date_info() {
        return comment_date_info;
    }

    public void setComment_date_info(String comment_date_info) {
        this.comment_date_info = comment_date_info;
    }

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
}
