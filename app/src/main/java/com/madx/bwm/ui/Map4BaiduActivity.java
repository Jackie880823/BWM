package com.madx.bwm.ui;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.madx.bwm.R;

public class Map4BaiduActivity extends BaseActivity {

    private MapView mMapView; // Might be null if Google Play services APK is not available.
    private LocationManager lm = null;
    private Location myLocation;
    private static final int INTERVAL_TIME = 2000;
    private Marker marker;
    private boolean toLocation = true;
    private LocationClient mLocClient;
    private BaiduMap mBaiduMap;
    public MyLocationListener myListener = new MyLocationListener();
    boolean isFirstLoc = true;// 是否首次定位
    /**记录当前位置*/
    private double mCurrentLantitude;
    private double mCurrentLongitude;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_map_choose_location);
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
//        rightTextButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void titleLeftEvent() {
        setResult();
        super.titleLeftEvent();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            if(event.getAction()==KeyEvent.ACTION_DOWN) {
                setResult();
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void titleRightEvent() {
//        setResult();
//        finish();
    }

    private void setResult() {
        if (marker != null) {
            Intent intent = new Intent();
            intent.putExtra("location_name", marker.getTitle());
            intent.putExtra("latitude", marker.getPosition().latitude);
            intent.putExtra("longitude", marker.getPosition().longitude);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_map_choose_location_baidu;
    }

    @Override
    protected Fragment getFragment() {
        return null;
//        return  ChooseMapFragment.newInstance();
    }

    @Override
    public void initView() {

        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();


        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                MyLocationData.Builder dbl = new MyLocationData.Builder();
                MyLocationData data = dbl.build();
                point.toString();
//                location.getAddrStr();
                addMarker(point);
            }
        });
    }

    private void addMarker(LatLng latLng){
        mBaiduMap.clear();
        BitmapDescriptor bd = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_map_target);
        OverlayOptions ooA = new MarkerOptions().position(latLng).icon(bd);
        mBaiduMap.addOverlay(ooA);
    }


