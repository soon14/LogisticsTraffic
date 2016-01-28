package com.bt.zhangzy.logisticstraffic.app;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.LoginActivity;
import com.bt.zhangzy.logisticstraffic.adapter.LocationListAdapter;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.ImageHelper;
import com.bt.zhangzy.tools.ContextTools;
import com.zhangzy.baidusdk.BaiduSDK;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZhangZy on 2015/6/10.
 */
public class LogisticsTrafficApplication extends Application  {

    private static final String TAG = LogisticsTrafficApplication.class.getSimpleName();


    private BaseActivity currentAct;

//    private JSONArray jsonCityList;


    @Override
    public void onCreate() {
        super.onCreate();
        //JPush 推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());


        AppParams.getInstance().init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());


        //初始化Image loader
        ImageHelper.getInstance().init(this);

        //先放在这里，后期如果数据加载时间过长 可以考虑放到别的位置！或者增加异步线程
        LoadAppData();

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


    public void setCurrentAct(BaseActivity act) {
        currentAct = act;
        Log.w(TAG, "设置当前Activity=" + act.TAG);
    }



    public void Exit(boolean isSave) {
        //定位服务推出
        BaiduSDK.getInstance().stopLocationServer();
        //语音服务退出
        BaiduSDK.getInstance().dismissVoiceDialog();

        //save
        if (isSave) {
            saveUser();
        } else {
            deleteUser();
        }
        if (currentAct != null)
            currentAct.finish();
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
            Log.e(TAG, "读取数据- 文件找不到", e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "读取数据", e);
            e.printStackTrace();
        }
        Log.i(TAG, "读取文件：内容=" + string);
        return string;
    }








    /**
     * 登陆后才能打电话
     */
    public void callPhone(String phoneNumber) {
        //todo 接口 更新拨打电话的次数
        if (!User.getInstance().getLogin()) {
            BaseDialog.showConfirmDialog(currentAct, "您还没有登陆，是否登陆？", "返回", "登陆", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentAct.startActivity(LoginActivity.class);
                }
            });

        } else {
            ContextTools.CallPhone(currentAct, phoneNumber);
        }
    }


    /**
     * 显示 设置日期 对话框
     *
     * @param listener
     */
    public final void showDataPickerDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(currentAct,
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
        new TimePickerDialog(currentAct, callback, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }


}
