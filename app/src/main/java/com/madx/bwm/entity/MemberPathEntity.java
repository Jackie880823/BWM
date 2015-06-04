package com.madx.bwm.entity;

import java.io.Serializable;

/**
 * Created by christepherzhang on 15/4/10.
 */
public class MemberPathEntity implements Serializable {

    private String user_id;
    private String member_id;
    private String relationship;
    private String user_fullname;
    private String member_fullname;
    private String member_nickname;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getMember_fullname() {
        return member_fullname;
    }

    public void setMember_fullname(String member_fullname) {
        this.member_fullname = member_fullname;
    }

    public String getMember_nickname() {
        return member_nickname;
    }

    public void setMember_nickname(String member_nickname) {
        this.member_nickname = member_nickname;
    }
}
