package com.bt.zhangzy.logisticstraffic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCarTrack;
import com.zhangzy.baidusdk.BaiduSDK;

import java.util.List;

/**
 * todo 后期可能要改成 remote service
 * 位置上传服务 com.bt.zhangzy.logisticstraffic.service.UpDataLocationService
 * Created by ZhangZy on 2016-2-29.
 */
public class UpDataLocationService extends Service implements Handler.Callback, BaiduSDK.LocationListener {

    static final String TAG = UpDataLocationService.class.getSimpleName();
    static final long UPDATA_DELAYED_TIME = 5 * 60 * 1000;//1 * 60 * 60 * 1000; //2个小时上传一次坐标
    Handler handler;
    JsonCarTrack params;
    JsonCallback callback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        handler = new Handler(this);
        //创建线程
//        HandlerThread handlerThread = new HandlerThread(getPackageName() + TAG);
//        handlerThread.start();
//        handler = new Handler(handlerThread.getLooper());
        BaiduSDK.getInstance().setLocationListener(this);
        BaiduSDK.getInstance().requestLocationServer(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        initLocationData();
        handler.sendEmptyMessage(0);

        return super.onStartCommand(intent, flags, startId);
    }


    private void initLocationData() {
        User user = User.getInstance();
        params = new JsonCarTrack();
        params.setDriverId(user.getDriverID());
        if (user.getJsonCar() != null)
            params.setCarId(user.getJsonCar().getId());
//        params.setOrderId();
        if (user.getLocation() != null) {
            params.setLongitude(Double.valueOf(user.getLocation().getLongitude()));
            params.setLatitude(Double.valueOf(user.getLocation().getLatitude()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaiduSDK.getInstance().stopLocationServer();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "handleMessage() 处理位置上传信息");

        requestUpdataLocation();
        if (msg.what == 0) {

        } else {
            BaiduSDK.getInstance().requestLocationServer(getApplicationContext());
        }
        handler.sendEmptyMessageDelayed(1, UPDATA_DELAYED_TIME);
        return false;
    }

    private void requestUpdataLocation() {
        if (params == null)
            return;
        if (callback == null)
            callback = new JsonCallback() {
                @Override
                public void onSuccess(String msg, String result) {

                }

                @Override
                public void onFailed(String str) {

                }
            };
        Log.i(TAG, "requestUpdataLocation() ");
        //更新用户信息
        User user = User.getInstance();
        params.setDriverId(user.getDriverID());
        if (user.getJsonCar() != null) {
            params.setCarId(user.getJsonCar().getId());
        }
        //获取订单列表
        List<Integer> orderId_list = user.getOrderIdList();
        if (orderId_list != null && !orderId_list.isEmpty()) {
            for (int orderId : orderId_list) {
                params.setOrderId(orderId);
                Log.i(TAG, "requestUpdataLocation() -- 发送请求 orderId=" + orderId);
                HttpHelper.getInstance().post(AppURL.PostUploadLocation, params, callback);
            }
        }


    }

    @Override
    public void callbackCityName(String province, String cityname, String latitude, String longitude) {
        Log.i(TAG, "callbackCityName(" + province + "-" + cityname + "-" + latitude + "-" + longitude + ")");
        if (params != null) {
            params.setLongitude(Double.valueOf(longitude));
            params.setLatitude(Double.valueOf(latitude));
        }
    }
}
