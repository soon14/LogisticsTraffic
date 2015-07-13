package com.bt.zhangzy.logisticstraffic.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.File;
import java.util.List;

/**
 * Created by ZhangZy on 2015/6/11.
 */
public class ContextTools {

    private static final String TAG = ContextTools.class.getSimpleName();

    /**
     * 打电话
     * 需要权限  <uses-permission android:name="android.permission.CALL_PHONE"/>
     * zhangzy
     */
    public static void CallPhone(Context context, String mobile) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
        context.startActivity(intent);

    }

    /**
     * 需要添加权限 <uses-permission android:name="android.permission.SEND_SMS" />
     * destinationAddress      发送短信的地址（也就是号码）
     * scAddress               短信服务中心，如果为null，就是用当前默认的短信服务中心
     * text                    短信内容
     * sentIntent              如果不为null，当短信发送成功或者失败时，这个PendingIntent会被广播出去成功的结果代码是Activity.RESULT_OK，或者下面这些错误之一  ：RESULT_ERROR_GENERIC_FAILURE,RESULT_ERROR_RADIO_OFF,RESULT_ERROR_NULL_PDU等
     * 通俗点说： 发送 -->中国移动 --> 中国移动发送失败 --> 返回发送成功或失败信号 --> 后续处理   即，这个意图包装了短信发送状态的信息
     * deliveryIntent          如果不为null，当这个短信发送到接收者那里，这个PendtingIntent会被广播，状态报告生成的pdu（指对等层次之间传递的数据单位）会拓展到数据（"pdu"）
     * 通俗点就是：发送 -->中国电信 --> 中国电信发送成功 --> 返回对方是否收到这个信息 --> 后续处理  即：这个意图包装了短信是否被对方收到的状态信息（供应商已经发送成功，但是对方没有收到）。
     * smsManager.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent)
     */
    public static void SendMessage(Context context, String number, String message) {
        //直接调用短信接口发短信
        SmsManager smsManager = SmsManager.getDefault();
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(number, null, text, null, null);
        }
    }


    /**
     * 调起系统发短信功能
     *
     * @param phoneNumber 发送短信的接收号码
     * @param message     短信内容
     */
    public static void SendSMS(Context context, String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }


    /**
     * 使用系统的图片浏览器
     */
    public static void OpenPicture(Activity context, Uri uri) {
        //下方是是通过Intent调用系统的图片查看器的关键代码
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
//        intent.setDataAndType(Uri.fromFile(file), "image/*");
        context.startActivity(intent);
    }


    /***/
    public static ComponentName getTopComponentName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentName = taskInfo.get(0).topActivity;
        return componentName;
    }

    /**
     * 判断MIUI的悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isMiuiFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        //TODO:判断MIUI的悬浮窗权限 http://www.cnblogs.com/fangyucun/p/4027750.html
/*
        if (version >= 19) {
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);  //自己写就是24 为什么是24?看AppOpsManager
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                if (AppOpsManager.MODE_ALLOWED == (Integer) ReflectUtils.invokeMethod(manager, "checkOp", op, Binder.getCallingUid(), context.packageName())) {  //这儿反射就自己写吧
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }*/
        return false;
    }

}
