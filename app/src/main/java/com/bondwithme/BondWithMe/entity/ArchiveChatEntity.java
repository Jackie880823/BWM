package com.bondwithme.BondWithMe.entity;

/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveChatEntity {
    private String group_id;// Group ID 群组ID
    private String group_owner_id;// Group owner ID 群主ID
    private String group_name;// Group name 群名字
    private String content_id;// Post content ID
    private String content_group_id;// Post content group ID
    private String content_creator_id;// Post creator ID 内容创建者ID
    private String content_creation_date;//Post create date
    private String content_creation_timestamp;//Post create timestamp 创建時間戳
    private String text_description;// Posting - Text content 内容文字
    private String photo_caption;// Posting - Photo caption 照片说明
    private String photo_multiple;// null – no photo, ‘0’ - single photo, ‘1’ - multiple photo
    private String file_id;// Posting - Photo file ID 照片文件ID
    private String loc_latitude;// Post map coordinates latitude 地图纬度
    private String loc_longitude;// Post map coordinates longitude 地图经度
    private String loc_name;// Post map location name 地标名字
    private String loc_caption;// Post map location caption 地标说明
    private String loc_type;// Map type 地图类型
    private String sticker_group_path;// Sticker path 大表情路径
    private String sticker_name;// Sticker name 大表情名字
    private String sticker_type;// Sticker type (.gif , .jpg, .png) 大表情文件格式
    private String user_id;// Posting creator ID 内容创建者ID
    private String user_given_name;//Posting creator name 内容创建者名字
    private String love_id;// Viewer love ID, if null means viewer havent love the post  赞ID，若是null，就是当前手机用户还没按赞
    private String comment_count;// Number of comments 评论总数
    private String love_count;// Number of love 赞总人数
    private String photo_count;// Number of photo 照片总数
    private String post_date_info;

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

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
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

    public void setPost_date_info(String post_date_info) {
        this.post_date_info = post_date_info;
    }
//            "group_id": "1319",
//           "group_owner_id": "47",
//            "group_name": "今天真好啊 :)",
//            "content_id": "4585",
//            "content_group_id": "4585",
//            "content_creator_id": "1",
//            "content_creation_date": " 2015-01-26 16:44:45",
//            "content_creation_timestamp ": "1422290685",
//            "text_description": null,
//            "photo_caption": null,
//            "photo_multiple": null
//            "file_id": null,
//            "loc_latitude": "3.126298",
//            "loc_longitude": "101.644783",
//            "loc_name": " Eastin Hotel",
//            "loc_caption": "",
//            "loc_type": "",
//            "sticker_group_path": null,
//            "sticker_name": null,
//            "sticker_type": null,
//            "user_id": "1",
//            "user_given_name": "Jolin Tay",
//            "love_id": null,
//
//           "comment_count": "0",
//            "love_count": "0",
//            "photo_count": "0",
//            "post_date_info": "{\"day\":\"1\",\"month\":\"0\",\"year\":\"0\",\"hour\":\"3\",\"min\":\"46\",\"sec\":\"4\"}"
}
