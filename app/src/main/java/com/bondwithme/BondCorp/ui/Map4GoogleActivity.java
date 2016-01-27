package com.bondwithme.BondCorp.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.PlaceAutocompleteAdapter;
import com.bondwithme.BondCorp.task.PlacesDisplayTask;
import com.bondwithme.BondCorp.util.LocationUtil;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.util.MessageUtil;
import com.bondwithme.BondCorp.util.SDKUtil;
import com.bondwithme.BondCorp.util.UIUtil;
import com.bondwithme.BondCorp.widget.MapWrapperLayout;
import com.bondwithme.BondCorp.widget.OnInfoWindowElemTouchListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Map4GoogleActivity extends BaseActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = Map4GoogleActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager lm = null;
    private MyLocationListener myLocationListener = null;
    private static final int INTERVAL_TIME = 2000;
    private Marker target;
    private boolean toLocation = true;
    //    private SearchView search_view;
    private AutoCompleteTextView mAutocompleteView;
    private LatLng myLocation;


    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private Button btn_search;
    //    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
    //            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));


    MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;

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
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_LOCATION_NAME, name);
        intent.putExtra(Constant.EXTRA_LATITUDE, latLng.latitude);
        intent.putExtra(Constant.EXTRA_LONGITUDE, latLng.longitude);
        setResult(RESULT_OK, intent);
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

    Geocoder geocoder;
    boolean gpsIsEnabled;

    @Override
    public void initView() {
        // Try to obtain the map from the SupportMapFragment.
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (target != null) {
                    target.hideInfoWindow();
                }
            }
        });
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        myLocationListener = new MyLocationListener();


        geocoder = new Geocoder(this, Locale.getDefault());
        mAutocompleteView = getViewById(R.id.autocomplete_places);
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, 0 /* clientId */, this).addApi(Places.GEO_DATA_API).build();
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(Map4GoogleActivity.this, android.R.layout.simple_list_item_1, mGoogleApiClient, null, null);
        mAutocompleteView.setAdapter(mAdapter);


        mAutocompleteView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //                if(keyCode==KeyEvent.KEYCODE_SEARCH){
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> results = mAdapter.getmResultList();
                    if (results != null) {
                        firstResult = true;
                        for (PlaceAutocompleteAdapter.PlaceAutocomplete place : results) {
                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.placeId.toString());
                            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                        }
                        hideSoftInputWindow();
                    }
                }
                return false;
            }
        });
        //        btn_search = getViewById(R.id.btn_search);
        //        btn_search.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        ////                try {
        ////                    searchByAddress(mAutocompleteView.getText().toString());
        ////                } catch (IOException e) {
        ////                    e.printStackTrace();
        ////                }
        //                ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> results = mAdapter.getmResultList();
        //                if (results != null) {
        //                    firstResult = true;
        //                    for (PlaceAutocompleteAdapter.PlaceAutocomplete place : results) {
        //                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
        //                                .getPlaceById(mGoogleApiClient, place.placeId.toString());
        //                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        //                    }
        //                }
        ////                getAutocomplete(mAutocompleteView.getText().toString());
        //            }
        //        });
        //        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        //            @Override
        //            public boolean onQueryTextSubmit(String queryText) {
        //
        //                // 得到输入管理对象
        //                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //                if (imm != null) {
        //                    // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
        //                    imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
        //                }
        //                search_view.clearFocus(); // 不获取焦点
        //                mAutocompleteView.setVisibility(View.VISIBLE);
        //
        //                if (search_view != null) {
        //                    if (!TextUtils.isEmpty(queryText)) {
        //                        try {
        //                            searchByAddress(search_view.getQuery().toString());
        //
        //
        //                        } catch (IOException e) {
        //                            e.printStackTrace();
        //                        }
        //                    }
        //                }
        //                return true;
        //
        //            }
        //
        //            @Override
        //            public boolean onQueryTextChange(String queryText) {
        //
        //                if (!TextUtils.isEmpty(queryText)) {
        ////                    mSuggestionSearch
        ////                            .requestSuggestion((new SuggestionSearchOption())
        ////                                    .keyword(queryText).city(city));
        //                }
        //                return true;
        //            }
        //        });

        // MapWrapperLayout initialization
        // 39 - default target height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mapWrapperLayout.init(mMap, UIUtil.getPixelsFromDp(this, 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_map_info_item, null);
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.marker_title);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.marker_address);
        this.infoButton = (Button) infoWindow.findViewById(R.id.btn_target);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        new ColorDrawable(getResources().getColor(R.color.tab_color_normal));
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton, new ColorDrawable(getResources().getColor(R.color.btn_bg_color_gray_normal)), new ColorDrawable(getResources().getColor(R.color.btn_bg_color_gray_press))) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                setResult(marker.getTitle(), marker.getPosition());
                finish();
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current target and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });


        Intent intent = getIntent();
        //        if (intent.getBooleanExtra("has_location", false)) {
        String locationName = intent.getStringExtra(Constant.EXTRA_LOCATION_NAME);

        if ((-1000 == intent.getDoubleExtra(Constant.EXTRA_LATITUDE, 0)) || (-1000 == intent.getDoubleExtra(Constant.EXTRA_LONGITUDE, 0)) || TextUtils.isEmpty(locationName)) {
            //            if (!TextUtils.isEmpty(locationName)) {
            setUpMapIfNeededNoLocation();
        } else {
            setUpMapIfNeededWithLocation(intent.getDoubleExtra(Constant.EXTRA_LATITUDE, 0), intent.getDoubleExtra(Constant.EXTRA_LONGITUDE, 0));
            //            }
        }
    }

    private void hideSoftInputWindow() {
        // 得到输入管理对象
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
            imm.hideSoftInputFromWindow(mAutocompleteView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
        }
        mAutocompleteView.clearFocus(); // 不获取焦点
        mAutocompleteView.dismissDropDown();
    }

    private static final int MAX_SEARCH_RESULT = 20;
    private int PROXIMITY_RADIUS = 5000;

    private void searchByAddress(String searchText) throws IOException {


        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + myLocation.latitude + "," + myLocation.longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&input=" + searchText);
        googlePlacesUrl.append("&sensor=true");
        //        googlePlacesUrl.append("&key=" + "AIzaSyDT_b4XSfwPPIuTTugObeZFi2Wo9M1UBVM");
        googlePlacesUrl.append("&key=" + getString(R.string.google_maps_place_key));

        //        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        //        Object[] toPass = new Object[2];
        //        toPass[0] = mMap;
        //        toPass[1] = googlePlacesUrl.toString();
        //        googlePlacesReadTask.execute(toPass);

        new HttpTools(this).get(googlePlacesUrl.toString(), null, this, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onResult(String response) {
                PlacesDisplayTask task = new PlacesDisplayTask();
                Object[] toPass = new Object[2];
                toPass[0] = mMap;
                toPass[1] = response;

                //for not work in down 11
                if (SDKUtil.IS_HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, toPass);
                } else {
                    task.execute(toPass);
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


        //        List<Address> addresses = geocoder.getFromLocationName(searchText, MAX_SEARCH_RESULT);
        //        if (addresses.size() > 0) {
        //
        //            LatLng firstLatLng = null;
        //            for (Address address : addresses) {
        //                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        //                if (firstLatLng == null) {
        //                    firstLatLng = latLng;
        //                }
        //                int maxLine = address.getMaxAddressLineIndex();
        //                String addressString;
        //                if (maxLine >= 2) {
        //                    addressString = address.getAddressLine(1) + address.getAddressLine(2);
        //                } else {
        //                    addressString = address.getAddressLine(1);
        //                }
        //                setMarkerContent(latLng, address.getAdminArea(), addressString);
        //                Log.i("", "address====" + address.toString());
        //            }
        //
        //
        //            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(firstLatLng
        //                    , 14);
        //
        //            mMap.animateCamera(cameraUpdate);
        //        }
    }

    @Override
    public void requestData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // 地图关闭取消注册的地址改变的监听
        lm.removeUpdates(myLocationListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void msgBarChangeByStatus(int status) {
        if (status == View.GONE) {
            tvMsg.setText(R.string.msg_no_internet);
        }
        super.msgBarChangeByStatus(status);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a target near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        //        setMarkerContent(latLng);
        center2Location(latLng);

        //        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng
        //                , 14);
        //                , getResources().getInteger(R.integer.map_zoom_initial));
        //        mMap.animateCamera(cameraUpdate);
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

        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);

            if (!hasLocation) {
                Location location = LocationUtil.getLastKnowLocation();
                if (location == null) {
                    lm.requestLocationUpdates(INTERVAL_TIME, 0, LocationUtil.getCriteria(), myLocationListener, null);
                } else {
                    //                            setUpMap(myLocation.getLatitude(), myLocation.getLongitude());
                    setMyLocation(location.getLatitude(), location.getLongitude());
                    center2Location(myLocation);
                }
            } else {
                setMyLocation(latitude, longitude);
                center2Location(myLocation);
                //                    setUpMap(latitude, longitude);
            }

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    LocationUtil.getAddressByHttp(getApplicationContext(), latLng.latitude, latLng.longitude, mHandler, MSG_LONG_CLICK_ADDRESS);
                }
            });
        }


    }

    private void center2Location(LatLng location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        mMap.animateCamera(cameraUpdate);
        addTarget(location);
        new Thread(new GetAddressRunnable(location.latitude, location.longitude)).start();
    }

    /**
     * 定位到搜到的位置
     *
     * @param place 位置信息
     */
    private void center2Location(Place place) {
        LatLng location = place.getLatLng();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
        mMap.animateCamera(cameraUpdate);
        addTarget(String.valueOf(place.getName()), String.valueOf(place.getAddress()), location);
    }

    private String POSITION_GETTING = "地址正在加载...";
    private final static int MSG_VIEW_LONGPRESS = 10;
    private final static int MSG_VIEW_ADDRESSNAME = 11;
    private final static int MSG_SERVICE_NOT_AV = 12;
    private final static int MSG_LONG_CLICK_ADDRESS = 13;
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
                case MSG_VIEW_ADDRESSNAME: {
                    Address address = (Address) msg.obj;
                    //获取到地址后显示在泡泡上
                    if (target == null) {
                        addTarget(new LatLng(address.getLatitude(), address.getLongitude()));
                    }
                    setMarkerContent(address, target);
                }
                break;
                case MSG_SERVICE_NOT_AV:
