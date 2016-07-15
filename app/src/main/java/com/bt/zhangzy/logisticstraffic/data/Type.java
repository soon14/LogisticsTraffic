package com.bt.zhangzy.logisticstraffic.data;

/**
 * 用户类型
 * Created by ZhangZy on 2015/7/9.
 */
public enum Type {

    EmptyType,
    /**
     * 司机
     */
    DriverType,
    /**
     * 企业
     */
    EnterpriseType,
    /**
     * 信息部
     */
    CompanyInformationType;

    public Type parseRole(int role) {
        return role < 0 || role >= Type.values().length ? EmptyType : Type.values()[role];
    }

    /**
     * 转化为服务器所需要的 int值
     *
     * @return
     */
    public int toRole() {
        return this.ordinal();
    }
}
