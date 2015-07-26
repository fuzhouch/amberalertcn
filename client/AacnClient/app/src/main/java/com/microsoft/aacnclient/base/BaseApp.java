package com.microsoft.aacnclient.base;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by icyfox-bupt on 7/26/2015.
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }

}
