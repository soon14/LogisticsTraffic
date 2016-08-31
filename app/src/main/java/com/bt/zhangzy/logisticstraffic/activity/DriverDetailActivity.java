package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonDriver;

/**
 * 司机详情展示
 * Created by ZhangZy on 2016-8-30.
 */
public class DriverDetailActivity  extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_detail);

        setPageName("司机详情");

        if(getIntent().hasExtra(AppParams.CAR_LIST_PAGE_DRIVER_KEY)){
           String driver_str = getIntent().getStringExtra(AppParams.CAR_LIST_PAGE_DRIVER_KEY);
            JsonDriver jsonDriver = JsonDriver.ParseEntity(driver_str,JsonDriver.class);
            initView(jsonDriver);
        }

    }

    private void initView(JsonDriver jsonDriver) {
        setTextView(R.id.driver_detail_name_tx,jsonDriver.getName());
//        setTextView(R.id.driver_detail_num_tx,jsonDriver.getId());

    }


}
