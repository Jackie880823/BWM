package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/4/9.
 */
public class NewsEntity implements Serializable {

    //旧的news API
//    private String action_user_id;
//    private String receiver_user_id;
//    private String reference_id;
//    private String module_id;
//    private String module_name;
//    private String module_action;
//    private String message_variable;
//    private String status;
//    private String creation_date;
//    private String action_username;
//    private String postowner_username;
//    private String message;
//    "release_date": "2015-09-24 00:00:00", //news datetime
//    "release_timestamp": "1443052800", //news timestamp


    //新的news API
    private String release_date;     //news datetime
    private String release_timestamp;//news timestamp
    private String title;            //news headline
    private String content_text;     //news content
    private String image;            //news image
    private String video;            //news video
    private String video_thumbnail;  //news video thumbnail

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getRelease_timestamp() {
        return release_timestamp;
    }

    public void setRelease_timestamp(String release_timestamp) {
        this.release_timestamp = release_timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }
}
