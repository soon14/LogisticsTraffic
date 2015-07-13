package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HistoryListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * Created by ZhangZy on 2015/6/15.
 */
public class HistoryActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        setPageName("历史浏览");
        listView = (ListView) findViewById(R.id.history_list);
        listView.setAdapter(new HistoryListAdapter());
    }
}
