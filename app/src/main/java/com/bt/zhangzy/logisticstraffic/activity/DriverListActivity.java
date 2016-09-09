package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.CarListDriverAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.tools.Tools;

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
        if (getIntent().getExtras() != null) {
            if(getIntent().getExtras().containsKey(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY)) {
                String json_str = getIntent().getStringExtra(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY);
                Log.d(TAG,"选择需要绑定的司机 : "+json_str);
                if(!Tools.isEmptyStrings(json_str)) {
                    JsonCar jsonCar = JsonCar.ParseEntity(json_str,JsonCar.class);
                }
            }
            setPageName("选择需要绑定的司机");

        } else
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

    /**
     * 完成按钮
     *
     * @param view
     */
    public void onClick_Finish(View view) {
        if (adapter == null)
            return;

        JsonDriver driver = adapter.getSelectDriver();
        if (driver == null) {
            showToast("请选择需要绑定的司机");
        } else {
            Log.d(TAG,"选择需要绑定的司机 ： "+ driver.toString());
            Intent data = new Intent();
            data.putExtra(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY, driver.toString());
            setResult(AppParams.CAR_DETAIL_REQUEST_CODE, data);
            finish();
        }

    }


    public void requestDriverList() {


        adapter = new CarListDriverAdapter(User.getInstance().getJsonDriverList(), true);
        listView.setAdapter(adapter);
    }


}
