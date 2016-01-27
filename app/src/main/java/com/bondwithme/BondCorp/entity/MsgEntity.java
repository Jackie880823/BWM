package com.bondwithme.BondCorp.entity;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by christepherzhang on 15/1/27.
 */
public class MsgEntity implements Serializable {
    public static final String SEND_IN = "1";
    public static final String SEND_FAIL = "2";
    public static final String SEND_SUCCESS = "3";

    private String group_id;            //: "250",
    private String group_owner_id;      //: "47",
    private String group_name;          //: "Chiah Cousin3",
    private String group_individual;    //: "0",
    private String group_type;          //: "0",
    private String group_creation_date; //: "2014-04-18 20:57:22",
    private String group_active_date;   //: "2014-12-18 18:54:50",
    private String group_event_date;    //: "0000-00-00 00:00:00",
    private String group_event_status;  //: "0",

    private String content_id;          //: "3561",
    private String content_group_id;    //: "3561",
    private String alert_status;        //: null,
    private String content_group_flag;  //: "1",
    private String content_group_public;//: "0",
    private String content_creator_id;  //: "47",
    private String content_type;        //: "post",
    private String first_post;          //: "0",
    private String content_creation_date;//: "2014-12-18 18:03:46", //Message create date time

    private String text_id;             //: null,
    private String text_description;    //: null, // message text
    private String text_url;            //: null,

    private String audio_id;            //: null,
    private String audio_caption;       //: null,
    private String audio_format1;       //: null,
    private String audio_format2;       //: null,

    private String photo_id;            //: null,
    private String photo_caption;       //: null, // Photo caption图片说明
    private String photo_thumbsize;     //: null,
    private String photo_postsize;      //: null,
    private String photo_fullsize;      //: null,
    private String photo_multiple;      //: null,
    private String file_id;             //: null, // Photo file id if null mean no photo

    private String video_id;            //: null,
    private String video_caption;       //: null,
    private String video_format1;       //: null,
    private String video_format2;       //: null,
    private String vfile_id;            //: null,

    private String loc_id;              //: "557",
    private String loc_latitude;        //: "3.126369", // location coordinates latitude纬度坐标
    private String loc_longitude;       //: "101.644756", // location coordinates longitude经度坐标
    private String loc_name;            //: "Eastin Hotel Petaling Jaya", // Location name位置名字
    private String loc_caption;         //: "",
    private String loc_type; //坐标类型，wing add

    private String sticker_id;          //: null, // Sticker id
    private String sticker_group_path;  //: null, // Sticker group
    private String sticker_name;        //: null, // sticker file name
    private String sticker_type;        //: null, // sticker file extension eg. .png or .gif

    private String user_id;             //: "47",
    private String user_fullname;       //: "Keng Wai",
    private String user_surname;        //: "Chiah",
    private String user_given_name;     //: "Keng Wai", // Message creator
    private String user_emoticon;       //: “froggy-001”,
    private String user_photo;          //: “profile.png”,
    private String user_status;         //: “active”,
    private String love_id;             //: null,
    private String comment_count;       //: “0”,
    private String love_count;          //: “0”,
    private String post_date_info;      //: “{\”day\”:\”29\”,\”month\”:\”0\”,\”year\”:\”0\”,\”hour\”:\”0\”,\”min\”:\”41\”,\”sec\”:\”36\”}” // other format for message created date time

    private Uri uri;
    private String isNate;
    private String video_filename;
    private String audio_duration;
    private String audio_filename;
    private String video_duration;
    private String video_thumbnail;
    private String sendStatus;
    private Uri failUri;

    public Uri getFailUri() {
        return failUri;
    }

