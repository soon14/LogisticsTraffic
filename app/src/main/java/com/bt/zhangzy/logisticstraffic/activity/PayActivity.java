package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.d.pay.WXPayResultCallback;
import com.bt.zhangzy.logisticstraffic.d.pay.WeiXinPay;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.tools.ContextTools;

import ali.pay.AliPay;
import ali.pay.AliPayResultCallback;

/**
 * Created by ZhangZy on 2016-1-7.
 */
public class PayActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    RadioGroup payGroup;
    RadioGroup payMethodGroup;
    double payMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_before);
        setPageName("支付订单");

        payGroup = (RadioGroup) findViewById(R.id.pay_select_group);
        payMethodGroup = (RadioGroup) findViewById(R.id.pay_method_group);
        payGroup.setOnCheckedChangeListener(this);
        payGroup.check(R.id.pay_select_12m);
        payMethodGroup.setOnCheckedChangeListener(this);
        payMethodGroup.check(R.id.pay_method_weixin);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == payGroup) {
            switch (checkedId) {
                case R.id.pay_select_3m:
                    payMoney = 30;
                    break;
                case R.id.pay_select_6m:
                    payMoney = 60;
                    break;
                case R.id.pay_select_12m:
                    payMoney = 120;
                    break;
            }
        } else if (group == payMethodGroup) {
            switch (checkedId) {
                case R.id.pay_method_weixin:
                    break;
                case R.id.pay_method_zhifubao:
                    break;
            }
        }

    }

    public void onClick_Pay(View view) {
        if (AppParams.DEBUG)
            payMoney = 0.01;
        String message = "您需要支付费用：" + payMoney + "元";

        switch (payMethodGroup.getCheckedRadioButtonId()) {
            case R.id.pay_method_weixin:
                //微信 计费单位是分
                WeiXinPay.getInstanse().payUnifiedOrder(this, message, (int) (payMoney * 100), (int) User.getInstance().getId());
                WeiXinPay.getInstanse().setCallback(new WXPayResultCallback() {
                    @Override
                    public void paySuccess() {
                        showConfirmDialog("支付成功", "恭喜您支付成功");
                    }

                    @Override
                    public void payFailed(String msg) {
                        showConfirmDialog("支付失败", msg);
                    }
                });
                break;
            case R.id.pay_method_zhifubao:
//                AliPayDemo.getInstance().pay(this);
                AliPay.getInstance().payUnifiedOrder(this, message, payMoney, (int) User.getInstance().getId(), ContextTools.getLocalIpAddress());
                AliPay.getInstance().setCallback(new AliPayResultCallback() {
                    @Override
                    public void paySuccess() {
                        showConfirmDialog("支付成功", "恭喜您支付成功");
                    }

                    @Override
                    public void payFailed(String msg) {
                        showConfirmDialog("支付失败", msg);
                    }
                });
                break;
        }
    }

    private void showConfirmDialog(String title, String msg) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跟新用户的支付状态
                User.getInstance().requestPayStatus();
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
