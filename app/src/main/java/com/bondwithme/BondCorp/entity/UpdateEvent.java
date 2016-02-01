package com.madxstudio.co8.entity;

/**
 * Created by liangzemian on 15/11/26.
 */
public class UpdateEvent {

    public final int count;

    public UpdateEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

}
