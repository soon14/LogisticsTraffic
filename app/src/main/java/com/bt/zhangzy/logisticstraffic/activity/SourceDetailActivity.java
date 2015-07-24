package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_source_detail);
        setPageName("车源详情");

    }

    public void onClick_Join(View view){
        //加入车队
        People devices = new People();
        devices.setName("卢爱华");
        devices.setPhoneNumber("18202268826");
        User.getInstance().getDriverList().add(devices);
        showToast("添加成功");
    }
}
