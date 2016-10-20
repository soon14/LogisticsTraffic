package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.d.pay.WXPayResultCallback;
import com.bt.zhangzy.logisticstraffic.d.pay.WeiXinPay;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.tools.ContextTools;

import ali.pay.AliPay;
import ali.pay.AliPayResultCallback;

/**
 * 支付方式选择 页面
 * Created by ZhangZy on 2016-1-7.
 */
public class PayActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    RadioGroup payGroup;
    RadioGroup payMethodGroup;
    double payMoney;
    Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_before);
        setPageName("支付订单");

        //检测是否有车辆数据传入
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY)) {
                car = getIntent().getParcelableExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY);
            }
        }

        payGroup = (RadioGroup) findViewById(R.id.pay_select_group);
        payMethodGroup = (RadioGroup) findViewById(R.id.pay_method_group);
        payGroup.setOnCheckedChangeListener(this);
        payGroup.check(R.id.pay_select_1y);
        payMethodGroup.setOnCheckedChangeListener(this);
        payMethodGroup.check(R.id.pay_method_zhifubao);

        if (AppParams.APP_LVJ) {
            setTextView(R.id.pay_select_1d, getString(R.string.pay_one_day_info));

        } else {
            findViewById(R.id.pay_select_1d).setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.pay_select_2y);
            textView.setText(Html.fromHtml(getString(R.string.pay_two_info)));
            textView = (TextView) findViewById(R.id.pay_select_3y);
            textView.setText(Html.fromHtml(getString(R.string.pay_three_info)));
        }

        if (!AppParams.DEBUG) {
            //微信支付 暂时屏蔽
            findViewById(R.id.pay_method_weixin_other).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == payGroup) {
            switch (checkedId) {
                case R.id.pay_select_1d:
                    payMoney = 20;
                    break;
                case R.id.pay_select_1y:
                    payMoney = 120;
                    break;
                case R.id.pay_select_2y:
                    payMoney = 216;
                    break;
                case R.id.pay_select_3y:
                    payMoney = 288;
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
                //微信 计费单位是分  2016年4月5日：微信支付改成元为单位，和支付宝保持统一
                WeiXinPay.getInstanse().payUnifiedOrder(this, message, (int) (payMoney /** 100*/), (int) User.getInstance().getId(), car.getId());
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
            case R.id.pay_method_weixin_other:

                WeiXinPay.getInstanse().payOther(this, message, (int) payMoney, (int) User.getInstance().getId(), car.getId());
                WeiXinPay.getInstanse().setCallback(new WXPayResultCallback() {
                    @Override
                    public void paySuccess() {
//                        gotoWebPage();
                    }

                    @Override
                    public void payFailed(String msg) {

                    }
                });
                break;
            case R.id.pay_method_zhifubao:
//                AliPayDemo.getInstance().pay(this);
                AliPay.getInstance().payUnifiedOrder(this, message, payMoney, (int) User.getInstance().getId(), ContextTools.getLocalIpAddress(), car.getId());
                AliPay.getInstance().setCallback(new AliPayResultCallback() {
                    @Override
                    public void paySuccess() {
                        showConfirmDialog("支付成功", "恭喜您支付成功");
                    }

                    @Override
                    public void payFailed(String msg) {
                        showConfirmDialog("支付失败", msg);
                    }

                    @Override
                    public void payOther(String url) {

                    }
                });
                break;
            case R.id.pay_method_zhifubao_other:
                String url = AliPay.getInstance().payOther(this, message, payMoney, (int) User.getInstance().getId(), ContextTools.getLocalIpAddress(), car.getId());
                gotoWebPage("支付宝扫码支付", url);
//                AliPay.getInstance().setCallback(new AliPayResultCallback() {
//                    @Override
//                    public void paySuccess() {
//
//                    }
//
//                    @Override
//                    public void payFailed(String msg) {
//
//                    }
//
//                    @Override
//                    public void payOther(String url) {
//                        gotoWebPage("支付宝扫码支付", url);
//                    }
//                });
                break;
        }
    }

    private void showConfirmDialog(String title, String msg) {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跟新用户的支付状态
                User.getInstance().requestPayStatus(null);
                finish();
            }
        };
        new ConfirmDialog(this)
                .setMessage(msg)
                .setConfirmListener(listener)
                .setCancelListener(listener)
                .show();


    }

    public void gotoWebPage(String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, title);
        bundle.putString(AppParams.WEB_PAGE_URL, url);
        startActivity(WebViewActivity.class, bundle);
    }

}
