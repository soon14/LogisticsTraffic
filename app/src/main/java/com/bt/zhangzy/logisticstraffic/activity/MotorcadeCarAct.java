package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.CarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.Driver;

import java.util.ArrayList;
import java.util.List;

/**
 * 车队页的二级页面
 * Created by ZhangZy on 2016-9-22.
 */
public class MotorcadeCarAct extends BaseActivity {

    ListView listView;
    CarListAdapter adapter;
    Driver driver;
    boolean isSelectMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_motorcade_car);

        setPageName("详情");

        listView = (ListView) findViewById(R.id.motorcade_list);


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(AppParams.ORDER_SELECT_DRIVER)) {
                driver = getIntent().getExtras().getParcelable(AppParams.ORDER_SELECT_DRIVER);
                isSelectMode = getIntent().getBooleanExtra(AppParams.ORDER_SELECT_MODE, false);
                Log.i(TAG, "driver" + driver);
                initView(driver);
            }
        }

    }

    private void initView(Driver driver) {
        setTextView(R.id.motorcade_car_driver_name_tx, getString(R.string.motorcade_driver_name, driver.getName()));
        setTextView(R.id.motorcade_car_driver_phone_tx, getString(R.string.motorcade_driver_phone, driver.getPhoneNumber()));
        View allCheck = findViewById(R.id.motorcade_car_check_bt);
        if (allCheck != null)
            allCheck.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);


        List<Car> listCar = driver.getListCar();
        adapter = new CarListAdapter(listCar);
        adapter.setCheckMode(isSelectMode);
        adapter.setLook(true);
        listView.setAdapter(adapter);
//        listView.setItemsCanFocus(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onClick_Back(null);
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 选择模式下的 全选按钮
     *
     * @param view
     */
    public void onClick_AllCheck(View view) {
        if (adapter != null)
            adapter.allCheck();
    }

    public void onClick_Back(View view) {

        super.onClick_Back(view);
    }

    public void onClick_FinishSelect(View view) {
        ArrayList<Car> checkList = adapter.getCheckList();
        driver.setSelectCars(checkList);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppParams.ORDER_SELECT_DRIVER, driver);
        intent.putExtras(bundle);
        setResult(AppParams.ORDER_SERECT_DRIVER_CODE, intent);
        finish();
    }
}
