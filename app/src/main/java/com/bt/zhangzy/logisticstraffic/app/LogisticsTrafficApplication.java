package com.bt.zhangzy.logisticstraffic.app;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.LoginActivity;
import com.bt.zhangzy.logisticstraffic.adapter.LocationListAdapter;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.zhangzy.baidusdk.BaiduSDK;

import java.util.Calendar;

/**
 * Created by ZhangZy on 2015/6/10.
 */
public class LogisticsTrafficApplication extends Application implements BaiduSDK.LocationListener {

    /**测试数据 城市列表*/ {
        //TODO 测试城市列表的JSON数据 未完成
//        JSONArray cityArray = new JSONArray();
//        JSONArray provinceJson = new JSONArray();
//        provinceJson.put()

    }

    private static final String TAG = LogisticsTrafficApplication.class.getSimpleName();
    private PopupWindow popupWindow;
    private ListView listView;
    private BaseActivity currentAct;
    private LocationCallback locationCallback;
    private TextView locationNetworkTx;
    private Location location;//用于缓存已经定位的信息


    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    public void setCurrentAct(BaseActivity act) {
        currentAct = act;
        Log.w(TAG, "设置当前Activity=" + act.TAG);
    }

    public void Exit() {
        //定位服务推出
        BaiduSDK.getInstance().stopLocationServer();
        //语音服务退出
        BaiduSDK.getInstance().dismissVoiceDialog();

        if (currentAct != null)
            currentAct.finish();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public void showLoacaitonList(View view) {
        if (popupWindow == null) {
            creatPopupWindow(currentAct);

        }

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, 0, 0);
            WindowManager.LayoutParams lp = currentAct.getWindow().getAttributes();
            lp.alpha = 0.3f;
            currentAct.getWindow().setAttributes(lp);
        }
    }

    /**
     * 地址选择对话框
     */
    private void creatPopupWindow(Context context) {
        //        Context context = this.getBaseContext();
        View tmp_view = LayoutInflater.from(context).inflate(R.layout.location_popupwindow, null);
        popupWindow = new PopupWindow(tmp_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        Button backBtn = (Button) tmp_view.findViewById(R.id.popupwindow_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = currentAct.getWindow().getAttributes();
                lp.alpha = 1f;
                currentAct.getWindow().setAttributes(lp);
            }
        });

        //用于显示网络定位的结果
        locationNetworkTx = (TextView) tmp_view.findViewById(R.id.location_network_tx);
        if (location != null) {
            locationNetworkTx.setText(location.getCityName());
        }

        listView = (ListView) tmp_view.findViewById(R.id.location_city_list);

        LocationListAdapter adapter = new LocationListAdapter();
        listView.setAdapter(adapter);
        adapter.setItemOnClickCallback(new LocationListAdapter.ItemOnClickCallback() {
            @Override
            public void onClickItem(String string) {
                Log.d(TAG, "点击了：" + string);
//                Toast.makeText(getBaseContext(),"点击了："+
                if (locationCallback != null) {
                    if (location == null) {
                        location = new Location();
                    }
                    location.setCityName(string);
                    locationCallback.chooseLocation(location);
                }
            }
        });

    }

    /**
     * 共用一个请求接口
     */
    public void requestLocation(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
        BaiduSDK.getInstance().setLocationListener(this);
        BaiduSDK.getInstance().requestLocationServer(getApplicationContext());
    }

    @Override
    public void callbackCityName(String cityname, String latitude, String langitude) {
        location = new Location();
        location.setCityName(cityname);
        location.setLatitude(latitude);
        location.setLangitude(langitude);

        if (locationNetworkTx != null) {
            locationNetworkTx.setText(cityname);
        }
        if (locationCallback != null) {
            locationCallback.networkLocation(location);
        }
    }

    /**
     * 登陆后才能打电话
     */
    public void callPhone(String phoneNumber) {
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

    public interface LocationCallback {
        /**
         * 网络定位 回调
         */
        public void networkLocation(Location location);

        /**
         * 用户选择位置 回调
         */
        public void chooseLocation(Location location);
    }
}
