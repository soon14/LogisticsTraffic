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
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.entity.JsonDriver;

/**
 * 司机列表
 * Created by ZhangZy on 2016-9-7.
 */
public class DriverListActivity extends BaseActivity {


    private ListView listView;
    private CarListDriverAdapter adapter;
    private Car bindCar;
    private int openPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_list);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(AppParams.CAR_DETAIL_PAGE_CAR_KEY)) {
                bindCar = getIntent().getParcelableExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY);

                Log.d(TAG, "选择需要绑定的司机 : " + bindCar.getNumber());
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
                        openPosition = position;
                        Bundle bundle = new Bundle();
                        bundle.putString(AppParams.CAR_LIST_PAGE_DRIVER_KEY, driver.toString());
                        bundle.putBoolean(AppParams.CAR_LIST_PAGE_SELECT_MODE, true);
                        startActivityForResult(DriverDetailActivity.class, bundle, AppParams.CAR_DETAIL_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppParams.CAR_DETAIL_REQUEST_CODE) {
            if (openPosition > -1) {
                adapter.setSelectDriver(openPosition);
            }
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
            Log.d(TAG, "选择需要绑定的司机 ： " + driver.toString());
            Intent data = new Intent();
            data.putExtra(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY, driver.toString());
            data.putExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY, bindCar);
            setResult(AppParams.CAR_DETAIL_REQUEST_CODE, data);
            finish();
        }

    }


    public void requestDriverList() {
        adapter = new CarListDriverAdapter(User.getInstance().getJsonDriverList(), bindCar != null);
        listView.setAdapter(adapter);
        adapter.setDefaultSelect(bindCar.getPilotId());
    }


}
