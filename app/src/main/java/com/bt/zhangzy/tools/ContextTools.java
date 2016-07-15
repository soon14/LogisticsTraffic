package com.bt.zhangzy.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * 系统工具类，主要用于获取设备的相关信息和功能
 * Created by ZhangZy on 2015/6/11.
 *
 * @author ZhangZy
 */
public class ContextTools {

    private static final String TAG = ContextTools.class.getSimpleName();

    /**
     * 获取ip地址
     * wifi 网络信息
     *
     * @param context
     * @return
     */
    public static String getLocalWifiIP(Context context) {
        //获取wifi服务
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        //判断wifi是否开启
//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
//        }
        int ipAddress = info.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        String ip_str = inetAddress.getHostAddress();
                        System.out.print("IpAddress=" + ip_str);
                        Log.i(TAG, "IpAddress=" + ip_str);
                        if (!TextUtils.isEmpty(ip_str)) {
                            return ip_str;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "WifiPreference IpAddress", ex);
        }


        return null;
    }


    /**
     * 获取应用的数据存储目录，便于统一管理和更改；
     */
    public static String getCacheDir(Context context) {
        if (context == null)
            return null;
        /*这两个方法是通过上下文对象Context获取的，只要应用程序被卸载，这两个目录下的文件都要被清空。
        context.getCacheDir()  获取应用程序自己的缓存目录
        context.getExternalCacheDir() 获取应用程序在外部存储的存储目录
        static File getDataDirectory() 获得data的目录（/data）。
        static File getDownloadCacheDirectory() 获得下载缓存目录。（/cache）
        static File getExternalStorageDirectory() 获得外部存储媒体目录。（/mnt/sdcard  or /storage/sdcard0）
        static File getRootDirectory() 获得系统主目录（/system）
        */

       /*
       Log.i(TAG, "缓存目录 目录路径：" + context.getCacheDir());
        Log.i(TAG, "getExternalCacheDir 目录路径：" + context.getExternalCacheDir());

        //可以通过静态方法getExternalStorageState()来获取外部存储的状态，如果程序需要在外部存储里面读写数据，必须要先判断：
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

        }
        Log.i(TAG, "Environment.getDataDirectory 目录路径：" + Environment.getDataDirectory());
        Log.i(TAG, "Environment.getDownloadCacheDirectory 目录路径：" + Environment.getDownloadCacheDirectory());
        Log.i(TAG, "getPackageName 目录路径：" + context.getPackageName());
        */
        String cacheDir = context.getExternalCacheDir().getPath();

        return cacheDir;
    }

    /**
     * //显示虚拟键盘
     *
     * @param v
     */
    public static void ShowKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);

    }


    /**
     * 隐藏输入法键盘
     *
     * @param v 任意界面中的view
     */
    //隐藏虚拟键盘
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

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
     * 显示手机通讯录
     *
     * @param act
     * @param requestCode
     */
    public static void showContacts(Activity act, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        act.startActivityForResult(intent, requestCode);
    }

    /**
     * 用于从手机通讯录返回数据处理  配合跳转使用
     *
     * @param act
     * @param data
     * @return
     */
    public static String[] OnActivityRsultForContacts(Activity act, Intent data) {
        if (data == null)
            return null;
        Uri uri = data.getData();
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = act.getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);

            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

            if (phone != null) {
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            }

            phone.close();
            cursor.close();
        } else {
            Log.w(TAG, "get Contacts is fail");
        }

        return contact;
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


  /*  public void sendMessage(String content){
        //直接调用短信接口发短信
        SmsManager smsManager = SmsManager.getDefault();
        List<String> divideContents = smsManager.divideMessage(content);
        for (String text : divideContents) {
            smsManager.sendTextMessage("150xxxxxxxx", null, text, sentPI, deliverPI);
        }
    }*/

    /**
     * 显示发送短信界面
     *
     * @param act
     * @param phoneNum
     * @param msg
     */
    public void showMsg(Activity act, String phoneNum, String msg) {
        Uri uri = Uri.parse("smsto:" + phoneNum);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", msg);
        act.startActivity(it);
    }

    public static boolean isMIUI() {
        Log.i(TAG, "================== current system BRAND=" + android.os.Build.BRAND + "  MODEL=" + android.os.Build.MODEL);
        if (android.os.Build.BRAND.equals("Xiaomi") || android.os.Build.MODEL.startsWith("MI") || android.os.Build.MODEL.startsWith("HM")) {
            return true;
        }
        return false;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
