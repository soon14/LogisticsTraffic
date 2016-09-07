package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.CarListDriverAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonDriver;

import java.util.ArrayList;

/**
 * 司机列表
 * Created by ZhangZy on 2016-9-7.
 */
public class DriverListActivity extends BaseActivity {


    private ListView listView;
    private CarListDriverAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_list);
        setPageName("司机列表");
        initListView();
    }

    /**
     * 页面初始化
     */
    private void initListView() {
        listView = (ListView) findViewById(R.id.car_list_listView);
//        listView.setAdapter(new OrderListAdapter(false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter != null) {
                    JsonDriver driver = adapter.getItem(position);
                    if (driver != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString(AppParams.CAR_LIST_PAGE_DRIVER_KEY, driver.toString());
                        startActivity(DriverDetailActivity.class, bundle);
                    }

                }
            }
        });
        if (adapter != null)
            listView.setAdapter(adapter);
        else {
            requestDriverList();
        }
    }


    public void requestDriverList() {
        //test data
        ArrayList<JsonDriver> driverArrayList = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            JsonDriver jsonDriver = new JsonDriver();
            jsonDriver.setName("张三");
            jsonDriver.setPhone("15001230123");
            driverArrayList.add(jsonDriver);
        }
        adapter = new CarListDriverAdapter(driverArrayList);
        listView.setAdapter(adapter);
    }


}
