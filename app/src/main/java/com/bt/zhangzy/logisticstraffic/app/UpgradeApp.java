package com.bt.zhangzy.logisticstraffic.app;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * App 升级功能封装
 * 先实现apk版本升级，以后实现增量升级。
 * Created by ZhangZy on 2016-8-10.
 */
public class UpgradeApp implements Handler.Callback {
    public static final String TAG = UpgradeApp.class.getSimpleName();
    public static final int HANDLER_LOAD = 1;//从服务器读取最新的app配置文件
    public static final int HANDLER_CHECK = 2;//和现有的版本做对比！
    public static final int HANDLER_DIALOG = 3;//如果需要升级 则弹出升级对话框
    public static final int HANDLER_UPGRADE = 4;//开始升级程序

    Properties properties;
    Context context;
    Handler handler;
    private String versionName;
    private String versionMsg;
    private String versionUrl;
    private int versionType = 1;//默认1 是可选升级，2为强制升级

    public UpgradeApp(Context context) {
        this.context = context;
        handler = new Handler(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null) {
            switch (msg.what) {
                case HANDLER_LOAD:
                    checkAppVersion();
                    break;
                case HANDLER_CHECK:
                    checkAppVersionProperties();
                    break;
                case HANDLER_DIALOG:
                    showUpgradeDialog();
                    break;
                case HANDLER_UPGRADE:
                    downLoadApk(versionUrl);
                    break;
            }
            return true;
        }
        return false;
    }

    /***
     * 开始升级程序检测
     */
    public void checkApp() {
        handler.sendEmptyMessageDelayed(HANDLER_LOAD, 1000);
    }

    /**
     * 升级检测
     */
    public void checkAppVersion() {
        Log.i(TAG, "==== Request App Version Properties File =========");
        /*
        http://www.yyt56.net:8080/conf/AndroidDriverConfig.properties
http://www.yyt56.net:8080/conf/AndroidCompanyConfig.properties
        * */
        String url = AppParams.DRIVER_APP ? AppURL.APP_UPDATA_DRIVER.toString() : AppURL.APP_UPDATA_COMPANY.toString();
        HttpHelper.getInstance().get(url, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.w(TAG, "升级文件加载失败：" + request.toString(), e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d(TAG, "升级文件加载成功:" + response.toString());
                properties = new Properties();
//                properties.load(response.body().byteStream());
                properties.load(new InputStreamReader(response.body().byteStream(), "UTF-8"));
                Log.d(TAG, "properties = " + properties.toString());

                handler.sendEmptyMessage(HANDLER_CHECK);

            }
        });
    }

    /**
     * 做版本比对
     */
    public void checkAppVersionProperties() {
        Log.i(TAG, "====check App Version Properties =========");
        if (properties == null) {
            Log.w(TAG, " Properties = null");
            return;
        }
        if (context == null) {
            Log.w(TAG, " context = null");
            return;
        }
        String packageName = context.getPackageName();
        if (packageName.equals(properties.getProperty("package"))) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
                int versionCode = Integer.parseInt(properties.getProperty("versionCode", "0"));
                if (packageInfo.versionCode < versionCode) {
                    versionName = properties.getProperty("versionName");
                    versionUrl = properties.getProperty("url");
                    //默认1 是可选升级，2为强制升级
                    versionType = Integer.parseInt(properties.getProperty("versionType", "1"));
                    versionMsg = "升级内容:\n" + properties.getProperty("message");
                    Log.i(TAG, "有新的版本 versionName=" + versionName);

                    //在ui线程中出对话框  30秒后在出对话框
                    new Handler(Looper.getMainLooper(), this).sendEmptyMessageDelayed(HANDLER_DIALOG, 2 * 1000);


                } else {
                    Log.i(TAG, "没有新版本!");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.w(TAG, e);
            }
        } else {
            Log.i(TAG, packageName + " != " + properties.getProperty("package") + " !!! ");
        }
    }

    public void showUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("请升级APP至版本:" + versionName)
                .setMessage(versionMsg)
                .setCancelable(false)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        handler.sendEmptyMessage(HANDLER_UPGRADE);
                        dialog.cancel();
                    }
                }).setNegativeButton(versionType == 2 ? "退出" : "跳过", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (versionType == 2) {
                            System.exit(0);
                        }
                    }
                });
        builder.show();
    }

    /*
     * 从服务器中下载APK
	 */
    protected void downLoadApk(String url) {
        if (context == null) {

        }
        //使用系统下载类
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置自定义下载路径和文件名
//                                    String apkName =  "yourName" + DateUtils.getCurrentMillis() + ".apk";
//                                    request.setDestinationInExternalPublicDir(yourPath, apkName);
//                                    MyApplication.getInstance().setApkName(apkName);
        //设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
        //request.setShowRunningNotification(false);

        //不显示下载界面
        request.setVisibleInDownloadsUi(false);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/cn.trinea.download.file");
        /*设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，下载后的文件
        在/mnt/sdcard/Android/data/packageName/files目录下面，如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个  目录下面*/
        //request.setDestinationInExternalFilesDir(this, null, "tar.apk");
        long id = downloadManager.enqueue(request);//TO DO 把id保存好，在接收者里面要用，最好保存在Preferences里面
        setApkId(Long.toString(id));//TO DO 把id存储在Preferences里面
    }

    /**
     * 设置下载APK ID
     *
     * @param id
     * @return
     */
    public void setApkId(String id) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString("APK_ID", id).commit()) {
            Log.i(TAG, "save apk id = " + id);
        }
    }


    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}
