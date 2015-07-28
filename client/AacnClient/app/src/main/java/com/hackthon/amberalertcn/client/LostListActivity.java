package com.hackthon.amberalertcn.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LostListActivity extends AppCompatActivity {

    private ListView lv;
    private List<LostBean> lostList;
    private LostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_list);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lostList = new ArrayList<>();
        for (int i=0;i<10;i++)
            lostList.add(new LostBean());

        lv = (ListView) findViewById(R.id.list);
        adapter = new LostAdapter();
        lv.setAdapter(adapter);
    }

    private void getAlerts(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(HttpConstant.GETALERTS, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                JSONArray arr = response.optJSONArray("alerts");
                for (int i = 0; i < arr.length();i++) {
                    LostBean lb = new LostBean();
                    JSONArray replys = arr.optJSONArray(i);
                    lb.count = replys.length() - 1;
                    JSONArray main = replys.optJSONArray(0);
                    lb.title = main.optString(0);

                }

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
                vh.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
                vh.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                vh.tvPosition = (TextView) convertView.findViewById(R.id.tv_position);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }

            LostBean bean = lostList.get(position);
            vh.tvUser.setText(bean.from_user_id);
            vh.tvDesc.setText(bean.description);
            vh.tvPosition.setText(bean.position);
            vh.tvTitle.setText(bean.title);
            return convertView;
        }

        class ViewHolder{
            TextView tvUser, tvDesc, tvPosition, tvTitle;
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
