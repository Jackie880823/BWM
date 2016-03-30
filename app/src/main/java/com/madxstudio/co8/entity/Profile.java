package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created 16/3/29.
 *
 * @author Jackie
 * @version 1.0
 */
public class Profile implements Serializable {
    private String id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String country;
    private String cover_id;
    private String demo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCover_id() {
        return cover_id;
    }

    public void setCover_id(String cover_id) {
        this.cover_id = cover_id;
    }

    public String getDemo() {
        return demo;
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }
}
