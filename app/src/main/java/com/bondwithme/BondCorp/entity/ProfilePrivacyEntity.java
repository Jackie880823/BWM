package com.bondwithme.BondCorp.entity;

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
    private String auto_acp_all;
    private String auto_acp_supervisor;
    private String auto_acp_subordinate;
    private String auto_acp_colleague;
    private String auto_acp_supplier;
    private String auto_acp_customer;
    private String dob_alert_1;
    private String dob_alert_3;
    private String dob_alert_7;
    private String dob_alert_30;
    private String group_add;

    public ProfilePrivacyEntity() {

    }

    public ProfilePrivacyEntity(String dob_date, String dob_year, String gender, String email, String phone, String location) {
        this.dob_date = dob_date;
        this.dob_year = dob_year;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }

    public String getAuto_acp_all() {
        return auto_acp_all;
    }

    public void setAuto_acp_all(String auto_acp_all) {
        this.auto_acp_all = auto_acp_all;
    }

    public String getAuto_acp_supervisor() {
        return auto_acp_supervisor;
    }

    public void setAuto_acp_supervisor(String auto_acp_supervisor) {
        this.auto_acp_supervisor = auto_acp_supervisor;
    }

    public String getAuto_acp_subordinate() {
        return auto_acp_subordinate;
    }

    public void setAuto_acp_subordinate(String auto_acp_subordinate) {
        this.auto_acp_subordinate = auto_acp_subordinate;
    }

    public String getAuto_acp_colleague() {
        return auto_acp_colleague;
    }

    public void setAuto_acp_colleague(String auto_acp_colleague) {
        this.auto_acp_colleague = auto_acp_colleague;
    }

    public String getAuto_acp_supplier() {
        return auto_acp_supplier;
    }

    public void setAuto_acp_supplier(String auto_acp_supplier) {
        this.auto_acp_supplier = auto_acp_supplier;
    }

    public String getAuto_acp_customer() {
        return auto_acp_customer;
    }

    public void setAuto_acp_customer(String auto_acp_customer) {
        this.auto_acp_customer = auto_acp_customer;
    }

    public String getDob_alert_1() {
        return dob_alert_1;
    }

    public void setDob_alert_1(String dob_alert_1) {
        this.dob_alert_1 = dob_alert_1;
    }

    public String getDob_alert_3() {
        return dob_alert_3;
    }

    public void setDob_alert_3(String dob_alert_3) {
        this.dob_alert_3 = dob_alert_3;
    }

    public String getDob_alert_7() {
        return dob_alert_7;
    }

    public void setDob_alert_7(String dob_alert_7) {
        this.dob_alert_7 = dob_alert_7;
    }

    public String getDob_alert_30() {
        return dob_alert_30;
    }

    public void setDob_alert_30(String dob_alert_30) {
        this.dob_alert_30 = dob_alert_30;
    }

    public String getGroup_add() {
        return group_add;
    }

    public void setGroup_add(String group_add) {
        this.group_add = group_add;
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

    @Override
    public String toString() {
        return "ProfilePrivacyEntity{" +
                "dob_date='" + dob_date + '\'' +
                ", dob_year='" + dob_year + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
