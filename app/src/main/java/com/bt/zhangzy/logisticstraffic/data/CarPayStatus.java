package com.bt.zhangzy.logisticstraffic.data;

/**
 * 车辆的付费状态
 * Created by ZhangZy on 2016-9-28.
 */
public enum CarPayStatus {
    NonPayment,//未付款,
    Payment,//支付中
    PaymentReceived,//已付款
    Overdue,//已过期
    Empty;

    public static CarPayStatus Parse(int status) {
        return status > -1 && status < values().length ? values()[status] : Empty;
    }
}
