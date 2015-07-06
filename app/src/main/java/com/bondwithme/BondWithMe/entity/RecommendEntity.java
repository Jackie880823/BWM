package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/4/9.
 */
public class RecommendEntity implements Serializable {
    private String user_id;
    private String bondwithme_id;
    private String user_surname;
    private String user_given_name;
    private String user_fullname;
    private String user_active_date;
    private String tree_type;
    private String user_recommend;
    private String user_recom_rel;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBondwithme_id() {
        return bondwithme_id;
    }

    public void setBondwithme_id(String bondwithme_id) {
        this.bondwithme_id = bondwithme_id;
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

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getUser_active_date() {
        return user_active_date;
    }

    public void setUser_active_date(String user_active_date) {
        this.user_active_date = user_active_date;
    }

    public String getTree_type() {
        return tree_type;
    }

    public void setTree_type(String tree_type) {
        this.tree_type = tree_type;
    }

    public String getUser_recommend() {
        return user_recommend;
    }

    public void setUser_recommend(String user_recommend) {
        this.user_recommend = user_recommend;
    }

    public String getUser_recom_rel() {
        return user_recom_rel;
    }

    public void setUser_recom_rel(String user_recom_rel) {
        this.user_recom_rel = user_recom_rel;
    }
}
