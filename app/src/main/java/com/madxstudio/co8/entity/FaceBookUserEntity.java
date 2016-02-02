package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by christepherzhang on 15/7/16.
 */
public class FaceBookUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private  String status;
    private String error_message;
    private String email;
    private String token;
    private String firstname;
    private String lastname;
    private String userId;
    private String tibbrUserId;
    private String userName;
    private String middleName;
    private String link;
    private String birthday;
    private String ProfilePictureUri;
    private String gender;
    private String locale;
    private String timezone;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getError_message() {
        return error_message;
    }
    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getTibbrUserId() {
        return tibbrUserId;
    }
    public void setTibbrUserId(String tibbrUserId) {
        this.tibbrUserId = tibbrUserId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getProfilePictureUri() {
        return ProfilePictureUri;
    }
    public void setProfilePictureUri(String profilePictureUri) {
        ProfilePictureUri = profilePictureUri;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public String getTimezone() {
        return timezone;
    }
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "FaceBookUserEntity{" +
                "status='" + status + '\'' +
                ", error_message='" + error_message + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", userId='" + userId + '\'' +
                ", tibbrUserId='" + tibbrUserId + '\'' +
                ", userName='" + userName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", link='" + link + '\'' +
                ", birthday='" + birthday + '\'' +
                ", ProfilePictureUri='" + ProfilePictureUri + '\'' +
                ", gender='" + gender + '\'' +
                ", locale='" + locale + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }


}
