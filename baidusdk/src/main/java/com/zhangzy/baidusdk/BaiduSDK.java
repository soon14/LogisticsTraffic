package com.zhangzy.baidusdk;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ZhangZy on 2015/6/10.
 */
public class BaiduSDK extends BDNotifyListener implements BDLocationListener {

    private static final String TAG = BaiduSDK.class.getSimpleName();
    private static final String API_KEY = "GmKDnhAYEHtZT0yYmELMGFIP";
    private static final String SECRET_KEY = "C7MzUrlLzvBwuodv96epCYHyL8zKEjiu";
    private static BaiduSDK instance = new BaiduSDK();
    private BaiduASRDigitalDialog baiduASRDigitalDialog;
    private DialogRecognitionListener mRecognitionListener;
    private VoiceListener voiceListener;

    public static BaiduSDK getInstance() {
        return instance;
    }

    private LocationClient client;
    private LocationListener listener;

    public void showVoiceDialog(Context context) {
        Bundle params = new Bundle();
//设置开放平台 API Key
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);
//设置开放平台 Secret Key
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);
//设置识别领域：搜索、输入、地图、音乐……，可选。默认为输入。
        params.putInt(BaiduASRDigitalDialog.PARAM_PROP, VoiceRecognitionConfig.PROP_INPUT);
//设置语种类型：中文普通话，中文粤语，英文，可选。默认为中文普通话
//        params.putString(BaiduASRDigitalDialog.PARAM_LANGUAGE, VoiceRecognitionConfig.LANGUAGE_CHINESE);
//如果需要语义解析，设置下方参数。领域为输入不支持
//        params.putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE, true);
// 设置对话框主题，可选。BaiduASRDigitalDialog 提供了蓝、暗、红、绿、橙四中颜色，每种颜色又分亮、暗两种色调。
// 共 8 种主题，开发者可以按需选择，取值参考 BaiduASRDigitalDialog 中前缀为 THEME_的常量。默认为亮蓝色
        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);
        baiduASRDigitalDialog = new BaiduASRDigitalDialog(context, params);
        baiduASRDigitalDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, true);//播放开始音
        baiduASRDigitalDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, true);//结束音
        baiduASRDigitalDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, true);
        //设置回调
        if (mRecognitionListener == null) {
            mRecognitionListener = new DialogRecognitionListener() {
                @Override
                public void onResults(Bundle results) {
//在 Results 中获取 Key 为 DialogRecognitionListener .RESULTS_RECOGNITION 的
//                StringArrayList，可能为空。获取到识别结果后执行相应的业务逻辑即可，此回调会在主线程调用。
                    ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION) : null;
                    if (rs != null && !rs.isEmpty()) {
                        //此处处理识别结果，识别结果可能有多个，按置信度从高到低排列，第一个元素是置信度最高的结果。
                        Log.d(TAG, "语音输入结果：" + Arrays.toString(rs.toArray(new String[0])));
                        if (voiceListener != null) {
                            voiceListener.callbackVoice(rs.get(0).replace("。", ""));
                        }
                    }
                }
            };
        }
        baiduASRDigitalDialog.setDialogRecognitionListener(mRecognitionListener);
        baiduASRDigitalDialog.show();
    }

    public void dismissVoiceDialog() {
        if (baiduASRDigitalDialog != null) {
            baiduASRDigitalDialog.dismiss();
        }
    }

    public void setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }

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
//        stopLocationServer();
        if (listener != null) {
            listener.callbackCityName(bdLocation.getProvince(), bdLocation.getCity(), String.valueOf(bdLocation.getLatitude()), String.valueOf(bdLocation.getLongitude()));
        }
    }

    public interface LocationListener {

        public void callbackCityName(String province, String cityname, String latitude, String longitude);
    }

    public interface VoiceListener {
        void callbackVoice(String string);
    }
}
