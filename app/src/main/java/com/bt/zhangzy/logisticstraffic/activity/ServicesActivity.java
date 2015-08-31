package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;

/**
 * Created by ZhangZy on 2015/7/30.
 */
public class ServicesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_services);
        setPageName("服务");

        if(Constant.DEVICES_APP){
            findViewById(R.id.services_check_ly).setVisibility(View.GONE);
        }
    }
}
