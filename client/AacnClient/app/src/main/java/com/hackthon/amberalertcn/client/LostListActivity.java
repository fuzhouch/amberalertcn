package com.hackthon.amberalertcn.client;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LostListActivity extends ActionBarActivity {

    private ListView lv;
    private List<LostBean> lostList;
    private LostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_list);

        lostList = new ArrayList<>();
        for (int i=0;i<10;i++)
            lostList.add(new LostBean());

        lv = (ListView) findViewById(R.id.list);
        adapter = new LostAdapter();
        lv.setAdapter(adapter);
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
                vh = (ViewHolder) convertView.getTag();
                vh.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
                vh.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                vh.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                vh.tvPosition = (TextView) convertView.findViewById(R.id.tv_position);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }

            LostBean bean = lostList.get(position);
            vh.tvUser.setText(bean.from_user_id);
            vh.tvDesc.setText(bean.description);
            vh.tvPosition.setText(bean.position);
            vh.tvTitle.setText(bean.amber_alert_title);
            return convertView;
        }

        class ViewHolder{
            TextView tvUser, tvDesc, tvPosition, tvTitle;
        }
    }

}
