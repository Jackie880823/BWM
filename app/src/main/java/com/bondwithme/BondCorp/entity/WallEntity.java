package com.bondwithme.BondCorp.entity;

import com.bondwithme.BondCorp.Constant;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wing on 15/1/23.
 */
public class WallEntity implements Serializable {

    public static final String CONTENT_TYPE_POST = "post";

    public static final String CONTENT_TYPE_ADS = "ads";

    /**
     * <br>Post create date
     * <br>such as: "2015-01-28 11:16:57"
     */
    private String content_creation_date;

    /**
     * 0- Private, 1-Public
     */
    private String content_group_public;

    /**
     * Post creator ID
     */
    private String content_creator_id;

    /**
     * Posting - Text content
     */
    private String text_description;

    /**
     * Posting - Photo file ID
     */
    private String file_id;

    /**
     * <br>Post map coordinates latitude
     * <br>such as: "3.127272"
     */
    private String loc_latitude;

    /**
     * <br>Post map coordinates longitude
     * <br>such as: "101.6451158"
     */
    private String loc_longitude;

    /**
     * 坐标类型
     */
    private String loc_type;

    /**
     * <br>Post map location
     * <br>such as:"Pusat Perdagangan Phileo Damansara"
     */
    private String loc_name;

    /**
     * <br>Post map location caption
     */
    private String loc_caption;

    /**
     * <br>Sticker ID
     */
    private String sticker_id;

    /**
     * <br>Sticker Path
     */
    private String sticker_group_path;

    /**
     * Sticker Name
     */
    private String sticker_name;

    /**
     * <br>Sticker Type
     * <br> such as:{@link Constant#Sticker_Png} or {@link Constant#Sticker_Gif}
     */
    private String sticker_type;

    private String video_id;

    /**
     * <br>视频文件名，若没有视频是null
     * <br> such as: "1439522914995561.MOV"
     */
    private String video_filename;

    /**
     * <br>视频小图文件名，没视频是null
     * <br>"1439522914995561.png",
     */
    private String video_thumbnail;

    /**
     * 视频长
     */
    private String video_duration;

    /**
     * <br>Posting creator name
     * <br> such as:  "Jolin Tay"
     */
    private String user_given_name;

    /**
     * <br> Number of comments
     * <br> default: "0"
     */
    private String comment_count;

    /**
     * <br>Number of love
     */
    private String love_count;

    /**
     * Number of photo
     */
    private String photo_count;

    /**
     * 照片最大序号，增加照片的时候有用，照片顺序可以用这个再续加
     */
    private String photo_max;

    /**
     * <br>Other format for posting date time
     * <br>"{\"day\":\"1\",\"month\":\"0\",\"year\":\"0\",\"hour\":\"3\",\"min\":\"46\",\"sec\":\"4\"}"
     */
    private String post_date_info;

    /**
     * Posting creator ID
     */
    private String user_id;

    /**
     *
     */
    private String user_photo;

    /**
     * Post content ID
     */
    private String content_id;

    /**
     * {@link #CONTENT_TYPE_POST} – 普通日记, {@link #CONTENT_TYPE_ADS} – 广告
     */
    private String content_type;

    /**
     *
     */
    private String group_id;

    /**
     * Post content group ID
     */
    private String content_group_id;

    private String love_id;

    /**
     * do and feel code 心情码
     */
    private String dofeel_code;

    /**
     * group tag list, if null - no group tag
     */
    private List<GroupEntity> tag_group;

    /**
     * member tag list, if null - no member tag
     */
    private List<UserEntity> tag_member;

    private String track_url;

    public String getContent_creation_date() {
        return content_creation_date;
    }

    public void setContent_creation_date(String content_creation_date) {
        this.content_creation_date = content_creation_date;
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

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
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

    public String getLoc_type() {
        return loc_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
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

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
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

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
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

    public String getPhoto_count() {
        return photo_count;
    }

    public void setPhoto_count(String photo_count) {
        this.photo_count = photo_count;
    }

    public String getPost_date_info() {
        return post_date_info;
    }

    public String getPhoto_max() {
        return photo_max;
    }

    public void setPhoto_max(String photo_max) {
        this.photo_max = photo_max;
    }

    public void setPost_date_info(String post_date_info) {
        this.post_date_info = post_date_info;
    }

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

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
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

    public String getTrack_url() {
        return track_url;
    }

    public void setTrack_url(String track_url) {
        this.track_url = track_url;
    }
}
