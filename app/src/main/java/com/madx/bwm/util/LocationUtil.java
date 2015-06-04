package com.madx.bwm.util;

import android.app.PendingIntent;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.madx.bwm.ui.Map4BaiduActivity;
import com.madx.bwm.ui.Map4GoogleActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wing on 15/3/25.
 */
public class LocationUtil implements LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static Geocoder geoCoder;
    private static LocationManager lm;
    private static double lat = 31.22997;
    private static double lon = 121.640756;
    public static double x_pi = lat * lon / 180.0;
    private static final String BAIDU_MAP_APP_PACKAGE = "com.baidu.BaiduMap";


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

    private void getCurrentLocation(FragmentActivity context) {
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
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    public static Intent getPlacePickerIntent(Context context, double latitude, double longitude, String name) {
        Intent intent = null;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //if gps open
//        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

        //判断是用百度还是google
        if (SystemUtil.checkPlayServices(context)) {
//                if(getLastKnowLocation(context)==null){
//
//                }
            intent = new Intent(context, Map4GoogleActivity.class);
            intent.putExtra("location_name", name);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            //using google map picker
//                try {
//                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
////                intentBuilder.setLatLngBounds(new LatLngBounds(new LatLng(latitude,longitude),new LatLng(latitude,longitude)));
//                    intent = intentBuilder.build(context);
//
//                    // Hide the pick option in the UI to prevent users from starting the picker
//                    // multiple times.
////                showPickAction(false);
//
//                } catch (GooglePlayServicesRepairableException e) {
//                } catch (GooglePlayServicesNotAvailableException e) {
//                }
        } else {
//                intent = new Intent(context, Map4GaoDeActivity.class);
            intent = new Intent(context, Map4BaiduActivity.class);
//        intent.putExtra("has_location", position_name.getText().toString());
            intent.putExtra("location_name", name);
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
        return ret;
    }
    //gcj02,bd09ll(百度经纬度坐标),bd09mc(百度墨卡托坐标),wgs84(gps)
    public static final String LOCATION_TYPE_GCJ02 = "gcj02";
    public static final String LOCATION_TYPE_BD09LL = "bd09ll";
//    public static final String LOCATION_TYPE_BD09MC = "bd09mc";
    public static final String LOCATION_TYPE_WGS84 = "wgs84";
    /**
     * 打开地图导航
     * @param context
     * @param latitude
     * @param longitude
     * @param locationType
     */
    public static void goNavigation(Context context, double latitude, double longitude, String locationType) {

        if (TextUtils.isEmpty(locationType)) {
            locationType = LOCATION_TYPE_GCJ02;
        }
//        if(LOCATION_TYPE_BD09LL.equals(locationType)||LOCATION_TYPE_BD09MC.equals(locationType)){
//            openWebview4BaiduMap(context, latitude, longitude, locationType);
//        }else {
            Intent intent = null;
            //14为缩放比例
            Log.i("", "goNavigation======" + latitude + "," + longitude);
            Log.i("", "locationType======" + locationType);

//                String url = "http://api.map.baidu.com/geoconv/v1/?coords=%s&ak=%s&from=1&to=5";
////                http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.575429778924;114.21892734521,29.575429778924&from=1&to=5&ak=WkHFTQQgdIvSphGk31aAaKWu
//                String ss = context.getResources().getString(R.string.jackie_baidu_maps_key_4_convertor);
//                Log.i("", "2url======" + String.format(url, latitude + "," + longitude, ss));
//                new HttpTools(context).get(String.format(url, latitude + "," + longitude, ss), null, new HttpCallback() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                    }
//
//                    @Override
//                    public void onResult(String response) {
//                        Log.i("", "onResult======" + response);
////                        try {
////                            JSONObject jsonObject = new JSONObject(response);
////                            jsonObject.getJSONArray("result ");
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//
//                    @Override
//                    public void onCancelled() {
//
//                    }
//
//                    @Override
//                    public void onLoading(long count, long current) {
//
//                    }
//                });


            if (SystemUtil.checkPlayServices(context)) {
                if(LOCATION_TYPE_BD09LL.equals(locationType)){
//                if(LOCATION_TYPE_BD09LL.equals(locationType)||LOCATION_TYPE_BD09MC.equals(locationType)){
                    openWebview4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
                }else {
                    try {
                        String uri = String.format("geo:%f,%f?z=14&q=%f,%f", latitude, longitude, latitude, longitude);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        openWebview4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
                    }
                }
            } else {
                //判断没有百度地图
                if (SystemUtil.isPackageExists(context, BAIDU_MAP_APP_PACKAGE)) {
                    String uri = String.format("intent://map/geocoder?location=%s&coord_type=%s&src=%s#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME);
                    try {
                        intent = Intent.getIntent(uri);
                        intent.setAction(Intent.ACTION_VIEW);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        openWebview4BaiduMap(context, latitude, longitude, locationType);
                    }

                } else {
                    openWebview4BaiduMap(context, latitude, longitude, locationType);
                }

            }
//        }


    }

    private static void openWebview4BaiduMap(Context context, double latitude, double longitude, String locationType) {
        String uri = "http://api.map.baidu.com/geocoder?location=%s&coord_type=%s&output=html&src=%s";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(String.format(uri, (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME));
        intent.setData(content_url);
        context.startActivity(intent);
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

    public static String getLocationPicUrl(Context context,String latitude,String longitude,String locationType){
//        Log.i("", "1locationType======" + locationType);
//        if (SystemUtil.checkPlayServices(context)) {
//            Log.i("", "2locationType======" + locationType);
//            if(LOCATION_TYPE_BD09LL.equals(locationType)){
//                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_BAIDU, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);
//            }else {
//                Log.i("", "3locationType======" + locationType);
//                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), "");
////                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);
//            }
//        } else {
//            Log.i("", "4locationType======" + locationType);
//            return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_BAIDU, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);
//
//        }
//        return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);
            return "http://api.map.baidu.com/staticimage?width=400&height=300&center=116.403874,39.914889&zoom=11&markers=116.441818,39.865286&markerStyles=m,T";
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解密成为火星坐标
     *
     * @param bd_lat
     * @param bd_lon
     * @return
     */
    public static String bd_decrypt(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return gg_lat + "," + gg_lon;
    }

    /**
     * 加密成为摩卡托坐标
     */
    public static String bd_encrypt(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return gg_lat + "," + gg_lon;
    }
}
