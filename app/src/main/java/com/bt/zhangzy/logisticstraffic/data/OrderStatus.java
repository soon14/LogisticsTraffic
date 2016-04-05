package com.bt.zhangzy.logisticstraffic.data;

/**
 * 订单状态
 无效订单	0
 临时订单（企业给信息部订单意向）	1
 未提交订单（已有订单的具体信息）<临时订单和未提交订单都属于未确认订单>	2
 订单分配中（物流 -> 司机）	3
 交易中订单（已绑定了司机 并开始配送）	4
 司机已经装货	5
 企业已经确认装货	6
 已完成订单（收货人已确认收货）	7
 已作废订单	8
 * Created by ZhangZy on 2016-1-29.
 */
public enum OrderStatus {

    Empty,
    TempOrder,//临时订单  提交订单
    UncommittedOrder,//未提交订单  发布货源
    AllocationOrder,//订单分配中  开始交易
    TradeOrder,//交易中订单
    LoadingOrder,//司机已经装货	5
    LoadingFinishOrder,//企业已经确认装货	6
    FinishedOrder,//已完成订单
    DiscardOrder;//已作废订单

    //解析从服务器返回的状态码
    public static OrderStatus parseStatus(int status) {
        return status >= 0 && status < values().length ? values()[status] : Empty;
    }
}
