package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wing on 15/4/9.
 */
    public class ContactListEntity implements Serializable {

        private String displayName;
        private List<String> phoneNumbers;
        private List<String> emails;




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

}
