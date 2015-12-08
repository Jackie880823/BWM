package com.bondwithme.BondWithMe.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.NumberProgressBar;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * 浏览视频的{@link Activity}传入{@value #CONTENT_CREATOR_ID}和{@value #VIDEO_FILENAME}对应的值来获取
 * 网络上的视频资源
 * <br>Created by Jackie on 8/14/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class PreviewVideoActivity extends Activity implements MediaPlayer.OnPreparedListener {

    private static final String TAG = PreviewVideoActivity.class.getSimpleName();

    /**
     * 隐式启动本{@link Activity}的Action
     */
    public static final String ACTION_PREVIEW_VIDEO_ACTIVITY = "com.bondwithme.BondWithMe.ui.PreviewVideoActivity";
    /**
     * 创建\上传本视频的用户id对应{@link WallEntity#getContent_creator_id()} 或 {@link UserEntity#getUser_id()}
     */
    public static final String CONTENT_CREATOR_ID = "content_creator_id";

    /**
     * 网络请求视频信息很到的视频名称
     */
    public static final String VIDEO_FILENAME = "video_filename";

    /**
     *
     */
    public static final String EXTRA_VIDEO_URI = "video_uri";

    /**
     * 网络请求视频信息很到的视频小图名称
     */
    public static final String VIDEO_THUMBNAIL = "video_thumbnail";

    /**
     * 视频存放路径
     */
    public static final String VIDEO_PATH = FileUtil.getCacheFilePath(App.getContextInstance(), false) + "/Video/";

    /**
     * 播放视频控件，用于播放传入的{@link #CONTENT_CREATOR_ID}和{@link #VIDEO_FILENAME}的值对应在远程服务器中的视频
     *
     * @see VideoView
     */
    private VideoView videoView;

    private ImageView imgSave;

    /**
     * 视频下载进度提示
     */
    private NumberProgressBar pbDownload;

    /**
     * 下载视频的请求
     */
    private DownloadRequest request;

    private String target;

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p/>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_preview_video);

        videoView = (VideoView) findViewById(R.id.preview_video_view);
        pbDownload = (NumberProgressBar) findViewById(R.id.download_progress);
        imgSave = (ImageView) findViewById(R.id.img_save_video);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.requestFocus();

        videoView.setMediaController(controller);
        videoView.setOnPreparedListener(this);
        videoView.setKeepScreenOn(true);
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 启动本Activity传递过来的Activity
        Intent data = getIntent();
        String fileName = data.getStringExtra(VIDEO_FILENAME);
        Log.i(TAG, "onResume& fileName: " + fileName);
        if (!TextUtils.isEmpty(fileName)) {
            String createdContentId = data.getStringExtra(CONTENT_CREATOR_ID);
            String url = String.format(Constant.API_GET_VIDEO, createdContentId, fileName);

            /*******为适应news、rewards中的视频下载，添加如下代码 **********/
            if (createdContentId.equals("for_news_or_rewards")) {
                url = fileName;
                fileName = url.substring(url.lastIndexOf('/') + 1);
                LogUtil.d(TAG, "fileName" + fileName);
            }

            String targetParent = VIDEO_PATH;
            File cacheFile = new File(targetParent);
            boolean canWrite = cacheFile.exists() || cacheFile.mkdir();
            //.....
            target = canWrite ? targetParent + fileName : FileUtil.getCacheFilePath(this, false) + String.format("/%s", fileName);

            downloadVideo(url, target);
        } else {
            pbDownload.setVisibility(View.GONE);
            imgSave.setVisibility(View.GONE);
            Uri video = Uri.parse(data.getStringExtra(EXTRA_VIDEO_URI));
            if (video != null && !Uri.EMPTY.equals(video)) {
                videoView.setVideoURI(video);
            }
        }
    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     * <p/>
     * <p><em>Note: do not count on this method being called as a place for
     * saving data! For example, if an activity is editing data in a content
     * provider, those edits should be committed in either {@link #onPause} or
     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
     * free resources like threads that are associated with an activity, so
     * that a destroyed activity does not leave such things around while the
     * rest of its application is still running.  There are situations where
     * the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to
     * do things that are intended to remain around after the process goes
     * away.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (request != null && !request.isCanceled()) {
            // 下载还在进行，需要停止下载
            request.stopDownload();

        }
    }

    private void downloadVideo(String url, final String target) {
        Log.i(TAG, "downloadVideo& url: " + url + "; target: " + target);
        request = new HttpTools(this).download(App.getContextInstance(), url, target, true, new HttpCallback() {
            @Override
            public void onStart() {
                pbDownload.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                videoView.setVisibility(View.VISIBLE);
                pbDownload.setVisibility(View.GONE);

                videoView.requestFocus();
                videoView.setVideoPath(target);
            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "downloadVideo&onLoading$ onResult: " + response);
            }

            @Override
            public void onError(Exception e) {
                LogUtil.i(TAG, "downloadVideo&onLoading$ onError: ");
                e.printStackTrace();
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
                finish();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {
//                LogUtil.i(TAG, "downloadVideo&onLoading$ count: " + count + "; current: " + current);
                int progress = (int) ((((double) current) / count) * 100);
//                LogUtil.i(TAG, "downloadVideo&onLoading$ progress = " + progress);
                pbDownload.setProgress(progress);
            }


        });
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //        videoView.setBackgroundResource(R.color.transparent_color);
        mp.start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_save_video:
                if (!TextUtils.isEmpty(target)) {
                    try {
                        File saveFile = FileUtil.saveVideoFile(this, true);
                        LogUtil.i(TAG, "save video to " + saveFile.getPath());
                        Files.copy(new File(target), saveFile);
                        MessageUtil.showMessage(this, this.getString(R.string.saved_to_path) + saveFile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
