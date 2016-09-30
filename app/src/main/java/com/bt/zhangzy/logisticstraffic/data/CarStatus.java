package com.bt.zhangzy.logisticstraffic.data;

/**
 * 车辆的审核状态
 * Created by ZhangZy on 2016-9-28.
 */
public enum CarStatus {
    UN_CHECKED,// 未审核
    COMMITED,//已提交资料
    CHECKED,//已审核
    LOCK,//
    DELETE,//
    Empty;

    public static CarStatus Parse(int status) {
        return status > -1 && status < values().length ? values()[status] : Empty;
    }
}
