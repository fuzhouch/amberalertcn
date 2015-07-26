package com.hackthon.amberalertcn.client;

import android.content.Context;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;
import android.util.Log;

/**
 * Created by icyfox-bupt on 7/26/2015.
 */

public class PushReveiver extends PushMessageReceiver {

    private static final String TAG = "PushReceiver";

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        Log.i(TAG, "onbind " + errorCode + " " + userId + " " + channelId);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
