package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    //跳转到注册页面
    public void onClick_Register(View view) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
//            }
//        });
        startActivity(RegisterActivity.class, getIntent().getExtras());
        finish();
    }

    //进行登录操作
    public void onClick_Login(View view) {
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
        User.getInstance().setLogin(true);
        User.getInstance().setUserType(Type.EnterpriseType);
//        startActivity(new Intent(LoginActivity.this,UserActivity.class));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            startActivity(HomeActivity.class, bundle);
        finish();

    }
}
