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
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.ui.Map4BaiduActivity;

import java.io.IOException;
import java.util.List;

/**
 * Created by wing on 15/3/25.
 */
public class LocationUtil implements LocationListener, GoogleApiClient.OnConnectionFailedListener {
    //gcj02,bd09ll(百度经纬度坐标),bd09mc(百度墨卡托坐标),wgs84(gps)
    public static final String LOCATION_TYPE_GCJ02 = "gcj02";
    public static final String LOCATION_TYPE_BD09LL = "bd09ll";
    //    public static final String LOCATION_TYPE_BD09MC = "bd09mc";
    public static final String LOCATION_TYPE_WGS84 = "wgs84";
    private final static String TAG = LocationUtil.class.getSimpleName();
    private static final String BAIDU_MAP_APP_PACKAGE = "com.baidu.BaiduMap";
    private static Geocoder geoCoder;
    private static LocationManager lm;
    private static double lat = 31.22997;
    private static double lon = 121.640756;
    public static double x_pi = lat * lon / 180.0;
    private static Location currentLocation;

    /**
     * 通过经纬度获取地址
     *
     * @return 返回地址名称
     */
    public static String getLocationAddress(Context context, double latitude, double longitude) {
        String add = null;
        Address address = getAddress(context, latitude, longitude);
        if(address != null) {
            int maxLine = address.getMaxAddressLineIndex();
            if(maxLine >= 2) {
                add = address.getAddressLine(1);
            } else {
                add = address.getAddressLine(0);
            }
        }
        Log.i(TAG, "getLocationAddress& address: " + add);
        return add;
    }

