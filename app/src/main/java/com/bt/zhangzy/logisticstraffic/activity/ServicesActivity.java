package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * Created by ZhangZy on 2015/7/30.
 */
public class ServicesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_services);
        setPageName("服务");

        if(AppParams.DEVICES_APP){
            findViewById(R.id.services_check_ly).setVisibility(View.GONE);
        }
    }

    public void onClick_InfoLocation(View view) {

        String pageName = null;
        switch (view.getId()) {
            case R.id.services_qiye_btn:
                pageName = "信息部";
                break;
            case R.id.services_wuliu_btn:
                pageName = "物流园区";
                break;
            case R.id.services_xinxibu_btn:
                pageName = "仓储";
                break;
            case R.id.services_jiayouzhan_btn:
                pageName = "加油站";
                break;
        }
        if (pageName != null) {
            Bundle bundle = new Bundle();
            bundle.putString("pageName", pageName);
            startActivity(LocationListActivity.class, bundle);
        }
    }
}
