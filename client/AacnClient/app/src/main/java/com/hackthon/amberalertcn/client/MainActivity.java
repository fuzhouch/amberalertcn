package com.hackthon.amberalertcn.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private boolean m_isLogin = false;

    private Button m_btnLogin;
    private Button m_btnSend;
    private String accessToken;

    private static final String TAG = "MainActivity";
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    SharedPreferences sp;

    private static final String HTTP_RESPONSE_AMBER_USER_ID = "amber_user_id";
    private static final String HTTP_RESPONSE_AMBER_DEVICE_ID = "amber_device_id";

    private String m_userId;
    private String m_channelId;
    private String uname, face_id;

    private double m_latitude;
    private double m_longtitude;
    private ImageView ivFace, ivLoc;
    private TextView tvId, tvLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("aacn_cache", Context.MODE_PRIVATE);
        accessToken = sp.getString("token", "");
        m_isLogin = sp.getBoolean("login", false);
        m_channelId = sp.getString("channelid", "");
        m_userId = sp.getString("userid", "");

        ivFace = (ImageView) findViewById(R.id.iv_face);
        tvId = (TextView) findViewById(R.id.tv_id);
        ivLoc = (ImageView) findViewById(R.id.iv_loc);
        tvLoc = (TextView) findViewById(R.id.tv_loc);

        m_btnLogin = (Button) findViewById(R.id.btnLogin);
        m_btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (m_isLogin){
                    sendRequestToServer(m_userId, m_channelId);
                }else{
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(intent, 100);
                }
            }
        });

        m_btnSend = (Button) findViewById(R.id.btn_send);
        m_btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AsyncHttpClient client = new AsyncHttpClient();
               String url = HttpConstant.SENDMESSAGE;
               url = String.format(url, m_userId, m_channelId, 1);
               RequestParams rp = new RequestParams();
                StringEntity se = null;
                try {
                    se = new StringEntity("{ \"message\":\"熊孩子再次跑潘家园去了\"}", "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, url);
               client.post(getApplicationContext(), url, se, "application/json", new JsonHttpResponseHandler(){

                   @Override
                   public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                       super.onSuccess(statusCode, headers, response);
                       try {
                           Log.i(TAG, response.toString(2));
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }

                   @Override
                   public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                       super.onFailure(statusCode, headers, responseString, throwable);
                       Log.i(TAG, statusCode +  " " + headers);
                   }
               });

            }
        });

        if (m_isLogin){
            m_btnLogin.setText(getResources().getString(R.string.help));
            showInfo();
            PushManager.startWork(this, PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
        }

        registerReceiver(m_userIdReceiver, new IntentFilter(PushReveiver.USER_ID_INTENT));

        //start to get Location.
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setScanSpan(30 * 1000);
        mLocationClient.setLocOption(option);

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
        if (id == R.id.action_list) {
            startActivity(new Intent(this, LostListActivity.class));
            return true;
        }

        if (id == R.id.action_detail){
            startActivity(new Intent(this, LostDetailActivity.class));
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
                    sp.edit().putBoolean("login", true).apply();
                    sp.edit().putString("token", accessToken).apply();
                    Log.i(TAG, "The access token is " + accessToken);
                    PushManager.startWork(this, PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
                }
                break;
        }
    }

    public class PublishAlertHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);

            try {
                int status = response.getInt("status_code");
                int count = response.optInt("alerted_count");
                Toast.makeText(getApplicationContext(), "发送成功,"+ count +"人收到通知", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
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

        String url = HttpConstant.PUBLISHALERT;
        String formattedUrl = String.format(url, m_userId, m_channelId, m_longtitude, m_latitude, Uri.encode(uname), face_id);
        Log.i(TAG, "sendRequestToServer: " + formattedUrl);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(this, formattedUrl, null, "application/json", new PublishAlertHandler());
    }

    private BroadcastReceiver m_userIdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == PushReveiver.USER_ID_INTENT) {
                m_userId = intent.getStringExtra(PushReveiver.EXTRA_USER_ID);
                m_channelId = intent.getStringExtra(PushReveiver.EXTRA_CHANNEL_ID);

                SharedPreferences.Editor edit = sp.edit();
                edit.putString("userid", m_userId);
                edit.putString("channelid", m_channelId);
                edit.commit();

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

            if (!TextUtils.isEmpty(location.getAddrStr())) {
                tvLoc.setText(location.getAddrStr());
                ivLoc.setImageResource(R.mipmap.loc_icon_press);
            }

            AsyncHttpClient client = new AsyncHttpClient();
            String url = String.format(HttpConstant.UPDATELOC, m_userId, m_channelId, m_longtitude, m_latitude, uname, face_id);
            client.post(url, null, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.i(TAG, response.toString());
                }
            });

        }
    }

    private void showInfo(){
        AsyncHttpClient sslClient = new AsyncHttpClient(true, 80, 443);
        sslClient.get(HttpConstant.BAIDUINFO + accessToken, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                uname = response.optString("uname");
                face_id = response.optString("portrait");

                Picasso.with(getApplicationContext()).load(HttpConstant.BAIDUFACE + face_id)
                        .into(ivFace);
                tvId.setText(uname);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Fetch info failed.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_userIdReceiver);
    }
}