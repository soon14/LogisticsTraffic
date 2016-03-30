package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class RequestAddCar extends BaseEntity {

    public RequestAddCar() {
    }

    JsonDriver driver;
    JsonCar car;

    public JsonDriver getDriver() {
        return driver;
    }

    public void setDriver(JsonDriver driver) {
        this.driver = driver;
    }

    public JsonCar getCar() {
        return car;
    }

    public void setCar(JsonCar car) {
        this.car = car;
    }
}
