package com.bt.zhangzy.logisticstraffic.data;

/**
 * Created by ZhangZy on 2016-3-11.
 */
public enum PayStatus {
    Empty,
    NonPayment,//未付款
    PaymentReceived,//已付款
    Overdue;//已过期

    public static PayStatus Parse(int status) {
        return status > -1 && status < values().length ? values()[status] : Empty;
    }
}
