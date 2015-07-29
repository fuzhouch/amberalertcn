package com.hackthon.amberalertcn.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class PostAlertActivity extends AppCompatActivity {

    EditText etDesc;
    TextView tvPosition;
    Button btnConfirm;

    double latitude, longitude;
    String addr;
    SharedPreferences sp;

    String uname, face_id, m_userId, m_channelId;

    ProgressDialog pd;

    LocationClient locClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_alert);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etDesc = (EditText) findViewById(R.id.et_desc);
        tvPosition = (TextView) findViewById(R.id.tv_position);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(click);
        tvPosition.setOnClickListener(click);

        sp = getSharedPreferences("aacn_cache", Context.MODE_PRIVATE);
        uname = sp.getString("uname", "");
        face_id = sp.getString("face_id", "");
        m_channelId = sp.getString("channelid", "");
        m_userId = sp.getString("userid", "");

        pd = new ProgressDialog(this);

        locClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(30 * 1000);
        option.setIsNeedAddress(true);
        locClient.setLocOption(option);
        locClient.registerLocationListener(loc);
        locClient.start();
    }

    BDLocationListener loc = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
          addr = bdLocation.getAddrStr();
          latitude = bdLocation.getLatitude();
          longitude = bdLocation.getLongitude();
          tvPosition.setText(addr);
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_position){
                Intent it  = new Intent(getApplicationContext(), MapActivity.class);
                startActivityForResult(it, 100);
            }

            if (v.getId() == R.id.btn_confirm){
                String msg = etDesc.getText().toString();
                if (TextUtils.isEmpty(msg)){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.empty_msg), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                sendRequestToServer(msg);
            }
        }
    };

    private void sendRequestToServer(String msg) {
        String url = HttpConstant.PUBLISHALERT;
        JSONObject json = new JSONObject();
        StringEntity se = null;
        try {
            json.put("message", msg);
            se = new StringEntity(json.toString(), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        String formattedUrl = String.format(url, m_userId, m_channelId, longitude, latitude, Uri.encode(uname), face_id, Uri.encode(addr));
        Log.i("XXX", "sendRequestToServer: " + formattedUrl + "\n" + se.toString());
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(this, formattedUrl, se, "application/json", alertHandler);
        pd.setTitle(getResources().getString(R.string.sending));
        pd.setMessage(getResources().getString(R.string.wait));
        pd.show();
    }

    JsonHttpResponseHandler alertHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            pd.dismiss();
            try {
                int status = response.getInt("status_code");
                int count = response.optInt("alerted_count");
                String tips = getResources().getString(R.string.sendtip);
                tips = String.format(tips, count);
                Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT).show();
                finish();
            } catch (JSONException e) {
                Log.e("XXX", "Exception", e);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            pd.dismiss();
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.sendfail),
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("XXX", requestCode + " " + resultCode + ": " + data);
        if (data != null){
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            addr = data.getStringExtra("addr");

            if (!TextUtils.isEmpty(addr)) {
                tvPosition.setText(addr);
                locClient.stop();
            }
        }
    }
}
