package com.bondwithme.BondWithMe.util;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by quankun on 15/8/26.
 */
public class AudioMediaRecorder {
    private MediaRecorder mediaRecorder;
    private boolean mIsRecording = false;

    public AudioMediaRecorder() {
    }


    public void startRecord(File file) {
        if (mIsRecording) {
            return;
        }
        try {
            mediaRecorder = new MediaRecorder();
            // 设置录音为麦克风
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //录音文件保存这里
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            mIsRecording = false;
        }
    }

    public void stopRecord() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.setPreviewDisplay(null);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                mIsRecording = false;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
