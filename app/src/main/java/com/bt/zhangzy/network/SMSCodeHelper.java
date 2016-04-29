package com.bt.zhangzy.network;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-3-18.
 */
public class SMSCodeHelper implements Handler.Callback {
    private static final String TAG = SMSCodeHelper.class.getSimpleName();
    private static final String SMS_TAG_PHONE = "SMS_TAG_PHONE";
    private static final String SMS_TAG_CODE = "SMS_TAG_CODE";
    private static final String SMS_TAG_TIME = "SMS_TAG_TIME";


    private static final long DELAY_TIME = 3 * 1000;//延时时间
    public static final int REQUSET_COUNT = 5;//请求次数

    private static final int WHAT_SEND_BT_HIDE = 2;//显示等待发送按钮
    private static final int WHAT_SEND_BT_WAITING = 3;//更新按钮文字
    private static final int WHAT_SEND_BT_AGAIN = 4;//重新发送

    static SMSCodeHelper instance = new SMSCodeHelper();
    private int verificationCode;//返回的验证码
    int requestNum;

    BaseActivity activity;
    String phoneNum;//发送的手机号
    String SMScode;//短信模板的编号
    Handler handler;//验证码延时获取处理
    private TextView sendBt;//发送按钮
    int waitingTime = 60;

    public static SMSCodeHelper getInstance() {
        return instance;
    }

    private SMSCodeHelper() {
        handler = new Handler(Looper.getMainLooper(), this);
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
    public boolean checkVerificationCode(BaseActivity act, String inputCode) {

        if (!TextUtils.isEmpty(inputCode))
            if (TextUtils.isDigitsOnly(inputCode)) {
//                int input_code = Integer.valueOf(inputCode);
                //如果还没有验证码 则到配置文件里去读取上次的验证码
                if (verificationCode == 0) {
                    loadCode(act);
                }
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
            switch (msg.what) {
                case 1:
//            if (msg.what == 1) {
                    //从服务器获取短信验证码
                    requestNum++;
                    if (requestNum < REQUSET_COUNT) {
                        requestVerificationCode(phoneNum);
                    }
                    return true;
                case WHAT_SEND_BT_HIDE:
//            }else if(msg.what == WHAT_SEND_BT_HIDE){
                    if (sendBt != null) {
                        sendBt.setEnabled(false);
                        waitingTime = 60;
                        sendBt.setText(String.format(activity.getString(R.string.sms_send_bt_waiting), waitingTime));
                        handler.sendEmptyMessageDelayed(WHAT_SEND_BT_WAITING, 1000);
                    }
                    return true;
                case WHAT_SEND_BT_WAITING:
                    if (sendBt != null) {
                        waitingTime -= 1;
                        sendBt.setText(String.format(activity.getString(R.string.sms_send_bt_waiting), waitingTime));
                        if (waitingTime > 0) {
                            handler.sendEmptyMessageDelayed(WHAT_SEND_BT_WAITING, 1000);
                        } else {
                            handler.sendEmptyMessage(WHAT_SEND_BT_AGAIN);
                        }
                    }
                    return true;
                case WHAT_SEND_BT_AGAIN:
                    if (sendBt != null) {
                        sendBt.setEnabled(true);
                        sendBt.setText(R.string.sms_send_bt);
                    }
                    return true;
            }

        }
        return false;
    }

    /**
     * 验证收货 短信发送接口
     *
     * @param activity
     * @param fromPhone
     * @param toPhone
     * @param code
     */
    public void sendSMS_Receiver(BaseActivity activity, final TextView sendBt, String fromPhone, String toPhone, String code) {
        if (Tools.isEmptyStrings(fromPhone, toPhone, code)) {
            activity.showToast("参数不能为空");
            return;
        }

        this.activity = activity;
        this.sendBt = sendBt;
        this.phoneNum = fromPhone + toPhone;
        SMScode = code;
        requestNum = 0;
        verificationCode = 0;
        String params = fromPhone + '/' + toPhone + "?template=" + code;
        HttpHelper.getInstance().get(AppURL.SendVerificationCodeToReceiver, params, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("验证码发送失败[" + str + "]");
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("验证码发送成功");
                getInstance().handler.sendEmptyMessage(WHAT_SEND_BT_HIDE);
                requestVerificationCode(getInstance().phoneNum);
            }

        });
    }

    /**
     * 发送短信
     *
     * @param activity
     * @param phoneNum
     * @param code
     */
    public void sendSMS(BaseActivity activity, TextView sendBt, String phoneNum, String code) {
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(code)) {
            activity.showToast("参数不能为空");
        }
        this.activity = activity;
        this.sendBt = sendBt;
        this.phoneNum = phoneNum;
        SMScode = code;
        requestNum = 0;
        verificationCode = 0;
        String params = phoneNum + "?template=" + code;
        HttpHelper.getInstance().get(AppURL.SendVerificationCode, params, new JsonCallback() {

            @Override
            public void onFailed(String str) {
                showToast("验证码发送失败[" + str + "]");
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("验证码发送成功");
                getInstance().handler.sendEmptyMessage(WHAT_SEND_BT_HIDE);
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
                saveCode();
                Log.w(TAG, "获取验证码成功" + entity);
            }

            @Override
            public void onFailed(String str) {
                Log.w(TAG, "获取验证码失败" + str);
                handler.sendEmptyMessageDelayed(1, DELAY_TIME);
            }
        });
    }


    private void saveCode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SMS_TAG_PHONE, phoneNum);
        editor.putInt(SMS_TAG_CODE, verificationCode);
        editor.putLong(SMS_TAG_TIME, System.currentTimeMillis());
        editor.commit();
        Log.i(TAG, preferences.toString());
    }

    private void loadCode(Activity act) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(act);
        String phone = preferences.getString(SMS_TAG_PHONE, "0");
//        if (phone.equals(phoneNum)) {
            long time = preferences.getLong(SMS_TAG_TIME, 0);
            //有效时间 5分钟
            if (System.currentTimeMillis() - time < 5 * 60 * 1000) {
                verificationCode = preferences.getInt(SMS_TAG_CODE, 0);
                Log.d(TAG, "load preferences code=" + verificationCode);
            } else
                Log.d(TAG, "load preferences time out");
//        } else
            Log.d(TAG, "load preferences change phone old=" + phone + " new=" + phoneNum);
    }


}
