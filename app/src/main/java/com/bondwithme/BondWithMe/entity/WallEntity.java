package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wing on 15/1/23.
 */
public class WallEntity implements Serializable {

    private String content_creation_date;

    private String text_description;
    private String file_id;
    private String loc_latitude;
    private String loc_longitude;
    /**坐标类型*/
    private String loc_type;
    private String loc_name;
    private String loc_caption;
    private String sticker_id;
    private String sticker_group_path;
    private String sticker_name;
    private String sticker_type;
    private String user_given_name;
    private String comment_count;
    private String love_count;
    private String photo_count;
    private String post_date_info;

    private String user_id;
    private String user_photo;
    private String content_id;
    private String group_id;
    private String content_group_id;
    public String getFile_id() {
        return file_id;
    }
    private String love_id;

    private String dofeel_code;
    private List<GroupEntity> tag_group;



    private List<UserEntity> tag_member;

    public String getContent_creation_date() {
        return content_creation_date;
    }

    public void setContent_creation_date(String content_creation_date) {
        this.content_creation_date = content_creation_date;
    }

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
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

    public String getLoc_caption() {
        return loc_caption;
    }

    public void setLoc_caption(String loc_caption) {
        this.loc_caption = loc_caption;
    }

    public String getSticker_id() {
        return sticker_id;
    }

    public void setSticker_id(String sticker_id) {
        this.sticker_id = sticker_id;
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

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getLove_count() {
        return love_count;
    }

    public void setLove_count(String love_count) {
        this.love_count = love_count;
    }

    public void setPhoto_count(String photo_count){ this.photo_count = photo_count; }

    public String getPhoto_count(){ return photo_count; }

    public String getPost_date_info() {
        return post_date_info;
    }

    public void setPost_date_info(String post_date_info) { this.post_date_info = post_date_info; }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getContent_group_id() {
        return content_group_id;
    }

    public void setContent_group_id(String content_group_id) {
        this.content_group_id = content_group_id;
    }

    public String getLove_id() {
        return love_id;
    }

    public void setLove_id(String love_id) {
        this.love_id = love_id;
    }

    public String getDofeel_code() {
        return dofeel_code;
    }

    public void setDofeel_code(String dofeel_code) {
        this.dofeel_code = dofeel_code;
    }

    public List<GroupEntity> getTag_group() {
        return tag_group;
    }

    public void setTag_group(List<GroupEntity> tag_group) {
        this.tag_group = tag_group;
    }

    public List<UserEntity> getTag_member() {
        return tag_member;
    }

    public void setTag_member(List<UserEntity> tag_member) {
        this.tag_member = tag_member;
    }

    public String getLoc_type() {
        return loc_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }
}
