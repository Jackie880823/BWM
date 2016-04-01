package com.madxstudio.co8.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created 16/3/30.
 *
 * @author Jackie
 * @version 1.0
 */
public class OrganisationDetail implements Serializable {
    private Profile profile;
    private List<UserEntity> admin;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<UserEntity> getAdmin() {
        return admin;
    }

    public void setAdmin(List<UserEntity> admin) {
        this.admin = admin;
    }
}
