package com.bt.zhangzy.logisticstraffic.d.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.d.pay.WeiXinPay;
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
        setContentView(com.bt.zhangzy.logisticstraffic.wxpay.R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, WeiXinPay.getInstanse().APPID);
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
        final int code = resp.errCode;
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
            new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                            if (WeiXinPay.getInstanse().getCallback() != null) {
                                if (code == 0)
                                    WeiXinPay.getInstanse().getCallback().paySuccess();
                                else
                                    WeiXinPay.getInstanse().getCallback().payFailed("支付失败");
                            }
                        }
                    }).show();

        }
    }
}