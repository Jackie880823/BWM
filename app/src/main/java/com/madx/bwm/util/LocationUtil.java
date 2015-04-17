package com.madx.bwm.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wing on 15/3/25.
 */
public class LocationUtil {


    /**
     * 通过经纬度获取地址
     *
     * @return
     */
    public static String getLocationAddress(Context context,double latitude, double longitude) {
        String add = "";
        Geocoder geoCoder = new Geocoder(context,
                Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    latitude, longitude, 1);
            if(addresses!=null&&addresses.size()>0) {
                Address address = addresses.get(0);
                int maxLine = address.getMaxAddressLineIndex();
                if (maxLine >= 2) {
                    add = address.getAddressLine(1) + address.getAddressLine(2);
                } else {
                    add = address.getAddressLine(1);
                }
            }
        } catch (IOException e) {
            add = "";
            e.printStackTrace();
        }

        return add;
    }
}
