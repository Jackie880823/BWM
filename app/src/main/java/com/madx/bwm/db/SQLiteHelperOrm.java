package com.madx.bwm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.madx.bwm.App;

public class SQLiteHelperOrm extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "bwm.db";
    private static final int DB_VERSION = 1;

	public SQLiteHelperOrm(Context context) {
//		super(context, App.DB_NAME, null,
//		App.DB_VERSION,R.raw.ormlite_config);
//		super(context, DB_NAME, null,
//                DB_VERSION, R.raw.ormlite_config);
        super(context, DB_NAME, null,
                DB_VERSION);

	}

	public SQLiteHelperOrm() {
//		super(App.getAppContext(), DB_NAME, null,
//		App.DB_VERSION,R.raw.ormlite_config);
//		super(App.getAppContext(), DB_NAME, null,
//                DB_VERSION, R.raw.ormlite_config);
        super(App.getAppContext(), DB_NAME, null,
                DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
//		try {
//			TableUtils.createTable(connectionSource, BindedDevice.class);
//		} catch (java.sql.SQLException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int arg2, int arg3) {
		// try {
		// TableUtils.dropTable(connectionSource, POUser.class, true);
		// onCreate(db, connectionSource);
		// } catch (SQLException e) {
		// Log.e("SQLiteHelperOrm", "onUpgrade", e);
		// }
	}
}