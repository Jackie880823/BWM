package com.bondwithme.BondWithMe.entity;

import android.content.Intent;
import android.net.Uri;

import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosFragment;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * <br>多媒体数据封装类，用于封装{@link SelectPhotosFragment}中从手机中获取的多媒体数据。
 * 其中{@link #getType()}值为{@link #TYPE_IMAGE}封装的为图片数据；为{@link #TYPE_VIDEO}封装的为视频数据
 * <br>Created by Jackie on 7/29/15.
 *
 * @author Jackie
 * @version 2.0
 */
public class MediaData {

    /**
     * 隐式启动录制视频的Activity
     */
    public static final String ACTION_RECORDER_VIDEO = "com.bondwithme.BondWithMe.RECORDER_VIDEO";


    public static final String EXTRA_MEDIA_TYPE = "result_media_type";
    public static final String EXTRA_VIDEO_DURATION = "duration";

    /**
     * <br>跳转至{@link SelectPhotosActivity}用{@link Intent#putExtra(String, boolean)}传递用于判断返回数据是否适
     * 用于第三方Universal Image Loader库来加载图片的第一个参数
     */
    public final static String EXTRA_USE_UNIVERSAL = "USE_TO_UNIVERSAL";

    public final static String EXTRA_RETURN_DATA = "return-data";

    /**
     * <br>跳转至{@link SelectPhotosActivity}用{@link Intent#putExtra(String, boolean)}传递用于判断是否可以返回视频数据的第一个参数
     */
    public final static String USE_VIDEO_AVAILABLE = "USE_VIDEO_AVAILABLE";

    /**
     * 封装的数据是图片类型的{@link Uri}和文件路径
     */
    public final static String TYPE_IMAGE = "URI_TYPE_IMAGE";

    /**
     * 封装的数据是视频类型的{@link Uri}和文件路径
     */
    public final static String TYPE_VIDEO = "URI_TYPE_VIDEO";

    public final static long MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 手机本地储存的多媒体数据的{@link Uri}
     */
    private final Uri contentUri;

    /**
     * 手机本地储存的多媒体数据的存储路径
     */
    private final String path;

    /**
     * 标识封装多媒体数据的类型:{@link #TYPE_VIDEO}、{@link #TYPE_VIDEO}
     */
    private final String type;

    private final long duration;

    /**
     * 封装{@link SelectPhotosActivity}显示的多媒体数据
     *  @param contentUri 储存在系统多媒体数据库的{@link Uri}
     * @param path       多媒体文件在手机本地的文件目录
     * @param type       多媒体数据类型标识:{@link #TYPE_VIDEO}、{@link #TYPE_VIDEO}
     * @param duration   视频的持续时长
     */
    public MediaData(Uri contentUri, String path, String type, long duration) {
        this.contentUri = contentUri;
        this.duration = duration;
        this.path = ImageDownloader.Scheme.FILE.wrap(path);
        this.type = type;
    }

    /**
     * 返回当前封装的多媒体数据在系统多媒体数据库中储存的{@link Uri}
     *
     * @return 显示此多媒体的控件可用此得到缩略图
     */
    public final Uri getContentUri() {
        return contentUri;
    }

    /**
     * 返回多媒体在手机中的路径
     *
     * @return 显示此多媒体的控件可用此得到缩略图
     */
    public final String getPath() {
        return path;
    }

    /**
     * 返回多媒体数据类型在此封装类中的标识: {@link #TYPE_VIDEO}、{@link #TYPE_VIDEO}
     *
     * @return 可根据此值来区别处理各类型的多媒体数据
     */
    public String getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }
}
