package com.hackthon.amberalertcn.client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MapActivity extends AppCompatActivity {

    private MapView bmap;
    private BaiduMap mapControl;
    String TAG = "map";
    TextView tvPos;
    LocationClient locClient;

    double latitude, longitude;
    String addr;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bmap = (MapView) findViewById(R.id.map);
        mapControl = bmap.getMap();
        mapControl.setOnMapStatusChangeListener(status);
        tvPos = (TextView) findViewById(R.id.tv_position);
        btnConfirm =(Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(click);

        locClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(0);
        locClient.setLocOption(option);
        locClient.registerLocationListener(bdListener);
        locClient.start();
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.btn_confirm == v.getId()){
                Intent back = new Intent();
                back.putExtra("latitude", latitude);
                back.putExtra("longitude", longitude);
                back.putExtra("addr", addr);
                setResult(0, back);
                finish();
            }
        }
    };

    BDLocationListener bdListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData ldata = new MyLocationData.Builder()
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mapControl.setMyLocationEnabled(true);
            mapControl.setMyLocationData(ldata);

            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);
            mapControl.animateMapStatus(msu);
            locClient.stop();
        }
    };

    BaiduMap.OnMapStatusChangeListener status = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            Log.i(TAG, mapStatus.target.latitude + " - " + mapStatus.target.longitude);
        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {
            Log.i(TAG, mapStatus.target.latitude + " - " + mapStatus.target.longitude);
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            Log.i(TAG, mapStatus.target.latitude + " - " + mapStatus.target.longitude);
            latitude = mapStatus.target.latitude;
            longitude = mapStatus.target.longitude;
            getPosition(mapStatus.target);
        }
    };

    OnGetGeoCoderResultListener geoListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            addr = reverseGeoCodeResult.getAddress();
            tvPos.setText(addr);
        }
    };

    private void getPosition(LatLng latlng){
        GeoCoder gcoder = GeoCoder.newInstance();
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latlng);
        gcoder.setOnGetGeoCodeResultListener(geoListener);
        gcoder.reverseGeoCode(option);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bmap.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bmap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bmap.onPause();
    }
}
