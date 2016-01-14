package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.NetCallback;
import com.bt.zhangzy.network.Url;

import java.util.HashMap;

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
        startActivity(RegisterActivity.class);
        finish();
    }

    //进行登录操作
    public void onClick_Login(View view) {
        loginSusses();
        EditText username = (EditText) findViewById(R.id.login_username_ed);
        EditText password = (EditText) findViewById(R.id.login_password_ed);
        String nameStr ,passwordStr;
        nameStr = username.getText().toString();
        passwordStr = password.getText().toString();
        if(nameStr.isEmpty() || passwordStr.isEmpty()){
            showToast("请填写用户名和密码");
            return ;
        }
        request_Login(nameStr,passwordStr);
    }

    private void loginSusses() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
        User.getInstance().setLogin(true);
        User.getInstance().setUserType(Type.EnterpriseType);
//        startActivity(new Intent(LoginActivity.this,UserActivity.class));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            startActivity(HomeActivity.class, bundle);
        finish();
    }

    // 发起登录请求
    private void request_Login( String username ,String password){

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);

        HttpHelper.getInstance().post(Url.Login, params, new NetCallback() {
            @Override
            public void onFailed(String str) {

            }

            @Override
            public void onSuccess(String str) {


            }
        });

    }
}
