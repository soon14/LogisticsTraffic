package com.bt.zhangzy.logisticstraffic.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.SourceDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.SourceListAdapter;

/**
 * 车源信息
 * Created by ZhangZy on 2015/7/24.
 */
public class SourceListFragment extends BaseHomeFragment {
    @Override
    int getLayoutID() {
        return R.layout.activity_source;
    }

    public SourceListFragment() {
        super("空车信息");
    }
    ListView listView;
    @Override
    void init() {
        super.init();
        listView = (ListView)findViewById(R.id.source_list);
        listView.setAdapter(new SourceListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(SourceDetailActivity.class);
            }
        });
    }
}
