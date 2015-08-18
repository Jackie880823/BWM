package com.bondwithme.BondWithMe.util;

import android.media.MediaPlayer;

/**
 * Created by quankun on 15/8/17.
 */
public class AudioPlayUtils {
    private static MediaPlayer mp;
    private String path;

    public static AudioPlayUtils getInstance(String filePath) {
        return new AudioPlayUtils(filePath);
    }

    private AudioPlayUtils(String filePath) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }
        mp = new MediaPlayer();
        this.path = filePath;
    }

    public void playAudio() {
        if (mp == null) {
            return;
        }
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAudio() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
    }
}
