package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/4/9.
 */
public class NewsEntity implements Serializable {

    //新的news API
    private String release_date;     //news datetime
    private String release_timestamp;//news timestamp
    private String title;            //news headline
    private String content_text;     //news content
    private String image;            //news image
    private String video;            //news video
    private String video_thumbnail;  //news video thumbnail
    private boolean visibleOfTvMore;

    public boolean isVisibleOfTvMore() {
        return visibleOfTvMore;
    }

    public void setVisibleOfTvMore(boolean visibleOfTvMore) {
        this.visibleOfTvMore = visibleOfTvMore;
    }

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
