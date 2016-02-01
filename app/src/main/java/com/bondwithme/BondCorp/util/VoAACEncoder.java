package com.madxstudio.co8.util;

public class VoAACEncoder {
	static {
		System.loadLibrary("VoAACEncoder");
	}

	public native int Init(int sampleRate, int bitRate, short nChannels,short adtsUsed);

	public native byte[] Enc(byte[] inputBuffer);

	public native int Uninit();
}
