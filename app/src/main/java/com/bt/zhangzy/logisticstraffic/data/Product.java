package com.bt.zhangzy.logisticstraffic.data;

/**
 * 展示的信息数据类型封装
 * Created by ZhangZy on 2015/7/9.
 */
public class Product {
    private Type type = Type.EmptyType;
    private String name ;
    private String phoneNumber;
    private String describe;


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
