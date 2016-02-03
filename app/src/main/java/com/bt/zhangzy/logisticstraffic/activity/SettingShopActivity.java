package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.entity.JsonCompany;

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

        if (User.getInstance().getJsonTypeEntity() != null) {
            if (User.getInstance().getUserType() == Type.InformationType) {
                JsonCompany company = User.getInstance().getJsonTypeEntity();
            }
        }
    }


    private void requestSetCompany(){
        //// TODO: 2016-1-25  店铺设置接口
        JsonCompany company = new JsonCompany();

        HttpHelper.getInstance().put(AppURL.PutCompaniesInfo, company, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }
}
