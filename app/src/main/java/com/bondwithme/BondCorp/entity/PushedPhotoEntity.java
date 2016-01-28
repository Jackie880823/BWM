package com.bondwithme.BondCorp.entity;

import java.io.Serializable;

/**
 * Created 10/24/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class PushedPhotoEntity implements Serializable {
    private String photo_id;
    private String photo_caption;

    public String getPhoto_caption() {
        return photo_caption;
    }

    public void setPhoto_caption(String photo_caption) {
        this.photo_caption = photo_caption;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }
}
