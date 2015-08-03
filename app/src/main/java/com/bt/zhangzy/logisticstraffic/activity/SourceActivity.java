package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.SourceListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * 车源信息页
 * 放弃不用
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        setPageName("车源信息");
        listView = (ListView) findViewById(R.id.source_list);
        listView.setAdapter(new SourceListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(SourceDetailActivity.class);
            }
        });

    }



}
