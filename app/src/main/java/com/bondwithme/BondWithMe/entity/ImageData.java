package com.bondwithme.BondWithMe.entity;

import android.net.Uri;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Created by Jackie on 7/29/15.
 * @author Jackie
 * @version 1.0
 */
public class ImageData {
    public final static String USE_UNIVERSAL = "USE_TO_UNIVERSAL";
    private final Uri contentUri;
    private final String path;

    public ImageData(Uri contentUri, String path) {
        this.contentUri = contentUri;
        this.path = ImageDownloader.Scheme.FILE.wrap(path);
    }

    public final Uri getContentUri() {
        return contentUri;
    }

    public final String getPath() {
        return path;
    }
}
