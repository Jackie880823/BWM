package com.bondwithme.BondWithMe.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import java.io.Serializable;

/**
 * Created by wing on 15/1/23.
 */
public class UserEntity implements Serializable {
    public static final String EXTRA_MEMBER_ID = "member_id";
    public static final String EXTRA_GROUP_ID = "groupId";
    public static final String EXTRA_GROUP_NAME = "groupName";

    /**实体关系UserEntity为测试用，可以到UserEntity注释看反引用关系写法*/
//	@ForeignCollectionField(eager = true,orderColumnName="date",orderAscending=false)
	@ForeignCollectionField(eager = true,orderAscending=false)
	private ForeignCollection<LocalStickerInfo> stickers;

    private String user_id;
    private String user_given_name;
    private String group_owner_id;

    private String user_gender;
    private String user_default_group;
    private String user_dob;
    private String user_email;
    private String sys_gender;
    private String user_photo;
    private String user_login_id;
    private String user_latitude;
    private String bondwithme_id;
    private String linked;
    private String user_country_code;
    private String user_tnc_read;
    private String user_fullname;
    private String user_location_name;
    private String user_login_type;
    private String user_surname;
    private String user_status;
    private String user_longitude;
    private String user_active_date;
    private String user_emoticon;
    private String user_phone;
    private String user_password;
    private String owner_user_id;
    private String user_creation_date;
    /**
     * 0 not friend, 1 friend
     */
    private String friend;

    private String fam_nickname;
    private String tree_type;
    private String tree_type_name;
    private String miss;
    private String fam_accept_flag;
    private String group_new_post;
    private String group_member_response;

//    private AppTokenEntity token;

    /*chat*/
    private String group_id;

    /*member(Message Main)*/
    private String unread;
    private String group_active_date;

    /*group member(Message Main)*/
    private String content_id;
    private String content_creation_date;

    /*add member*/
    private String memberAcceptFlag;//member accept user flag
    private String userAcceptFlag;//user accept member flag

    private String join_group;

    private String added_flag;

    private String own_flag;

    private String profile_url_ori;
    private String profile_url;

    public String getJoin_group() {
        return join_group;
    }

    public void setJoin_group(String join_group) {
        this.join_group = join_group;
    }

    public String getAdded_flag() {
        return added_flag;
    }

    public void setAdded_flag(String added_flag) {
        this.added_flag = added_flag;
    }

    private String dofeel_code;
    /*Update Profile Details */

    public String getDofeel_code() {
        return dofeel_code;
    }

    public void setDofeel_code(String dofeel_code) {
        this.dofeel_code = dofeel_code;
    }

    private String dis_bondwithme_id;

    public String getDis_bondwithme_id() {
        return dis_bondwithme_id;
    }

    public void setDis_bondwithme_id(String dis_bondwithme_id) {
        this.dis_bondwithme_id = dis_bondwithme_id;
    }

    public String getMemberAcceptFlag() {
        return memberAcceptFlag;
    }

    public void setMemberAcceptFlag(String memberAcceptFlag) {
        this.memberAcceptFlag = memberAcceptFlag;
    }

    public String getUserAcceptFlag() {
        return userAcceptFlag;
    }

    public void setUserAcceptFlag(String userAcceptFlag) {
        this.userAcceptFlag = userAcceptFlag;
    }

    public String getContent_creation_date() {
        return content_creation_date;
    }

    public void setContent_creation_date(String content_creation_date) {
        this.content_creation_date = content_creation_date;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getGroup_active_date() {
        return group_active_date;
    }

    public void setGroup_active_date(String group_active_date) {
        this.group_active_date = group_active_date;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
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

    public String getGroup_owner_id() {
        return group_owner_id;
    }

    public void setGroup_owner_id(String group_owner_id) {

        this.group_owner_id = group_owner_id;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_default_group() {
        return user_default_group;
    }

    public void setUser_default_group(String user_default_group) {
        this.user_default_group = user_default_group;
    }

    public String getUser_dob() {
        return user_dob;
    }

    public void setUser_dob(String user_dob) {
        this.user_dob = user_dob;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getSys_gender() {
        return sys_gender;
    }

    public void setSys_gender(String sys_gender) {
        this.sys_gender = sys_gender;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_login_id() {
        return user_login_id;
    }

    public void setUser_login_id(String user_login_id) {
        this.user_login_id = user_login_id;
    }

    public String getUser_latitude() {
        return user_latitude;
    }

    public void setUser_latitude(String user_latitude) {
        this.user_latitude = user_latitude;
    }

    public String getBondwithme_id() {
        return bondwithme_id;
    }

    public void setBondwithme_id(String bondwithme_id) {
        this.bondwithme_id = bondwithme_id;
    }

    public String getLinked() {
        return linked;
    }

    public void setLinked(String linked) {
        this.linked = linked;
    }

    public String getUser_country_code() {
        return user_country_code;
    }

    public void setUser_country_code(String user_country_code) {
        this.user_country_code = user_country_code;
    }

    public String getUser_tnc_read() {
        return user_tnc_read;
    }

    public void setUser_tnc_read(String user_tnc_read) {
        this.user_tnc_read = user_tnc_read;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getUser_location_name() {
        return user_location_name;
    }

    public void setUser_location_name(String user_location_name) {
        this.user_location_name = user_location_name;
    }

    public String getUser_login_type() {
        return user_login_type;
    }

    public void setUser_login_type(String user_login_type) {
        this.user_login_type = user_login_type;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_longitude() {
        return user_longitude;
    }

    public void setUser_longitude(String user_longitude) {
        this.user_longitude = user_longitude;
    }

    public String getUser_active_date() {
        return user_active_date;
    }

    public void setUser_active_date(String user_active_date) {
        this.user_active_date = user_active_date;
    }

    public String getUser_emoticon() {
        return user_emoticon;
    }

    public void setUser_emoticon(String user_emoticon) {
        this.user_emoticon = user_emoticon;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getOwner_user_id() {
        return owner_user_id;
    }

    public void setOwner_user_id(String owner_user_id) {
        this.owner_user_id = owner_user_id;
    }

    public String getUser_creation_date() {
        return user_creation_date;
    }

    public void setUser_creation_date(String user_creation_date) {
        this.user_creation_date = user_creation_date;
    }

    public String getGroup_member_response() {
        return group_member_response;
    }

    public void setGroup_member_response(String group_member_response) {
        this.group_member_response = group_member_response;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getOwn_flag() {
        return own_flag;
    }

    public void setOwn_flag(String own_flag) {
        this.own_flag = own_flag;
    }

    //    public AppTokenEntity getToken() {
//        return token;
//    }
//
//    public void setToken(AppTokenEntity token) {
//        this.token = token;
//    }


    public ForeignCollection<LocalStickerInfo> getStickers() {
        return stickers;
    }

    public void setStickers(ForeignCollection<LocalStickerInfo> stickers) {
        this.stickers = stickers;
    }

    public String getProfile_url_ori() {
        return profile_url_ori;
    }

    public void setProfile_url_ori(String profile_url_ori) {
        this.profile_url_ori = profile_url_ori;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }
}
