package com.hackthon.amberalertcn.client;

import android.content.Context;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;

import android.content.Intent;
import android.util.Log;

/**
 * Created by icyfox-bupt on 7/26/2015.
 */

public class PushReveiver extends PushMessageReceiver {

    private static final String TAG = "PushReceiver";
    public static final String USER_ID_INTENT = "com.hachthon.amberalertcn.UserIdIntent";
    public static final String EXTRA_USER_ID = "UserId";
    public static final String EXTRA_CHANNEL_ID = "ChannelId";

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        Log.i(TAG, "onbind " + errorCode + " " + userId + " " + channelId);
        Intent intent = new Intent(USER_ID_INTENT);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        context.sendBroadcast(intent);
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
