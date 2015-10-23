package com.bondwithme.BondWithMe.entity;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created 10/21/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class DiaryPhotoEntity implements Serializable {
    private Uri uri;
    private String photoCaption = "";

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPhotoCaption() {
        return photoCaption;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }
}
