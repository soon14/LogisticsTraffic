package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.NetCallback;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.tools.Tools;

import java.util.HashMap;

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

    @Override
    public void onClick_Back(View view) {
        if (!AppParams.DRIVER_APP) {

        }
        super.onClick_Back(view);
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
        type = Type.InformationType;
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
        final String phoneNum = phoneNumEd.getText().toString();
//        Json json = new Json();
//        json.put("phoneNumber", phoneNum);

        HttpHelper.getInstance().get(AppURL.SendVerificationCode + phoneNum, new NetCallback() {

            @Override
            public void onFailed(String str) {
                showToast("验证码发送失败");
            }

            @Override
            public void onSuccess(String str) {
//                Json js = Json.ToJson(str);
                showToast("验证码发送成功");
                requestVerificationCode(phoneNum);
            }
        });


    }

    @Deprecated
    private void testSMS() {
        HashMap map = new HashMap();
        map.put("account", "cf_yiyuntong");
        map.put("password", "zhang123456");
        map.put("mobile", "18686192818");
        String msg = "您的验证码是：【1212】。请不要把验证码泄露给其他人。";
        map.put("content", msg);
        HttpHelper.getInstance().post("http://106.ihuyi.cn/webservice/sms.php?method=Submit", map, new NetCallback() {
            @Override
            public void onFailed(String str) {

            }

            @Override
            public void onSuccess(String str) {

            }
        });
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
        if (verificationCode == 0) {
            requestVerificationCode(phoneNum);
        }
        if (!password.equals(passwordConfirmEd.getText().toString())) {
            showToast("密码输入不一致");
            return;
        }
        if (phoneNumEd.getText().length() != 11) {
            showToast("手机号输入错误");
            return;
        }
        if (TextUtils.isEmpty(verficationEd.getText())) {
            showToast("请输入验证码");
            return;
        }
        verfication = verficationEd.getText().toString().trim();
        //test 万能验证码
        if (AppParams.DEBUG && verfication.equals("0000")) {

        } else if (!verfication.equals(String.valueOf(verificationCode))) {
            showToast("验证码错误");
            return;
        }

        nickname = nicknameEd.getText().toString();

        recommend = TextUtils.isEmpty(recommendEd.getText()) ? "0" : recommendEd.getText().toString();

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
        jsonUser.setRole(type == Type.DriverType ? 1 : type == Type.EnterpriseType ? 2 : type == Type.InformationType ? 3 : -1);
        jsonUser.setRecommendCode(recommend);

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
                JsonUser jsonUser = ParseJson_Object(jsonstr, JsonUser.class);
                User user = User.getInstance();
                user.setUserType(type);
                user.setLogin(true);
//                showToast(JSON.toJSONString(jsonUser));
                user.setId(jsonUser.getId());
                user.setUserName(jsonUser.getName());
                user.setPhoneNum(jsonUser.getPhoneNumber());
                user.setNickName(jsonUser.getNickname());
                user.setJsonUser(jsonUser);

                registerSuccess();

            }

        };
        HttpHelper.getInstance().post(AppURL.Register, jsonUser, responseCallback);

        Log.e(TAG, "====>> end");

    }

    /**
     * 注册成功
     */
    private void registerSuccess() {
        //登录成功后保存一下信息；
        getApp().saveUser();
        getApp().setAliasAndTag();

        if (type == Type.DriverType) {
            startActivity(DetailPhotoActivity.class);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                startActivity(HomeActivity.class, bundle);
            }
        }
        finish();
    }

    int verificationCode;

    /*获取手机验证码*/
    private void requestVerificationCode(String phoneNum) {

        HttpHelper.getInstance().get(AppURL.GetVerificationCode + phoneNum, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                BaseEntity entity = ParseJson_Object(result, BaseEntity.class);
                verificationCode = entity.getCode();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    public void onClick_OpenLaw(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "法律申明");
        bundle.putString(AppParams.WEB_PAGE_URL, AppURL.REGISTER_LAW.toString());
        startActivity(WebViewActivity.class, bundle);
    }
}
