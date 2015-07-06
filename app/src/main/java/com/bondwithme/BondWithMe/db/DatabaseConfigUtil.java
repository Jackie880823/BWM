package com.bondwithme.BondWithMe.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.OrmEntityDemo;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[] {
            OrmEntityDemo.class,
            LocalStickerInfo.class
    };

	public static void main(String[] args) throws Exception {
		writeConfigFile("ormlite_config.txt", classes);
	}
}