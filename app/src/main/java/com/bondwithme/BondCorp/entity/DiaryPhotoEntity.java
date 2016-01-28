package com.bondwithme.BondCorp.entity;

import android.net.Uri;

/**
 * Created 10/21/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class DiaryPhotoEntity extends PushedPhotoEntity {
    private Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
