package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * 运输中订单返回 json模型
 * Created by ZhangZy on 2016-9-29.
 */
public class ResponseFindByDriver extends BaseEntity {
    JsonCar car;
    JsonOrder order;

    public JsonCar getCar() {
        return car;
    }

    public void setCar(JsonCar car) {
        this.car = car;
    }

    public JsonOrder getOrder() {
        return order;
    }

    public void setOrder(JsonOrder order) {
        this.order = order;
    }
}