//    private void getLocationAddress(){
//        //实例化一个地理编码查询对象
//        GeoCoder geoCoder = GeoCoder.newInstance();
//        //设置反地理编码位置坐标
//        ReverseGeoCodeOption op = new ReverseGeoCodeOption();
//        op.location(latLng);
//        //发起反地理编码请求(经纬度->地址信息)
//        geoCoder.reverseGeoCode(op);
//        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
//                //获取点击的坐标地址
//                address = arg0.getAddress();
//                System.out.println("address="+address);
//            }
//
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult arg0) {
//            }
//        });
//    }

    @Override
    public void requestData() {

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }


//    /**
//     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
//     * just add a marker near Africa.
//     * <p/>
//     * This should only be called once and when we are sure that {@link #mMapView} is not null.
//     */
//    private void setUpMap(double latitude, double longitude) {
//        LatLng latLng = new LatLng(latitude, longitude);
//        addMarker(latLng);
//        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng
//                , 16);
////                , getResources().getInteger(R.integer.map_zoom_initial));
//        mMapView.animateCamera(cameraUpdate);
//    }
//
//    private String getBestProvider(Context context) {
//        Criteria criteria = getCriteria();
//
//        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        // 这里可能返回 null, 地理位置信息服务未开启
//        return lm.getBestProvider(criteria, true);
//    }
//
//    public Location getLastKnowLocation(Context context) {
//        Location ret = null;
//        String bestProvider = getBestProvider(context);
//        if (TextUtils.isEmpty(bestProvider)) {
//            // 这里可能会返回 null, 表示按照当前的查询条件无法获取系统最后一次更新的地理位置信息
//            ret = lm.getLastKnownLocation(bestProvider);
//        }
//        return ret;
//    }
//
//
//    public Criteria getCriteria() {
//        Criteria c = new Criteria();
//        c.setAccuracy(Criteria.ACCURACY_FINE);          //设置查询精度,(Criteria.ACCURACY_COARSE...)
//        c.setSpeedRequired(false);                      //设置是否要求速度
//        c.setCostAllowed(false);                        //设置是否允许产生费用
//        c.setBearingRequired(false);                    //设置是否需要得到方向
//        c.setAltitudeRequired(false);                   //设置是否需要得到海拔高度
//        c.setPowerRequirement(Criteria.POWER_LOW);      //设置允许的电池消耗级别
//        return c;
//    }
//
//    /**
//     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
//     * installed) and the map has not already been instantiated.. This will ensure that we only ever
//     * <p/>
//     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
//     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
//     * install/update the Google Play services APK on their device.
//     * <p/>
//     * A user can return to this FragmentActivity after following the prompt and correctly
//     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
//     * have been completely destroyed during this process (it is likely that it would only be
//     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
//     * method in {@link #onResume()} to guarantee that it will be called.
//     */
//    private void setUpMapIfNeeded(boolean hasLocation, double latitude, double longitude) {
//
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMapView == null) {
//
//            // Try to obtain the map from the SupportMapFragment.
//            mMapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//            if (mMapView != null) {
//                mMapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                mMapView.setMyLocationEnabled(true);
//
//                if (!hasLocation) {
//                    if (!TextUtils.isEmpty(getBestProvider(this))) {
//                        myLocation = getLastKnowLocation(this);
//                        if (myLocation == null) {
//                            lm.requestLocationUpdates(INTERVAL_TIME, 0, getCriteria(), myLocationListener, null);
//                        } else {
//                            setUpMap(myLocation.getLatitude(), myLocation.getLongitude());
//                        }
//                    }
//                } else {
//                    setUpMap(latitude, longitude);
//                }
//
//
//                mMapView.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//                    @Override
//                    public void onMapLongClick(LatLng latLng) {
//                        toLocation = false;
//                        latLng.describeContents();
//                        new Location(latLng.toString());
//                        addMarker(latLng);
//                        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
//                    }
//                });
//            }
//
//
//        }
//    }
//
//    private String POSITION_GETTING = "地址正在加载...";
//    private final static int MSG_VIEW_LONGPRESS = 10;
//    private final static int MSG_VIEW_ADDRESSNAME = 11;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_VIEW_LONGPRESS://处理长按时间返回位置信息{
////                    overlayitem = new OverlayItem(locPoint, "地址名称",
////                            "正在地址加载...");
////                    if (mLongPressOverlay.size() > 0) {
////                        mLongPressOverlay.removeOverlay(0);
////                    }
////                    popView.setVisibility(View.GONE);
////                    mLongPressOverlay.addOverlay(overlayitem);
////                    mLongPressOverlay.setFocus(overlayitem);
////                    mapOverlays.add(mLongPressOverlay);
////                    mMapCtrl.animateTo(locPoint);
////                    mapView.invalidate();
//                    break;
//                case MSG_VIEW_ADDRESSNAME:
//                    //获取到地址后显示在泡泡上
//                    marker.setTitle((String) msg.obj);
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 用线程异步获取
//     */
//    class GetAddressRunnable implements Runnable {
//        private double latitude;
//        private double longitude;
//
//        public GetAddressRunnable(double latitude, double longitude) {
//            this.latitude = latitude;
//            this.longitude = longitude;
//        }
//
//        @Override
//        public void run() {
//            String addressName = "";
//            while (true) {
//                addressName = LocationUtil.getLocationAddress(getApplicationContext(),latitude, longitude);
//                if (!"".equals(addressName)) {
//                    break;
//                }
//            }
//
//            Message msg = new Message();
//            msg.what = MSG_VIEW_ADDRESSNAME;
//            msg.obj = addressName;
//            mHandler.sendMessage(msg);
//        }
//    }
//
//    ;
//
//    private void setUpMapIfNeededNoLocation() {
//        setUpMapIfNeeded(false, 0, 0);
//    }
//
//    private void setUpMapIfNeededWithLocation(double latitude, double longitude) {
//        setUpMapIfNeeded(true, latitude, longitude);
//    }
//
//    private void addMarker(LatLng latLng) {
//        mMapView.clear();
//        marker = mMapView.addMarker(new MarkerOptions().position(latLng));
//        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
//    }
//
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位；
     * 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
     */
    private void center2myLoc()
    {
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }

}
