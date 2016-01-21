package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.tools.Tools;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class LoginActivity extends BaseActivity {

    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.login_username_ed);
        if (!TextUtils.isEmpty(User.getInstance().getUserName())) {
            username.setText(User.getInstance().getUserName());
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id.login_remember_ck);
        checkBox.setChecked(User.getInstance().isSave());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                User.getInstance().setIsSave(isChecked);
                if (isChecked) {
                    if (!TextUtils.isEmpty(username.getText()))
                        User.getInstance().setUserName(username.getText().toString());
                }
            }
        });
    }

    //跳转到注册页面
    public void onClick_Register(View view) {
        startActivity(RegisterActivity.class);
        finish();
    }

    //进行登录操作
    public void onClick_Login(View view) {
//        loginSusses();
//        EditText username = (EditText) findViewById(R.id.login_username_ed);
        EditText password = (EditText) findViewById(R.id.login_password_ed);
        String nameStr, passwordStr;
        nameStr = username.getText().toString();
        passwordStr = password.getText().toString();
        if (nameStr.isEmpty() || passwordStr.isEmpty()) {
            showToast("请填写用户名和密码");
            return;
        }
        request_Login(nameStr, Tools.MD5(passwordStr));
    }

    private void loginSusses() {
//        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
//        User.getInstance().setLogin(true);
//        User.getInstance().setUserType(Type.EnterpriseType);
//        startActivity(new Intent(LoginActivity.this,UserActivity.class));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            startActivity(HomeActivity.class, bundle);
        finish();
    }

    // 发起登录请求
    private void request_Login(String username, String password) {

        JsonUser user = new JsonUser();
        user.setName(username);
        user.setPhoneNumber(username);
        user.setPassword(password);
//        user.setRole(2);
//        if(Constant.DEVICES_APP) {
//            user.setRole(1);
//        }else{
//
//        }

        HttpHelper.getInstance().post(Url.Login, user, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("用户登录失败：" + str);
            }

            @Override
            public void onSuccess(String msg, String jsonstr) {
                showToast("用户登录成功");
                JsonUser jsonUser = ParseJson_Object(jsonstr, JsonUser.class);
                User user = User.getInstance();
                user.setLogin(true);
                showToast(JSON.toJSONString(jsonUser));
                user.setId(jsonUser.getId());
                user.setUserName(jsonUser.getName());
                user.setPhoneNum(jsonUser.getName());
                user.setNickName(jsonUser.getNickname());
                switch (jsonUser.getRole()) {
                    case 1:
                        user.setUserType(Type.DriverType);
                        break;
                    case 2:
                        user.setUserType(Type.EnterpriseType);
                        break;
                    case 3:
                        user.setUserType(Type.InformationType);
                        break;
                }

                loginSusses();
            }

        });

    }
}
