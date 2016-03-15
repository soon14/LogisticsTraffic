package com.bt.zhangzy.pay;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WXAppRegister extends BroadcastReceiver implements IWXAPIEventHandler {
    private Context context;


//    @Override
//    public void onReceive(Context context, Intent intent) {
//        final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
//
//        // 将该app注册到微信
//        msgApi.registerApp(WeiXinPay.APPID);
//    }

    // override broadcast receiver
    public void onReceive(Context context, Intent data) {
        this.context = context;
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
        if (api.handleIntent(data, this)) {
            // intent handled as wechat request/response
            return;
        }

        // process other intent as you like
    }

    // process request
    public void onReq(BaseReq req) {

        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                Toast.makeText(context, "get message from wx, processed here", Toast.LENGTH_LONG).show();
                break;

            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                Toast.makeText(context, "show message from wx, processed here", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    // process response
    public void onResp(BaseResp resp) {
        switch (resp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH:
                Toast.makeText(context, "get auth resp, processed here", Toast.LENGTH_LONG).show();
                break;

            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                // 处理微信主程序返回的SendMessageToWX.Resp
                break;

            default:
                break;
        }
    }
}
