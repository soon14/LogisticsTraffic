package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.NetCallback;
import com.bt.zhangzy.network.Url;

import java.util.HashMap;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class RegisterActivity extends BaseActivity {

    private Type type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DEVICES_APP) {
            setContentView(R.layout.register_company);
        } else {
            setContentView(R.layout.activity_register);
        }
    }

    /**
     * 企业注册
     */
    public void onClick_RegisterCompany(View view) {
        setContentView(R.layout.register_company);
        type = Type.EnterpriseType;
    }


    /**
     * 物流门企注册
     */
    public void onClick_RegisterDepartment(View view) {
        setContentView(R.layout.register_department);
        type = Type.InformationType;
    }

    public void onClick_RegisterBtn(View view) {
//        startActivity(new Intent(this,UserActivity.class));
        requestRegister();
        User.getInstance().setUserType(type);
        User.getInstance().setLogin(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            startActivity(HomeActivity.class, bundle);
        }
        finish();
    }

    /**
     * 发起注册请求
     */
    private void requestRegister() {
        Log.e(TAG, "============================================");

        String json = "{\"username\":\"android_00\",\"password\":\"123123123\",\"usertype\":\"1\"}";
        HashMap builder = new HashMap();
        builder.put("username", "android_00");
        builder.put("password", "123123123");
        builder.put("usertype", "1");

        HttpHelper.getInstance().post(Url.Register, builder, new NetCallback() {
            @Override
            public void onFailed(String str) {
            }

            @Override
            public void onSuccess(String str) {
                Log.e(TAG, "====>> onResponse:" + str);
            }
        });

        Log.e(TAG, "====>> end");


    }
}
