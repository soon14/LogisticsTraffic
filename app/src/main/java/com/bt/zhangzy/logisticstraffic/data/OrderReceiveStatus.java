package com.bt.zhangzy.logisticstraffic.data;

/**
 * OrderHistory 接单状态
 * <p/>
 * 名称	属性值	说明
 * 未接单	0
 * 已接单	1
 * 已拒绝	2
 * 已同意接单	3	物流公司同意司机接单
 * 司机已经装货	4
 * 企业确认装货完成	5
 * 确认收货	6
 * Created by ZhangZy on 2016-3-31.
 */
public enum OrderReceiveStatus {
    NotReceive,
    Receive,
    Reject,
    Accept,//已同意接单	3	物流公司同意司机接单
    Loading,
    LoadingFinish,
    Finish;

    //解析从服务器返回的状态码
    public static OrderReceiveStatus parseStatus(int status) {
        return status >= 0 && status < values().length ? values()[status] : NotReceive;
    }

}
