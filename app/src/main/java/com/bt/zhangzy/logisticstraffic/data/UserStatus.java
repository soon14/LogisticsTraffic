package com.bt.zhangzy.logisticstraffic.data;

/**
 * 用户状态
 * 名称	属性值
 * 未审核	-1
 * 已审核	0
 * 冻结	1
 * 删除	2
 * 已付费	3
 * 已提交资料
 * Created by ZhangZy on 2016-4-5.
 */
public enum UserStatus {
    UN_CHECKED,// = -1;
    CHECKED,//= 0;
    LOCK,//= 1;
    DELETE,// = 2;
    PAID,//= 3;
    COMMITED// = 4; //已提交资料
    ;

    public static UserStatus parse(int status) {
        return status > -1 && status < values().length ? values()[status] : UN_CHECKED;
    }

    public int toStatus() {
        return this.ordinal();
    }

}
