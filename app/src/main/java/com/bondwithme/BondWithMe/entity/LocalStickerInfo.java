package com.bondwithme.BondWithMe.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.bondwithme.BondWithMe.ui.MainActivity;

import java.io.Serializable;
import java.lang.Override;
import java.lang.String;

/**
 * 本地的sticker Info
 * Created by heweidong on 15/6/18.
 */
@DatabaseTable(tableName = "sticker_info")
public class LocalStickerInfo implements Serializable {
    public static final String LOGIN_USER_ID=MainActivity.getUser().getUser_id();
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
    @DatabaseField
    private int position;               //sticker group position
    @DatabaseField
    private String loginUserId;

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId() {
        this.loginUserId = MainActivity.getUser().getUser_id();
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "LocalStickerInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sticker_name='" + sticker_name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", position=" + position +
                '}';
    }
}
