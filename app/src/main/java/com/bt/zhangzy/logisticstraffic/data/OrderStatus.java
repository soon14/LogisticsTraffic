package com.bt.zhangzy.logisticstraffic.data;

/**
 * 订单状态
 * 无效订单	0
 * 临时订单（企业给信息部订单意向）	1
 * 未提交订单（已有订单的具体信息）<临时订单和未提交订单都属于未确认订单>	2
 * 交易中订单（已绑定了司机 并开始配送）	3
 * 已完成订单（收货人已确认收货）	4
 * 已作废订单	5
 * Created by ZhangZy on 2016-1-29.
 */
public enum OrderStatus {

    Empty,
    TempOrder,
    UncommittedOrder,
    CommitOrder,
    FinishedOrder,
    DiscardOrder;

    //解析从服务器返回的状态码
    public static OrderStatus parseStatus(int status) {
        return status >= 0 && status < values().length ? values()[status] : Empty;
    }
}
