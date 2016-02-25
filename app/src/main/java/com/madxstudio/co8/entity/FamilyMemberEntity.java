package com.madxstudio.co8.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by quankun on 15/5/12.
 */
public class FamilyMemberEntity implements Serializable {

    // add by Jackie 2015/08/19
    /**
     * 用户id
     */
    private String user_id;

    /**
     * 用户名
     */
    private String user_given_name;

    /**
     * 关系类型
     */
    private String relationship_type;

    /**
     * 关系名
     */
    private String tree_type_name;

    private String position;

    /**
     * 生日
     */
    private String DOB;
    // add end by Jackie 2015/08/19

    private ArrayList<FamilyMemberEntity> spouse;
    private String bondwithme_id;//": "80000492",
    private String user_status; // member status
    private String fam_nickname; // member nickname , show nickname as name if nickname not empty
    private String tree_type;//": "children",
    private String miss; // miss if null mean no miss
    private String group_id;//personal chat group id
    private String fam_accept_flag;//1 – member, 0 – awaiting for approval from member
    private String group_new_post;//
    private String added_flag;//new flag to determine user add flow
    private String dofeel_code;//心情代码

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    // add by Jackie 2015/08/19
    public String getRelationship_type() {
        return relationship_type;
    }

    public void setRelationship_type(String relationship_type) {
        this.relationship_type = relationship_type;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public ArrayList<FamilyMemberEntity> getSpouse() {
        return spouse;
    }

    public void setSpouse(ArrayList<FamilyMemberEntity> spouse) {
        this.spouse = spouse;
    }
    // add end by Jackie 2015/08/19

    public String getBondwithme_id() {
        return bondwithme_id;
    }

    public void setBondwithme_id(String bondwithme_id) {
        this.bondwithme_id = bondwithme_id;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getFam_nickname() {
        return fam_nickname;
    }

    public void setFam_nickname(String fam_nickname) {
        this.fam_nickname = fam_nickname;
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

    public String getMiss() {
        return miss;
    }

    public void setMiss(String miss) {
        this.miss = miss;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getFam_accept_flag() {
        return fam_accept_flag;
    }

    public void setFam_accept_flag(String fam_accept_flag) {
        this.fam_accept_flag = fam_accept_flag;
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

    public String getDofeel_code() {
        return dofeel_code;
    }

    public void setDofeel_code(String dofeel_code) {
        this.dofeel_code = dofeel_code;
    }
}
