package com.bondwithme.BondCorp.entity;

/**
 * Created by wing on 15/1/23.
 */
public class PhotoEntity extends PushedPhotoEntity{

    private String content_id;
    private String file_id;
    private String photo_multipe;
    private String user_id;

    private String creation_date;



    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getPhoto_multipe() {
        return photo_multipe;
    }

    public void setPhoto_multipe(String photo_multipe) {
        this.photo_multipe = photo_multipe;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }
}



