package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCompany;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class SetCompanyActivity extends BaseActivity {

    JsonCompany company;
    EditText introduceEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_company);

        setPageName("店铺设置");

        introduceEd = (EditText) findViewById(R.id.set_introduce_ed);


        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            findViewById(R.id.setting_lines_ly).setVisibility(View.GONE);
        }

        if (User.getInstance().getJsonTypeEntity() != null) {
            if (User.getInstance().getUserType() == Type.InformationType) {
                company = User.getInstance().getJsonTypeEntity();
            }
        }

        if (company != null) {
            introduceEd.setText(company.getOftenRoute());
        }
    }

    @Override
    protected void onPause() {
        requestSetCompany();
        super.onPause();
    }

    private void requestSetCompany() {
        //// TODO: 2016-1-25  店铺设置接口
        if (company == null) {
            showToast("数据对象为空");
            return;
        }
//        JsonCompany company = new JsonCompany();
        if (!TextUtils.isEmpty(introduceEd.getText())) {
            company.setOftenRoute(introduceEd.getText().toString());
        }

        HttpHelper.getInstance().put(AppURL.PutCompaniesInfo + String.valueOf(company.getId()), company, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("数据上传成功");
            }

            @Override
            public void onFailed(String str) {
                showToast("数据上传失败" + str);
            }
        });
    }
}
