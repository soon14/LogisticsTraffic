package com.zhangzy.baidusdk;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by ZhangZy on 2015/6/10.
 */
public class BaiduSDK extends BDNotifyListener implements BDLocationListener {

    private static final String TAG = BaiduSDK.class.getSimpleName();
    private static BaiduSDK instance = new BaiduSDK();

    public static BaiduSDK getInstance() {
        return instance;
    }

    private LocationClient client;
    private LocationListener listener;

    public void setLocationListener(LocationListener listener) {
        this.listener = listener;
    }


    /**
     * 定位服务初始化
     */
    public void onCreateLocation(Context context) {
        Log.i(TAG, "百度地图定位 --初始化");
        client = new LocationClient(context.getApplicationContext());

        client.registerNotify(this);


        LocationClientOption option = new LocationClientOption();
        //Hight_Accuracy高精度、Battery_Saving低功耗、Device_Sensors仅设备(GPS)
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
//        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(60000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
//        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        client.setLocOption(option);
//        client.start();
//        client.stop();
//        client.requestLocationServer();
    }

    public void stopLocationServer() {
        stopLocationServer(this);
    }

    public void stopLocationServer(BDLocationListener ls) {
        Log.i(TAG, "百度地图定位 --停止定位请求服务");
        if (client != null) {
            client.stop();
            client.unRegisterLocationListener(ls);
            client.removeNotifyEvent(this);
            client = null;
        }
    }

    public void requestLocationServer(Context context) {
        requestLocationServer(context, this);
    }

    /**
     * 定位请求
     */
    public void requestLocationServer(Context context, BDLocationListener bdlistener) {

        Log.i(TAG, "百度地图定位 --开启定位服务");
        if (client == null) {
            onCreateLocation(context);
        }

        client.registerLocationListener(bdlistener);
        if (!client.isStarted()) {
            client.start();
        }
        int code = client.requestLocation();
        /*0：正常发起了定位。
        1：服务没有启动。
        2：没有监听函数。
        6：请求间隔过短。 前后两次请求定位时间间隔不能小于1000ms。*/
        switch (code) {
            case 0:
                break;
            case 1:
                Log.i(TAG, "百度地图定位 --服务没有启动");
                client.start();
                break;
            case 2:
                Log.i(TAG, "百度地图定位 --没有监听函数");
                break;
            case 6:
                Log.i(TAG, "百度地图定位 --请求间隔过短");
                break;
        }
    }


    /**
     * 1.接收异步返回的定位结果，参数是BDLocation类型参数。
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null)
            return;
        StringBuffer sb = new StringBuffer(256);
        sb.append("定位结果：");
        sb.append(bdLocation.getProvince());//获取省份信息
        sb.append("-");
        sb.append(bdLocation.getCity());//获取城市信息
        sb.append("-");
        sb.append(bdLocation.getDistrict());//获取区县信息
        sb.append("-");
        sb.append(bdLocation.getLatitude() + "," + bdLocation.getLongitude());//

        Log.i(TAG, "百度地图定位 --结果：" + sb.toString());
        stopLocationServer();
        if (listener != null) {
            listener.callbackCityName(bdLocation.getCity(), String.valueOf(bdLocation.getLatitude()), String.valueOf(bdLocation.getLongitude()));
        }
    }

    public interface LocationListener {

        public void callbackCityName(String cityname, String latitude, String longitude);
    }
}
