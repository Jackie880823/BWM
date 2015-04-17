package com.madx.bwm.entity;

import java.io.Serializable;

/**
 * Created by wing on 15/4/10.
 */
public class AppTokenEntity implements Serializable {

    private String user_token;
    private String auth_id;


    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getAuth_id() {
        return auth_id;
    }

    public void setAuth_id(String auth_id) {
        this.auth_id = auth_id;
    }
}
