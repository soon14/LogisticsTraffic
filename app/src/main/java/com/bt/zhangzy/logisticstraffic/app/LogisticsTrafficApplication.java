package com.bt.zhangzy.logisticstraffic.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhangzy.baidusdk.BaiduSDK;
import com.zhangzy.baidusdk.LBSTraceSDK;
import com.zhangzy.base.http.ImageHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import params.devices.zhangzy.bugly.BuglySDK;

/**
 * Created by ZhangZy on 2015/6/10.
 */
public class LogisticsTrafficApplication extends Application {

    private static final String TAG = LogisticsTrafficApplication.class.getSimpleName();
    public static final String APP_TYPE = "APP_TYPE";
    public static final String APP_HOST = "APP_HOST";

    private String versionName;

//    private JSONArray jsonCityList;


    @Override
    public void onCreate() {
        super.onCreate();

//        loadAppParams();

        //JPush 推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());


        AppParams.getInstance().init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());


        //初始化Image loader
        ImageHelper.getInstance().init(this);

        //Bugly 初始化；
        BuglySDK.getInstance().init(this,AppParams.DEBUG);

        //先放在这里，后期如果数据加载时间过长 可以考虑放到别的位置！或者增加异步线程
//        LoadAppData();

    }

    /**
     * 如果需要根据客户端类型 初始化服务的操作,写在这个方法里
     */
    private void initAPP() {
        Log.i(TAG, "============根据客户端类型 初始化服务================");
        //启动鹰眼服务 在司机端
        if (AppParams.DRIVER_APP) {
            LBSTraceSDK.getInstance().initApplication(this);
        }
    }

    Properties properties;
    /**
     * 升级检测
     */
    public void checkAppVersion() {
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
//                new Handler(Looper.getMainLooper(), new Handler.Callback() {
//                    @Override
//                    public boolean handleMessage(Message msg) {
////                        checkAppVersionProperties(properties);
//                        return true;
//                    }
//                }).sendEmptyMessage(0);

            }
        });
    }

    public void checkAppVersionProperties(Activity context) {
        Log.i(TAG, "====check App Version Properties =========");
        if (properties == null) {
            Log.i(TAG, " Properties = null");
            return;
        }
        if (getPackageName().equals(properties.getProperty("package"))) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                int versionCode = Integer.parseInt(properties.getProperty("versionCode", "0"));
                if (packageInfo.versionCode < versionCode) {
                    String versionName = properties.getProperty("versionName");
                    final String downloadUrl = properties.getProperty("url");
                    String message = "升级内容:\n" + properties.getProperty("message");
                    Log.i(TAG, "有新的版本 versionName=" + versionName);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("请升级APP至版本" + versionName)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    downLoadApk(downloadUrl);
                                    dialog.cancel();
                                }
                            }).setNegativeButton("跳过", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();


                } else {
                    Log.i(TAG, "没有新版本!");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.w(TAG, e);
            }
        } else {
            Log.i(TAG, getPackageName() + " != " + properties.getProperty("package") + " !!! ");
        }
    }

    /*
     * 从服务器中下载APK
	 */
    protected void downLoadApk(String url) {
        //使用系统下载类
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        startActivity(intent);
    }

    public String getVersionName() {
        if (versionName == null) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
        return versionName;
    }

    public void loadAppParams() {
        try {
            ApplicationInfo appInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            String type = appInfo.metaData.getString("APP_TYPE");
            String typeStr = getString(R.string.app_type);
            Log.w(TAG, "=====>读取客户端类型" + type + " string-type=" + typeStr);
            AppParams.DRIVER_APP = type.equals("driver");
//            System.out.println("myMsg:" + msg);

            String host = appInfo.metaData.getString("APP_Host");
            Log.i(TAG, "=====>请求地址：" + host);
            AppParams.APP_HOST = host;

            // 将配置信息保存到本地
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(APP_TYPE, type);
            editor.putString(APP_HOST, host);
            editor.commit();
            Log.i(TAG, "==============保存配置===================");

            initAPP();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadAppParams() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String type = preferences.getString(APP_TYPE, " ");
        AppParams.DRIVER_APP = type.equals("driver");
        AppParams.APP_HOST = preferences.getString(APP_HOST, AppParams.APP_HOST);
        Log.i(TAG, "==============重新读取配置（type=" + type + "  APP_HOST=" + AppParams.APP_HOST + "）===================");
        initAPP();
    }


    /**
     * 设置推送标签
     * alias 用户注册的手机号
     * setTag 车队id，司机，企业，信息部/物流公司
     */
    public void setAliasAndTag() {
        //上传推送标签
        String alias = User.getInstance().getPhoneNum();
        Set<String> set = new LinkedHashSet<String>();
//        switch (User.getInstance().getUserType()) {
//            case CompanyInformationType:
//                set.add("信息部");
//                break;
//            case EnterpriseType:
//                set.add("企业");
//                break;
//            case DriverType:
//                set.add("司机");
//                break;
//        }
//        List<JsonMotorcades> motorcades = User.getInstance().getMotorcades();
        if (User.getInstance().getMotorcades() != null)
            for (JsonMotorcades motorcades : User.getInstance().getMotorcades()) {
                set.add("车队_" + motorcades.getId());
            }
        JPushInterface.setAliasAndTags(getApplicationContext(), alias, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.i(TAG, "JPush 标签设置结果：状态码：" + i + " 别名：" + s + " 标签：" + set.toString());
            }
        });
    }

    /**
     * 加载APP数据
     */
    public void LoadAppData() {
        ////        TODO 数据读取 未完成  测试位置更改 点击立即体验后加载
        Object obj = loadFile(User.class.getSimpleName());
        if (obj != null) {
            Log.i(TAG, "读取文件：before User=" + User.getInstance());
            User.getInstance().loadUser((User) obj);
            Log.i(TAG, "读取文件：User=" + User.getInstance());
        }
        //触发城市列表获取
        LocationView.getInstance();

    }


    public void stopLocationServer() {
        BaiduSDK.getInstance().stopLocationServer();
    }


    public void Exit(Activity activity, boolean isSave) {

        //语音服务退出
        BaiduSDK.getInstance().dismissVoiceDialog();

        //save
        if (!isSave) {
            //删除前 先保留用户名 等 非重要信息
            User.getInstance().resetUser();
//            deleteUser();

            //定位服务推出
            BaiduSDK.getInstance().stopLocationServer();

            //停止定位服务
//            stopService(new Intent(this, UpDataLocationService.class));
        }
        saveUser();
        if (activity != null)
            activity.finish();
        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void deleteUser() {
        deleteFile(User.class.getSimpleName());
    }

    public void saveUser() {
        saveFile(User.class.getSimpleName(), User.getInstance());
    }

    public void delFile(String fileName) {
        Log.i(TAG, "删除文件：文件名=" + fileName);
        getBaseContext().deleteFile(fileName);
    }

    public boolean saveFile(String fileName, Object content) {
        try {
            Log.i(TAG, "保存内容：文件名=" + fileName + ";内容=" + content.toString());
            FileOutputStream outStream = getBaseContext().openFileOutput(fileName, Context.MODE_WORLD_READABLE);

//            outStream.write(content.getBytes());
            ObjectOutputStream oos = new ObjectOutputStream(outStream);
            oos.writeObject(content);
            oos.flush();
            oos.close();
            outStream.close();


        } catch (FileNotFoundException e) {
            Log.e(TAG, "保存数据-文件错误", e);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e(TAG, "保存数据", e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Object loadFile(String fileName) {
        Log.i(TAG, "读取文件：文件名=" + fileName + " dir=" + this.getFilesDir());
        Object string = null;
        try {
            FileInputStream inStream = this.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(inStream);
            string = ois.readObject();
            ois.close();
            inStream.close();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "读取数据- 文件找不到", e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "读取数据", e);
            e.printStackTrace();
        }
        Log.i(TAG, "读取文件：内容=" + string);
        return string;
    }


    /**
     * 显示 设置日期 对话框
     *
     * @param listener
     */
    public final void showDataPickerDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(getApplicationContext(),
                // 绑定监听器
                listener
                // 设置初始日期
                , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                .get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 显示 设置时间 对话框
     *
     * @param callback
     */
    public final void showTimeDialog(TimePickerDialog.OnTimeSetListener callback) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(getApplicationContext(), callback, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }


}
