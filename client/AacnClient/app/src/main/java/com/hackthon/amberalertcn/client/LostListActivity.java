package com.hackthon.amberalertcn.client;

import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LostListActivity extends AppCompatActivity {

    private ListView lv;
    private List<LostBean> lostList;
    private LostAdapter adapter;
    private List<String> jsons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_list);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lostList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        adapter = new LostAdapter();
        lv.setAdapter(adapter);

        getAlerts();

        jsons = new ArrayList<>();
        lv.setOnItemClickListener(item);
    }

    AdapterView.OnItemClickListener item = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent it = new Intent(getApplicationContext(), LostDetailActivity.class);
            it.putExtra("detail", jsons.get(position));
            startActivity(it);
        }
    };

    private void getAlerts(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HttpConstant.GETALERTS, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                JSONArray arr = response.optJSONArray("alerts");
                for (int i = 0; i < arr.length();i++) {
                    LostBean lb = new LostBean();

                    try {
                        JSONArray replys = arr.optJSONArray(i);
                        jsons.add(replys.toString());
                        lb.count = replys.length() - 1;
                        JSONArray main = replys.optJSONArray(0);
                        lb.title = main.optString(0);
                        lb.description = main.optString(1);
                        lb.alert_id = main.optString(2);
                        lb.from_user_id = main.optString(3);
                        lb.uname = main.optString(4);
                        lb.uface = main.optString(5);
                        lb.position = main.optString(6);
                        lb.time = main.optLong(7);
                        Log.i("lost", lb.toString());
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("tag", e.getMessage(), e);
                    }

                    lostList.add(lb);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
            }
        });

    }

    class LostAdapter extends BaseAdapter{

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
            ViewHolder vh;
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.item_lost, null);
                vh = new ViewHolder();
                vh.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                vh.tvPosition = (TextView) convertView.findViewById(R.id.tv_position);
                vh.ivFace = (ImageView) convertView.findViewById(R.id.iv_face);
                vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                vh.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }

            LostBean bean = lostList.get(position);
            vh.tvDesc.setText(bean.description);
            vh.tvPosition.setText(bean.position);
            vh.tvTitle.setText(bean.title);
            vh.tvName.setText(bean.uname);
            vh.tvTime.setText(HttpConstant.convertTime(bean.time));
            Picasso.with(getApplicationContext()).load(HttpConstant.BAIDUFACE + bean.uface)
                    .into(vh.ivFace);
            return convertView;
        }

        class ViewHolder{
            TextView tvDesc, tvPosition, tvTitle, tvName, tvTime;
            ImageView ivFace;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
