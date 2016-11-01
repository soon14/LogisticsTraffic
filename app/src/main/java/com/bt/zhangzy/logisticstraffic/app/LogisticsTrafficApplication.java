package com.bt.zhangzy.logisticstraffic.app;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.zhangzy.baidusdk.BaiduSDK;
import com.zhangzy.baidusdk.LBSTraceSDK;
import com.zhangzy.base.http.ImageHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.LinkedHashSet;
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
        JPushInterface.setDebugMode(AppParams.DEBUG);
        JPushInterface.init(getApplicationContext());


        AppParams.getInstance().init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());


        //初始化Image loader
        ImageHelper.getInstance().init(this);

        //Bugly 初始化；
        BuglySDK.getInstance().init(this,false);

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

            AppParams.APP_LVJ = appInfo.metaData.getBoolean("APP_LVJ");

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
