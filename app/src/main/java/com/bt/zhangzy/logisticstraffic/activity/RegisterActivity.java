package com.bt.zhangzy.logisticstraffic.activity;

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
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.SMSCodeHelper;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;
import com.zhangzy.base.app.AppProgress;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class RegisterActivity extends BaseActivity {

    private Type type;
    JsonDriver requestJsonDriver;
    JsonUser jsonUser = new JsonUser();
    JsonCompany requestJsonCompany;
    JsonEnterprise requestJsonEnterprise;

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
        setContentView(R.layout.register_company);
        type = Type.EnterpriseType;
        setPageName("企业用户注册");
        initCompanyView();
    }


    /**
     * 物流门企注册
     */
    public void onClick_RegisterDepartment(View view) {
        setContentView(R.layout.register_company);
        type = Type.CompanyInformationType;
        setPageName("物流用户注册");
        initCompanyView();
    }

    /**
     * 初始化 公司注册页面
     */
    private void initCompanyView() {
        if (type == Type.CompanyInformationType)
            requestJsonCompany = new JsonCompany();
        else
            requestJsonEnterprise = new JsonEnterprise();

        //默认选中
        CheckBox confirmCk = (CheckBox) findViewById(R.id.reg_confirm_ck);
        if (confirmCk != null) {
            confirmCk.setChecked(true);
        }

        //默认位置
        String locStr = getStringFromTextView(R.id.reg_city_ed);
        if (TextUtils.isEmpty(locStr)) {
            Location location = User.getInstance().getLocation();
            if (location != null) {
                String params = location.toText();
                setTextView(R.id.reg_city_ed, params);
                if (requestJsonCompany != null)
                    requestJsonCompany.setArea(params);
                if (requestJsonEnterprise != null)
                    requestJsonEnterprise.setArea(params);
            }
        }
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


    /**
     * 注册按钮
     *
     * @param view
     */
    public void onClick_RegisterBtn(View view) {
//        startActivity(new Intent(this,UserActivity.class));

        CheckBox confirmCk = (CheckBox) findViewById(R.id.reg_confirm_ck);
        if (!confirmCk.isChecked()) {
            showToast("请阅读并确认法律申明");
            return;
        }

//        EditText nameEd = (EditText) findViewById(R.id.reg_nickname_ed);
//        EditText phoneNumEd = (EditText) findViewById(R.id.reg_phoneNum_ed);
////        EditText passwordEd = (EditText) findViewById(R.id.reg_password_ed);
////        EditText passwordConfirmEd = (EditText) findViewById(R.id.reg_password_confirm_ed);
//        EditText verficationEd = (EditText) findViewById(R.id.reg_verification_ed);
//        EditText idcard = (EditText) findViewById(R.id.reg_id_card_ed);
        String name, phoneNum, password, recommend, verfication, idCardStr;

        name = getStringFromTextView(R.id.reg_nickname_ed);
        phoneNum = getStringFromTextView(R.id.reg_phoneNum_ed);
        verfication = getStringFromTextView(R.id.reg_verification_ed);
        idCardStr = getStringFromTextView(R.id.reg_id_card_ed);


        if (Tools.isEmptyStrings(name, phoneNum)) {
            showToast("信息不完整，请检查后在试");
            return;
        }


        // 司机用户信息判断
        if (type == Type.DriverType) {
            if (Tools.isEmptyStrings(idCardStr)) {
                showToast("请输入身份证号码");
                return;
            }
            if (Tools.isEmptyStrings(jsonUser.getIdCardPhotoUrl())) {
                showToast("请上传证件照");
                return;
            }
            requestJsonDriver.setIdCard(idCardStr);
//            jsonUser.setIdCardPhotoUrl();
        }
        //企业 和 信息部用户注册 信息判断
        else {
            String company_name = getStringFromTextView(R.id.reg_company_name_ed);
            String company_address = getStringFromTextView(R.id.reg_address_ed);
            if (Tools.isEmptyStrings(company_name)) {
                showToast("请输入公司名称");
                return;
            }
            if (Tools.isEmptyStrings(company_address)) {
                showToast("请输入公司地址");
                return;
            }

            if (requestJsonCompany != null) {
                if (Tools.isEmptyStrings(requestJsonCompany.getArea())) {
                    showToast("请选择公司所属城市");
                    return;
                }
                if (Tools.isEmptyStrings(requestJsonCompany.getBusinessLicenseUrl(), requestJsonCompany.getPhotoUrl())) {
                    showToast("请上传门头照和营业执照");
                    return;
                }
                requestJsonCompany.setName(company_name);
                requestJsonCompany.setAddress(company_address);
            }
            if (requestJsonEnterprise != null) {
                if (Tools.isEmptyStrings(requestJsonEnterprise.getArea())) {
                    showToast("请选择公司所属城市");
                    return;
                }
                if (Tools.isEmptyStrings(requestJsonEnterprise.getBusinessLicenseUrl(), requestJsonEnterprise.getPhotoUrl())) {
                    showToast("请上传门头照和营业执照");
                    return;
                }
                requestJsonEnterprise.setName(company_name);
                requestJsonEnterprise.setAddress(company_address);
            }
        }

        if (!Tools.IsPhoneNum(phoneNum)) {
            showToast("手机号输入错误");
            return;
        }
        if (TextUtils.isEmpty(verfication)) {
            showToast("请输入验证码");
            return;
        }
//        verfication = verficationEd.getText().toString().trim();
        if (!SMSCodeHelper.getInstance().checkVerificationCode(this, phoneNum, verfication)) {
            showToast("验证码错误");
            return;
        }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else
            super.onActivityResult(requestCode, resultCode, data);
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
//            startActivity(HomeActivity.class);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ConfirmDialog(RegisterActivity.this)
                        .setMessage(getString(R.string.register_success_info))
                        .setConfirm("确认")
                        .setHideCancelBt()
                        .setListener(new ConfirmDialog.ConfirmDialogListener() {
                            @Override
                            public void onClick(boolean isConfirm) {
                                startActivity(HomeActivity.class);
                                finish();
                            }
                        }).show();

            }
        });

    }


    public void onClick_OpenLaw(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "法律申明");
        bundle.putString(AppParams.WEB_PAGE_URL, AppURL.REGISTER_LAW.toString());
        startActivity(WebViewActivity.class, bundle);
    }

    /**
     * 位置选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(View view) {
        if (view == null || !(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;
        String string = ViewUtils.getStringFromTextView(textView);
        Location location;
        if (TextUtils.isEmpty(string)) {
            location = User.getInstance().getLocation();
        } else {
            location = Location.Parse(string);
        }
        LocationView.createDialog(this)
                .setCurrentLocation(location)
                .setListener(new LocationView.ChangingListener() {
                    @Override
                    public void onChanged(Location loc) {
                    }

                    public void onCancel(Location loc) {
                        if (loc == null)
                            return;

                        String params = loc.toText();
                        textView.setText(params);

                        if (requestJsonCompany != null)
                            requestJsonCompany.setArea(params);
                        if (requestJsonEnterprise != null)
                            requestJsonEnterprise.setArea(params);
                    }
                }).show();
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
                        case R.id.reg_company_business_img:
                            if (requestJsonCompany != null)
                                requestJsonCompany.setBusinessLicenseUrl(url);
                            if (requestJsonEnterprise != null)
                                requestJsonEnterprise.setBusinessLicenseUrl(url);
                            break;
                        case R.id.reg_company_photo_img:
                            if (requestJsonCompany != null)
                                requestJsonCompany.setPhotoUrl(url);
                            if (requestJsonEnterprise != null)
                                requestJsonEnterprise.setPhotoUrl(url);
                            break;
                    }
                }
            }
        });
    }


    /**
     * 发起注册请求 注册user 然后在注册 角色
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
                //更新user信息
                jsonUser = User.getInstance().getJsonUser();
                if (type == Type.DriverType) {
                    requestVerifyDriver();

                } else if (type == Type.CompanyInformationType) {
                    requestVerifyInformation();
                } else if (type == Type.EnterpriseType) {
                    requestVerifyEnterprise();
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


    /**
     * 信息部 注册接口
     */
    private void requestVerifyInformation() {
//        company = new JsonCompany();
        JsonCompany company = requestJsonCompany;
        company.setId(User.getInstance().getCompanyID());
        company.setUserId(jsonUser.getId());
//        company.setName(name);
//        company.setAddress(address);
        //坐标
        Location location = User.getInstance().getLocation();
        if (location != null) {
            company.setLatitude(Float.valueOf(location.getLatitude()));
            company.setLongitude(Float.valueOf(location.getLongitude()));
//            company.setArea(location.toText());
        }
        //门头照片
//        company.setPhotoUrl(mtzpUrl);
        //营业执照
//        company.setBusinessLicenseUrl(yyzzUrl);
        //税务登记证
//        company.setTaxRegistrationCertificateUrl(swdjzUrl);
        //法人照 frsfzUrl

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                registerSuccess();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        HttpHelper.getInstance().post(AppURL.PostVerifyCompanies, company, callback);
    }

    /**
     * 企业注册接口
     */
    private void requestVerifyEnterprise() {
        JsonEnterprise enterprise = requestJsonEnterprise;
        enterprise.setUserId(jsonUser.getId());
//        enterprise.setName(name);
//        enterprise.setAddress(address);
//        //门头照片
//        enterprise.setPhotoUrl(mtzpUrl);
//        //营业执照
//        enterprise.setBusinessLicenseUrl(yyzzUrl);
        //税务登记证
//        enterprise.setTaxRegistrationCertificateUrl(swdjzUrl);
        //法人照 frsfzUrl

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                registerSuccess();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        HttpHelper.getInstance().post(AppURL.PostVerifyEnterprises, enterprise, callback);
    }
}
