package com.zhangzy.baidusdk;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度鹰眼服务
 * 相关操作封装
 * 主要用途，位置采集 和 上传；
 * 调用流程  先init 然后 addEntity  最后 startTrace
 * <p/>
 * Created by ZhangZy on 2016-7-8.
 */
public class LBSTraceSDK {

    private static final String TAG = LBSTraceSDK.class.getSimpleName();
    static LBSTraceSDK instance = new LBSTraceSDK();

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    protected static long serviceId = 119300; // serviceId为开发者创建的鹰眼服务ID

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private final int traceType = 2;

    Context context;
    /*
  * 概念：
   采集周期：可理解为定位周期，多久定位一次
  回传周期：鹰眼为节省电量和流量，并不是定位一次就回传一次数据，而是隔段时间将一批定位数据打包压缩回传。*/
    //位置采集周期
    final int gatherInterval = 30;//30秒
    //打包周期
    final int packInterval = 2 * 60;//秒
    /**
     * 轨迹服务客户端
     */
    protected LBSTraceClient client = null;
    Trace trace;
    String entityName;

    /**
     * 开启轨迹服务监听器
     */
    protected OnStartTraceListener startTraceListener = null;

    /**
     * 停止轨迹服务监听器
     */
    protected OnStopTraceListener stopTraceListener = null;

    /**
     * Entity监听器
     */
    protected OnEntityListener entityListener = null;
    /**
     * Track监听器
     */
    protected OnTrackListener trackListener = null;

    //自定义字段 订单列表
    Map<String, String> map;

    //唤醒任务
    HandlerThread handlerThread;
    Handler handler;
    long awakenTime = 60 * 1000;//检查是否唤醒 的 时间间隔
    boolean isAwakenOn = false;

    private LBSTraceSDK() {
    }

    public static LBSTraceSDK getInstance() {
        return instance;
    }

    public void setOrderIds(List<Integer> orderId_list) {
//        this.orderIds_str = orderIds_str;
        if (orderId_list != null && !orderId_list.isEmpty()) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int orderId : orderId_list) {
                stringBuffer.append(orderId).append(',');
            }
            stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
            Log.i(TAG, "orderIds = " + stringBuffer.toString());
            if (map == null)
                map = new HashMap<String, String>();
            else
                map.clear();
            map.put("orderId", stringBuffer.toString());
        }
    }

    public void init(Context context) {
        this.context = context;
        initApplication();

//        SDKInitializer.initialize(context);

        client = new LBSTraceClient(context);
        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);

        //设置位置采集和打包周期
        client.setInterval(gatherInterval, packInterval);

        startTraceListener = new OnStartTraceListener() {
            @Override
            public void onTraceCallback(int errorNo, String message) {
                /*errorNo - 错误编码 0：success， 10000：开启服务请求发送失败， 10001：开启服务失败， 10002：参数错误， 10003：网络连接失败，
                10004：网络未开启， 10005：服务正在开启， 10006：服务已开启， 10007：服务正在停止， 10008：开启缓存， 10009：已开启缓存*/
                Log.i(TAG, "开启轨迹服务回调接口消息 [消息编码 : " + errorNo + "，消息内容 : " + message + "]");
                if (errorNo == 0 || errorNo == 10006 || errorNo == 10008) {
                    isAwakenOn = true;
                } else {
                    isAwakenOn = false;
                }
            }

            @Override
            public void onTracePushCallback(byte messageType, String message) {
                Log.i(TAG, "onTracePushCallback(" + messageType + "," + message + ")");
            }
        };

        // 初始化stopTraceListener
        stopTraceListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                // TODO Auto-generated method stub
                Log.i(TAG, "停止轨迹服务成功");
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                Log.w(TAG, "停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
            }
        };

        initOnEntityListener();
        initOnTrackListener();
    }

    public void initApplication() {
        //        唤醒任务初始化
        handlerThread = new HandlerThread("LogisticsTraffic_LBSTrace");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Log.i(TAG, "Handler Thread Awaken :" + isAwakenOn);
                if (isAwakenOn) {

                } else {

                }
                startTrace();
                handler.sendEmptyMessageDelayed(1, awakenTime);
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(1, awakenTime);
    }

    /**
     * 初始化OnEntityListener
     */
    private void initOnEntityListener() {
        entityListener = new OnEntityListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Log.w(TAG, "entity请求失败回调接口消息 : " + arg0);
                Looper.loop();
            }

            // 添加entity回调接口
            @Override
            public void onAddEntityCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Log.i(TAG, "添加entity回调接口消息 : " + arg0);
                Looper.loop();
            }

            // 查询entity列表回调接口
            @Override
            public void onQueryEntityListCallback(String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onReceiveLocation(TraceLocation location) {
                Log.i(TAG, location.toString());
            }

        };
    }

    /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Log.w(TAG, "track请求失败回调接口消息 : " + arg0);
                Looper.loop();
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
//                mTrackQueryFragment.showHistoryTrack(arg0);
            }

            //轨迹属性回调接口
            @Override
            public Map<String, String> onTrackAttrCallback() {
                Log.i(TAG, "onTrackAttrCallback ->>" + map.toString());
                return map;
            }
        };

        client.setOnTrackListener(trackListener);
    }

    /**
     * 添加entity
     *
     * @param entityName
     * @param columnKey
     */
    public void addEntity(String entityName, String columnKey) {
        this.entityName = entityName;

        // entity标识
//        String entityName = MainActivity.entityName;
        // 属性名称（格式 : "key1=value1,columnKey2=columnValue2......."）
//        String columnKey = "";
        client.addEntity(serviceId, entityName, columnKey, entityListener);
    }

    /**
     * 开启轨迹服务
     */
    public boolean startTrace() {
        Log.i(TAG, "startTrace:" + entityName);
        if (entityName == null)
            return false;
        // 初始化轨迹服务
        trace = new Trace(context, serviceId, entityName, traceType);

        // 通过轨迹服务客户端client开启轨迹服务
        if (client != null)
            client.startTrace(trace, startTraceListener);
        return true;
    }

    /**
     * 停止轨迹服务
     */
    public void stopTrace() {
        if (client == null || trace == null)
            return;
        // 通过轨迹服务客户端client停止轨迹服务
        client.stopTrace(trace, stopTraceListener);
    }

    /**
     * 查询实时轨迹
     */
    private void queryRealtimeTrack() {
        client.queryRealtimeLoc(serviceId, entityListener);
    }
}
