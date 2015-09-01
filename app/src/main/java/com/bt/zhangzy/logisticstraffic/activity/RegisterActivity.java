package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class RegisterActivity extends BaseActivity {

    private Type type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Constant.DEVICES_APP){
            setContentView(R.layout.register_company);
        }else {
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
        User.getInstance().setUserType(type);
        User.getInstance().setLogin(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            startActivity(HomeActivity.class, bundle);
        }
        finish();
    }
}
