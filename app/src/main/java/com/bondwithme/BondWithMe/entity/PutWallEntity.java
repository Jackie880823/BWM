package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created 10/24/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class PutWallEntity implements Serializable {
    private String text_description;
    private String loc_latitude;
    private String loc_longitude;
    private String loc_name;
    private String loc_type;
    private String user_id;
    private String dofeel_code;
    private String photo_max;
    private String new_video;
    private String new_photo;
    private List<PushedPhotoEntity> edit_photo;
    private List<String> delete_photo;
    private List<String> delete_video;
    private List<String> tag_member;
    private List<String> tag_group;

    public String getPhoto_max() {
        return photo_max;
    }

    public void setPhoto_max(String photo_max) {
        this.photo_max = photo_max;
    }

    public String getText_description() {
        return text_description;
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
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

    public String getLoc_type() {
        return loc_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDofeel_code() {
        return dofeel_code;
    }

    public void setDofeel_code(String dofeel_code) {
        this.dofeel_code = dofeel_code;
    }

    public String getNew_video() {
        return new_video;
    }

    public void setNew_video(String new_video) {
        this.new_video = new_video;
    }

    public String getNew_photo() {
        return new_photo;
    }

    public void setNew_photo(String new_photo) {
        this.new_photo = new_photo;
    }

    public List<PushedPhotoEntity> getEdit_photo() {
        return edit_photo;
    }

    public void setEdit_photo(List<PushedPhotoEntity> edit_photo) {
        this.edit_photo = edit_photo;
    }

    public List<String> getDelete_photo() {
        return delete_photo;
    }

    public void setDelete_photo(List<String> delete_photo) {
        this.delete_photo = delete_photo;
    }

    public List<String> getDelete_video() {
        return delete_video;
    }

    public void setDelete_video(List<String> delete_video) {
        this.delete_video = delete_video;
    }

    public List<String> getTag_member() {
        return tag_member;
    }

    public void setTag_member(List<String> tag_member) {
        this.tag_member = tag_member;
    }

    public List<String> getTag_group() {
        return tag_group;
    }

    public void setTag_group(List<String> tag_group) {
        this.tag_group = tag_group;
    }
}
