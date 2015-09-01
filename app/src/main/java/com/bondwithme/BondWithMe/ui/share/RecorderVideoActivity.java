/************************************************************
 * * EaseMob CONFIDENTIAL
 * __________________
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * NOTICE: All information contained herein is, and remains
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.bondwithme.BondWithMe.ui.share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 录制视频
 *
 * @author Jackie
 * @version 1.0
 */
public class RecorderVideoActivity extends Activity implements OnClickListener, SurfaceHolder.Callback, OnErrorListener, OnInfoListener {
    private static final String TAG = RecorderVideoActivity.class.getSimpleName();
    private final static String CLASS_LABEL = "RecordActivity";

    private PowerManager.WakeLock mWakeLock;
    /**
     * 开始录制按钮
     */
    private ImageView btnStart;
    /**
     * 停止录制按钮
     */
    private ImageView btnStop;
    /**
     * 录制视频的类
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 显示视频的控件
     */
    private VideoView mVideoView;
    /**
     * 录制的视频路径
     */
    String localPath = "";
    private Camera mCamera;
    /**
     * 预览的宽
     */
    private int previewWidth = 480;
    /**
     * 预览的高
     */
    private int previewHeight = 480;
    private Chronometer chronometer;
    /**
     * {@value CameraInfo#CAMERA_FACING_BACK}是后置摄像头，{@value CameraInfo#CAMERA_FACING_FRONT}是前置摄像头
     */
    private int frontCamera = 0;
    private Button btn_switch;
    Parameters cameraParameters = null;
    private SurfaceHolder mSurfaceHolder;
    int defaultVideoFrameRate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 选择支持半透明模式，在有surfaceview的activity中使用
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.recorder_activity);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        mWakeLock.acquire();
        initViews();
    }

    private void initViews() {
        btn_switch = (Button) findViewById(R.id.switch_btn);
        btn_switch.setOnClickListener(this);
        btn_switch.setVisibility(View.VISIBLE);
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        btnStart = (ImageView) findViewById(R.id.recorder_start);
        btnStop = (ImageView) findViewById(R.id.recorder_stop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        mSurfaceHolder = mVideoView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
    }

    /**
     * 在本Activity的布局文件中返回箭头点击调用此函数，android:onClick="back"
     *
     * @param view 返回箭头视图
     */
    public void back(View view) {
        releaseRecorder();
        releaseCamera();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWakeLock == null) {
            // 获取唤醒锁,保持屏幕常亮
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            mWakeLock.acquire();
        }
        //		if (!initCamera()) {
        //			showFailDialog();
        //		}
        Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * @return
     */
    @SuppressLint("NewApi")
    private boolean initCamera() {
        try {
            if(frontCamera == CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
            } else {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
            }
            Parameters camParams = mCamera.getParameters();
            mCamera.lock();
            mSurfaceHolder = mVideoView.getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mCamera.setDisplayOrientation(90);

        } catch(RuntimeException ex) {
            ex.printStackTrace();
            releaseCamera();
            return false;
        }
        return true;
    }

    private void handleSurfaceChanged() {
        if(mCamera == null) {
            finish();
            return;
        }
        boolean hasSupportRate = false;
        List<Integer> supportedPreviewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
        if(supportedPreviewFrameRates != null && supportedPreviewFrameRates.size() > 0) {
            Collections.sort(supportedPreviewFrameRates);
            for(int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                int supportRate = supportedPreviewFrameRates.get(i);

                if(supportRate == 15) {
                    hasSupportRate = true;
                }

            }
            if(hasSupportRate) {
                defaultVideoFrameRate = 15;
            } else {
                defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
            }

        }
        // 获取摄像头的所有支持的分辨率
        List<Size> resolutionList = getResolutionList(mCamera);
        if(resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new ResolutionComparator());
            Size previewSize = null;
            boolean hasSize = false;
            // 如果摄像头支持640*480，那么强制设为640*480
            for(int i = 0; i < resolutionList.size(); i++) {
                Size size = resolutionList.get(i);
                if(size != null && size.width == 640 && size.height == 480) {
                    previewSize = size;
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                    hasSize = true;
                    break;
                }
            }
            // 如果不支持设为中间的那个
            if(!hasSize) {
                int mediumResolution = resolutionList.size() / 2;
                if(mediumResolution >= resolutionList.size())
                    mediumResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(mediumResolution);
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;

            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.switch_btn:
                switchCamera();
                break;
            case R.id.recorder_start:
                // start recording
                if(!startRecording())
                    return;
                Toast.makeText(this, R.string.video_start, Toast.LENGTH_SHORT).show();
                btn_switch.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                // 重置其他
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;
            case R.id.recorder_stop:
                // 停止拍摄
                stopRecording();
                btn_switch.setVisibility(View.VISIBLE);
                chronometer.stop();
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.INVISIBLE);
                new AlertDialog.Builder(this).setMessage(R.string.ask_send).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendVideo(null);

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(mCamera == null) {
                            initCamera();
                        }
                        try {
                            mCamera.setPreviewDisplay(mSurfaceHolder);
                            mCamera.startPreview();
                            handleSurfaceChanged();
                        } catch(IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }).setCancelable(false).show();
                break;

            default:
                break;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mCamera == null) {
            if(!initCamera()) {
                showFailDialog();
                return;
            }

        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            handleSurfaceChanged();
        } catch(Exception e1) {
            e1.printStackTrace();
            showFailDialog();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        LogUtil.i(TAG, "surfaceDestroyed");
    }

    public boolean startRecording() {
        if(mMediaRecorder == null) {
            if(!initRecorder())
                return false;
        }
        mMediaRecorder.setOnInfoListener(this);
        mMediaRecorder.setOnErrorListener(this);
        try {
            mMediaRecorder.start();
        } catch(Exception e) {
        }
        return true;
    }

    /**
     * 初始化{@link MediaRecorder}参数
     *
     * @return {@value false}:设置失败；{@value true}:设置成功
     */
    private boolean initRecorder() {
        if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            showNoSDCardDialog();
            return false;
        }

        if(mCamera == null) {
            if(!initCamera()) {
                showFailDialog();
                return false;
            }
        }
        mVideoView.setVisibility(View.VISIBLE);
        // TODO init button
        mCamera.stopPreview();
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            // 设置录制视频源为Camera（相机）
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if(frontCamera == CameraInfo.CAMERA_FACING_FRONT) {
                // 前置摄像头
                mMediaRecorder.setOrientationHint(270);
            } else {
                // 后置摄像头
                mMediaRecorder.setOrientationHint(90);
            }
            // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            // 设置录制的视频编码h263 h264
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            mMediaRecorder.setVideoSize(previewWidth, previewHeight);
            // 设置视频的比特率
            mMediaRecorder.setVideoEncodingBitRate(384 * 1024);
            // // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            if(defaultVideoFrameRate != -1) {
                mMediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
            }

            Intent intent = getIntent();

            // 获取视频文件输出的路径
            Uri uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            if(uri == null || Uri.EMPTY.equals(uri)) {
                localPath = FileUtil.getVideoRootPath(this) + "/" + System.currentTimeMillis() + ".mp4";
            } else {
                localPath = uri.getPath();
            }
            // 设置输入目录
            mMediaRecorder.setOutputFile(localPath);

            // 判断可录制时长限制
            int maxDuration = intent.getIntExtra(MediaStore.EXTRA_DURATION_LIMIT, 0);
            if(maxDuration > 0) {
                mMediaRecorder.setMaxDuration(maxDuration);
            }

            // 判断可录制文件大小限制
            long maxSize = intent.getLongExtra(MediaStore.EXTRA_SIZE_LIMIT, 0);
            if(maxSize > 0) {
                mMediaRecorder.setMaxFileSize(maxSize);
            }

            // 设置预览
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            try {
                mMediaRecorder.prepare();
            } catch(IllegalStateException e) {
                e.printStackTrace();
                return false;
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch(Exception e) {
            releaseRecorder();
            releaseCamera();
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        if(mMediaRecorder != null) {
            // 停止监听
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            try {
                mMediaRecorder.stop();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        // 释放资源
        releaseRecorder();

        if(mCamera != null) {
            // 停止预览
            mCamera.stopPreview();
            releaseCamera();
        }
    }

    /**
     * 释放{@link MediaRecorder}的资源
     */
    private void releaseRecorder() {
        if(mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 释放相机
     */
    protected void releaseCamera() {
        if(mCamera != null) {
            try {
                mCamera.stopPreview();
            } finally {
                mCamera.release();
                mCamera = null;

            }
        }
    }

    /**
     * 切换相机,{@value CameraInfo#CAMERA_FACING_BACK}是后置摄像头，{@value CameraInfo#CAMERA_FACING_FRONT}是前置摄像头
     */
    @SuppressLint("NewApi")
    public void switchCamera() {

        if(mCamera == null) {
            return;
        }
        if(Camera.getNumberOfCameras() >= 2) {
            btn_switch.setEnabled(false);
            if(mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            switch(frontCamera) {
                case CameraInfo.CAMERA_FACING_BACK:
                    mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
                    frontCamera = 1;
                    break;
                case CameraInfo.CAMERA_FACING_FRONT:
                    mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
                    frontCamera = 0;
                    break;
            }
            if(mCamera != null) {
                try {
                    mCamera.lock();
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewDisplay(mVideoView.getHolder());
                    mCamera.startPreview();
                } catch(IOException e) {
                    mCamera.release();
                    mCamera = null;
                }
            }
            btn_switch.setEnabled(true);

        }

    }

    MediaScannerConnection msc = null;
    ProgressDialog progressDialog = null;

    public void sendVideo(View view) {
        if(TextUtils.isEmpty(localPath)) {
            LogUtil.e(TAG, "recorder fail please try again!");
            return;
        }
        if(msc == null)
            msc = new MediaScannerConnection(this, new MediaScannerConnectionClient() {

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    LogUtil.d(TAG, String.format("scanner completed& path: %s; uri: %s;", path, uri));
                    msc.disconnect();
                    progressDialog.dismiss();
                    if(getIntent().getBooleanExtra(MediaData.EXTRA_RETURN_DATA, true)) {

                        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                        metadataRetriever.setDataSource(getBaseContext(), uri);
                        String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        metadataRetriever.release();
                        long duration = Long.valueOf(durationStr);
                        LogUtil.i(TAG, "scanner completed&: duration = " + duration);
                        if(duration > 0) {
                            Intent intent = new Intent();
                            intent.putExtra(MediaData.EXTRA_MEDIA_TYPE, MediaData.TYPE_VIDEO);
                            intent.putExtra(MediaData.EXTRA_VIDEO_DURATION, Long.valueOf(durationStr));
                            intent.setData(Uri.parse(ImageDownloader.Scheme.FILE.wrap(path)));
                            setResult(RESULT_OK, intent);
                        } else {
                            setResult(RESULT_CANCELED);
                            File file = new File(path);
                            file.deleteOnExit();
                        }

                    } else {
                        setResult(RESULT_OK);
                    }
                    finish();
                }

                @Override
                public void onMediaScannerConnected() {
                    msc.scanFile(localPath, "video/*");
                }
            });


        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.text_processing));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
        msc.connect();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        LogUtil.v(TAG, "onInfo");
        if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            LogUtil.v(TAG, "max duration reached");
            stopRecording();
            btn_switch.setVisibility(View.VISIBLE);
            chronometer.stop();
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.INVISIBLE);
            chronometer.stop();
            if(localPath == null) {
                return;
            }
            String st3 = getString(R.string.ask_send);
            new AlertDialog.Builder(this).setMessage(st3).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    sendVideo(null);

                }
            }).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
        }

    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        LogUtil.e(TAG, "recording onError:");
        stopRecording();
        Toast.makeText(this, R.string.recording_error, Toast.LENGTH_SHORT).show();

    }

    public void saveBitmapFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), "a.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();

        if(mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

    }

    @Override
    public void onBackPressed() {
        back(null);
    }

    private void showFailDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.prompt).setMessage(R.string.open_the_equipment_failure).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        }).setCancelable(false).show();

    }

    private void showNoSDCardDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.prompt).setMessage(R.string.no_sd_card).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        }).setCancelable(false).show();
    }


    private List<Camera.Size> getResolutionList(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }


    private class ResolutionComparator implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            if(lhs.height != rhs.height)
                return lhs.height - rhs.height;
            else
                return lhs.width - rhs.width;
        }

    }
}
