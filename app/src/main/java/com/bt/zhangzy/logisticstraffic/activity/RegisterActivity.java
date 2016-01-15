package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.NetCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.tools.Json;
import com.bt.zhangzy.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;

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
            type = Type.DriverType;
            setContentView(R.layout.register_beginning);
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
    }


    /**
     * 物流门企注册
     */
    public void onClick_RegisterDepartment(View view) {
        setContentView(R.layout.register_beginning);
        type = Type.InformationType;
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
        if (!password.equals(passwordConfirmEd.getText().toString())) {
            showToast("密码输入不一致");
            return;
        }
        if (phoneNumEd.getText().length() != 11) {
            showToast("手机号输入错误");
            return;
        }
        nickname = nicknameEd.getText().toString();
        phoneNum = phoneNumEd.getText().toString();
        verfication = verficationEd.getText().toString();
        recommend = TextUtils.isEmpty(recommendEd.getText()) ? "0" : recommendEd.getText().toString();

        requestRegister(nickname, phoneNum, password, recommend, verfication);

    }

    /**
     * 发起注册请求
     */
    private void requestRegister(String nickname, String phoneNum, String password, String recommend, String verficaiton) {
        Log.e(TAG, "============================================");
        try {

            Json json = new Json();
            json.put("nickname", nickname);
            json.put("name", phoneNum);
            json.put("password", password);
            json.put("role", type == Type.DriverType ? "1" : type == Type.EnterpriseType ? "2" : type == Type.InformationType ? "3" : "-1");
            json.put("recommendCode", recommend);

            JsonCallback responseCallback = new JsonCallback() {
                @Override
                public void onFailed(String str) {
                    showToast("用户注册失败");
                }

                @Override
                public void onSuccess(String msg, Json json) {
                    showToast("用户注册成功");
                    User.getInstance().setUserType(type);
                    User.getInstance().setLogin(true);

//                Bundle bundle = getIntent().getExtras();
//                if (bundle != null) {
//                    startActivity(HomeActivity.class, bundle);
//                }
//                finish();
                }

            };
            HttpHelper.getInstance().post(Url.Register, json, responseCallback);

            Log.e(TAG, "====>> end");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
