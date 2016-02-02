package com.madxstudio.co8.db;

import com.madxstudio.co8.entity.LocalStickerInfo;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[] {
            LocalStickerInfo.class
    };

	public static void main(String[] args) throws Exception {
		writeConfigFile("ormlite_config.txt", classes);
	}
}