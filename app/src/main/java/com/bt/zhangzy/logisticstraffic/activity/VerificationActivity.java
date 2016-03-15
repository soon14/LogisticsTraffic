package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.NetCallback;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.RequestOrderAccept_Reject;

/**
 * Created by ZhangZy on 2016-2-23.
 */
public class VerificationActivity extends BaseActivity {

    long lastSendCodeTime;
    String phoneNumber;
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_order);

        setPageName("验证收货");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppParams.BUNDLE_VERIFICATION_ORDER_PHONE_KEY)) {
                phoneNumber = bundle.getString(AppParams.BUNDLE_VERIFICATION_ORDER_PHONE_KEY);
            }
            if (bundle.containsKey(AppParams.BUNDLE_VERIFICATION_ORDER_ID_KEY)) {
                orderId = bundle.getInt(AppParams.BUNDLE_VERIFICATION_ORDER_ID_KEY);
            }
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("目标手机号为空");
        }
        Log.i(TAG, "目标手机号为:" + phoneNumber + " 订单ID:" + orderId);
    }

    public void onClick_Verification(View view) {
        EditText verficationEd = (EditText) findViewById(R.id.order_verification_ed);
        if (TextUtils.isEmpty(verficationEd.getText())) {
            showToast("请输入验证码");
            return;
        }
        String verfication = verficationEd.getText().toString().trim();
        //test 万能验证码
        if (AppParams.DEBUG && verfication.equals("0000")) {

        } else if (!verfication.equals(String.valueOf(verificationCode))) {
            showToast("验证码错误");
            return;
        }

        requestFinishOrder();
    }

    private void requestFinishOrder() {

        RequestOrderAccept_Reject params = new RequestOrderAccept_Reject();
        params.setOrderId(orderId);
        HttpHelper.getInstance().post(AppURL.PostFinishOrder, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("订单已完成");
                finish();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }


    /* 给手机发送验证码  */
    public void onClick_SendVerificationCode(View view) {
        if (Math.abs(System.currentTimeMillis() - lastSendCodeTime) < 60 * 1000) {
            //发送间隔小于一分钟
            showToast("验证码发送太频繁了，请稍后再试");
            return;
        }
        lastSendCodeTime = System.currentTimeMillis();
//        EditText phoneNumEd = (EditText) findViewById(R.id.reg_phoneNum_ed);
//        if (TextUtils.isEmpty(phoneNumEd.getText()) || phoneNumEd.getText().length() != 11) {
//            showToast("手机号输入错误");
//            return;
//        }

        HttpHelper.getInstance().get(AppURL.SendVerificationCode + phoneNumber, new NetCallback() {

            @Override
            public void onFailed(String str) {
                showToast("验证码发送失败");
            }

            @Override
            public void onSuccess(String str) {
//                Json js = Json.ToJson(str);
                showToast("验证码发送成功");
                requestVerificationCode(phoneNumber);
            }
        });


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
}