    /**
     * 根据经纬度获取地址信息
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return 返回地址信息封装对象Address
     */
    private static Address getAddress(Context context, double latitude, double longitude) {
        Log.i(TAG, "getAddress& latitude: " + latitude + "; longitude: " + longitude);
        if(latitude == 0 && longitude == 0) {
            Location location = getLastKnowLocation();
            if(location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        Address address = null;

        // 判断地址编码器是否为空
        if(geoCoder == null) {
            geoCoder = new Geocoder(context);
        }

        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
            if(addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
                int lines = address.getMaxAddressLineIndex();
                for(int i = 0; i < lines; i++) {
                    Log.i(TAG, "getAddress& address i: " + i + " " + address.getAddressLine(i));
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public static void setRequestLocationUpdates(Context context) {
        if(lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        lm.requestLocationUpdates(2000, 0, getCriteria(), new LocationUtil(), null);
    }

    /**
     * 获取地图picker Intent
     *
     * @param context
     * @param latitude
     * @param longitude
     * @param name
     * @return
     */
    public static Intent getPlacePickerIntent(Context context, double latitude, double longitude, String name) {
        Intent intent = new Intent();
        if(lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

//        if(currentLocation != null) {
//            Address address = getAddress(context, currentLocation.getLatitude(), currentLocation.getLongitude());
//            if(address != null) {
//                Log.i(TAG, "getPlacePickerIntent& locale: country: " + address.getCountryName());
//            }
//        }

        intent.putExtra(Constant.EXTRA_LOCATION_NAME, name);
        intent.putExtra(Constant.EXTRA_LATITUDE, latitude);
        intent.putExtra(Constant.EXTRA_LONGITUDE, longitude);

        //判断是用百度还是google
        if(!SystemUtil.checkPlayServices(context)) {
            intent.setClass(context, Map4BaiduActivity.class);
        } else {
            intent.setClass(context, Map4BaiduActivity.class);
        }
        return intent;
    }

    /**
     * @return
     */
    public static String getBestProvider() {
        Criteria criteria = getCriteria();
        // 这里可能返回 null, 地理位置信息服务未开启
        return lm.getBestProvider(criteria, true);
    }

    /**
     * 获取位置提供器
     *
     * @return 位置提供器
     */
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

    public static Location getLastKnowLocation() {
        Location ret;
        String bestProvider = getBestProvider();
        // 这里可能会返回 null, 表示按照当前的查询条件无法获取系统最后一次更新的地理位置信息
        ret = lm.getLastKnownLocation(bestProvider);
        return ret;
    }

    /**
     * 打开地图导航找到指定的地址
     *
     * @param context
     * @param latitude
     * @param longitude
     * @param locationType
     */
    public static void goNavigation(Context context, double latitude, double longitude, String locationType) {

        if(TextUtils.isEmpty(locationType)) {
            // 地址类型为空
            locationType = LOCATION_TYPE_GCJ02;
        }
        //        if(LOCATION_TYPE_BD09LL.equals(locationType)||LOCATION_TYPE_BD09MC.equals(locationType)){
        //            openWebView4BaiduMap(context, latitude, longitude, locationType);
        //        }else {
        Intent intent;
        //14为缩放比例
        Log.i("", "goNavigation======" + latitude + "," + longitude);
        Log.i("", "locationType======" + locationType);


        // 判断是否有谷歌服务
        if(SystemUtil.checkPlayServices(context)) {
            if(LOCATION_TYPE_BD09LL.equals(locationType)) {
                //                if(LOCATION_TYPE_BD09LL.equals(locationType)||LOCATION_TYPE_BD09MC.equals(locationType)){
                openWebView4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
            } else {
                try {
                    String uri = String.format("geo:%f,%f?z=14&q=%f,%f", latitude, longitude, latitude, longitude);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(intent);
                } catch(Exception e) {
                    openWebView4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
                }
            }
        } else {
            //判断没有百度地图
            if(SystemUtil.isPackageExists(context, BAIDU_MAP_APP_PACKAGE)) {
                String uri = String.format("intent://map/geocoder?location=%s&coord_type=%s&src=%s#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME);
                try {
                    intent = Intent.getIntent(uri);
                    intent.setAction(Intent.ACTION_VIEW);
                    context.startActivity(intent);
                } catch(Exception e) {
                    e.printStackTrace();
                    openWebView4BaiduMap(context, latitude, longitude, locationType);
                }

            } else {
                openWebView4BaiduMap(context, latitude, longitude, locationType);
            }

        }
        //        }


    }

    private static void openWebView4BaiduMap(Context context, double latitude, double longitude, String locationType) {
        String uri = "http://api.map.baidu.com/geocoder?location=%s&coord_type=%s&output=html&src=%s";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(String.format(uri, (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME));
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static String getLocationPicUrl(Context context, String latitude, String longitude, String locationType) {
        Log.i("", "1locationType======" + locationType);
        String ssssaa = latitude + "," + longitude;
        if(SystemUtil.checkPlayServices(context)) {
            Log.i("", "2locationType======" + locationType);
            if(LOCATION_TYPE_BD09LL.equals(locationType)) {
                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_BAIDU, ssssaa, context.getString(R.string.google_map_pic_size), ssssaa);
            } else {
                Log.i("", "3locationType======" + locationType);
                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, ssssaa, context.getString(R.string.google_map_pic_size), ssssaa);
                //                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);
            }
        } else {
            Log.i("", "4locationType======" + locationType);
            return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_BAIDU, ssssaa, context.getString(R.string.google_map_pic_size), ssssaa);

        }
        //        return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);

        //        String sss = "http://api.map.baidu.com/staticimage?width=400&height=300&center=" + ssssaa + "&zoom=11&markers=" + ssssaa + "&markerStyles=m,T";
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;

    }

    /**
     * 强制帮用户打开 GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch(PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
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

    public static LatLng convert2BaiduLocation(Context context, String latitude, String longitude) {

        LatLng sourceLatLng = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();

        // 将GPS设备采集的原始GPS坐标转换成百度坐标
//        CoordinateConverter converter = new CoordinateConverter();
//        converter.from(CoordinateConverter.CoordType.GPS);
//        // sourceLatLng待转换坐标
//        converter.coord(sourceLatLng);
//        LatLng desLatLng = converter.convert();

        return desLatLng;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged()& latitude: " + location.getLatitude() + "; longitude: " + location.getLongitude());
        currentLocation = location;
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
