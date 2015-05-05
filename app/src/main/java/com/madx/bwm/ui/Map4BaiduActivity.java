package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.madx.bwm.R;
import com.madx.bwm.adapter.SuggestAddressAdapter;

import java.util.ArrayList;
import java.util.List;

public class Map4BaiduActivity extends BaseActivity implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

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
    /**
     * 记录当前位置
     */
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private GeoCoder mGeoCoder;
    private InfoWindow markerInfoWindow;
    private SearchView search_view;
    private CursorAdapter sugAdapter;
    private List<SuggestionResult.SuggestionInfo> suggestions;
    private SuggestAddressAdapter suggestionAdapter;
    private RecyclerView address_suggest_list;
    private PoiSearch mPoiSearch;

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_map_choose_location);
    }

    @Override
    protected void onDestroy() {

        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
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

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                setResult();
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void titleRightEvent() {
        setResult();
        finish();
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
        //定义缩放比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16);
        mBaiduMap.setMapStatus(msu);
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
        address_suggest_list = getViewById(R.id.address_suggest_list);

        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
//                MyLocationData.Builder dbl = new MyLocationData.Builder();
//                dbl.latitude(point.latitude);
//                dbl.longitude(point.longitude);
//                MyLocationData data = dbl.build();

                getLocationAddress(point);

            }
        });


        search_view = getViewById(R.id.search_view);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                if (search_view != null) {
                    searchByAddress(search_view.getQuery().toString());
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String queryText) {

                if (!TextUtils.isEmpty(queryText)) {
                    Log.i("", "result.city=======" + city);
                    mSuggestionSearch
                            .requestSuggestion((new SuggestionSearchOption())
                                    .keyword(queryText).city(city));
                }
                return true;
            }
        });

        search_view.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                return true;
            }
        });

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(Map4BaiduActivity.this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(Map4BaiduActivity.this);

        findViewById(R.id.my_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                center2myLoc();
            }
        });
    }

    private SuggestionSearch mSuggestionSearch = null;
    private String city = "";

    private void addMarker(LatLng latLng, String title) {
        if (marker == null) {
            BitmapDescriptor bd = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_map_target);
            OverlayOptions ooA = new MarkerOptions().position(latLng).icon(bd).title(title);
            marker = (Marker) mBaiduMap.addOverlay(ooA);
        }

        if (marker != null) {
            marker.setTitle(title);
            marker.setPosition(latLng);
            markerInfoWindow = addInfoWindow(latLng, title);
        }

    }

    int indexCount;

    private InfoWindow addInfoWindow(LatLng latLng, String text) {

        if (mBaiduMap != null && mBaiduMap.getProjection() != null) {
            TextView textView = new TextView(this);
            textView.setText(text);
            textView.setBackgroundResource(R.drawable.popup);
            textView.setGravity(Gravity.CENTER);

            Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
            p.y -= 47;
            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
            InfoWindow infoWindow = new InfoWindow(textView, llInfo, indexCount++);
            mBaiduMap.showInfoWindow(infoWindow);

            return infoWindow;
        }
        return null;
    }

    private void getLocationAddress(final LatLng mLatLng) {
        //实例化一个地理编码查询对象
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption op = new ReverseGeoCodeOption();
        op.location(mLatLng);
        //发起反地理编码请求(经纬度->地址信息)
        geoCoder.reverseGeoCode(op);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                Message message = mHandler.obtainMessage();
                message.what = ADD_MARKER;
                message.obj = arg0.getAddress();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", mLatLng.latitude);
                bundle.putDouble("longitude", mLatLng.longitude);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {

            }
        });
    }

    private final static int ADD_MARKER = 10;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_MARKER:
                    if (msg.obj != null) {
                        Bundle bundle = msg.getData();
                        addMarker(new LatLng(bundle.getDouble("latitude", 0), bundle.getDouble("longitude", 0)), msg.obj.toString());
                    }
                    break;
            }
            return false;
        }
    });


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

    public void onGetPoiResult(PoiResult result) {
        //TODO
        //POI搜索结果（范围检索、城市POI检索、周边检索）
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            return;
        }

//        new OverlayManager(mBaiduMap);
//
//        // 在地图上显示PoiOverlay（将搜索到的兴趣点标注在地图上）
//        mMapView.addOverlay(poioverlay);
//
//
//        if(result.getNumPois() > 0) {
//            // 设置其中一个搜索结果所在地理坐标为地图的中心
//            MKPoiInfo poiInfo = result.getPoi(0);
//            mapController.setCenter(poiInfo.pt);
//        }
        Log.i("","result.error======="+result.error);
        Log.i("","result.error======="+city);
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

//            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//            String strInfo = "在";
//            for (CityInfo cityInfo : result.getSuggestCityList()) {
//                strInfo += cityInfo.city;
//                strInfo += ",";
//            }
//            strInfo += "找到结果";
//            Toast.makeText(Map4BaiduActivity.this, strInfo, Toast.LENGTH_LONG)
//                    .show();

            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    PopupWindow popupwindow;
    private LinearLayoutManager llm;


    public void onGetPoiDetailResult(PoiDetailResult result) {
        //TODO
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggestions = res.getAllSuggestions();
        address_suggest_list.setVisibility(View.VISIBLE);
        address_suggest_list.bringToFront();
        llm = new LinearLayoutManager(this);
        address_suggest_list.setLayoutManager(llm);
        if (suggestions == null) {
            suggestions = new ArrayList<>();
        }
//        if(suggestionAdapter==null) {
            suggestionAdapter = new SuggestAddressAdapter(this, suggestions);
            address_suggest_list.setAdapter(suggestionAdapter);

            suggestionAdapter.setItemCheckListener(new SuggestAddressAdapter.ItemCheckListener() {
                @Override
                public void onItemCheckedChange(final SuggestionResult.SuggestionInfo suggestionInfo) {

                    searchByAddress(suggestionInfo.key,suggestionInfo.city);

                }
            });

//        }else{
//            suggestionAdapter.notifyDataSetChanged();
//        }
//        initmPopupWindowView();
    }

    private int load_Index = 0;
    private void searchByAddress(String address){
        searchByAddress(address,city);
    }

    private void searchByAddress(String address,String city){
        // 得到输入管理对象
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
            imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
        }
        search_view.clearFocus(); // 不获取焦点
        address_suggest_list.setVisibility(View.GONE);
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(address)
                .pageNum(load_Index));
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
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            if (isFirstLoc) {
                isFirstLoc = false;
                center2myLoc();
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//                mBaiduMap.animateMapStatus(u);

            }

            city = location.getCity() == null ? "" : location.getCity();

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 地图移动到我的位置,此处可以重新发定位请求，然后定位；
     * 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
     */
    private void center2myLoc() {
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
    }


    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

}
