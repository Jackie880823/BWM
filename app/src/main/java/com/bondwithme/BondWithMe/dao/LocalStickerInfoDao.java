package com.bondwithme.BondWithMe.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.bondwithme.BondWithMe.db.SQLiteHelperOrm;
import com.bondwithme.BondWithMe.entity.LocalStickerInfo;
import com.bondwithme.BondWithMe.util.FileUtil;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/6/26.
 */
public class LocalStickerInfoDao {
    private static Dao<LocalStickerInfo, String> stickerInfoDao;
    private static LocalStickerInfoDao stickerInfoDaoInstance;
    private Context mContext;
    private static final String STICKER_FILE_PATH_NAME = "Sticker";

    public LocalStickerInfoDao(Context mContext) {
        this.mContext = mContext;
    }

    public String getSavePath() {
        File bootFile = FileUtil.getSavePath(mContext, true);
        String filePath = bootFile.getAbsolutePath();
        filePath = filePath + File.separator + STICKER_FILE_PATH_NAME;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static LocalStickerInfoDao getInstance(Context paramContext) {
        try {
            if (stickerInfoDaoInstance == null) {
                stickerInfoDaoInstance = new LocalStickerInfoDao(paramContext);
                try {
                    stickerInfoDao = SQLiteHelperOrm.getHelper(paramContext).getDao(LocalStickerInfo.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            LocalStickerInfoDao localExpertsListDBDao = stickerInfoDaoInstance;
            return localExpertsListDBDao;
        } finally {
        }
    }

    public void addOrUpdate(LocalStickerInfo stickerInfo) {
        if (null == stickerInfo) {
            return;
        }
        try {
            stickerInfo.setLoginUserId();
            QueryBuilder queryBuilder = stickerInfoDao.queryBuilder();
            queryBuilder.where().eq("loginUserId", LocalStickerInfo.LOGIN_USER_ID).and().eq("path", stickerInfo.getPath());
            List<LocalStickerInfo> localList = queryBuilder.query();
            if ((localList != null) && (localList.size() > 0)) {
                //stickerInfoDao.update(stickerInfo);
                return;
            }
            stickerInfoDao.createIfNotExists(stickerInfo);
        } catch (SQLException localSQLException) {
            localSQLException.printStackTrace();
        }
    }

    public List<String> queryAllSticker() {
        List<String> list = new ArrayList<>();
        try {
            List<LocalStickerInfo> localList = stickerInfoDao.queryForEq("loginUserId", LocalStickerInfo.LOGIN_USER_ID);
            if (null != localList && localList.size() > 0) {
                for (LocalStickerInfo stickerInfo : localList) {
                    String name = stickerInfo.getPath();
                    list.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
//        Map<String, List<String>> map = new HashMap<>();
//        try {
//            List<LocalStickerInfo> localList = stickerInfoDao.queryForEq("loginUserId", LocalStickerInfo.LOGIN_USER_ID);
//            if (null != localList && localList.size() > 0) {
//                for (LocalStickerInfo stickerInfo : localList) {
//                    String name = stickerInfo.getSticker_name();
//                    String path = getSavePath() + File.separator + name;
//                    File file = new File(path);
//                    File[] files = file.listFiles();
//                    if (null != files && files.length > 0) {
//                        List<String> list = new ArrayList<>();
//                        for (File file1 : files) {
//                            String subFilePath = file1.getAbsolutePath();
//                            list.add(subFilePath);
//                        }
//                        map.put(name, list);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return map;
    }

}
