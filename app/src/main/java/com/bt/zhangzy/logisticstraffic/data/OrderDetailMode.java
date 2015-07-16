package com.bt.zhangzy.logisticstraffic.data;

/**
 * Created by ZhangZy on 2015/7/16.
 */
public enum OrderDetailMode {
    EmptyMode,
    /**
     * 司机
     */
    DriverMode,
    /**
     * 企业
     */
    EnterpriseMode,
    /**
     * 信息部
     */
    InformationMode,
    /**
     * 未完成订单
     */
    UntreatedMode,
    /**
     * 已提交订单
     */
    SubmittedMode,
    /**
     * 已完成订单
     */
    CompletedMode
}
