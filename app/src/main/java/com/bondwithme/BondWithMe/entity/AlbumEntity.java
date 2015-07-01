package com.bondwithme.BondWithMe.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by quankun on 15/5/19.
 */
public class AlbumEntity implements Serializable {
    private String year;// year 年份
    private String month;// month 月份
    private List<AlbumPhotoEntity> photoList;
    private String total;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<AlbumPhotoEntity> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<AlbumPhotoEntity> photoList) {
        this.photoList = photoList;
    }
}
