package com.madx.bwm.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.madx.bwm.R;
import com.madx.bwm.db.PlacesDisplayTask;
import com.madx.bwm.util.LocationUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Map4GoogleActivity extends BaseActivity implements GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager lm = null;
    private MyLocationListener myLocationListener = null;
    private static final int INTERVAL_TIME = 2000;
    private Marker marker;
    private boolean toLocation = true;
    private SearchView search_view;
    private RecyclerView address_suggest_list;
    private LatLng myLocation;

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

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
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

    Geocoder geocoder;
    boolean gpsIsEnabled;

    @Override
    public void initView() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gpsIsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean networkIsEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if(!gpsIsEnabled&&!networkIsEnabled){
//            MessageUtil.showMessage(this,"无法定位");
//            return;
//        }

        myLocationListener = new MyLocationListener();
        Intent intent = getIntent();
//        if (intent.getBooleanExtra("has_location", false)) {
        if (!TextUtils.isEmpty(intent.getStringExtra("location_name"))) {
            setUpMapIfNeededWithLocation(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
        } else {
            setUpMapIfNeededNoLocation();
        }

        geocoder = new Geocoder(this, Locale.getDefault());


        search_view = getViewById(R.id.search_view);
        address_suggest_list = getViewById(R.id.address_suggest_list);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {

                // 得到输入管理对象
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                    imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                }
                search_view.clearFocus(); // 不获取焦点
                address_suggest_list.setVisibility(View.GONE);

                if (search_view != null) {
                    if (!TextUtils.isEmpty(queryText)) {
                        try {
//                            new GeocoderTask().execute(queryText);
                            searchByAddress(search_view.getQuery().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String queryText) {

                if (!TextUtils.isEmpty(queryText)) {
//                    mSuggestionSearch
//                            .requestSuggestion((new SuggestionSearchOption())
//                                    .keyword(queryText).city(city));
                }
                return true;
            }
        });
    }

    private static final int MAX_SEARCH_RESULT = 10;
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

        new HttpTools(this).get(googlePlacesUrl.toString(), null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
                Object[] toPass = new Object[2];
                toPass[0] = mMap;
                toPass[1] = response;
                placesDisplayTask.execute(toPass);
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
//                addMarker(latLng, address.getAdminArea(), addressString);
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
//        addMarker(latLng);
        center2Location(latLng);
        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();

//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng
//                , 14);
//                , getResources().getInteger(R.integer.map_zoom_initial));
//        mMap.animateCamera(cameraUpdate);
    }

    private String getBestProvider(Context context) {
        Criteria criteria = getCriteria();
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
                        Location location = getLastKnowLocation(this);
                        if (location == null) {
                            lm.requestLocationUpdates(INTERVAL_TIME, 0, getCriteria(), myLocationListener, null);
                        } else {
//                            setUpMap(myLocation.getLatitude(), myLocation.getLongitude());
                            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            center2Location(myLocation);
                        }
                    }
                } else {
                    myLocation = new LatLng(latitude, longitude);
                    center2Location(myLocation);
                    addTarget(myLocation);
//                    setUpMap(latitude, longitude);
                }


                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        toLocation = false;
                        latLng.describeContents();
                        new Location(latLng.toString());
                        addTarget(latLng);
                        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
                    }
                });
            }


        }
    }

    private void center2Location(LatLng location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location
                , 14);
        mMap.animateCamera(cameraUpdate);
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
                    if (marker != null) {
//                        marker.setSnippet((String) msg.obj);
                        marker.setTitle((String) msg.obj);
                        marker.showInfoWindow();
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onMyLocationButtonClick() {

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
            String addressName = "";
            while (true) {
                addressName = LocationUtil.getLocationAddress(getApplicationContext(), latitude, longitude);
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

    private void setUpMapIfNeededNoLocation() {
        setUpMapIfNeeded(false, 0, 0);
    }

    private void setUpMapIfNeededWithLocation(double latitude, double longitude) {
        setUpMapIfNeeded(true, latitude, longitude);
    }

    /**
     * 添加目标
     *
     * @param latLng
     */
    private void addTarget(LatLng latLng) {
//        mMap.clear();
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_target)));
//        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_target));
        marker.setTitle("");
//        marker.setTitle(getString(R.string.text_destination));
        marker.setSnippet("");
        marker.setDraggable(true);
        new Thread(new GetAddressRunnable(latLng.latitude, latLng.longitude)).start();
    }

    private Marker addMarker(LatLng latLng, String title, String snippet) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
//        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_target));
        marker.setTitle(title);
//        marker.setSnippet(snippet);
        marker.showInfoWindow();
        return marker;
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (toLocation) {
                toLocation = false;
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
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

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], MAX_SEARCH_RESULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            mMap.clear();
            addTarget(myLocation);

//            LatLng firstLatLng = null;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Address address : addresses) {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                if (firstLatLng == null) {
//                    firstLatLng = latLng;
//                    center2Location(firstLatLng);
//                }
                int maxLine = address.getMaxAddressLineIndex();
                String addressString;
                if (maxLine >= 2) {
                    addressString = address.getAddressLine(1) + address.getAddressLine(2);
                } else {
                    addressString = address.getAddressLine(1);
                }
                Marker marker = addMarker(latLng, addressString, addressString);
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 10; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);

        }
    }


    public void performSearch() throws Exception {


//        CursorLoader cLoader = null;
//        if(arg0==0)
//            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
//        else if(arg0==1)
//            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);


//        try {
//            System.out.println("Perform Search ....");
//            System.out.println("-------------------");
//            HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
//            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
//            Map<String,String> params = new HashMap<>();
//            request.url.put("key", "");
//            request.url.put("location", latitude + "," + longitude);
//            request.url.put("radius", 500);
//            request.url.put("sensor", "false");
//
//            if (PRINT_AS_STRING) {
//                System.out.println(request.execute().parseAsString());
//            } else {
//
//                PlacesList places = request.execute().parseAs(PlacesList.class);
//                System.out.println("STATUS = " + places.status);
//                for (Place place : places.results) {
//                    System.out.println(place);
//                }
//            }
//
//        } catch (HttpResponseException e) {
//            System.err.println(e.response.parseAsString());
//            throw e;
//        }
    }

}
