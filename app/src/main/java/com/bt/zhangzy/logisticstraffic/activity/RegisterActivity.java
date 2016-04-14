package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
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
import com.bt.zhangzy.tools.Tools;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class RegisterActivity extends BaseActivity {

    private Type type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppParams.DRIVER_APP) {
            type = Type.DriverType;
            setContentView(R.layout.register_beginning);
            setPageName("司机用户注册");
        } else {
            setContentView(R.layout.activity_register);
        }
    }

    /**
     * 企业注册
     */
    public void onClick_RegisterCompany(View view) {
        setContentView(R.layout.register_beginning);
        type = Type.EnterpriseType;
        setPageName("企业用户注册");
    }


    /**
     * 物流门企注册
     */
    public void onClick_RegisterDepartment(View view) {
        setContentView(R.layout.register_beginning);
        type = Type.CompanyInformationType;
        setPageName("物流用户注册");
    }

    /* 给手机发送验证码  */
    public void onClick_SendVerificationCode(View view) {
//        testSMS();
        EditText phoneNumEd = (EditText) findViewById(R.id.reg_phoneNum_ed);
        if (TextUtils.isEmpty(phoneNumEd.getText()) || phoneNumEd.getText().length() != 11) {
            showToast("手机号输入错误");
            return;
        }
        String phoneNum = phoneNumEd.getText().toString();
        SMSCodeHelper.getInstance().sendSMS(this, phoneNum, "83674");


    }


    public void onClick_RegisterBtn(View view) {
//        startActivity(new Intent(this,UserActivity.class));

        CheckBox confirmCk = (CheckBox) findViewById(R.id.reg_confirm_ck);
        if (!confirmCk.isChecked()) {
            showToast("请阅读并确认法律申明");
            return;
        }

        EditText nicknameEd = (EditText) findViewById(R.id.reg_nickname_ed);
        EditText phoneNumEd = (EditText) findViewById(R.id.reg_phoneNum_ed);
        EditText passwordEd = (EditText) findViewById(R.id.reg_password_ed);
        EditText passwordConfirmEd = (EditText) findViewById(R.id.reg_password_confirm_ed);
        EditText recommendEd = (EditText) findViewById(R.id.reg_recommend_ed);
        EditText verficationEd = (EditText) findViewById(R.id.reg_verification_ed);
        String nickname, phoneNum, password, recommend, verfication;
        if (TextUtils.isEmpty(nicknameEd.getText()) || TextUtils.isEmpty(phoneNumEd.getText()) ||
                TextUtils.isEmpty(passwordEd.getText()) || TextUtils.isEmpty(passwordConfirmEd.getText())
                || TextUtils.isEmpty(verficationEd.getText())) {
            showToast("填写内容不能为空");
            return;
        }
        password = passwordEd.getText().toString();
        phoneNum = phoneNumEd.getText().toString();
        if (!password.equals(passwordConfirmEd.getText().toString())) {
            showToast("密码输入不一致");
            return;
        }
        if (!Tools.IsPhoneNum(phoneNum)) {
            showToast("手机号输入错误");
            return;
        }
        if (TextUtils.isEmpty(verficationEd.getText())) {
            showToast("请输入验证码");
            return;
        }
        verfication = verficationEd.getText().toString().trim();
        if (!SMSCodeHelper.getInstance().checkVerificationCode(verfication)) {
            showToast("验证码错误");
            return;
        }

        nickname = nicknameEd.getText().toString();

        recommend = "";
        if (!TextUtils.isEmpty(recommendEd.getText())) {
            recommend = recommendEd.getText().toString();
            if (!Tools.IsPhoneNum(recommend)) {
                showToast("推荐人手机号错误");
                return;
            }
        }

        requestRegister(nickname, phoneNum, Tools.MD5(password), recommend, verfication);

    }

    /**
     * 发起注册请求
     */
    private void requestRegister(String nickname, String phoneNum, String password, String recommend, String verficaiton) {
        Log.e(TAG, "============================================");
        showProgress("注册中...");
        JsonUser jsonUser = new JsonUser();
//        jsonUser.setNickname(nickname);
        jsonUser.setName(nickname);
        jsonUser.setPhoneNumber(phoneNum);
        jsonUser.setPassword(password);
        jsonUser.setRole(type == Type.DriverType ? 1 : type == Type.EnterpriseType ? 2 : type == Type.CompanyInformationType ? 3 : -1);
        jsonUser.setRecommendCode(recommend);

        if (User.getInstance().isSave()) {
            User.getInstance().setPassword(password);
        }
        JsonCallback responseCallback = new JsonCallback() {

            @Override
            public void onFailed(String str) {
                showToast("用户注册失败：" + str);
                cancelProgress();
            }

            public void onSuccess(String msg, String jsonstr) {
                cancelProgress();
                if (TextUtils.isEmpty(jsonstr)) {
                    showToast("用户注册失败：" + msg);
                    return;
                }
                showToast("用户注册成功");
                ResponseLogin response = ParseJson_Object(jsonstr, ResponseLogin.class);
                User.getInstance().setLoginResponse(response);

                registerSuccess();

            }

        };
        HttpHelper.getInstance().post(AppURL.Register, jsonUser, responseCallback);

        Log.e(TAG, "====>> end");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onClick_Back(null);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick_Back(View view) {
//        super.onClick_Back(view);
            startActivity(HomeActivity.class);
        finish();
    }

    /**
     * 注册成功
     */
    private void registerSuccess() {
        //登录成功后保存一下信息；
        getApp().saveUser();
        getApp().setAliasAndTag();
        //注册后默认跳转到验证审核页面
        startActivity(DetailPhotoActivity.class, null);
//        if (type == Type.DriverType) {
//            startActivity(DetailPhotoActivity.class);
//        } else {
//            Bundle bundle = getIntent().getExtras();
//            if (bundle != null) {
//                startActivity(HomeActivity.class, bundle);
//            }
//        }
        finish();
    }


    public void onClick_OpenLaw(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "法律申明");
        bundle.putString(AppParams.WEB_PAGE_URL, AppURL.REGISTER_LAW.toString());
        startActivity(WebViewActivity.class, bundle);
    }
}