    public void setFailUri(Uri failUri) {
        this.failUri = failUri;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    public String getVideo_filename() {
        return video_filename;
    }

    public void setVideo_filename(String video_filename) {
        this.video_filename = video_filename;
    }

    public String getAudio_duration() {
        return audio_duration;
    }

    public void setAudio_duration(String audio_duration) {
        this.audio_duration = audio_duration;
    }

    public String getAudio_filename() {
        return audio_filename;
    }

    public void setAudio_filename(String audio_filename) {
        this.audio_filename = audio_filename;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getIsNate() {
        return isNate;
    }

    public void setIsNate(String isNate) {
        this.isNate = isNate;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_individual() {
        return group_individual;
    }

    public void setGroup_individual(String group_individual) {
        this.group_individual = group_individual;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getGroup_creation_date() {
        return group_creation_date;
    }

    public void setGroup_creation_date(String group_creation_date) {
        this.group_creation_date = group_creation_date;
    }

    public String getGroup_active_date() {
        return group_active_date;
    }

    public void setGroup_active_date(String group_active_date) {
        this.group_active_date = group_active_date;
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

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getContent_group_id() {
        return content_group_id;
    }

    public void setContent_group_id(String content_group_id) {
        this.content_group_id = content_group_id;
    }

    public String getAlert_status() {
        return alert_status;
    }

    public void setAlert_status(String alert_status) {
        this.alert_status = alert_status;
    }

    public String getContent_group_flag() {
        return content_group_flag;
    }

    public void setContent_group_flag(String content_group_flag) {
        this.content_group_flag = content_group_flag;
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

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getFirst_post() {
        return first_post;
    }

    public void setFirst_post(String first_post) {
        this.first_post = first_post;
    }

    public String getContent_creation_date() {
        return content_creation_date;
    }

    public void setContent_creation_date(String content_creation_date) {
        this.content_creation_date = content_creation_date;
    }

    public String getText_id() {
        return text_id;
    }

    public void setText_id(String text_id) {
        this.text_id = text_id;
    }

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
    }

    public String getText_url() {
        return text_url;
    }

    public void setText_url(String text_url) {
        this.text_url = text_url;
    }

    public String getAudio_id() {
        return audio_id;
    }

    public void setAudio_id(String audio_id) {
        this.audio_id = audio_id;
    }

    public String getAudio_caption() {
        return audio_caption;
    }

    public void setAudio_caption(String audio_caption) {
        this.audio_caption = audio_caption;
    }

    public String getAudio_format1() {
        return audio_format1;
    }

    public void setAudio_format1(String audio_format1) {
        this.audio_format1 = audio_format1;
    }

    public String getAudio_format2() {
        return audio_format2;
    }

    public void setAudio_format2(String audio_format2) {
        this.audio_format2 = audio_format2;
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

    public String getPhoto_thumbsize() {
        return photo_thumbsize;
    }

    public void setPhoto_thumbsize(String photo_thumbsize) {
        this.photo_thumbsize = photo_thumbsize;
    }

    public String getPhoto_postsize() {
        return photo_postsize;
    }

    public void setPhoto_postsize(String photo_postsize) {
        this.photo_postsize = photo_postsize;
    }

    public String getPhoto_fullsize() {
        return photo_fullsize;
    }

    public void setPhoto_fullsize(String photo_fullsize) {
        this.photo_fullsize = photo_fullsize;
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

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_caption() {
        return video_caption;
    }

    public void setVideo_caption(String video_caption) {
        this.video_caption = video_caption;
    }

    public String getVideo_format1() {
        return video_format1;
    }

    public void setVideo_format1(String video_format1) {
        this.video_format1 = video_format1;
    }

    public String getVideo_format2() {
        return video_format2;
    }

    public void setVideo_format2(String video_format2) {
        this.video_format2 = video_format2;
    }

    public String getVfile_id() {
        return vfile_id;
    }

    public void setVfile_id(String vfile_id) {
        this.vfile_id = vfile_id;
    }

    public String getLoc_id() {
        return loc_id;
    }

    public void setLoc_id(String loc_id) {
        this.loc_id = loc_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getUser_emoticon() {
        return user_emoticon;
    }

    public void setUser_emoticon(String user_emoticon) {
        this.user_emoticon = user_emoticon;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
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

    public String getPost_date_info() {
        return post_date_info;
    }

    public void setPost_date_info(String post_date_info) {
        this.post_date_info = post_date_info;
    }

    public String getLoc_type() {
        return loc_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }
}
