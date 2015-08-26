package com.bondwithme.BondWithMe.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.sinaapp.bashell.VoAACEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by quankun on 15/8/26.
 */
public class AudioMediaRecorder {
    private AudioRecord recordInstance;
    private boolean isStart;
    private FileOutputStream fos;
    private final int READ_SIZE = 2048;
    private final int simpleRate = 16000;

    public AudioMediaRecorder() {

    }

    public void startRecord(File file) {
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                VoAACEncoder vo = new VoAACEncoder();
                vo.Init(simpleRate, 32000, (short) 1, (short) 1);
                int min = AudioRecord.getMinBufferSize(simpleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                if (min < READ_SIZE) {
                    min = READ_SIZE;
                }
                byte[] tempBuffer = new byte[READ_SIZE];
                recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, simpleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min);
                recordInstance.startRecording();
                isStart = true;
                while (isStart) {
                    int bufferRead = recordInstance.read(tempBuffer, 0, READ_SIZE);
                    byte[] ret = vo.Enc(tempBuffer);
                    if (bufferRead > 0) {
                        try {
                            fos.write(ret);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                recordInstance.stop();
                recordInstance.release();
                recordInstance = null;
                vo.Uninit();
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopRecord() {
        isStart = false;
        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
