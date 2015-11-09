package com.bondwithme.BondWithMe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.entity.OrmEntityDemo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class SQLiteHelperOrm extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "bwm.db";
    private static final int DB_VERSION = 3;
    private static SQLiteHelperOrm helper = null;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    public SQLiteHelperOrm(Context context) {
        super(context, DB_NAME, null,
                DB_VERSION, R.raw.ormlite_config);
//		super(context, DB_NAME, null,
//				DB_VERSION);

    }

    public SQLiteHelperOrm() {
        super(App.getContextInstance(), DB_NAME, null,
                DB_VERSION, R.raw.ormlite_config);
//		super(App.getContextInstance(), DB_NAME, null,
//				DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        //TODO
        try {
//			TableUtils.createTable(connectionSource, OrmEntityDemo.class);
//            TableUtils.createTable(connectionSource, UserEntity.class);
            TableUtils.createTable(connectionSource, LocalStickerInfo.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 2) {
                TableUtils.dropTable(connectionSource, OrmEntityDemo.class, true);
                TableUtils.dropTable(connectionSource, LocalStickerInfo.class, true);
                TableUtils.createTable(connectionSource, LocalStickerInfo.class);
            }
            if (oldVersion < 3) {
//                Dao<LocalStickerInfo, Integer> recordItemDao = getDao(LocalStickerInfo.class);
//                recordItemDao.executeRaw("ALTER TABLE `sticker_info` ADD COLUMN defaultSticker VARCHAR default '0';");
                db.execSQL("ALTER TABLE `sticker_info` ADD COLUMN defaultSticker VARCHAR default '0';");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static synchronized SQLiteHelperOrm getHelper(Context paramContext) {
        try {
            if (helper == null) {
                synchronized (SQLiteHelperOrm.class) {
                    helper = new SQLiteHelperOrm(paramContext);
                }
            }
            usageCounter.incrementAndGet();
            SQLiteHelperOrm localExpertsTaskListDatabaseHelper = helper;
            return localExpertsTaskListDatabaseHelper;
        } finally {
        }
    }

}