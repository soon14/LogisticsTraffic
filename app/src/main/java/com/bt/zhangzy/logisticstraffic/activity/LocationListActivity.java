package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.CollectListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.zhangzy.baidusdk.BaiduMapActivity;

/**
 * Created by ZhangZy on 2015/7/8.
 */
public class LocationListActivity extends BaseActivity {


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TODO 位置列表 左上角可以跳转到地图页面

        setContentView(R.layout.activity_location_list);
        if (getIntent().getExtras() != null) {
            setPageName(getIntent().getStringExtra("pageName"));
        }

        listView = (ListView) findViewById(R.id.location_listview);
        listView.setAdapter(new CollectListAdapter(true));

    }

    public void onClick_gotoMap(View view) {
        startActivity(BaiduMapActivity.class);
    }
}
