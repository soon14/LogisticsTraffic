package com.bt.zhangzy.logisticstraffic.d.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhangzy.base.http.HttpHelper;
import com.zhangzy.base.http.JsonCallback;
import com.zhangzy.base.tools.ContextTools;
import com.zhangzy.base.tools.Tools;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * 支付流程 ：1、 服务器先调用 微信的统一下单接口
 * 2、然后返回给客户端支付交易会话ID
 * 3、客户端发起支付请求
 * 4、微信后台回调 服务器 并通知客户端；
 * Created by ZhangZy on 2016-1-29.
 */
public class WeiXinPay {
    static final String TAG = WeiXinPay.class.getSimpleName();
    static WeiXinPay instanse = new WeiXinPay();
    String Host = null;//"http://182.92.77.31";
    final String PayURL = "/pay/weixin_submit";//    PayWeiXin("/pay/weixin_submit"),
    final String PayOtherURL = "/pay/weixin_qr_submit";
    public final String APPID = "wxd8934ee255eb1e0f";//wxd8934ee255eb1e0f wxd8934ee255eb1e0f
//    static final String KEY = "";//key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置

    Activity activity;
    IWXAPI iwxapi;
    WXResponse response;
    ProgressDialog progress;
    WXPayResultCallback callback;

    WeiXinPay() {
    }

    public static WeiXinPay getInstanse() {
        return instanse;
    }

    public WXPayResultCallback getCallback() {
        return callback;
    }

    public void setCallback(WXPayResultCallback callback) {
        this.callback = callback;
    }

    /*
        初始化 实例
         */
    private void init(Activity context) {
        //获取实例
        iwxapi = WXAPIFactory.createWXAPI(context, APPID, false);
        loadAppParams(context);
//        iwxapi = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信
//        boolean registerApp = iwxapi.registerApp(APPID);
//        Log.d(TAG, "registerApp=" + registerApp);
//        iwxapi.handleIntent(Intent.getIntent())
    }

    public void loadAppParams(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            String host = appInfo.metaData.getString("APP_Host");
            Log.w(TAG, "请求地址：" + host);
            Host = host;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void payOther(Activity context, String msg, int amount, int userId) {
        activity = context;
        showProgress();

//        Log.d(TAG, "调用微信openWXApp:" + iwxapi.openWXApp());

        WXRequest params = new WXRequest();
        params.setTitle("会员购买");
        params.setDetail(msg);
        params.setAmount(amount);
        params.setUserId(userId);
        String localWifiIP = ContextTools.getLocalWifiIP(context);
        String localIpAddress = ContextTools.getLocalIpAddress();
        Log.d(TAG, "wifi IP=" + localWifiIP + " IP=" + localIpAddress);
        params.setFrom(localWifiIP.startsWith("0.") ? localIpAddress : localWifiIP);
        HttpHelper.getInstance().get(Host + PayOtherURL, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                cancelProgress();
                Log.w(TAG, "下单成功:" + result);
                if (callback != null) {

                }
            }

            @Override
            public void onFailed(String str) {
                showToast("下单失败");
                Log.w(TAG, "下单失败:" + str);
                cancelProgress();
            }
        });
    }

    public void payUnifiedOrder(Activity context, String msg, int amount, int userId) {
        activity = context;
        showProgress();

//        Log.d(TAG, "调用微信openWXApp:" + iwxapi.openWXApp());

        WXRequest params = new WXRequest();
        params.setTitle("会员购买");
        params.setDetail(msg);
        params.setAmount(amount);
        params.setUserId(userId);
        String localWifiIP = ContextTools.getLocalWifiIP(context);
        String localIpAddress = ContextTools.getLocalIpAddress();
        Log.d(TAG, "wifi IP=" + localWifiIP + " IP=" + localIpAddress);
        params.setFrom(localWifiIP.startsWith("0.") ? localIpAddress : localWifiIP);
        HttpHelper.getInstance().get(Host + PayURL, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "http:" + msg + " result=" + result);
                response = ParseJson_Object(result, WXResponse.class);
                if (response == null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "response = null ", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                WXResponsePay weixinPay = response.getWeixinPay();
                if (weixinPay != null) {
                    if ("SUCCESS".equals(weixinPay.getResult_code())) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pay(response);
                            }
                        });
