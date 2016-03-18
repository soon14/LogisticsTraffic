package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.SMSCodeHelper;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;

/**
 * Created by ZhangZy on 2016-3-18.
 */
public class ForgetActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        setPageName("验证码登录");
    }

    public void onClick_SendVerificationCode(View view) {
        EditText phoneNumEd = (EditText) findViewById(R.id.forget_phoneNum_ed);
        if (TextUtils.isEmpty(phoneNumEd.getText()) || phoneNumEd.getText().length() != 11) {
            showToast("手机号输入错误");
            return;
        }
        String phoneNum = phoneNumEd.getText().toString();
        SMSCodeHelper.getInstance().sendSMS(this, phoneNum, "83803");
    }


    //进行登录操作
    public void onClick_Login(View view) {
//        loginSusses();
//        EditText username = (EditText) findViewById(R.id.login_username_ed);
//        EditText password = (EditText) findViewById(R.id.login_password_ed);
        String nameStr, input_code;
        nameStr = getStringFromTextView(R.id.forget_phoneNum_ed);
        input_code = getStringFromTextView(R.id.forget_verification_ed);
        if (nameStr == null || input_code == null) {
            showToast("内容不能为空");
            return;
        }
        if (SMSCodeHelper.getInstance().checkVerificationCode(input_code)) {
            request_Login(nameStr, input_code);
        } else {
            showToast("验证码错误");
        }


    }

    // 发起登录请求
    private void request_Login(String username, String password) {

        JsonUser user = new JsonUser();
//        user.setName(username);
        user.setPhoneNumber(username);
        user.setPassword(password);
//        if (User.getInstance().isSave()) {
//            User.getInstance().setPassword(password);
//        }

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
                User.getInstance().setLoginResponse(json);
//                JsonUser jsonUser = ParseJson_Object(jsonstr, JsonUser.class);
                if (AppParams.DRIVER_APP) {
                    if (User.getInstance().getUserType() != Type.DriverType) {
                        showToast("用户类型错误");
                        return;
                    }
                } else {
                    if (User.getInstance().getUserType() == Type.DriverType) {
                        showToast("用户类型错误");
                        return;
                    }
                }
                showToast("用户登录成功");

                loginSusses();
            }

        });

    }

    private void loginSusses() {
        //登录成功后保存一下信息；
        getApp().saveUser();
        getApp().setAliasAndTag();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            startActivity(HomeActivity.class, bundle);
        finish();
    }
}
