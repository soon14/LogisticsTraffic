package com.bt.zhangzy.logisticstraffic.data;

/**
 * 联系人数据封装
 * Created by ZhangZy on 2015/7/10.
 */
public class People {
    private String name;
    private String phoneNumber;


    public String getName() {
        return name;
    }

    public People setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public People setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