//                        pay(response);
                    } else {
                        showToast("下单失败:" + weixinPay.getResult_code());
                        cancelProgress();
                    }
                } else {
                    showToast("下单失败:" + response);
                    cancelProgress();
                }
            }

            @Override
            public void onFailed(String str) {
                showToast("下单失败");
                Log.w(TAG, "下单失败:" + str);
                cancelProgress();
            }
        });
    }

    private void showToast(final String s) {
        Log.i(TAG, "Toast:" + s);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
            }
        });


    }

    private void showProgress() {
        if (progress == null) {
            progress = new ProgressDialog(activity); // 获取对象
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置样式为圆形样式
//        progress.setTitle("友情提示"); // 设置进度条的标题信息
            progress.setMessage("支付中···"); // 设置进度条的提示信息
//        progress.setIcon(R.drawable.ic_launcher); // 设置进度条的图标
            progress.setIndeterminate(true); // 设置进度条是否为不明确
            progress.setCancelable(false); // 设置进度条是否按返回键取消
        }
        progress.show();
    }

    private void cancelProgress() {
        if (progress != null) {
            if (progress.isShowing())
                progress.cancel();
        }
    }

    private void showDialog(String msg) {
        new AlertDialog.Builder(activity)
                .setMessage(msg)
                .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        activity.finish();
                    }
                }).show();
    }


    //https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_12
    public void pay(WXResponse response) {
        Log.d(TAG, "调用微信支付接口:" + response.toString());
        WXResponsePay jsonPay = response.getWeixinPay();
        //获取实例
        iwxapi = WXAPIFactory.createWXAPI(activity, jsonPay.appid, false);
        if (!iwxapi.isWXAppInstalled()) {
            showDialog("请先安装微信客户端");
            return;
        }

        PayReq request = new PayReq();
        request.appId = jsonPay.appid;//APPID;//"wxd930ea5d5a258f4f";//微信分配的公众账号ID
        request.partnerId = jsonPay.mch_id;//"1309528301";//微信支付分配的商户号
        request.prepayId = jsonPay.prepay_id;//"1101000000140415649af9fc314aa427";//微信返回的支付交易会话ID
        request.packageValue = "Sign=WXPay";//暂填写固定值Sign=WXPay
//        request.packageValue = "prepay_id=" + jsonPay.prepay_id;
        request.nonceStr = jsonPay.nonce_str;//"1101000000140429eb40476f8896f4c9";//随机字符串，不长于32位。推荐随机数生成算法
        long time = response.getMember().getModifyDate().getTime();
        request.timeStamp = String.valueOf(time / 1000);//.substring(0, 10);//"1398746574";//时间戳，请见接口规则-参数规定
//        request.timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);//"1398746574";//时间戳，请见接口规则-参数规定
//        request.sign = jsonPay.sign;
        request.sign = sign(request, jsonPay.key);//sign(request);//jsonPay.sign;//"7FFECB600D7157C5AA49810D2D8F28BC2811827B";//签名，详见签名生成算法
//        request.extData = "app data"; // optional
//        request.signType = jsonPay.trade_type;
        Log.d(TAG, "微信支付  合法性检测：" + request.checkArgs());
        iwxapi.registerApp(jsonPay.appid);
        boolean req = iwxapi.sendReq(request);
        Log.d(TAG, "微信支付  结果：" + req);
        cancelProgress();

    }

    public String sign(PayReq request, String signKey) {
//        if (paramsMap == null || paramsMap.isEmpty())
//            return "";
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("appid", request.appId);
        paramsMap.put("partnerid", request.partnerId);
        paramsMap.put("prepayid", request.prepayId);
        paramsMap.put("package", request.packageValue);
        paramsMap.put("noncestr", request.nonceStr);
        paramsMap.put("timestamp", request.timeStamp);

        Set<String> strings = paramsMap.keySet();
        String[] key_string = strings.toArray(new String[strings.size()]);
        Arrays.sort(key_string);
        StringBuffer stringBuffer = new StringBuffer();
        for (String key : key_string) {
            stringBuffer.append(key.toLowerCase()).append("=").append(paramsMap.get(key));
            stringBuffer.append("&");
        }
        stringBuffer.append("key").append("=").append(signKey);
        Log.d(TAG, "排序后:" + stringBuffer.toString());
//        String stringSignTemp = stringBuffer.toString()+"&key=192006250b4c09247ec02edce69f6a2d";
        String stringSignTemp = Tools.MD5(stringBuffer.toString()).toUpperCase();
        Log.d(TAG, "签名后:" + stringSignTemp);
        return stringSignTemp;
    }

    /*分享或收藏的目标场景，通过修改scene场景值实现。
发送到聊天界面——WXSceneSession
发送到朋友圈——WXSceneTimeline
添加到微信收藏——WXSceneFavorite
    * */
    public void shareText(Context ctx, String string) {
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = string;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = string;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
//        req.openId = getOpenId();
        // 调用api接口发送数据到微信
        iwxapi.sendReq(req);
    }


    public void shareWebUrl(Activity ctx, String url) {
        if (iwxapi == null) {
            init(ctx);
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;//"http://www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "易运通";
        msg.description = "物流软件易运通下载链接";
//        Bitmap thumb = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher);
//        msg.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = req.WXSceneTimeline;
//        req.scene = SendMessageToWX.Req.WXSceneFavorite;
//        req.openId = getOpenId();
        iwxapi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}

