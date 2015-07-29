package com.hackthon.amberalertcn.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
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
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_position){
                Intent it  = new Intent(getApplicationContext(), MapActivity.class);
                startActivityForResult(it, 100);
            }

            if (v.getId() == R.id.btn_confirm){
                sendRequestToServer();
            }
        }
    };

    private void sendRequestToServer() {
        String url = HttpConstant.PUBLISHALERT;
        String formattedUrl = String.format(url, m_userId, m_channelId, longitude, latitude, Uri.encode(uname), face_id);
        Log.i("XXX", "sendRequestToServer: " + formattedUrl);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(this, formattedUrl, null, "application/json", alertHandler);
        pd.setTitle("Sending");
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
                Toast.makeText(getApplicationContext(), "发送成功,"+ count +"人收到通知", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e("XXX", "Exception", e);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
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

            tvPosition.setText(addr);
        }
    }
}
