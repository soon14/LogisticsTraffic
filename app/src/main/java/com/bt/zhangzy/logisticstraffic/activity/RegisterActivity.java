package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.SMSCodeHelper;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.app.AppProgress;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class RegisterActivity extends BaseActivity {

    private Type type;
    JsonDriver requestJsonDriver;
    JsonUser jsonUser = new JsonUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppParams.DRIVER_APP) {
            type = Type.DriverType;
            setContentView(R.layout.register_beginning);
            setPageName("司机用户注册");
            initDriverView();

        } else {
            setContentView(R.layout.activity_register);
        }
    }

    /**
     * 司机版注册页面初始化
     */
    private void initDriverView() {
        requestJsonDriver = new JsonDriver();

        //默认选中
        CheckBox confirmCk = (CheckBox) findViewById(R.id.reg_confirm_ck);
        if (confirmCk != null) {
            confirmCk.setChecked(true);
        }
        String recommend = getString(R.string.register_recommend);
        Log.i(TAG, "=====>预留推荐码：" + recommend);
        //预留推荐码
        if (!TextUtils.isEmpty(recommend)) {
            EditText recommendEd = (EditText) findViewById(R.id.reg_recommend_ed);
//                recommendEd.setText(recommend);
            recommendEd.setHint(R.string.register_recommend_info);
            recommendEd.setBackgroundColor(getResources().getColor(R.color.def_line));
            recommendEd.setEnabled(false);
//                recommendEd.setVisibility(View.INVISIBLE);
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
        if (TextUtils.isEmpty(phoneNumEd.getText()) || phoneNumEd.getText().length() != 11
                || !Tools.IsPhoneNum(phoneNumEd.getText().toString())) {
            showToast("手机号输入错误");
            return;
        }

        String phoneNum = phoneNumEd.getText().toString();
        SMSCodeHelper.getInstance().sendSMS(this, (TextView) view, phoneNum, getString(R.string.sms_code_register));


    }


    public void onClick_RegisterBtn(View view) {
//        startActivity(new Intent(this,UserActivity.class));

        CheckBox confirmCk = (CheckBox) findViewById(R.id.reg_confirm_ck);
        if (!confirmCk.isChecked()) {
            showToast("请阅读并确认法律申明");
            return;
        }

        EditText nameEd = (EditText) findViewById(R.id.reg_nickname_ed);
        EditText phoneNumEd = (EditText) findViewById(R.id.reg_phoneNum_ed);
//        EditText passwordEd = (EditText) findViewById(R.id.reg_password_ed);
//        EditText passwordConfirmEd = (EditText) findViewById(R.id.reg_password_confirm_ed);
        EditText verficationEd = (EditText) findViewById(R.id.reg_verification_ed);
        EditText idcard = (EditText) findViewById(R.id.reg_id_card_ed);
        String name, phoneNum, password, recommend, verfication, idCardStr;
        if (TextUtils.isEmpty(nameEd.getText()) || TextUtils.isEmpty(phoneNumEd.getText())
//                || TextUtils.isEmpty(passwordEd.getText()) || TextUtils.isEmpty(passwordConfirmEd.getText())
                || TextUtils.isEmpty(idcard.getText())
                || TextUtils.isEmpty(verficationEd.getText())) {
            showToast("填写内容不能为空");
            return;
        }
//        password = passwordEd.getText().toString();
        phoneNum = phoneNumEd.getText().toString();
//        if (password.length() < 6) {
//            showToast("密码长度太短");
//            return;
//        }
//        if (!password.equals(passwordConfirmEd.getText().toString())) {
//            showToast("密码输入不一致");
//            return;
//        }
        if (!Tools.IsPhoneNum(phoneNum)) {
            showToast("手机号输入错误");
            return;
        }
        if (TextUtils.isEmpty(verficationEd.getText())) {
            showToast("请输入验证码");
            return;
        }
        verfication = verficationEd.getText().toString().trim();
        if (!SMSCodeHelper.getInstance().checkVerificationCode(this, phoneNum, verfication)) {
            showToast("验证码错误");
            return;
        }

        name = nameEd.getText().toString();
        idCardStr = idcard.getText().toString();

        recommend = getString(R.string.register_recommend);
        if (TextUtils.isEmpty(recommend)) {
            EditText recommendEd = (EditText) findViewById(R.id.reg_recommend_ed);
            if (!TextUtils.isEmpty(recommendEd.getText())) {
                recommend = recommendEd.getText().toString();
                if (!Tools.IsPhoneNum(recommend)) {
                    showToast("推荐人手机号错误");
                    return;
                }
            }
        } else {

        }

        //设置默认密码
        password = phoneNum.substring(phoneNum.length() - 6);
        password = Tools.MD5(password);

        //数据模型建立
//        JsonUser jsonUser = new JsonUser();
        jsonUser.setNickname(name);
        jsonUser.setName(name);
        jsonUser.setPhoneNumber(phoneNum);
        jsonUser.setPassword(password);
        jsonUser.setRole(type == Type.DriverType ? 1 : type == Type.EnterpriseType ? 2 : type == Type.CompanyInformationType ? 3 : -1);
        jsonUser.setRecommendCode(recommend);

        if (User.getInstance().isSave()) {
            User.getInstance().setPassword(password);
        }
        requestRegister();

    }

    /**
     * 发起注册请求
     */
    private void requestRegister() {
        Log.i(TAG, "============================================");
        AppProgress.getInstance().showProgress(this, "注册中...");

        JsonCallback responseCallback = new JsonCallback() {

            @Override
            public void onFailed(String str) {
                showToast("用户注册失败：" + str);
                AppProgress.getInstance().cancelProgress();
            }

            public void onSuccess(String msg, String jsonstr) {
                if (TextUtils.isEmpty(jsonstr)) {
                    showToast("用户注册失败：" + msg);
                    return;
                }
                ResponseLogin response = ParseJson_Object(jsonstr, ResponseLogin.class);
                User.getInstance().setLoginResponse(response);
                if (type == Type.DriverType) {
                    requestVerifyDriver();

                } else {
//                    AppProgress.getInstance().cancelProgress();
                    showToast("用户注册成功");

                    registerSuccess();
                }

            }

        };
        HttpHelper.getInstance().post(AppURL.Register, jsonUser, responseCallback);

        Log.i(TAG, "====>> end");

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
//        startActivity(DetailPhotoActivity.class, null);
        AppProgress.getInstance().cancelProgress();
        if (type == Type.DriverType) {
//            startActivity(HomeActivity.class);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.upload_verify_info).setNegativeButton("联系客服", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //todo 拨打客服电话
                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(HomeActivity.class);
                            finish();
                        }
                    });
                    builder.create().show();
                }
            });

        } else {
//            Bundle bundle = getIntent().getExtras();
//            if (bundle != null) {
//                startActivity(HomeActivity.class, bundle);
//            }
            startActivity(DetailPhotoActivity.class);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }


    public void onClick_OpenLaw(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "法律申明");
        bundle.putString(AppParams.WEB_PAGE_URL, AppURL.REGISTER_LAW.toString());
        startActivity(WebViewActivity.class, bundle);
    }


    /**
     * 照片选择
     *
     * @param view
     */
    public void onClick_Photo(View view) {
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView imageView, String url) {
                if (imageView != null) {
                    switch (imageView.getId()) {
                        case R.id.reg_driver_certificate_img:
                            //驾驶证
                            requestJsonDriver.setLicensePhotoUrl(url);
                            //身份证
                            jsonUser.setIdCardPhotoUrl(url);
                            break;
                    }
                }
            }
        });
    }


    /**
     * 司机的资料提交接口
     */
    private void requestVerifyDriver() {
        if (requestJsonDriver == null)
            return;
        requestJsonDriver.setUserId((int) User.getInstance().getId());

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

                //跟新driver信息
                requestJsonDriver = ParseJson_Object(result, JsonDriver.class);
                showToast("用户注册成功");
                registerSuccess();

            }

            @Override
            public void onFailed(String str) {
                AppProgress.getInstance().cancelProgress();
                showToast(getString(R.string.information_upload_fail) + str);
            }
        };
        HttpHelper.getInstance().post(AppURL.PostVerifyDrivers, requestJsonDriver, callback);
    }
}
