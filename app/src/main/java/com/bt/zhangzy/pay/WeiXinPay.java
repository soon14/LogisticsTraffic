package com.bt.zhangzy.pay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.tools.Tools;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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

    //    public static final String APPID = "06330317e7c40e63bb7c5685a8709d64";
    public static final String APPID = "wxd8934ee255eb1e0f";//wxd8934ee255eb1e0f wxd8934ee255eb1e0f
    static final String KEY = "";//key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置

    BaseActivity activity;
    IWXAPI iwxapi;
    WXResponse response;

    WeiXinPay() {
    }

    public static WeiXinPay getInstanse() {
        return instanse;
    }

    /*
    初始化 实例
     */
    public void init(Activity context) {
        //获取实例
        iwxapi = WXAPIFactory.createWXAPI(context, APPID, true);
//        iwxapi = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信
        boolean registerApp = iwxapi.registerApp(APPID);
        Log.d(TAG, "registerApp=" + registerApp);
//        iwxapi.handleIntent(Intent.getIntent())
    }

    public void testPay() {
        String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
        HttpHelper.getInstance().get(url, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    public void payUnifiedOrder(BaseActivity context, int amount, int userId) {
        activity = context;

//        Log.d(TAG, "调用微信openWXApp:" + iwxapi.openWXApp());

        WeiXinJson params = new WeiXinJson();
//        params.setTitle("支付测试Android");
//        params.setDetail("支付测试详情android");
        params.setAmount(amount);
        params.setUserId(userId);
        String localWifiIP = ContextTools.getLocalWifiIP(context);
        Log.d(TAG, "wifi IP=" + localWifiIP);
        String localIpAddress = ContextTools.getLocalIpAddress();
        params.setFrom(localWifiIP);
        HttpHelper.getInstance().get(AppURL.PayWeiXin, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                response = ParseJson_Object(result, WXResponse.class);
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
                        activity.showToast("支付失败:" + weixinPay.getResult_code());
                    }
                }
            }

            @Override
            public void onFailed(String str) {
                activity.showToast("支付失败");
                Log.w(TAG, "支付失败:" + str);
            }
        });
    }

    //https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_12
    public void pay(WXResponse response) {
        boolean isPaySupported = iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        Log.d(TAG, "微信支付接口检测:" + isPaySupported);
        Log.d(TAG, "调用微信支付接口:" + response.toString());
        WXResponsePay jsonPay = response.getWeixinPay();
        PayReq request = new PayReq();
        request.appId = jsonPay.appid;//APPID;//"wxd930ea5d5a258f4f";//微信分配的公众账号ID
        request.partnerId = jsonPay.mch_id;//"1900000109";//微信支付分配的商户号
        request.prepayId = jsonPay.prepay_id;//"1101000000140415649af9fc314aa427";//微信返回的支付交易会话ID
        request.packageValue = "Sign=WXPay";//暂填写固定值Sign=WXPay
        request.nonceStr = jsonPay.nonce_str;//"1101000000140429eb40476f8896f4c9";//随机字符串，不长于32位。推荐随机数生成算法
        long time = response.getMember().getModifyDate().getTime();
        request.timeStamp = String.valueOf(time).substring(0, 10);//"1398746574";//时间戳，请见接口规则-参数规定
//        request.timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);//"1398746574";//时间戳，请见接口规则-参数规定

        request.sign = sign(request, response.getWeixinPay().key);//sign(request);//jsonPay.sign;//"7FFECB600D7157C5AA49810D2D8F28BC2811827B";//签名，详见签名生成算法
        request.extData = "app data"; // optional
        Log.d(TAG, "微信支付  合法性检测：" + request.checkArgs());
        boolean req = iwxapi.sendReq(request);
        Log.d(TAG, "微信支付  结果：" + req);

    }

    public String sign(PayReq request, String signKey) {
//        if (paramsMap == null || paramsMap.isEmpty())
//            return "";
        HashMap<String, String> paramsMap = new HashMap<>();
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
        //删除最后一个 &
//        if (stringBuffer.length() > 1)
//            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        stringBuffer/*.append("&")*/.append("key").append("=").append(signKey);
        Log.d(TAG, "排序后:" + stringBuffer.toString());
//        String stringSignTemp = stringBuffer.toString()+"&key=192006250b4c09247ec02edce69f6a2d";
        String stringSignTemp = Tools.MD5(stringBuffer.toString()).toUpperCase();
        Log.d(TAG, "签名后:" + stringSignTemp);
        return stringBuffer.toString();
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
        req.scene = SendMessageToWX.Req.WXSceneSession;
//        req.openId = getOpenId();
        // 调用api接口发送数据到微信
        iwxapi.sendReq(req);
    }


    public void shareWebUrl(Context ctx, String url) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        Bitmap thumb = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneFavorite;
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

    //参数封装
    class WeiXinJson extends BaseEntity {
        String title, detail,
        //                orderId,//返回的订单号包含yyyyMMdd前缀
//                totalFee,//价格 单位：分
        from;//发送订单的手机的ip地址
        int userId;
        int amount;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }


        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }


}

