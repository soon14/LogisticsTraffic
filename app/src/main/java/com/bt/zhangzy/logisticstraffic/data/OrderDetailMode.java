package com.bt.zhangzy.logisticstraffic.data;

/**
 * Created by ZhangZy on 2015/7/16.
 */
public enum OrderDetailMode {
    EmptyMode,
//    /**
//     * 司机
//     */
//    DriverMode,
//    /**
//     * 企业
//     */
//    EnterpriseMode,
//    /**
//     * 信息部
//     */
//    InformationMode,

    /**
     * 创建订单  适用范围：企业给信息部下单、 信息部创建订单
     */
    CreateMode,
    /**
     * 未完成订单 填写信息  适用于信息部填写相关信息
     */
    UntreatedMode,
    /**
     * 已提交订单  绑定了司机 适用于信息部查看抢单的司机
     */
    SubmittedMode,
    /**
     * 已完成订单 适用于各个角色查看订单的状态，已经进入了不可修改的状态
     */
    CompletedMode;

    public static OrderDetailMode parse(int ordinal) {
        return ordinal > 0 && ordinal < values().length ? values()[ordinal] : EmptyMode;
    }
}
