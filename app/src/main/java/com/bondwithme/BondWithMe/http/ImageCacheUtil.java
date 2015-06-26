package com.bondwithme.BondWithMe.http;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCacheUtil {
	private static LruCache<String, Bitmap> mMemoryCache;
	private static int maxSize;

	static {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// Use 1/8th of the available memory for this memory cache.
		maxSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(maxSize);
	}

	public static Bitmap get(String key) {
		return mMemoryCache.get(key);
	}

	public static void put(String key, Bitmap bitmap) {

        mMemoryCache.put(key, bitmap);


	}

}
