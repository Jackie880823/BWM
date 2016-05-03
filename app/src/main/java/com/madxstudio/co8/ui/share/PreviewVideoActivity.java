package com.madxstudio.co8.ui.share;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.DownloadRequest;
import com.google.common.io.Files;
import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.widget.NumberProgressBar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 浏览视频的{@link Activity}传入{@value #CONTENT_CREATOR_ID}和{@value #VIDEO_FILENAME}对应的值来获取
 * 网络上的视频资源
 * <br>Created by Jackie on 8/14/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class PreviewVideoActivity extends BaseActivity implements MediaPlayer.OnPreparedListener {

    private static final String TAG = PreviewVideoActivity.class.getSimpleName();

    /**
     * 隐式启动本{@link Activity}的Action
     */
    public static final String ACTION_PREVIEW_VIDEO_ACTIVITY = "com.madxstudio.co8.ui.PreviewVideoActivity";
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

    /**
     * 视频下载进度提示
     */
    private NumberProgressBar pbDownload;

    /**
     * 下载视频的请求
     */
    private DownloadRequest request;

    private String target;

    @Override
    public int getLayout() {
        return R.layout.activity_preview_video;
    }

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {

    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {

    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {
        popUpMenu();
    }

    @Override
    public void initView() {
        videoView = getViewById(R.id.preview_video_view);
        pbDownload = getViewById(R.id.download_progress);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.requestFocus();

        videoView.setMediaController(controller);
        videoView.setOnPreparedListener(this);
        videoView.setKeepScreenOn(true);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();

        // 需要新的图
        titleBar.setBackgroundColor(Color.BLACK);
        leftButton.setImageResource(R.drawable.back_press);
        rightButton.setImageResource(R.drawable.option_dots_view);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected Fragment getFragment() {
        return null;
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
            rightButton.setVisibility(View.GONE);
            Uri video = Uri.parse(data.getStringExtra(EXTRA_VIDEO_URI));
            if (video != null && !Uri.EMPTY.equals(video)) {
                videoView.setVideoURI(video);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView.isPlaying()) {
            videoView.pause();
            videoView.stopPlayback();
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void popUpMenu() {
        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(this, rightButton);
        popupMenu.getMenuInflater().inflate(R.menu.preview_video_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_save_video:
                        if (!TextUtils.isEmpty(target)) {
                            try {
                                File saveFile = FileUtil.saveVideoFile(PreviewVideoActivity.this, true);
                                LogUtil.i(TAG, "save video to " + saveFile.getPath());
                                Files.copy(new File(target), saveFile);

                                // 通知媒体库更新文件
                                Intent updateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(saveFile));
                                updateIntent.setData(Uri.fromFile(saveFile));
                                PreviewVideoActivity.this.sendBroadcast(updateIntent);

                                MessageUtil.getInstance().showShortToast(PreviewVideoActivity.this.getString(R.string.saved_to_path) + saveFile.getPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                return true;
            }
        });

        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        popupMenu.show();
    }
}
