package com.madxstudio.co8.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import com.madxstudio.co8.App;

import java.io.IOException;

/**
 * Created by quankun on 15/8/17.
 */
public class AudioPlayUtils {
//    private static MediaPlayer mp;
//    private String path;
//    private static LinearLayoutManager llm;
//    private static MessageChatAdapter messageChatAdapter;
//
//    public static AudioPlayUtils getInstance(String filePath, LinearLayoutManager llm, MessageChatAdapter messageChatAdapter) {
//        return new AudioPlayUtils(filePath, llm, messageChatAdapter);
//    }
//
//    private AudioPlayUtils(String filePath, LinearLayoutManager llm, MessageChatAdapter messageChatAdapter) {
//        stopAudio();
//        mp = new MediaPlayer();
//        this.path = filePath;
//        this.messageChatAdapter = messageChatAdapter;
//        this.llm = llm;
//    }
//
//    public void playAudio() {
//        if (mp == null) {
//            return;
//        }
//        try {
//            mp.reset();
//            mp.setDataSource(path);
//            mp.prepare();
//            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mp.start();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static boolean audioIsPlaying() {
//        return mp != null && mp.isPlaying();
//    }
//
//    public static void stopAudio() {
//        try {
//            if (mp != null) {
//                if (mp.isPlaying()) {
//                    mp.stop();
//                }
//                mp.reset();
//                mp.release();
//                mp = null;
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (llm != null && messageChatAdapter != null) {
//            messageChatAdapter.setPlayPros(0);
//            messageChatAdapter.setAudioName(null);
//            int scrollPosition = llm.findLastVisibleItemPosition();
//            messageChatAdapter.notifyDataSetChanged();
//            llm.scrollToPosition(scrollPosition);
//            llm = null;
//            messageChatAdapter = null;
//        }
//    }


    /**
     * 外放模式
     */
    public static final int MODE_SPEAKER = 0;

    /**
     * 耳机模式
     */
    public static final int MODE_HEADSET = 1;

    /**
     * 听筒模式
     */
    public static final int MODE_EARPIECE = 2;

    private static AudioPlayUtils playerManager;

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean isPause = false;
    private int currentMode = MODE_SPEAKER;
    private StopCallback stopCallback;

    public static AudioPlayUtils getManager() {
        if (playerManager == null) {
            synchronized (AudioPlayUtils.class) {
                playerManager = new AudioPlayUtils();
            }
        }
        return playerManager;
    }

    private AudioPlayUtils() {
        this.context = App.getContextInstance();
        initMediaPlayer();
        initAudioManager();
    }

    public interface StopCallback {
        void stopPlayAudio();
    }

    /**
     * 初始化播放器
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * 初始化音频管理器
     */
    private void initAudioManager() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//        } else {
//            audioManager.setMode(AudioManager.MODE_IN_CALL);
//        }
        audioManager.setSpeakerphoneOn(true);            //默认为扬声器播放
    }

    /**
     * 播放音乐
     *
     * @param path 音乐文件路径
     */
    public void play(String path, StopCallback callback) {
//        stop();
        if (mediaPlayer == null) {
            initMediaPlayer();
            initAudioManager();
        }
        if (callback != null) {
            stopCallback = callback;
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse(path));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    resetPlayMode();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPause() {
        return isPause;
    }

    public void pause() {
        if (isPlaying()) {
            isPause = true;
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (isPause) {
            isPause = false;
            mediaPlayer.start();
        }
    }

    /**
     * 获取当前播放模式
     *
     * @return
     */
    public int getCurrentMode() {
        return currentMode;
    }

    /**
     * 切换到听筒模式
     */
    public void changeToEarpieceMode() {
        currentMode = MODE_EARPIECE;
        audioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_CALL), AudioManager.FX_KEY_CLICK);
        }
    }

    /**
     * 切换到耳机模式
     */
    public void changeToHeadsetMode() {
        currentMode = MODE_HEADSET;
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到外放模式
     */
    public void changeToSpeakerMode() {
        currentMode = MODE_SPEAKER;
        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    public void resetPlayMode() {
        if (audioManager.isWiredHeadsetOn()) {
            changeToHeadsetMode();
        } else {
            changeToSpeakerMode();
        }
    }

    /**
     * 调大音量
     */
    public void raiseVolume() {
//        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        if (currentVolume < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
//            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                    AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
//        }
    }

    /**
     * 调小音量
     */
    public void lowerVolume() {
//        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        if (currentVolume > 0) {
//            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                    AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
//        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (stopCallback != null) {
            stopCallback.stopPlayAudio();
        }
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否正在播放
     *
     * @return 正在播放返回true, 否则返回false
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
