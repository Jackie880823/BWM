package com.bondwithme.BondWithMe.util;

import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;

import com.bondwithme.BondWithMe.adapter.MessageChatAdapter;

/**
 * Created by quankun on 15/8/17.
 */
public class AudioPlayUtils {
    private static MediaPlayer mp;
    private String path;
    private static LinearLayoutManager llm;
    private static MessageChatAdapter messageChatAdapter;

    public static AudioPlayUtils getInstance(String filePath, LinearLayoutManager llm, MessageChatAdapter messageChatAdapter) {
        return new AudioPlayUtils(filePath, llm, messageChatAdapter);
    }

    private AudioPlayUtils(String filePath, LinearLayoutManager llm, MessageChatAdapter messageChatAdapter) {
        stopAudio();
        mp = new MediaPlayer();
        this.path = filePath;
        this.messageChatAdapter = messageChatAdapter;
        this.llm = llm;
    }

    public void playAudio() {
        if (mp == null) {
            return;
        }
        try {
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean audioIsPlaying() {
        if (mp != null && mp.isPlaying()) {
            return true;
        }
        return false;
    }

    public static void stopAudio() {
        try {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.reset();
                mp.release();
                mp = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (llm != null && messageChatAdapter != null) {
            messageChatAdapter.setPlayPros(0);
            messageChatAdapter.setAudioName(null);
            int scrollPosition = llm.findLastVisibleItemPosition();
            messageChatAdapter.notifyDataSetChanged();
            llm.scrollToPosition(scrollPosition);
            llm = null;
            messageChatAdapter = null;
        }
    }
}
