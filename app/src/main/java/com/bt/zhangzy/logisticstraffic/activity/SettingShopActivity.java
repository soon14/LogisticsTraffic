package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class SettingShopActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_shop);

        setPageName("店铺设置");

        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            findViewById(R.id.setting_lines_ly).setVisibility(View.GONE);
        }
    }
}
