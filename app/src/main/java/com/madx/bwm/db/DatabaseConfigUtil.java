package com.madx.bwm.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.madx.bwm.entity.BindedDevice;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[] {
            BindedDevice.class
    };

	public static void main(String[] args) throws Exception {
		writeConfigFile("ormlite_config.txt", classes);
	}
}