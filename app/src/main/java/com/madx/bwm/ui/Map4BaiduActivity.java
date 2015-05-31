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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
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
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener, OnGetGeoCoderResultListener {

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
    private static final int MAX_COUNT = 20;

    String location_name;

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
//        setResult();
        super.titleLeftEvent();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                setResult();
//            }
//        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void titleRightEvent() {
//        setResult();
//        finish();
    }

    private void setResult(String name, LatLng latLng) {
//        if (marker != null) {
        Intent intent = new Intent();
        intent.putExtra("location_name", name);
        intent.putExtra("latitude", latLng.latitude);
        intent.putExtra("longitude", latLng.longitude);
        setResult(RESULT_OK, intent);
//        }
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

    boolean hasSetLocation;

    @Override
    public void initView() {

        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
//                addInfoWindow(mapPoi.getPosition(), mapPoi.getName(), "");
                return true;
            }
        });

//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Log.i("", "onMarkerClick==========");
//                if (poiInfos != null) {
//                    for (PoiInfo poiInfo : poiInfos) {
//                        if (poiInfo.location.latitude == marker.getPosition().latitude && poiInfo.location.longitude == marker.getPosition().longitude) {
//                            addInfoWindow(poiInfo.location, poiInfo.name, poiInfo.address);
//                            break;
//                        }
//                    }
//                }
//                return false;
//            }
//        });


        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(this);
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

        Intent intent = getIntent();


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
                center2myLoc(false);
            }
        });
        if (!TextUtils.isEmpty(intent.getStringExtra("location_name"))) {
            hasSetLocation = true;
            LatLng latLng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
            location_name = intent.getStringExtra("location_name");
            addTarget(latLng, location_name);
            center2myLoc(latLng,true);
        }



    }

    private SuggestionSearch mSuggestionSearch = null;
    private String city = "";

    private void addTarget(LatLng latLng, String title) {
        BitmapDescriptor bd = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_map_target);
        OverlayOptions ooA = new MarkerOptions().position(latLng).icon(bd).title(title).zIndex(5);
        marker = (Marker) mBaiduMap.addOverlay(ooA);
//        addInfoWindow(latLng, title, "");
    }

    private void addMarker(LatLng latLng, String title,String address) {
        BitmapDescriptor bd = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_map_marker);
        OverlayOptions ooA = new MarkerOptions().position(latLng).icon(bd).title(title).zIndex(4);
        mBaiduMap.addOverlay(ooA);
        addInfoWindow(latLng, title, address);
    }

    int indexCount;

    private InfoWindow addInfoWindow(final LatLng latLng, final String name, String address) {

        if (mBaiduMap != null && mBaiduMap.getProjection() != null) {


            Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
            p.y -= 60;
            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);


            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

            View view = inflater.inflate(R.layout.layout_map_info_item, null);
            final TextView marker_title = (TextView) view.findViewById(R.id.marker_title);
            marker_title.setText(name);
            TextView marker_address = (TextView) view.findViewById(R.id.marker_address);
            marker_address.setText(address);
            Button btn_target = (Button) view.findViewById(R.id.btn_target);
            btn_target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(name, latLng);
                    finish();
                }
            });


            InfoWindow infoWindow = new InfoWindow(view, llInfo, indexCount++);

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
                Bundle bundle = new Bundle();
                bundle.putString("name", arg0.getAddress());
                bundle.putString("address", arg0.getAddress());
                bundle.putDouble("latitude", arg0.getLocation().latitude);
                bundle.putDouble("longitude", arg0.getLocation().longitude);
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
                        Bundle bundle = msg.getData();
                    if(bundle!=null) {
//                        addTarget(new LatLng(bundle.getDouble("latitude", 0), bundle.getDouble("longitude", 0)), msg.obj.toString());
                        LatLng latLng = new LatLng(bundle.getDouble("latitude", 0), bundle.getDouble("longitude", 0));
                        PoiInfo poiInfo = new PoiInfo();
                        poiInfo.location = latLng;
                        poiInfo.name = bundle.getString("name");
                        poiInfo.address = bundle.getString("address");
                        poiInfos.add(poiInfo);
                        addMarker(latLng, poiInfo.name, poiInfo.address);
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
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            if(marker!=null) {
                addTarget(marker.getPosition(),location_name);
            }
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
        markerInfoWindow = addInfoWindow(result.getLocation(), result.getName(), result.getAddress());

//        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
//                    .show();
//        } else {
//            Toast.makeText(this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
//                    .show();
//        }
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

                searchByAddress(suggestionInfo.key, suggestionInfo.city);

            }
        });

//        }else{
//            suggestionAdapter.notifyDataSetChanged();
//        }
//        initmPopupWindowView();
    }

    private int load_Index = 0;

    private void searchByAddress(String address) {
        searchByAddress(address, city);
    }

    private void searchByAddress(String address, String city) {
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

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        Log.i("", "onGetGeoCodeResult==========" + geoCodeResult.getAddress());
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        mBaiduMap.clear();
        if(marker!=null) {
            addTarget(marker.getPosition(),location_name);
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("", "onMarkerClick==========" );
                if(poiInfos!=null) {
                    for (PoiInfo poiInfo:poiInfos) {
                        if(poiInfo.location.latitude==marker.getPosition().latitude&&poiInfo.location.longitude==marker.getPosition().longitude){
                            addInfoWindow(poiInfo.location, poiInfo.name, poiInfo.address);
                            break;
                        }
                    }
                }
                return false;
            }
        });

        city = reverseGeoCodeResult.getAddressDetail().city;
        Log.i("", "onGetReverseGeoCodeResult-==========" + reverseGeoCodeResult.getAddress());
        Log.i("", "onGetReverseGeoCodeResult-==========" + reverseGeoCodeResult.getBusinessCircle());
        Log.i("", "onGetReverseGeoCodeResult-==========" + reverseGeoCodeResult.getAddressDetail());

        poiInfos = reverseGeoCodeResult.getPoiList();

        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_marker);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (PoiInfo info : poiInfos) {
            if(marker!=null&&info.location.latitude==marker.getPosition().latitude&&info.location.longitude==marker.getPosition().longitude){
                continue;
            }
            OverlayOptions oo = new MarkerOptions().icon(bd).position(info.location);
            mBaiduMap.addOverlay(oo);
            builder.include(info.location);
        }
        LatLngBounds bounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
        mBaiduMap.animateMapStatus(u);
    }
    List<PoiInfo> poiInfos;

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
                    .direction(300).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            if(!hasSetLocation&&isFirstLoc) {
                isFirstLoc = false;
                center2myLoc(true);

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
    private void center2myLoc(boolean searchNearby) {
        LatLng latLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
        center2myLoc(latLng,searchNearby);

    }

    /**
     * 移动到目标为中心的地点
     *
     * @param latLng
     */
    private void center2myLoc(LatLng latLng, boolean searchNearby) {
        if (latLng == null) {
            return;
        }
//        if(searchNearby) {
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(mCurrentLantitude, mCurrentLongitude)));
            //搜索附近
//        mPoiSearch.searchNearby(new PoiNearbySearchOption().location(latLng).keyword("饭店").pageNum(20));//一定要keyword....
//        } else {
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
//            mBaiduMap.animateMapStatus(u);
//            mBaiduMap.setMapStatus(u);
//        }

    }


    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            try {
                PoiInfo poi = getPoiResult().getAllPoi().get(index);
                // if (poi.hasCaterDetails) {
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUid(poi.uid));
                // }
            } catch (Exception e) {
            }
            return true;
        }
    }

}
