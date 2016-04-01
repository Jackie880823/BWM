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
    private List<Admin> admin;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Admin> getAdmin() {
        return admin;
    }

    public void setAdmin(List<Admin> admin) {
        this.admin = admin;
    }
}
