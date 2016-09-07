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
 * Created 16/9/7.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceEntity implements Serializable {

    /**
     * group_id : 120
     * group_owner_id : 1
     * content_id : 4625
     * content_type : post
     * content_group_id : 4625
     * content_group_public : 1
     * content_creator_id : 1
     * content_creation_date : 2015-01-28 11:16:57
     * content_creation_timestamp  : 1422443817
     * content_title  : This is title
     * text_description : This is description
     * user_id : 1
     * user_given_name : Jolin Tay
     * love_id : null
     * comment_count : 0
     * love_count : 0
     * attachment_count : 0
     * content_member_count : 4
     * to_do_count : 0
     * is_invited : 1
     * content_cover : 1473134985681556
     */

    private String group_id;
    private String group_owner_id;
    private String content_id;
    private String content_type;
    private String content_group_id;
    private String content_group_public;
    private String content_creator_id;
    private String content_creation_date;
    private String content_creation_timestamp;
    private String content_title;
    private String text_description;
    private String user_id;
    private String user_given_name;
    private Object love_id;
    private String comment_count;
    private String love_count;
    private String attachment_count;
    private String content_member_count;
    private String to_do_count;
    private String is_invited;
    private String content_cover;

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

    public Object getLove_id() {
        return love_id;
    }

    public void setLove_id(Object love_id) {
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

    public String getAttachment_count() {
        return attachment_count;
    }

    public void setAttachment_count(String attachment_count) {
        this.attachment_count = attachment_count;
    }

    public String getContent_member_count() {
        return content_member_count;
    }

    public void setContent_member_count(String content_member_count) {
        this.content_member_count = content_member_count;
    }

    public String getTo_do_count() {
        return to_do_count;
    }

    public void setTo_do_count(String to_do_count) {
        this.to_do_count = to_do_count;
    }

    public String getIs_invited() {
        return is_invited;
    }

    public void setIs_invited(String is_invited) {
        this.is_invited = is_invited;
    }

    public String getContent_cover() {
        return content_cover;
    }

    public void setContent_cover(String content_cover) {
        this.content_cover = content_cover;
    }
}
