package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by heweidong on 15/10/16.
 */
public class RewardsEntity implements Serializable {
    private String description;        // "Redeem code: A88BWM18", description
    private String image;              //: "http://dev.bondwith.me/bondwithme/images/Mooncake.png",  //图片链接
    private String video;              //视频链接
    private String video_thumbnail;    //视频预览图链接

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
