package com.bondwithme.BondCorp.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wing on 15/4/9.
 */
    public class ContactDetailEntity implements Serializable {

        private String id;
        private String displayName;
        private List<String> phoneNumbers;
        private List<String> emails;

        private String user_id;
        private String dis_bondwithme_id;
        private String user_login_id;
        private String memberType;

    public String getUser_id() {
        return user_id;
    }

    public String getDis_bondwithme_id() {
        return dis_bondwithme_id;
    }

    public String getUser_login_id() {
        return user_login_id;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
