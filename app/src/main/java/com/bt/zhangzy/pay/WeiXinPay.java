package com.bt.zhangzy.pay;

import android.content.Context;
import android.util.Log;

import com.bt.zhangzy.tools.Tools;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * 支付流程 ：1、 服务器先调用 微信的统一下单接口
 *            2、然后返回给客户端支付交易会话ID
 *            3、客户端发起支付请求
 *            4、微信后台回调 服务器 并通知客户端；
 * Created by ZhangZy on 2016-1-29.
 */
public class WeiXinPay {
    static final String TAG = WeiXinPay.class.getSimpleName();
    static WeiXinPay instanse = new WeiXinPay();

    static final String APPID = "06330317e7c40e63bb7c5685a8709d64";
    static final String KEY = "";//key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置


    IWXAPI iwxapi;

    WeiXinPay() {
    }

    public static WeiXinPay getInstanse() {
        return instanse;
    }

    public void init(Context context) {
        iwxapi = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信
        iwxapi.registerApp(APPID);
    }

    //https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_12
    public void pay() {
        PayReq request = new PayReq();
        request.appId = APPID;//"wxd930ea5d5a258f4f";//微信分配的公众账号ID
        request.partnerId = "1900000109";//微信支付分配的商户号
        request.prepayId = "1101000000140415649af9fc314aa427";//微信返回的支付交易会话ID
        request.packageValue = "Sign=WXPay";//暂填写固定值Sign=WXPay
        request.nonceStr = "1101000000140429eb40476f8896f4c9";//随机字符串，不长于32位。推荐随机数生成算法
        request.timeStamp = "1398746574";//时间戳，请见接口规则-参数规定
        request.sign = "7FFECB600D7157C5AA49810D2D8F28BC2811827B";//签名，详见签名生成算法
        iwxapi.sendReq(request);
    }

    public String sign(HashMap<String,String> paramsMap){
        if(paramsMap == null || paramsMap.isEmpty())
            return "";
//        HashMap<String,String> paramsMap = new HashMap<>();
//        paramsMap.put("appid","wxd930ea5d5a258f4f");
//        paramsMap.put("mch_id","10000100");
//        paramsMap.put("device_info","1000");
//        paramsMap.put("body", "test");
//        paramsMap.put("nonce_str", "ibuaiVcKdpRxkhJA");
        Set<String> strings = paramsMap.keySet();
        String[] key_string = strings.toArray(new String[strings.size()]);
        Arrays.sort(key_string);
        StringBuffer stringBuffer = new StringBuffer();
        for(String key : key_string){
            stringBuffer.append(key).append("=").append(paramsMap.get(key));
            stringBuffer.append("&");
        }
        //删除最后一个 &
//        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        Log.d(TAG, "排序后:" + stringBuffer.toString());
        stringBuffer/*.append("&")*/.append("key").append("=").append(KEY);
//        String stringSignTemp = stringBuffer.toString()+"&key=192006250b4c09247ec02edce69f6a2d";
        String stringSignTemp = Tools.MD5(stringBuffer.toString()).toUpperCase();
        Log.d(TAG, "签名后:" + stringSignTemp);
        return stringBuffer.toString();
    }

}
