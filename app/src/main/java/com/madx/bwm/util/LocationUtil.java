package com.madx.bwm.util;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.madx.bwm.R;
import com.madx.bwm.ui.Map4BaiduActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wing on 15/3/25.
 */
public class LocationUtil {

    private static Geocoder geoCoder;

    private static Geocoder getGeocoder(Context context) {
        if (geoCoder == null) {
            geoCoder = new Geocoder(context,
                    Locale.getDefault());
        }
        return geoCoder;
    }

    /**
     * 通过经纬度获取地址
     *
     * @return
     */
    public static String getLocationAddress(Context context, double latitude, double longitude) {
        String add = "";
        Geocoder geoCoder = getGeocoder(context);
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
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

    /**
     * 获取地图picker Intent
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    public static Intent getPlacePickerIntent(Context context,double latitude,double longitude){
        Intent intent = null;
        //判断是用百度还是google
        if (SystemUtil.checkPlayServices(context)) {
//            intent = new Intent(context, Map4GoogleActivity.class);
            try {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//                intentBuilder.setLatLngBounds(new LatLngBounds(new LatLng(latitude,longitude),new LatLng(latitude,longitude)));
                intent = intentBuilder.build(context);

                // Hide the pick option in the UI to prevent users from starting the picker
                // multiple times.
//                showPickAction(false);

            } catch (GooglePlayServicesRepairableException e) {
            } catch (GooglePlayServicesNotAvailableException e) {
            }
        }else {
            intent = new Intent(context, Map4BaiduActivity.class);
//        intent.putExtra("has_location", position_name.getText().toString());
//            intent.putExtra("location_name", position_name.getText().toString());
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }
        return intent;
    }

    /**
     * 打开地图导航
     */
    public static void goNavigation(Context context,double latitude,double longitude) {

        try {
            Intent intent;
            //14为缩放比例
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=14&q=%f,%f", latitude, longitude, latitude, longitude);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        } catch (Exception e) {
            MessageUtil.showMessage(context, R.string.msg_no_map_app);
        }
    }
}
