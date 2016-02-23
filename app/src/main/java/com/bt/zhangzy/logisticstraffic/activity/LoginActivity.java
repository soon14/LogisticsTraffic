package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;
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
        //登录成功后保存一下信息；
        getApp().saveUser();
        getApp().setAliasAndTag();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            startActivity(HomeActivity.class, bundle);
        finish();
    }

    // 发起登录请求
    private void request_Login(String username, String password) {

        JsonUser user = new JsonUser();
//        user.setName(username);
        user.setPhoneNumber(username);
        user.setPassword(password);

        HttpHelper.getInstance().post(AppURL.Login, user, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("用户登录失败：" + str);
            }

            @Override
            public void onSuccess(String msg, String jsonstr) {
                if (TextUtils.isEmpty(jsonstr)) {
                    showToast("用户登录失败：" + msg);
                    return;
                }

                ResponseLogin json = ParseJson_Object(jsonstr, ResponseLogin.class);
//                JsonUser jsonUser = ParseJson_Object(jsonstr, JsonUser.class);
                JsonUser jsonUser = json.getUser();
                User user = User.getInstance();
                user.setLogin(true);
//                showToast(JSON.toJSONString(jsonUser));
                user.setId(jsonUser.getId());
                user.setUserName(jsonUser.getName());
                user.setPhoneNum(jsonUser.getPhoneNumber());
                user.setNickName(jsonUser.getNickname());
                user.setJsonUser(jsonUser);
                switch (jsonUser.getRole()) {
                    case 1:
                        user.setUserType(Type.DriverType);
                        if (json.getDriver() != null) {
                            user.setJsonTypeEntity(json.getDriver());
                            user.setDriverID(json.getDriver().getId());
                        }
                        break;
                    case 2:
                        user.setUserType(Type.EnterpriseType);
                        if (json.getEnterprise() != null) {
                            user.setJsonTypeEntity(json.getEnterprise());
                            user.setEnterpriseID(json.getEnterprise().getId());
                        }
                        break;
                    case 3:
                        user.setUserType(Type.InformationType);
                        if (json.getCompany() != null) {
                            user.setJsonTypeEntity(json.getCompany());
                            user.setCompanyID(json.getCompany().getId());
                        }
                        break;
                }
                if (AppParams.DRIVER_APP) {
                    if (user.getUserType() != Type.DriverType) {
                        showToast("不是司机用户");
                        return;
                    }
                }

                user.setJsonFavorites(json.getFavorites());
                user.setMotorcades(json.getMotorcades());
                showToast("用户登录成功");

                loginSusses();
            }

        });

    }
}
