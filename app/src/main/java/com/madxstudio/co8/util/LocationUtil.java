package com.madxstudio.co8.util;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.Map4BaiduActivity;
import com.madxstudio.co8.ui.Map4GoogleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wing on 15/3/25.
 */
public class LocationUtil {
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
    /**
     * <br>    谷歌服务是否可用标识位，在应用启动后获取到位置信息则标识位置设置为true,没有获取到位置信息或服务不用都为
     * <br>false。默认状态也为false
     */
    private static boolean googleAvailable = false;
    private static boolean googleAvailableCheckFinish = false;

    /**
     * 通过经纬度获取地址
     *
     * @return 返回地址名称
     */
    public static String getLocationAddress(Context context, double latitude, double longitude) {
        String add = null;
        Address address = null;
        try {
            address = getAddress(context, latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address != null) {
            int maxLine = address.getMaxAddressLineIndex();
            if (maxLine >= 2) {
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
     * 不推荐使用！获取的地址信息少。
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @return 返回地址信息封装对象Address
     */
    public static Address getAddress(Context context, double latitude, double longitude) throws IOException {
        Log.i(TAG, "getAddress& latitude: " + latitude + "; longitude: " + longitude);
        if (latitude == 0 && longitude == 0) {
            Location location = getLastKnowLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        Address address = null;

        // 判断地址编码器是否为空
        if (geoCoder == null) {
            geoCoder = new Geocoder(context);
        }

        List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
        }
        return address;
    }

    /**
     * 根据经纬度获取地址信息
     * google，网络限制
     *
     * @param context   上下文
     * @param latitude  纬度
     * @param longitude 经度
     * @param handler   得到地址更新的线程
     * @param what      更新消息的标识
     */
    public static void getAddressByHttp(Context context, double latitude, double longitude, final Handler handler, final int what) {
        Log.i(TAG, "getAddress& latitude: " + latitude + "; longitude: " + longitude);
        if (latitude == 0 && longitude == 0) {
            Location location = getLastKnowLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1s,%2s&key=%3s";
        final String request = String.format(url, latitude, longitude, context.getString(R.string.google_maps_place_key));
        com.madxstudio.co8.util.LogUtil.i(TAG, "getAddressByHttp& " + request);
        HttpTools httpTools = new HttpTools(context);
        HttpTools.getHeaders().put("Accept-Language", Locale.getDefault().getLanguage());
        httpTools.get(request, null, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                LogUtil.i(TAG, "getAddressByHttp& onResult: " + string);
                try {
                    JSONObject dataJson;
                    dataJson = new JSONObject(string);
                    String status = dataJson.getString("status");
                    if (status.equals("OK")) {
                        JSONArray resultJson = dataJson.getJSONArray("results");
//                    JSONArray data = resultJson.get(0);
//                    JSONArray data = resultJson.getJSONArray("address_components");
                        if (resultJson.length() > 0) {
                            JSONObject info = resultJson.getJSONObject(0);
                            Address address = new Address(null);
                            address.setAddressLine(0, info.getString("formatted_address"));
                            JSONObject location = info.getJSONObject("geometry").getJSONObject("location");
                            address.setLatitude(location.getDouble("lat"));
                            address.setLongitude(location.getDouble("lng"));
                            Message message = new Message();
                            message.what = what;
                            message.obj = address;
                            handler.sendMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    /**
     * @return boonlean 用来判断google服务+翻墙网络 是否都有效
     */
    public static boolean isGoogleAvailable() {
        return googleAvailable;
    }

    public interface GoogleServiceCheckTaskListener{
        public void googleServiceCheckFinished(boolean googleAvailable);
    }

    /**
     * 判断google服务和翻墙网络 得出标识boolean googleAvailable 用于后续的使用何种地图
     *
     * <br>注册监听谷歌地图变化，在应用启动是监听获取地后则取消监听，只用于验证是否可以通过谷歌地图获取位置信息，得到
     * <br>判断谷歌地图是否可用的标识，如果没有谷歌服务不启用监听
     *
     * @param context 上下文资源
     */
    public static void setRequestLocationUpdates(Context context, final GoogleServiceCheckTaskListener mGoogleServiceCheckTaskListener) {
        if (SystemUtil.checkPlayServices(context)) {
            if (lm == null) {
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }
            new HttpTools(context).get("https://www.google.com/maps", null, TAG, new HttpCallback() {
                @Override
                public void onStart() {
                    //            Criteria c = new Criteria();
                    //            c.setAccuracy(Criteria.ACCURACY_COARSE);          //设置查询精度,(Criteria.ACCURACY_COARSE...)
                    //            c.setSpeedRequired(false);                      //设置是否要求速度
                    //            c.setCostAllowed(false);                        //设置是否允许产生费用
                    //            c.setBearingRequired(false);                    //设置是否需要得到方向
                    //            c.setAltitudeRequired(false);                   //设置是否需要得到海拔高度
                    //            c.setPowerRequirement(Criteria.POWER_LOW);      //设置允许的电池消耗级别
                    //            lm.requestLocationUpdates(2000, 0, c, locationListener, null);
                    ////            lm.getBestProvider(c, false);
                    //        }
                    //    }

                }

                @Override
                public void onFinish() {
                    if(mGoogleServiceCheckTaskListener!=null) {
                        mGoogleServiceCheckTaskListener.googleServiceCheckFinished(googleAvailable);
                    }
                    googleAvailableCheckFinish = true;
                }

                @Override
                public void onResult(String string) {
                    googleAvailable = true;
                    LogUtil.i(TAG, string);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    googleAvailable = false;
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }else{
            mGoogleServiceCheckTaskListener.googleServiceCheckFinished(false);
        }
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

        intent.putExtra(Constant.EXTRA_LOCATION_NAME, name);
        intent.putExtra(Constant.EXTRA_LATITUDE, latitude);
        intent.putExtra(Constant.EXTRA_LONGITUDE, longitude);

        Log.d("","getPlacePickerIntent" + googleAvailable + googleAvailableCheckFinish);

        //判断是用百度还是google
        //判断这个存在缺陷。如果用户网络状态突然更改不翻墙，此时导致用户无法使用谷歌地图，导致此功能不能使用。
        if (!googleAvailable) {
            // 应用启动后用谷歌获取了一次地址信息，如果这次地址信息为空则证明谷歌地图不可用，启用百度地
            intent.setClass(context, Map4BaiduActivity.class);
        } else {
            intent.setClass(context, Map4GoogleActivity.class);
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
        c.setAccuracy(Criteria.ACCURACY_LOW);          //设置查询精度,(Criteria.ACCURACY_COARSE...)
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

        if (TextUtils.isEmpty(locationType)) {
            // 地址类型为空
            locationType = LOCATION_TYPE_GCJ02;//火星坐标
        }

        Intent intent;
        //14为缩放比例
        Log.i("", "goNavigation======" + latitude + "," + longitude);
        Log.i("", "locationType======" + locationType);

//
//        艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹
//        艹艹艹艹艹艹艹艹艹艹艹艹艹艹别用这段代码 坑的一B
//        艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹艹
//        // 判断是否有谷歌服务
//        if(googleAvailable) {
//            if(LOCATION_TYPE_BD09LL.equals(locationType)) {
//                //                if(LOCATION_TYPE_BD09LL.equals(locationType)||LOCATION_TYPE_BD09MC.equals(locationType)){
//                openWebView4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
//            } else {
//                try {
//                    String uri = String.format("geo:%f,%f?z=14&q=%f,%f", latitude, longitude, latitude, longitude);
//                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                    context.startActivity(intent);
//                } catch(Exception e) {
//                    openWebView4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
//                }
//            }
//        } else {
//            //判断没有百度地图
//            if(SystemUtil.isPackageExists(context, BAIDU_MAP_APP_PACKAGE)) {
//                String uri = String.format("intent://map/geocoder?location=%s&coord_type=%s&src=%s#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME);
//                try {
//                    intent = Intent.getIntent(uri);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    context.startActivity(intent);
//                } catch(Exception e) {
//                    e.printStackTrace();
//                    openWebView4BaiduMap(context, latitude, longitude, locationType);
//                }
//
//            } else {
//                openWebView4BaiduMap(context, latitude, longitude, locationType);
//            }
//
//        }

        /**
         * christopher begin
         * 先判断坐标类型：baidu，google
         * baidu：直接打开
         * google：需再判断网络
         */
        if (LOCATION_TYPE_BD09LL.equals(locationType))//百度
        {
            goWithBaidu(context, latitude, longitude, locationType);

        }
        else//google
        {
            if (googleAvailable)//翻墙+google服务
            {
                try {
                    String uri = String.format("geo:%f,%f?z=14&q=%f,%f", latitude, longitude, latitude, longitude);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(intent);
                } catch (Exception e) {
                    openWebView4BaiduMap(context, latitude, longitude, LOCATION_TYPE_BD09LL);
                }
            }
            else//不翻墙，暂时先直接打开webview
            {
                String uri = "http://www.google.cn/maps/place/%s,%s";
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri content_url = Uri.parse(String.format(uri, latitude, longitude));
                intent.setData(content_url);
                context.startActivity(intent);
            }
        }

        /**
         * christopher end
         */


    }

    private static void goWithBaidu(Context context, double latitude, double longitude, String locationType) {
        Intent intent;//判断没有百度地图
        if (SystemUtil.isPackageExists(context, BAIDU_MAP_APP_PACKAGE)) {
            String uri = String.format("intent://map/geocoder?location=%s&coord_type=%s&src=%s#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME);
            try {
                intent = Intent.getIntent(uri);
                intent.setAction(Intent.ACTION_VIEW);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                openWebView4BaiduMap(context, latitude, longitude, locationType);
            }

        } else {
            openWebView4BaiduMap(context, latitude, longitude, locationType);
        }
    }

    private static void openWebViewGoogle2baidu(final Context context, final double latitude, final double longitude) {
        String url = String.format("http://api.map.baidu.com/geoconv/v1/?coords=%s,%s&mcode=%s&from=3&to=5&ak=%s", longitude, latitude, "E9:37:78:D3:36:04:44:E3:D3:51:84:CA:D3:17:57:07:5A:67:75:E2;com.bondwithme.BondWithMe", context.getResources().getString(R.string.baidu_maps_key));
        LogUtil.d(TAG, "0string============" + url);
        new HttpTools(context).get(url, null, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                LogUtil.d(TAG, "2string============");
            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "1string============" + string);
                try {
                    JSONArray result = (JSONArray) new JSONObject(string).get("result");
                    double x = 0;
                    double y = 0;
                    if (result != null && result.length() > 0) {
                        JSONObject jsonObject = (JSONObject) result.get(0);
                        x = (double) jsonObject.get("x");
                        y = (double) jsonObject.get("y");
                    }
                    LogUtil.d(TAG, "4string============" + x);
                    LogUtil.d(TAG, "4string============" + y);
                    goWithBaidu(context, y, x, LOCATION_TYPE_BD09LL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private static void openWebView4BaiduMap(Context context, double latitude, double longitude, String locationType) {
        String uri = "http://api.map.baidu.com/geocoder?location=%s&coord_type=%s&output=html&src=%s";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(String.format(uri, (latitude + "," + longitude), locationType, AppInfoUtil.APP_NAME));
        LogUtil.d(TAG, "5string============" + content_url);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    /**
     * 获取地图位置截图，得到图片的网络路径
     *
     * @param context      上下文资源
     * @param latitude     纬度坐标
     * @param longitude    经度坐标
     * @param locationType 坐标类型({@link #LOCATION_TYPE_BD09LL}, {@link #LOCATION_TYPE_GCJ02}, {@link #LOCATION_TYPE_WGS84})
     * @return 返回的所需位置图片的URL路径
     */
    public static String getLocationPicUrl(Context context, String latitude, String longitude, String locationType) {
        LogUtil.i(TAG, "getLocationPicUrl& locationType: " + locationType);
        String location;
        String result;
        if (googleAvailable) {
            // 谷歌地图片服务可用，并能获取到位置
            if (LOCATION_TYPE_BD09LL.equals(locationType)) {//这里判断是有google服务和翻墙网络。但是还是使用的百度地图，因为需要经过百度地图坐标转火星坐标。TODO
                // 坐标类型为百度的坐标从百度服务获取路径
                location = longitude + "," + latitude;
                result = String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_BAIDU, location, context.getString(R.string.google_map_pic_size), location);
            } else {
                // 从谷歌获取路径
                location = latitude + "," + longitude;
                result = String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, location, context.getString(R.string.google_map_pic_size), location);
                //                return String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_GOOGLE, latitude + "," + longitude, context.getString(R.string.google_map_pic_size), latitude + "," + longitude);
            }
        } else {
            // 谷歌服用获取不到位置，从百度服务中获取图片路径
            location = longitude + "," + latitude;
            result = String.format(Constant.MAP_API_GET_LOCATION_PIC_BY_BAIDU, location, context.getString(R.string.google_map_pic_size), location);
        }
        LogUtil.i(TAG, "getLocationPicUrl& result picture url: " + result);
        return result;
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
        return gps;

    }

    /**
     * 强制帮用户打开 GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {

        final Intent poke = new Intent();
        poke.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        context.sendBroadcast(poke);
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

        LatLng sourceLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
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
}
