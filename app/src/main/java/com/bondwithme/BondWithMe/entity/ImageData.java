package com.bondwithme.BondWithMe.entity;

import android.net.Uri;

/**
 * Created by Jackie on 7/29/15.
 * @author Jackie
 * @version 1.0
 */
public class ImageData {
    public final static String USE_UNIVERSAL = "USE_TO_UNIVERSAL";
    private final Uri contentUri;
    private final Uri pathUri;

    public ImageData(Uri contentUri, Uri pathUri) {
        this.contentUri = contentUri;
        this.pathUri = pathUri;
    }

    public final Uri getContentUri() {
        return contentUri;
    }

    public final Uri getPathUri() {
        return pathUri;
    }
}
