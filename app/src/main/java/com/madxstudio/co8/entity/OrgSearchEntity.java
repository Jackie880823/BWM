package com.madxstudio.co8.entity;

import java.io.Serializable;

/**
 * Created by quankun on 16/3/29.
 */
public class OrgSearchEntity implements Serializable {
    private String id;
    private String name;

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
}
