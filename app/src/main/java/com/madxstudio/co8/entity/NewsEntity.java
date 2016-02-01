package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/4/9.
 */
public class NewsEntity implements Serializable {

    //新的news API

    private boolean visibleOfTvMore;

    public boolean isVisibleOfTvMore() {
        return visibleOfTvMore;
    }

    public void setVisibleOfTvMore(boolean visibleOfTvMore) {
        this.visibleOfTvMore = visibleOfTvMore;
    }

    public static final String CONTENT_TYPE_ADS = "ads";

    private String group_id;
    private String group_owner_id;
    private String content_id;
    private String content_type;
    private String content_group_id;
    private String content_group_public;
    private String content_creator_id;
    private String content_creation_date;
    private String content_creation_timestamp;
    private String category_id;
    private String category_name;
    private String content_title;
    private String text_description;
    private String photo_id;
    private String photo_caption;
    private String photo_multiple;
    private String file_id;
    private String loc_latitude;
    private String loc_longitude;
    private String loc_name;
    private String loc_caption;
    private String loc_type;
    private String video_id;
    private String video_filename;
    private String video_thumbnail;
    private String video_duration;
    private String dofeel_code;
    private String user_id;
    private String user_given_name;
    private String love_id;
    private String comment_count;
    private String love_count;
    private String photo_count;
    private String photo_max;
    private String track_url;
    private String video_url;

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_owner_id() {
        return group_owner_id;
    }

    public void setGroup_owner_id(String group_owner_id) {
        this.group_owner_id = group_owner_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_group_id() {
        return content_group_id;
    }

    public void setContent_group_id(String content_group_id) {
        this.content_group_id = content_group_id;
    }

    public String getContent_group_public() {
        return content_group_public;
    }

    public void setContent_group_public(String content_group_public) {
        this.content_group_public = content_group_public;
    }

    public String getContent_creator_id() {
        return content_creator_id;
    }

    public void setContent_creator_id(String content_creator_id) {
        this.content_creator_id = content_creator_id;
    }

    public String getContent_creation_date() {
        return content_creation_date;
    }

    public void setContent_creation_date(String content_creation_date) {
        this.content_creation_date = content_creation_date;
    }

    public String getContent_creation_timestamp() {
        return content_creation_timestamp;
    }

    public void setContent_creation_timestamp(String content_creation_timestamp) {
        this.content_creation_timestamp = content_creation_timestamp;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getContent_title() {
        return content_title;
    }

    public void setContent_title(String content_title) {
        this.content_title = content_title;
    }

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoto_caption() {
        return photo_caption;
    }

    public void setPhoto_caption(String photo_caption) {
        this.photo_caption = photo_caption;
    }

    public String getPhoto_multiple() {
        return photo_multiple;
    }

    public void setPhoto_multiple(String photo_multiple) {
        this.photo_multiple = photo_multiple;
    }

    public String getFile_id() {
        return file_id;
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

    public String getLoc_type() {
        return loc_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_filename() {
        return video_filename;
    }

    public void setVideo_filename(String video_filename) {
        this.video_filename = video_filename;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getDofeel_code() {
        return dofeel_code;
    }

    public void setDofeel_code(String dofeel_code) {
        this.dofeel_code = dofeel_code;
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

    public String getPhoto_count() {
        return photo_count;
    }

    public void setPhoto_count(String photo_count) {
        this.photo_count = photo_count;
    }

    public String getPhoto_max() {
        return photo_max;
    }

    public void setPhoto_max(String photo_max) {
        this.photo_max = photo_max;
    }

    public String getTrack_url() {
        return track_url;
    }

    public void setTrack_url(String track_url) {
        this.track_url = track_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
