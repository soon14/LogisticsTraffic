package com.bt.zhangzy.logisticstraffic.app;

import android.content.Context;

import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.tools.Tools;

/**
 * app的常用配置 和 一些常用功能 单例
 * Created by ZhangZy on 2016-1-20.
 */
public final class AppParams {



    static AppParams instance = new AppParams();

    private AppParams() {
    }

    public static AppParams getInstance() {
        return instance;
    }

    private String crashLogDir;
    private String cacheImageDir;
    private String paramsDir;
    public void init(Context ctx) {
        String path = ContextTools.getCacheDir(ctx);
        cacheImageDir = path+"/Image/";
        crashLogDir = path + "/Crash/";
        paramsDir = path + "/params/";
    }

    public String getCrashLogDir() {
        return crashLogDir;
    }

    public String getCacheImageDir() {
        return cacheImageDir;
    }

    public String getParamsDir() {
        return paramsDir;
    }

    /* 标记是否为司机端 */
    public static boolean DEVICES_APP = false;

    /*=============activity跳转请求常量 =================*/
    /*  web页面 的页面名称 */
    public static final String WEB_PAGE_NAME = "WEB_PAGE_NAME";
    public static final String WEB_PAGE_URL = "WEB_PAGE_URL";//加载的地址

    /**
     * 注册页跳转请求常量
     */
    public static final String BUNDLE_PAGE_KEY = "BUNDLE_PAGE_KEY";
    public static final String PAGE_HOME = "HomeFragment";
    public static final String PAGE_USER = "UserFragment";
    //    public static final String PAGE_HAPPY = "HappyFragment";
    public static final String PAGE_SERVICES = "ServicesFragment";

    public static final String RESULT_CODE_KEY = "RESULT_CODE_KEY";
    /**
     * 订单页面选择车队司机时的请求码
     */
    public static final int RESULT_CODE_SELECT_DEVICES = 0x001;//订单页面选择车队司机时的请求码
    public static final String SELECT_DEVICES_SIZE_KEY = "SELECT_DEVICES_SIZE_KEY";//订单页面选择车队司机时的司机数量


    /**
     * 跳转到通讯录时的请求码
     */
    public static final int REQUEST_CODE_CONTACT = 0x010;//跳转到通讯录时的请求码
    public static final String REQUEST_BUNDLE_CONTACT_KEY = "REQUEST_BUNDLE_CONTACT_KEY";

    /**
     * 跳转到订单详情页 请求码
     * */
    public static final String ORDER_DETAIL_KEY_TYPE = "ORDER_DETAIL_KEY_TYPE";
    public static final String ORDER_DETAIL_KEY_ORDER = "ORDER_DETAIL_KEY_ORDER";//跳转时传的 订单对象

    /**
     * product 在 bundle中的key值
     */
    public static final String BUNDLE_PRODUCT_KEY = "BUNDLE_PRODUCT_KEY";

    /**
     * 车队列表 跳转到 车队详情页 所需要传递的 车队ID
     */
    public static final String BUNDLE_MOTOCARDE_ID = "BUNDLE_MOTOCARDE_ID";
}
