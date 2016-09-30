package ali.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.zhangzy.base.http.HttpHelper;
import com.zhangzy.base.http.JsonCallback;

import java.util.HashMap;

/**
 * Created by ZhangZy on 2016-3-25.
 */
public class AliPay {
    private static final String TAG = AliPay.class.getSimpleName();

    static String HOST = null;
    public static final String PAY_URL = "/pay/alipay_submit";
    public static final String PAY_OTHER_URL = "/pay/alipay_qr_submit";

    private static final int SDK_PAY_FLAG = 1;
    // 商户PID
    public static final String PARTNER = "2088121804817103";// APPID= "2016012101110584";
    // 商户收款账号
//    public static final String SELLER = "dev_wuliuhui@sina.com";
    public static final String SELLER = "dev@yyt56.net";


    static AliPay instance = new AliPay();
    private Activity activity;

    private AliPay() {

    }

    public static AliPay getInstance() {
        return instance;
    }

    Response response;
    AliPayResultCallback callback;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    AliPayResult payResult = new AliPayResult((String) msg.obj);
                    Log.d(TAG, "支付宝 支付结果:" + payResult);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
                        if (callback != null)
                            callback.paySuccess();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        /*9000	订单支付成功
                            8000	正在处理中
                            4000	订单支付失败
                            6001	用户中途取消
                            6002	网络连接出错*/
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        String message = "支付失败";
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            message = "支付结果确认中";
                        }
                        if (TextUtils.equals(resultStatus, "6001")) {
                            Toast.makeText(activity, "用户中途取消", Toast.LENGTH_SHORT).show();
                            message = "用户中途取消";
                        }
                        if (TextUtils.equals(resultStatus, "6002")) {
                            Toast.makeText(activity, "网络连接出错", Toast.LENGTH_SHORT).show();
                            message = "网络连接出错";
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                        if (callback != null)
                            callback.payFailed(message);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    public void loadAppParams(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            String host = appInfo.metaData.getString("APP_Host");
            Log.w(TAG, "请求地址：" + host);
            HOST = host;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCallback(AliPayResultCallback callback) {
        this.callback = callback;
    }

    public String payOther(Activity context, String msg, double amount, int userId, String ip,int id) {
        //http://182.92.77.31:8080/pay/alipay_qr_submit?userId=164&subject=1&body=1&total_fee=1
        if (TextUtils.isEmpty(HOST)) {
            loadAppParams(context);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("subject", "会员购买");
        params.put("body", msg);
        params.put("total_fee", String.valueOf(amount));
        params.put("userId", String.valueOf(userId));
        params.put("carId",String.valueOf(id));
        params.put("from", TextUtils.isEmpty(ip) ? "192.168.1.1" : ip);

        return HttpHelper.toString(HOST + PAY_OTHER_URL, params);
//        HttpHelper.getInstance().get(PAY_OTHER_URL, params, new JsonCallback() {
//            @Override
//            public void onSuccess(String msg, String result) {
//                if(callback !=null)
//                    callback.payOther(result);
//            }
//
//            @Override
//            public void onFailed(String str) {
//
//            }
//        });
    }

    public void payUnifiedOrder(Activity context, String msg, double amount, int userId, String ip, int id) {
        if (TextUtils.isEmpty(HOST)) {
            loadAppParams(context);
        }
        activity = context;
//        activity.showProgress("支付中···");


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("subject", "会员购买");
        params.put("body", msg);
        params.put("total_fee", String.valueOf(amount));
        params.put("userId", String.valueOf(userId));
        params.put("carId",String.valueOf(id));
        params.put("from", TextUtils.isEmpty(ip) ? "192.168.1.1" : ip);

        HttpHelper.getInstance().get(HOST + PAY_URL, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                response = ParseJson_Object(result, Response.class);
//                JsonAliPay pay = response.getAlipay();
                pay();
            }

            @Override
            public void onFailed(final String str) {
//                activity.showToast("下单失败");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "下单失败:" + str, Toast.LENGTH_LONG).show();
                    }
                });
                Log.w(TAG, "下单失败:" + str);
//                activity.cancelProgress();
            }
        });

    }

    private void pay() {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {

                JsonAliPay pay = response.getAlipay();
//                String payInfo = getOrderInfo(pay);
                String payInfo = pay.getOrderInfo() + "&sign=\"" + pay.getSign() + "\"&sign_type=\"" + pay.getSign_type() + "\"";
                Log.d(TAG, "alipay:" + payInfo);
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }


    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(JsonAliPay pay) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + pay.getOut_trade_no() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + pay.getSubject() + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + pay.getBody() + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + pay.getTotal_fee() + "\"";

        // 服务器异步通知页面路径
//        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
        orderInfo += "&notify_url=" + "\"" + pay.getNotify_url() + "\"";


        // 服务接口名称， 固定值
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
        orderInfo += "&service=\"" + pay.getService() + "\"";


        // 支付类型， 固定值
//        orderInfo += "&payment_type=\"1\"";
        orderInfo += "&payment_type=\"" + pay.getPayment_type() + "\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";


        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//        orderInfo += "&it_b_pay=\"30m\"";
        orderInfo += "&it_b_pay=\"" + pay.getIt_b_pay() + "\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";
        orderInfo += "&return_url=\"" + pay.getReturn_url() + "\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        orderInfo += "&sign=\"" + pay.getSign() + "\"&" + pay.getSign_type();
        return orderInfo;
    }
}
