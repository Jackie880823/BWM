package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.madx.bwm.R;
import com.madx.bwm.util.LocationUtil;

public class Map4GoogleActivity extends BaseActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager lm = null;
    private Location myLocation;
    private MyLocationListener myLocationListener = null;
    private static final int INTERVAL_TIME = 2000;
    private Marker marker;
    private boolean toLocation = true;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_map_choose_location);
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
        return R.layout.fragment_map_choose_location_google;
    }

    @Override
    protected Fragment getFragment() {
        return null;
//        return  ChooseMapFragment.newInstance();
    }


    @Override
    public void initView() {
//        mapView = (MapView) findViewById(R.id.map_view);
//        mapView.setBuiltInZoomControls(true);
//        mapView.setClickable(true);
//        initPopView();
//        mMapCtrl = mapView.getController();
//        myLocationOverlay = new MyItemizedOverlay(myLocationDrawable,this, mapView, popView, mMapCtrl);
//        mLongPressOverlay = new MyItemizedOverlay(mylongPressDrawable,this, mapView, popView, mMapCtrl);
//        mapOverlays = mapView.getOverlays();
//        mapOverlays.add(new LongPressOverlay(this, mapView, mHandler, mMapCtrl));
//        //以北京市中心为中心
//        GeoPoint cityLocPoint = new GeoPoint(39909230, 116397428);
//        mMapCtrl.animateTo(cityLocPoint);
//        mMapCtrl.setZoom(12);
//        FzLocationManager.init(FzMapActivity.this.getApplicationContext() , FzMapActivity.this);
//        fzLocation = FzLocationManager.getContextInstance();


        myLocationListener = new MyLocationListener();
        Intent intent = getIntent();
//        if (intent.getBooleanExtra("has_location", false)) {
        if (!TextUtils.isEmpty(intent.getStringExtra("location_name"))) {
            setUpMapIfNeededWithLocation(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
        } else {
            setUpMapIfNeededNoLocation();
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        addMarker(latLng);
        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng
                , 16);
//                , getResources().getInteger(R.integer.map_zoom_initial));
        mMap.animateCamera(cameraUpdate);
    }

    private String getBestProvider(Context context) {
        Criteria criteria = getCriteria();

        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 这里可能返回 null, 地理位置信息服务未开启
        return lm.getBestProvider(criteria, true);
    }

    public Location getLastKnowLocation(Context context) {
        Location ret = null;
        String bestProvider = getBestProvider(context);
        if (TextUtils.isEmpty(bestProvider)) {
            // 这里可能会返回 null, 表示按照当前的查询条件无法获取系统最后一次更新的地理位置信息
            ret = lm.getLastKnownLocation(bestProvider);
        }
        return ret;
    }


    public Criteria getCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);          //设置查询精度,(Criteria.ACCURACY_COARSE...)
        c.setSpeedRequired(false);                      //设置是否要求速度
        c.setCostAllowed(false);                        //设置是否允许产生费用
        c.setBearingRequired(false);                    //设置是否需要得到方向
        c.setAltitudeRequired(false);                   //设置是否需要得到海拔高度
        c.setPowerRequirement(Criteria.POWER_LOW);      //设置允许的电池消耗级别
        return c;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(boolean hasLocation, double latitude, double longitude) {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);

                if (!hasLocation) {
                    if (!TextUtils.isEmpty(getBestProvider(this))) {
                        myLocation = getLastKnowLocation(this);
                        if (myLocation == null) {
                            lm.requestLocationUpdates(INTERVAL_TIME, 0, getCriteria(), myLocationListener, null);
                        } else {
                            setUpMap(myLocation.getLatitude(), myLocation.getLongitude());
                        }
                    }
                } else {
                    setUpMap(latitude, longitude);
                }


                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        toLocation = false;
                        latLng.describeContents();
                        new Location(latLng.toString());
                        addMarker(latLng);
                        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
                    }
                });
            }


        }
    }

    private String POSITION_GETTING = "地址正在加载...";
    private final static int MSG_VIEW_LONGPRESS = 10;
    private final static int MSG_VIEW_ADDRESSNAME = 11;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VIEW_LONGPRESS://处理长按时间返回位置信息{
//                    overlayitem = new OverlayItem(locPoint, "地址名称",
//                            "正在地址加载...");
//                    if (mLongPressOverlay.size() > 0) {
//                        mLongPressOverlay.removeOverlay(0);
//                    }
//                    popView.setVisibility(View.GONE);
//                    mLongPressOverlay.addOverlay(overlayitem);
//                    mLongPressOverlay.setFocus(overlayitem);
//                    mapOverlays.add(mLongPressOverlay);
//                    mMapCtrl.animateTo(locPoint);
//                    mapView.invalidate();
                    break;
                case MSG_VIEW_ADDRESSNAME:
                    //获取到地址后显示在泡泡上
                    marker.setTitle((String) msg.obj);
                    break;
            }
        }
    };

    /**
     * 用线程异步获取
     */
    class GetAddressRunnable implements Runnable {
        private double latitude;
        private double longitude;

        public GetAddressRunnable(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void run() {
            String addressName = "";
            while (true) {
                addressName = LocationUtil.getLocationAddress(getApplicationContext(),latitude, longitude);
                if (!"".equals(addressName)) {
                    break;
                }
            }

            Message msg = new Message();
            msg.what = MSG_VIEW_ADDRESSNAME;
            msg.obj = addressName;
            mHandler.sendMessage(msg);
        }
    }

    ;

    private void setUpMapIfNeededNoLocation() {
        setUpMapIfNeeded(false, 0, 0);
    }

    private void setUpMapIfNeededWithLocation(double latitude, double longitude) {
        setUpMapIfNeeded(true, latitude, longitude);
    }

    private void addMarker(LatLng latLng) {
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions().position(latLng));
        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(toLocation) {
                toLocation = false;
                setUpMap(location.getLatitude(), location.getLongitude());
            }
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


}
