package com.hackthon.amberalertcn.client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;

import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(s);
        builder.setContentText(s1);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("STRING", s);
        resultIntent.putExtra("STRING1", s1);

        Log.i(TAG, s + " -");
        Log.i(TAG, s1 + " ---");

        String title = "", message = "";
        try {
            JSONObject obj = new JSONObject(s);
            title = obj.optString("title");
            message = obj.optString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
