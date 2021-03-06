package com.madxstudio.co8.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 本地的sticker Info
 * Created by heweidong on 15/6/18.
 */
@DatabaseTable(tableName = "sticker_info")
public class LocalStickerInfo implements Serializable {
    public static final String DEFAULT_STICKER = "1";
    public static final String DEFAULT_INSTALL_STICKER= "0";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;                 //package name
    @DatabaseField
    private String path;                 //file name
    @DatabaseField
    private String sticker_name;         //icon name
    @DatabaseField
    private String version;         //sticker group version
    @DatabaseField
    private String type;                //picture type
    @DatabaseField(unique = true)
    private long order;               //sticker group order
    @DatabaseField
    private String loginUserId;
    @DatabaseField
    private String defaultSticker;

    public String getDefaultSticker() {
        return defaultSticker;
    }

    public void setDefaultSticker(String defaultSticker) {
        this.defaultSticker = defaultSticker;
    }

    //    @DatabaseField(foreign = true, foreignAutoRefresh = true,columnName="user_id")
//    private UserEntity userEntity;

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public LocalStickerInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSticker_name() {
        return sticker_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSticker_name(String sticker_name) {
        this.sticker_name = sticker_name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

//    public UserEntity getUserEntity() {
//        return userEntity;
//    }

//    public void setUserEntity(UserEntity userEntity) {
//        this.userEntity = userEntity;
//    }

    @Override
    public String toString() {
        return "LocalStickerInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sticker_name='" + sticker_name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", order=" + order +
                '}';
    }
}
