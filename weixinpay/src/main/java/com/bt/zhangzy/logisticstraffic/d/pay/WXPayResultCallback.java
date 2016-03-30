package com.bt.zhangzy.logisticstraffic.d.pay;

/**
 * 微信支付结果回调
 * Created by ZhangZy on 2016-3-30.
 */
public interface WXPayResultCallback {
    void paySuccess();

    void payFailed(String msg);
}
