package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-2-25.
 */
public class RequestSourceCarSearch extends BaseEntity {
    /*
    汽车类型	type	否
    车长	length	否
    载重	capacity	否
    车辆常住地	usualResidence	否
    pageNum	pageNum	否
    pageSize	pageSize	否
    */
    String type, length, capacity, usualResidence;
    int pageNum, pageSize;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getUsualResidence() {
        return usualResidence;
    }

    public void setUsualResidence(String usualResidence) {
        this.usualResidence = usualResidence;
    }

}
