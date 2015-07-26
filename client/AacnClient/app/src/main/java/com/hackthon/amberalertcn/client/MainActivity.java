package com.hackthon.amberalertcn.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private boolean m_isLogin = false;
    private Button m_btnLogin;
    private String accessToken;

    private static final String TAG = "MainActivity";
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_btnLogin = (Button) findViewById(R.id.btnLogin);
        m_btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        registerReceiver(m_userIdReceiver, new IntentFilter(PushReveiver.USER_ID_INTENT));

        //start to get Location.
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        mLocationClient.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    m_isLogin = true;
                    accessToken = data.getStringExtra(LoginActivity.ACCESS_TOKEN);
                    Log.i(TAG, "The access token is " + accessToken);
                    PushManager.startWork(this, PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
                }
                break;
        }
    }

    private static final String HTTP_RESPONSE_AMBER_USER_ID = "amber_user_id";
    private static final String HTTP_RESPONSE_AMBER_DEVICE_ID = "amber_device_id";

    public class BaiduPushHandler extends JsonHttpResponseHandler{
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            try {
                int status = response.getInt("status_code");
                String amberUserId = response.getString(HTTP_RESPONSE_AMBER_USER_ID);
                String amberDeviceId = response.getString(HTTP_RESPONSE_AMBER_DEVICE_ID);
                Log.i(TAG, "status=" +  statusCode + ", userId=" + amberUserId + ", amberDevId=" + amberDeviceId);
            }
            catch (JSONException e)
            {
                Log.e(TAG, "Exception", e);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.e(TAG, "ErrorArray", throwable);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.e(TAG, "ErrorObj", throwable);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.e(TAG, "Error:" + responseString, throwable);
        }
    }

    private void sendRequestToServer(String userId, String channelId) {
        // TODO: send to baidu

        String url = "http://10.172.120.69:5001/api/v1/updatelocation?&user_id=%s&channel_id=%s&longitude=%f&latitude=%f";
        String formattedUrl = String.format(url, m_userId, m_channelId, m_longtitude, m_latitude);
        Log.i(TAG, "sendRequestToServer: " + formattedUrl);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(formattedUrl, null, new BaiduPushHandler());
    }

    private String m_userId;
    private String m_channelId;
    private double m_latitude;
    private double m_longtitude;


    private BroadcastReceiver m_userIdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == PushReveiver.USER_ID_INTENT)
            {
                m_userId = intent.getStringExtra(PushReveiver.EXTRA_USER_ID);
                m_channelId = intent.getStringExtra(PushReveiver.EXTRA_CHANNEL_ID);
                Log.i(TAG, "UserId=" + m_userId + ", ChannelId = " + m_channelId);
                sendRequestToServer(m_userId, m_channelId);
            }
        }
    };

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            m_longtitude = location.getLongitude();
            m_latitude = location.getLatitude();
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");// 位置语义化信息
            sb.append(location.getCity());
            System.out.printf(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }
}
