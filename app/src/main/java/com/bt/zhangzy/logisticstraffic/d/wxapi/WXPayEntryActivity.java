package com.bt.zhangzy.logisticstraffic.d.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.pay.WeiXinPay;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXPayEntryActivity.class.getSimpleName();

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, WeiXinPay.APPID);
//        api.registerApp(WeiXinPay.APPID);
        api.handleIntent(getIntent(), this);
        Log.d(TAG, "onCreate:" + getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        Log.d(TAG, "onNewIntent:" + getIntent());
    }

    @Override
    public void onReq(BaseReq req) {
        Log.d(TAG, "onReq req=" + req);
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        Log.d(TAG, "onPayFinish, errStr = " + resp.errStr + "==>" + resp.transaction);
        int code = resp.errCode;
        String msg = null;
        switch (code) {
            case 0://支付成功后的界面
                msg = "恭喜您支付成功!";
                break;
            case -1:
                msg = "签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、您的微信账号异常等。";
                break;
            case -2://用户取消支付后的界面
                msg = "您已取消支付";
                break;
        }
        //微信支付后续操作，失败，成功，取消

        if (msg == null)
            finish();
        else {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            };
            new ConfirmDialog(this)
                    .setMessage(msg)
                    .setConfirmListener(listener)
                    .setCancelListener(listener)
                    .show();
        }
    }
}