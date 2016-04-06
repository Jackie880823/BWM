package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by quankun on 15/5/12.
 */
public class OrgMemberEntity implements Serializable {
    private String user_id;//user_id
    private String user_surname; //用户姓
    private String user_given_name;//用户名字
    private String department; //用户部门
    private String position; //用户职位
    private String organisation;
    private String tree_type;
    private String tree_type_name;
    private String goodjob;
    private String fam_accept_flag;
    private String group_id;
    private String group_new_post;
    private String added_flag;
    private String admin_flag;

    public String getAdmin_flag() {
        return admin_flag;
    }

    public void setAdmin_flag(String admin_flag) {
        this.admin_flag = admin_flag;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getTree_type() {
        return tree_type;
    }

    public void setTree_type(String tree_type) {
        this.tree_type = tree_type;
    }

    public String getTree_type_name() {
        return tree_type_name;
    }

    public void setTree_type_name(String tree_type_name) {
        this.tree_type_name = tree_type_name;
    }

    public String getGoodjob() {
        return goodjob;
    }

    public void setGoodjob(String goodjob) {
        this.goodjob = goodjob;
    }

    public String getFam_accept_flag() {
        return fam_accept_flag;
    }

    public void setFam_accept_flag(String fam_accept_flag) {
        this.fam_accept_flag = fam_accept_flag;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_new_post() {
        return group_new_post;
    }

    public void setGroup_new_post(String group_new_post) {
        this.group_new_post = group_new_post;
    }

    public String getAdded_flag() {
        return added_flag;
    }

    public void setAdded_flag(String added_flag) {
        this.added_flag = added_flag;
    }
}
