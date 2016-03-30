package com.bt.zhangzy.network;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.zhangzy.base.http.BaseEntity;
import com.zhangzy.base.http.NetCallback;

/**
 * Created by ZhangZy on 2016-3-18.
 */
public class SMSCodeHelper implements Handler.Callback {
    private static final String TAG = SMSCodeHelper.class.getSimpleName();
    private static final long DELAY_TIME = 3 * 1000;//延时时间
    public static final int REQUSET_COUNT = 5;//请求次数
    static SMSCodeHelper instance = new SMSCodeHelper();
    private int verificationCode;//返回的验证码
    int requestNum;

    BaseActivity activity;
    String phoneNum;//发送的手机号
    String SMScode;//短信模板的编号
    Handler handler;//验证码延时获取处理

    public static SMSCodeHelper getInstance() {
        return instance;
    }

    private SMSCodeHelper() {
        handler = new Handler(this);
    }

    /**
     * 获取服务器上的验证码
     *
     * @return
     */
    public int getVerificationCode() {
        return verificationCode;
    }

    /**
     * 检查验证码
     *
     * @param inputCode
     * @return 是否一致
     */
    public boolean checkVerificationCode(String inputCode) {

        if (!TextUtils.isEmpty(inputCode))
            if (TextUtils.isDigitsOnly(inputCode)) {
//                int input_code = Integer.valueOf(inputCode);
                if (inputCode.equals(String.valueOf(verificationCode)))
                    return true;
                else
                    //测试用
                    return AppParams.DEBUG && inputCode.equals("0000");
            }
        return false;

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null) {
            if (msg.what == 1) {
                //从服务器获取短信验证码
                requestNum++;
                if (requestNum < REQUSET_COUNT) {
                    requestVerificationCode(phoneNum);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 发送短信
     *
     * @param activity
     * @param phoneNum
     * @param code
     */
    public void sendSMS(BaseActivity activity, String phoneNum, String code) {
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(code)) {
            activity.showToast("参数不能为空");
        }
        this.activity = activity;
        this.phoneNum = phoneNum;
        SMScode = code;
        requestNum = 0;
        verificationCode = 0;
        String params = phoneNum + "?template=" + code;
        HttpHelper.getInstance().get(AppURL.SendVerificationCode, params, new NetCallback() {

            @Override
            public void onFailed(String str) {
                showToast("验证码发送失败");
            }

            @Override
            public void onSuccess(String str) {
//                Json js = Json.ToJson(str);
                showToast("验证码发送成功");
                requestVerificationCode(getInstance().phoneNum);
            }
        });
    }

    /*@Deprecated
    private void testSMS() {
        HashMap map = new HashMap();
        map.put("account", "cf_yiyuntong");
        map.put("password", "zhang123456");
        map.put("mobile", "18686192818");
        String msg = "您的验证码是：【1212】。请不要把验证码泄露给其他人。";
        map.put("content", msg);
        HttpHelper.getInstance().post("http://106.ihuyi.cn/webservice/sms.php?method=Submit", map, new NetCallback() {
            @Override
            public void onFailed(String str) {

            }

            @Override
            public void onSuccess(String str) {

            }
        });
    }*/

    private void showToast(String s) {
        if (activity != null)
            activity.showToast(s);
    }

    /*获取手机验证码*/
    private void requestVerificationCode(String phoneNum) {

        HttpHelper.getInstance().get(AppURL.GetVerificationCode + phoneNum, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                if (TextUtils.isEmpty(result)) {
                    handler.sendEmptyMessageDelayed(1, DELAY_TIME);
                    return;
                }
                BaseEntity entity = ParseJson_Object(result, BaseEntity.class);
                verificationCode = entity.getCode();
                Log.w(TAG, "获取验证码成功" + entity);
            }

            @Override
            public void onFailed(String str) {
                Log.w(TAG, "获取验证码失败" + str);
                handler.sendEmptyMessageDelayed(1, DELAY_TIME);
            }
        });
    }


}
