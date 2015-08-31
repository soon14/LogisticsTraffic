package com.bt.zhangzy.logisticstraffic.app;

/**
 * 定义一些常量
 * Created by ZhangZy on 2015/7/14.
 */
public final class Constant {
    /* 标记是否为司机端 */
    public static final boolean DEVICES_APP = true;

    /*=============activity跳转请求常量 =================*/
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

    /**
     * 跳转到通讯录时的请求码
     */
    public static final int REQUEST_CODE_CONTACT = 0x010;//跳转到通讯录时的请求码
    public static final String REQUEST_BUNDLE_CONTACT_KEY = "REQUEST_BUNDLE_CONTACT_KEY";

    /**
     * 跳转到订单详情页 请求码
     * */
    public static final String ORDER_DETAIL_KEY_TYPE = "ORDER_DETAIL_KEY_TYPE";

    /**
     * product 在 bundle中的key值
     */
    public static final String BUNDLE_PRODUCT_KEY = "BUNDLE_PRODUCT_KEY";



}
