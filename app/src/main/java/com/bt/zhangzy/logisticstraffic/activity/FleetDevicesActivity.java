package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015-9-15.
 */
public class FleetDevicesActivity extends BaseActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fleet_devices);
        setPageName("车队详情");
        listView = (ListView) findViewById(R.id.fleet_list);
        FleetListAdapter adapter = new FleetListAdapter(true);
        adapter.addPeople(User.getInstance().getDriverList());
        listView.setAdapter(adapter);
    }

    public void onclick_QuitFleet(View view) {
        showToast("正在申请退出！");
    }
}
