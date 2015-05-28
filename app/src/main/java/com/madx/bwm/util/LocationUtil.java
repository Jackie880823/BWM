package com.madx.bwm.util;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.madx.bwm.R;
import com.madx.bwm.ui.Map4BaiduActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wing on 15/3/25.
 */
public class LocationUtil implements LocationListener,GoogleApiClient.OnConnectionFailedListener{

    private static Geocoder geoCoder;
    private static LocationManager lm;

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

    private void getCurrentLocation(FragmentActivity context){
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .enableAutoManage(context, 0 /* clientId */, this)
//                .addApi(Places.GEO_DATA_API)
//                .build();
//        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
//                .getCurrentPlace(mGoogleApiClient, null);
//        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//            @Override
//            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
//                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                    Log.i("", String.format("Place '%s' has likelihood: %g",
//                            placeLikelihood.getPlace().getName(),
//                            placeLikelihood.getLikelihood()));
//                }
//                likelyPlaces.release();
//            }
//        });



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
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //if gps open
//        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            //判断是用百度还是google
            if (SystemUtil.checkPlayServices(context)) {
//                if(getLastKnowLocation(context)==null){
//
//                }
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
            } else {
                intent = new Intent(context, Map4BaiduActivity.class);
//        intent.putExtra("has_location", position_name.getText().toString());
//            intent.putExtra("location_name", position_name.getText().toString());
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
            }
//        }
        return intent;
    }

    private static String getBestProvider(Context context) {
        Criteria criteria = getCriteria();
        // 这里可能返回 null, 地理位置信息服务未开启
        return lm.getBestProvider(criteria, true);
    }

    public static Criteria getCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);          //设置查询精度,(Criteria.ACCURACY_COARSE...)
        c.setSpeedRequired(false);                      //设置是否要求速度
        c.setCostAllowed(false);                        //设置是否允许产生费用
        c.setBearingRequired(false);                    //设置是否需要得到方向
        c.setAltitudeRequired(false);                   //设置是否需要得到海拔高度
        c.setPowerRequirement(Criteria.POWER_LOW);      //设置允许的电池消耗级别
        return c;
    }

    public static Location getLastKnowLocation(Context context) {
        Location ret = null;
        String bestProvider = getBestProvider(context);
        if (TextUtils.isEmpty(bestProvider)) {
            // 这里可能会返回 null, 表示按照当前的查询条件无法获取系统最后一次更新的地理位置信息
            ret = lm.getLastKnownLocation(bestProvider);
        }
        if(ret!=null) {
            Log.i("", "getLastKnowLocation======" + ret.getProvider());
            Log.i("", "getLastKnowLocation======" + ret.getAccuracy());
            Log.i("", "getLastKnowLocation======" + ret.getBearing());
            Log.i("", "getLastKnowLocation======" + ret.toString());
        }
        return ret;
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private static Location currentLocation;
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
//        if(currentLocation.getProvider()){
//
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
