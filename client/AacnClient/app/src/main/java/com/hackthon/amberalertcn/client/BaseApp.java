package com.hackthon.amberalertcn.client;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import java.util.List;

/**
 * Created by v-xuesli on 7/26/2015.
 */
public class BaseApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
