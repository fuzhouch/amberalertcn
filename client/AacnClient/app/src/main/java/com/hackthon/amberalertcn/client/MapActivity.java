package com.hackthon.amberalertcn.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

public class MapActivity extends AppCompatActivity {

    private MapView bmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bmap = (MapView) findViewById(R.id.map);
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
