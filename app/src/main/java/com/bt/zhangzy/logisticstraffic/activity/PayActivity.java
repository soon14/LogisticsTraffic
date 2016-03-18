package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.pay.AliPay;
import com.bt.zhangzy.pay.WeiXinPay;

/**
 * Created by ZhangZy on 2016-1-7.
 */
public class PayActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    RadioGroup payGroup;
    RadioGroup payMethodGroup;
    String payMoney;

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
                    payMoney = "30";
                    break;
                case R.id.pay_select_6m:
                    payMoney = "60";
                    break;
                case R.id.pay_select_12m:
                    payMoney = "120";
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
        String message = "您需要支付费用：" + payMoney + "元";
        switch (payMethodGroup.getCheckedRadioButtonId()) {
            case R.id.pay_method_weixin:
                //
                WeiXinPay.getInstanse().payUnifiedOrder(this, message, 10, (int) User.getInstance().getId());
                break;
            case R.id.pay_method_zhifubao:
                AliPay.getInstance().pay(this);
                break;
        }
    }

}
