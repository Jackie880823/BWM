package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;

/**
 * 浏览视频的{@link Activity}传入{@value #CONTENT_CREATION_ID}和{@value #VIDEO_FILENAME}对应的值来获取
 * 网络上的视频资源
 * <br>Created by Jackie on 8/14/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class PreviewVideoActivity extends Activity implements MediaPlayer.OnPreparedListener {
    /**
     * 隐式启动本{@link Activity}的Action
     */
    public static final String ACTION_PREVIEW_VIDEO_ACTIVITY = "com.bondwithme.BondWithMe.ui.PreviewVideoActivity";
    /**
     * 创建\上传本视频的用户id对应{@link WallEntity#getContent_creator_id()} 或 {@link UserEntity#getUser_id()}
     */
    public static final String CONTENT_CREATION_ID = "content_creation_id";

    /**
     * 网络请求视频信息很到的视频名称
     */
    public static final String VIDEO_FILENAME = "video_filename";
    private VideoView videoView;

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

        videoView = new VideoView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(params);
        setContentView(videoView);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.requestFocus();

        videoView.setMediaController(controller);
        videoView.setOnPreparedListener(this);
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
        String createdContentId = getIntent().getStringExtra(CONTENT_CREATION_ID);
        String fileName = getIntent().getStringExtra(VIDEO_FILENAME);
        if(TextUtils.isEmpty(fileName)) {
            String url = String.format(Constant.API_GET_VIDEO, createdContentId, fileName);
            videoView.setVideoURI(Uri.parse(url));
        }
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
