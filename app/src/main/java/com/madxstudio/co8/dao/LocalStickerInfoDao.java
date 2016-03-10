package com.madxstudio.co8.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.madxstudio.co8.App;
import com.madxstudio.co8.db.SQLiteHelperOrm;
import com.madxstudio.co8.entity.LocalStickerInfo;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.ZipUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/6/26.
 */
public class LocalStickerInfoDao {
    private static Dao<LocalStickerInfo, Integer> stickerInfoDao;
    private static LocalStickerInfoDao stickerInfoDaoInstance;
    private Context mContext;
    private static final String STICKER_FILE_PATH_NAME = "NewSticker";

    public LocalStickerInfoDao(Context mContext) {
        this.mContext = mContext;
    }

    public String getSavePath() {
        File bootFile = FileUtil.getSaveRootPath(mContext, false);
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
            }
            if (stickerInfoDao == null) {
                try {
                    stickerInfoDao = SQLiteHelperOrm.getHelper(paramContext).getDao(LocalStickerInfo.class);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            return stickerInfoDaoInstance;
        } finally {
        }
    }

    public void addOrUpdate(LocalStickerInfo stickerInfo) {
        if (null == stickerInfo) {
            return;
        }
        try {
            stickerInfo.setLoginUserId(App.getLoginedUser().getUser_id());
            QueryBuilder queryBuilder = stickerInfoDao.queryBuilder();
            queryBuilder.where().eq("loginUserId", App.getLoginedUser().getUser_id()).and().eq("path", stickerInfo.getPath());
            List<LocalStickerInfo> localList = queryBuilder.query();
            if ((localList != null) && (localList.size() > 0)) {
                LocalStickerInfo info = localList.get(0);
                if (!TextUtils.isEmpty(stickerInfo.getName())) {
                    info.setName(stickerInfo.getName());
                }
                if (stickerInfo.getOrder() != 0) {
                    info.setOrder(stickerInfo.getOrder());
                }
                if (!TextUtils.isEmpty(stickerInfo.getSticker_name())) {
                    info.setSticker_name(stickerInfo.getSticker_name());
                }
                if (!TextUtils.isEmpty(stickerInfo.getType())) {
                    info.setType(stickerInfo.getType());
                }
                if (!TextUtils.isEmpty(stickerInfo.getVersion())) {
                    info.setVersion(stickerInfo.getVersion());
                }
                if (!TextUtils.isEmpty(stickerInfo.getDefaultSticker())) {
                    info.setDefaultSticker(stickerInfo.getDefaultSticker());
                }
                stickerInfoDao.update(info);
            } else {
                stickerInfoDao.createIfNotExists(stickerInfo);
            }
        } catch (SQLException localSQLException) {
            localSQLException.printStackTrace();
        }
    }

    public void updateDefaultSticker(String stickerPath) {
        if (TextUtils.isEmpty(stickerPath)) {
            return;
        }
        QueryBuilder queryBuilder = stickerInfoDao.queryBuilder();
        try {
            queryBuilder.where().eq("path", stickerPath);
            List<LocalStickerInfo> localList = queryBuilder.query();
            if ((localList != null) && (localList.size() > 0)) {
                for (LocalStickerInfo info : localList) {
                    info.setDefaultSticker(LocalStickerInfo.DEFAULT_INSTALL_STICKER);
                    stickerInfoDao.update(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<String> queryAllDefaultSticker() {
        QueryBuilder queryBuilder = stickerInfoDao.queryBuilder();
        List<String> list = new ArrayList<>();
        try {
            queryBuilder.where().eq("defaultSticker", LocalStickerInfo.DEFAULT_STICKER);
            List<LocalStickerInfo> localList = queryBuilder.query();
            if ((localList != null) && (localList.size() > 0)) {
                for (LocalStickerInfo info : localList) {
                    list.add(info.getPath());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean hasDownloadSticker(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            QueryBuilder queryBuilder = stickerInfoDao.queryBuilder();
            queryBuilder.where().eq("loginUserId", App.getLoginedUser().getUser_id()).and().eq("path", path);
            List<LocalStickerInfo> localList = queryBuilder.query();
            if ((localList != null) && (localList.size() > 0)) {
//                stickerInfoDao.update(stickerInfo);
                return true;
            }
        } catch (SQLException localSQLException) {
            localSQLException.printStackTrace();
        }
        return false;
    }

    public LocalStickerInfo queryStickerByPath(Context mContext, String path) {
        try {
            Dao<LocalStickerInfo, String> stickerInfoDao = SQLiteHelperOrm.getHelper(mContext).getDao(LocalStickerInfo.class);
            QueryBuilder qb = stickerInfoDao.queryBuilder();
            qb.where().eq("path", path);
            List<LocalStickerInfo> stickerInfoList = qb.query();
            LocalStickerInfo stickerInfo = stickerInfoList.get(0);
            return stickerInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteSticker(Context mContext, String path) {
        if (hasDownloadSticker(path)) {
            LocalStickerInfo stickerInfo = queryStickerByPath(mContext, path);
            try {
                stickerInfoDao = App.getContextInstance().getDBHelper().getDao(LocalStickerInfo.class);
                stickerInfoDao.delete(stickerInfo);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<String> queryAllSticker(boolean isNewSticker) {
        if (stickerInfoDao == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        try {
            List<LocalStickerInfo> localList = null;
            QueryBuilder qb = stickerInfoDao.queryBuilder();
            qb.selectColumns("path").orderBy("order", false).where().eq("loginUserId", App.getLoginedUser().getUser_id());
            localList = qb.query();
            if (null != localList) {
//            if (null != localList && localList.size() > 0) {
//                list.add(DEF_FIRST_STICKER);
//                list.add(DEF_SECOND_STICKER);
//                list.add(DEF_THREAD_STICKER);
                String path;
                for (LocalStickerInfo stickerInfo : localList) {
//                    String name = stickerInfo.getPath();
//                    if (DEF_FIRST_STICKER.equalsIgnoreCase(name) || DEF_SECOND_STICKER.equalsIgnoreCase(name) || DEF_THREAD_STICKER.equalsIgnoreCase(name)) {
//                        continue;
//                    } else {
                    path = stickerInfo.getPath();
                    if (!isNewSticker && (ZipUtils.PATH_DEFAULT_STICKER_1.equals(path) || ZipUtils.PATH_DEFAULT_STICKER_2.equals(path))) {
                        path = path.substring(0, path.length() - 1);
                    }
                    list.add(path);
//                    }
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
//                    String path = getSaveRootPath() + File.separator + name;
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


    public List<LocalStickerInfo> queryAllLocalStickerInfo() {
        if (stickerInfoDao == null) {
            return null;
        }
        List<LocalStickerInfo> localList = null;
        try {
            localList = stickerInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return localList;
    }
}
