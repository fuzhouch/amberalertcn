package com.hackthon.amberalertcn.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LostDetailActivity extends AppCompatActivity {

    private ListView list;
    private DetailAdapter adapter;
    private View header;
    private Button btnSend;
    private EditText etMsg;
    SharedPreferences sp;
    private String userId, channelId, uname, uface;
    private String detail;

    private TextView tvSender, tvMsg, tvLoc, tvSendTime;
    private ImageView ivFace;

    private List<LostBean> lostList;
    private ProgressDialog pd;
    private int alert_id;
    private LocationClient mLocationClient;

    String addr = "unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_detail);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lostList = new ArrayList<>();

        btnSend = (Button) findViewById(R.id.btn_send);
        etMsg = (EditText) findViewById(R.id.et_msg);
        list = (ListView) findViewById(R.id.list);
        adapter = new DetailAdapter();
        header = getLayoutInflater().inflate(R.layout.item_detail_header, null);
        list.addHeaderView(header);
        list.setAdapter(adapter);
        btnSend.setOnClickListener(click);

        tvSender = (TextView) header.findViewById(R.id.tv_name);
        tvMsg = (TextView) header.findViewById(R.id.tv_desc);
        tvSendTime = (TextView) header.findViewById(R.id.tv_time);
        tvLoc = (TextView) header.findViewById(R.id.tv_loc);
        ivFace = (ImageView) header.findViewById(R.id.iv_face);

        sp = getSharedPreferences("aacn_cache", Context.MODE_PRIVATE);
        channelId = sp.getString("channelid", "");
        userId = sp.getString("userid", "");
        uname = sp.getString("uname", "");
        uface = sp.getString("face_id", "");

        detail = getIntent().getStringExtra("detail");

        pd = new ProgressDialog(this);

        doJson();

        //start to get Location.
        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(loc);

        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setScanSpan(30 * 1000);
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    BDLocationListener loc = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            addr = bdLocation.getAddrStr();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    private void doJson(){
        try {
            Log.i("json", detail);
            JSONArray arr = new JSONArray(detail);
            JSONArray main = arr.getJSONArray(0);

            tvSender.setText(main.getString(4));
            tvSendTime.setText(HttpConstant.convertTime(main.getLong(7)));
            tvMsg.setText(main.getString(1));
            tvLoc.setText(main.getString(6));
            alert_id = main.getInt(2);
            Picasso.with(this).load(HttpConstant.BAIDUFACE + main.getString(5))
                    .into(ivFace);

            for (int i = 1; i < arr.length(); i++){
                LostBean lb = new LostBean();
                JSONArray lost = arr.getJSONArray(i);
                lb.title = lost.optString(0);
                lb.description = lost.optString(1);
                lb.alert_id = lost.optString(2);
                lb.from_user_id = lost.optString(3);
                lb.uname = lost.optString(4);
                lb.uface = lost.optString(5);
                lb.position = lost.optString(6);
                lb.time = lost.optLong(7);
                lostList.add(lb);
            }
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String msg = etMsg.getText().toString();
            if (TextUtils.isEmpty(msg)) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.empty_msg)
                        , Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            String url = HttpConstant.SENDMESSAGE;
            url = String.format(url, userId, channelId, alert_id, Uri.encode(uname), uface, Uri.encode(addr));
            StringEntity se = null;
            try {
                JSONObject json = new JSONObject();
                json.put("message", msg);
                se = new StringEntity(json.toString(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("xxx", url);
            pd.setTitle(getResources().getString(R.string.sending));
            pd.setMessage(getResources().getString(R.string.wait));
            pd.show();
            client.post(getApplicationContext(), url, se, "application/json", new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.sendsuccess),
                            Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.sendfail),
                            Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    };

    private void getDetail() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HttpConstant.GETALERTBYID, new JsonHttpResponseHandler());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class DetailAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lostList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_detail, null);
            ImageView iv;
            TextView tvName, tvMsg, tvTime, tvLoc;
            iv = (ImageView) convertView.findViewById(R.id.iv_face);
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            tvLoc = (TextView) convertView.findViewById(R.id.tv_loc);
            tvMsg = (TextView) convertView.findViewById(R.id.tv_desc);

            LostBean lb = lostList.get(position);
            tvName.setText(lb.uname);
            tvTime.setText(HttpConstant.convertTime(lb.time));
            tvLoc.setText(lb.position);
            tvMsg.setText(lb.description);

            Picasso.with(getApplicationContext()).load(
                    HttpConstant.BAIDUFACE + lb.uface
            ).into(iv);

            return convertView;
        }
    }

}
