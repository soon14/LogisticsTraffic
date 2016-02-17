package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;

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

    public void onClick_Join(View view) {
        //加入车队 // TODO: 2016-1-28  车源详情中 加入车队接口
        People devices = new People();
        devices.setName("卢爱华");
        devices.setPhoneNumber("18202268826");
        User.getInstance().getDriverList().add(devices);
        showToast("添加成功");
    }

    public void onClick_CallPhone(View view) {
//        ContextTools.callPhone(this, "10010");
        if(AppParams.DEVICES_APP && !User.getInstance().isVIP()){
            ConfirmDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PayActivity.class);
                }
            });
            return;
        }
        getApp().callPhone("10010");
    }
}