//                    msgBarChangeByStatus(View.VISIBLE);
//                    tvMsg.setText(getString(R.string.msg_service_not_available));
                    break;

                case MSG_LONG_CLICK_ADDRESS: {
                    Address address = (Address) msg.obj;
                    if (address == null) {
                        // 没有获取到地址信息返回不做任何处理
                        return;
                    } else {
                        msgBarChangeByStatus(View.GONE);
                    }

                    toLocation = false;

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));

                    setMarkerContent(address, marker);
                }
                break;
            }
        }
    };

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("", "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    private Marker tempMarker;

    @Override
    public boolean onMarkerClick(Marker marker) {
        //        try {
        //            if (this.tempMarker != null && target != tempMarker) {
        //                tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        //            }
        //            target.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_target));
        //            this.target = target;
        //            tempMarker = target;
        //        } catch (Exception e) {
        //
        //        }
        if (marker != null) {
            marker.showInfoWindow();
        }
        return false;
    }


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
            Address address = null;
            long beginTime = System.currentTimeMillis();
            long lastTime = beginTime;
            //wait 10 second
            while (lastTime - beginTime < 10 * 1000) {
                LogUtil.i(TAG, "run& lastTime: " + lastTime + "; beginTime: " + beginTime);
                try {
                    address = LocationUtil.getAddress(getApplicationContext(), latitude, longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_SERVICE_NOT_AV);
                }
                if (address == null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastTime = System.currentTimeMillis();
                } else {
                    break;
                }
            }

            Message msg = new Message();
            msg.what = MSG_VIEW_ADDRESSNAME;
            msg.obj = address;
            mHandler.removeMessages(MSG_VIEW_ADDRESSNAME);
            mHandler.sendMessage(msg);
        }
    }

    private void setUpMapIfNeededNoLocation() {
        setUpMapIfNeeded(false, 0, 0);
    }

    private void setUpMapIfNeededWithLocation(double latitude, double longitude) {
        setUpMapIfNeeded(true, latitude, longitude);
        if (mAdapter != null) {
            mAdapter.setBounds(new LatLngBounds(myLocation, myLocation));
        }
    }

    /**
     * 添加目标
     *
     * @param latLng
     */
    private void addTarget(LatLng latLng) {
        LogUtil.i(TAG, "addTarget&");
        //        mMap.clear();
        if (target != null) {
            target.remove();
        }
        if (this.tempMarker != null) {
            tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }
        target = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        try {
            Address address = LocationUtil.getAddress(Map4GoogleActivity.this, latLng.latitude, latLng.longitude);
            setMarkerContent(address, target);
        } catch (IOException e) {
            e.printStackTrace();
            target.setTitle("");
            //        target.setTitle(getString(R.string.text_destination));
            target.setSnippet("");
        }
        //        target = mMap.setMarkerContent(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_target)));
        //        target.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_target));
        target.setDraggable(true);
    }

    private void addTarget(String name, String address, LatLng latLng) {
        LogUtil.i(TAG, "addTarget&");
        //        mMap.clear();
        if (target != null) {
            target.remove();
        }
        if (this.tempMarker != null) {
            tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }
        target = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        target.setTitle(name);
        target.setSnippet(address);

        target.showInfoWindow();
        target.setDraggable(true);
    }

    /**
     * 设置显选择地址详细信息弹出框的内容并显示
     *
     * @param address 地址信息
     * @param marker  持有弹出框的标识封装对象
     * @return 返回持有弹出框的标识封装对象
     */
    private void setMarkerContent(Address address, Marker marker) {
        if (address == null) {
            LogUtil.w(TAG, "setMarkerContent address is null");
            return;
        }

        String title;
        String snippet;

        if (address.getMaxAddressLineIndex() > 2) {
            title = address.getAddressLine(1);
            snippet = address.getAddressLine(0);
        } else {
            title = snippet = address.getAddressLine(0);
        }

        // 判空字符对像用""替代
        title = TextUtils.isEmpty(title) ? "" : title;
        snippet = TextUtils.isEmpty(snippet) ? "" : snippet;
        LogUtil.i(TAG, "setMarkerContent& title: " + title + "; snippet: " + snippet);
        marker.setTitle(title);
        marker.setSnippet(snippet);

        marker.showInfoWindow();

        return;
    }

    private void setMyLocation(double latitude, double longitude) {
        myLocation = new LatLng(latitude, longitude);
        if (mAdapter != null) {
            mAdapter.setBounds(new LatLngBounds(myLocation, myLocation));
        }
    }

    /**
     * 地址改变监听事件
     */
    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (toLocation) {
                toLocation = false;
                setMyLocation(location.getLatitude(), location.getLongitude());
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

    /**
     *  An AsyncTask class for accessing the GeoCoding Web Service
     */
    //    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
    //
    //        @Override
    //        protected List<Address> doInBackground(String... locationName) {
    //            // Creating an instance of Geocoder class
    //            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
    //
    //            List<Address> addresses = null;
    //
    //            try {
    //                // Getting a maximum of 3 Address that matches the input text
    //                addresses = geocoder.getFromLocationName(locationName[0], MAX_SEARCH_RESULT);
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //            return addresses;
    //        }
    //
    //        @Override
    //        protected void onPostExecute(List<Address> addresses) {
    //
    //            if (addresses == null || addresses.size() == 0) {
    //                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
    //            }
    //
    //            mMap.clear();
    //            addTarget(myLocation);
    //
    ////            LatLng firstLatLng = null;
    //            LatLngBounds.Builder builder = new LatLngBounds.Builder();
    //            for (Address address : addresses) {
    //                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
    ////                if (firstLatLng == null) {
    ////                    firstLatLng = latLng;
    ////                    center2Location(firstLatLng);
    ////                }
    //                int maxLine = address.getMaxAddressLineIndex();
    //                String addressString;
    //                if (maxLine >= 2) {
    //                    addressString = address.getAddressLine(1) + address.getAddressLine(2);
    //                } else {
    //                    addressString = address.getAddressLine(1);
    //                }
    //                Marker target = setMarkerContent(latLng, addressString, addressString);
    //                builder.include(target.getPosition());
    //            }
    //            LatLngBounds bounds = builder.build();
    //            int padding = 10; // offset from edges of the map in pixels
    //            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
    //            mMap.animateCamera(cu);
    //
    //        }
    //    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            hideSoftInputWindow();

        }
    };


    private boolean firstResult;
    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            try {
                if (!places.getStatus().isSuccess()) {
                    // Request did not complete successfully
                    Log.e("", "Place query did not complete. Error: " + places.getStatus().toString());
                    places.release();
                    return;
                }
                firstResult = true;
                // Get the Place object from the buffer.
                final Place place = places.get(0);

                if (place != null) {
                    // Format details of the place for display and show it in a TextView.

                    LogUtil.i(TAG, "onResult& " + "name:" + place.getName() + ";" + " address:" + place.getAddress().toString());
                    if (firstResult) {
                        firstResult = false;
                        center2Location(place);
                    }
                    places.release();

                    //            // 得到输入管理对象
                    //            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //            if (imm != null) {
                    //                // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                    //                imm.hideSoftInputFromWindow(mAutocompleteView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                    //            }
                }
            } catch (Exception e) {
                MessageUtil.showMessage(Map4GoogleActivity.this, R.string.location_search_error);
            }
        }
    };

}
