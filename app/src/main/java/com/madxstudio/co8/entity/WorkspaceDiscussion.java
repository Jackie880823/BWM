/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Workspace 的评论封装类
 * Created 16/9/7.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceDiscussion implements Serializable {
    /**
     * "comment_id": "966", // Comment ID
     * "content_group_id": "1410", // Post content group ID
     * "comment_owner_id": "175",  // Comment creator ID
     * "comment_content": "vvaaaa",  // Comment content
     * "comment_creation_date": "2014-06-06 11:16:57", //Comment create date
     * "comment_creation_time": "140208359", //Comment creation time
     * "comment_edited": "0", // 判断有没有被编辑过，0-原文，1-编辑过
     * "group_id": "142", // Group ID
     * "content_id": "1410", //  Post content ID
     * "sticker_code": "GwynPenguinFamily_B_5.png"，// 表情代码
     * "user_id": "175", // Comment creator ID
     * "user_given_name": "Christ Ng", //Comment creator name
     * "love_id": null, // Viewer love ID, if null means viewer havent love the comment
     * "love_count": "0", // Number of love
     * "file_id": "1430750914", // 照片file_id，若没有是null
     * "stickers_url": "http://bwm88.bondwith.me/stickers/GwynPenguinFamily /B/5.png?Policy=ew0…
     * ", //
     * 大表情链接
     * "photo_url_l": "http://bwm88.bondwith.me/photo/175/1430750914_l.jpg?Policy=ew…", //
     * 400*400 照片链接
     * "photo_url_m": "http://bwm88.bondwith.me/photo/175/1430750914_m.jpg?Policy=ew…",// 200*200
     * 照片链接
     * "photo_url_ori": "http://bwm88.bondwith.me/photo/175/1430750914_ori.jpg?Policy=ew…",// 原图照片链接
     */
    private String comment_id;
    private String content_group_id;
    private String comment_owner_id;
    private String comment_content;
    private String comment_creation_date;
    private String comment_creation_time;
    private String comment_edited;
    private String group_id;
    private String content_id;
    private String sticker_code;
    private String user_id;
    private String user_given_name;
    private String love_id;
    private String love_count;
    private String file_id;
    private String stickers_url;
    private String photo_url_l;
    private String photo_url_m;
    private String photo_url_ori;

    public String getPhoto_url_ori() {
        return photo_url_ori;
    }

    public void setPhoto_url_ori(String photo_url_ori) {
        this.photo_url_ori = photo_url_ori;
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

    public String getComment_edited() {
        return comment_edited;
    }

    public void setComment_edited(String comment_edited) {
        this.comment_edited = comment_edited;
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

    public String getSticker_code() {
        return sticker_code;
    }

    public void setSticker_code(String sticker_code) {
        this.sticker_code = sticker_code;
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

    public String getStickers_url() {
        return stickers_url;
    }

    public void setStickers_url(String stickers_url) {
        this.stickers_url = stickers_url;
    }

    public String getPhoto_url_l() {
        return photo_url_l;
    }

    public void setPhoto_url_l(String photo_url_l) {
        this.photo_url_l = photo_url_l;
    }

    public String getPhoto_url_m() {
        return photo_url_m;
    }

    public void setPhoto_url_m(String photo_url_m) {
        this.photo_url_m = photo_url_m;
    }

    @Override
    public String toString() {
        return "WorkspaceDiscussion{" +
                "comment_id='" + comment_id + '\'' +
                ", content_group_id='" + content_group_id + '\'' +
                ", comment_owner_id='" + comment_owner_id + '\'' +
                ", comment_content='" + comment_content + '\'' +
                ", comment_creation_date='" + comment_creation_date + '\'' +
                ", comment_creation_time='" + comment_creation_time + '\'' +
                ", comment_edited='" + comment_edited + '\'' +
                ", group_id='" + group_id + '\'' +
                ", content_id='" + content_id + '\'' +
                ", sticker_code='" + sticker_code + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_given_name='" + user_given_name + '\'' +
                ", love_id='" + love_id + '\'' +
                ", love_count='" + love_count + '\'' +
                ", file_id='" + file_id + '\'' +
                ", stickers_url='" + stickers_url + '\'' +
                ", photo_url_l='" + photo_url_l + '\'' +
                ", photo_url_m='" + photo_url_m + '\'' +
                ", photo_url_ori='" + photo_url_ori + '\'' +
                '}';
    }
}
