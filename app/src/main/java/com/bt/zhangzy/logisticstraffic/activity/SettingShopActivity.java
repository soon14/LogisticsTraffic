package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class SettingShopActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_shop);

        setPageName("店铺设置");
    }
}
