package com.bt.zhangzy.logisticstraffic.data;

/**
 * Created by ZhangZy on 2016-9-28.
 */
public enum CarRunStatus {
    Leisure,//空闲
    Busy,
    OffStream,//停运
    Empty;

    public static CarRunStatus Parse(int status) {
        return status > -1 && status < values().length ? values()[status] : Empty;
    }
}
