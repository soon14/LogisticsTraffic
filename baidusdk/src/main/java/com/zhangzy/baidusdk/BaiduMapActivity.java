package com.zhangzy.baidusdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.bt.zhangzy.baidusdk.R;

/**
 * Created by ZhangZy on 2015/6/29.
 */
public class BaiduMapActivity extends Activity implements BDLocationListener {

    private static final String TAG = BaiduMapActivity.class.getSimpleName();
    MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    boolean isFirstLoc = true;// 是否首次定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidu_map_view);
//        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
//普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

//        BaiduSDK.getInstance().requestLocationServer(this, this);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位回调
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
//        StringBuffer sb = new StringBuffer(256);
//        sb.append("定位结果：");
//        sb.append(bdLocation.getProvince());//获取省份信息
//        sb.append("-");
//        sb.append(bdLocation.getCity());//获取城市信息
//        sb.append("-");
//        sb.append(bdLocation.getDistrict());//获取区县信息
//        sb.append("-");
//        sb.append(bdLocation.getLatitude() + "," + bdLocation.getLongitude());//
//
//        Log.i(TAG, "地图 --结果：" + sb.toString());
        location(bdLocation);
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        mMapView.onDestroy();
        super.onDestroy();
//        BaiduSDK.getInstance().stopLocationServer(this);
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    public void location(BDLocation location) {

        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null)
            return;
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius()) // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }

    }

}
