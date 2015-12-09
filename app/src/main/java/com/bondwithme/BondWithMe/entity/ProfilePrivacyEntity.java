package com.bondwithme.BondWithMe.entity;

/**
 * Created by heweidong on 15/12/7.
 */
public class ProfilePrivacyEntity {
    private String dob_date;
    private String dob_year;
    private String gender;
    private String email;
    private String phone;
    private String location;


    public ProfilePrivacyEntity(String dob_date, String dob_year, String gender, String email, String phone, String location) {
        this.dob_date = dob_date;
        this.dob_year = dob_year;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }

    public String getDob_date() {
        return dob_date;
    }

    public void setDob_date(String dob_date) {
        this.dob_date = dob_date;
    }

    public String getDob_year() {
        return dob_year;
    }

    public void setDob_year(String dob_year) {
        this.dob_year = dob_year;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
