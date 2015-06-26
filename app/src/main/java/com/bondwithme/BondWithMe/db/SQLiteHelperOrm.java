package com.bondwithme.BondWithMe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.OrmEntityDemo;

public class SQLiteHelperOrm extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "bwm.db";
    private static final int DB_VERSION = 1;

	public SQLiteHelperOrm(Context context) {
//		super(context, App.DB_NAME, null,
//		App.DB_VERSION,R.raw.ormlite_config);
		super(context, DB_NAME, null,
                DB_VERSION, R.raw.ormlite_config);

	}

	public SQLiteHelperOrm() {
//		super(App.getContextInstance(), DB_NAME, null,
//		App.DB_VERSION,R.raw.ormlite_config);
		super(App.getContextInstance(), DB_NAME, null,
                DB_VERSION, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		//TODO
		try {
			TableUtils.createTable(connectionSource, OrmEntityDemo.class);
            TableUtils.createTable(connectionSource, LocalStickerInfo.class);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int arg2, int arg3) {
		//TODO
		// try {
		// TableUtils.dropTable(connectionSource, POUser.class, true);
		// onCreate(db, connectionSource);
		// } catch (SQLException e) {
		// Log.e("SQLiteHelperOrm", "onUpgrade", e);
		// }
	}
}