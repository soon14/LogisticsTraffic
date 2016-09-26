package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.CarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.Driver;

import java.util.List;

/**
 * 车队页的二级页面
 * Created by ZhangZy on 2016-9-22.
 */
public class MotorcadeCarAct extends BaseActivity {

    ListView listView;
    Driver driver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_motorcade_car);

        setPageName("详情");

        listView = (ListView) findViewById(R.id.motorcade_list);


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(AppParams.REQUEST_PAGE_KEY)) {
                driver = getIntent().getExtras().getParcelable(AppParams.REQUEST_PAGE_KEY);
                Log.i(TAG, "driver" + driver);
                initView(driver);
            }
        }

    }

    private void initView(Driver driver) {
        setTextView(R.id.motorcade_car_driver_name_tx, getString(R.string.motorcade_driver_name, driver.getName()));
        setTextView(R.id.motorcade_car_driver_phone_tx, getString(R.string.motorcade_driver_phone, driver.getPhone()));
        CheckBox allCheck = (CheckBox) findViewById(R.id.motorcade_car_check_bt);
        allCheck.setVisibility(View.GONE);

        List<Car> listCar = driver.getListCar();
        CarListAdapter adapter = new CarListAdapter(listCar);
        listView.setAdapter(adapter);
    }
}
