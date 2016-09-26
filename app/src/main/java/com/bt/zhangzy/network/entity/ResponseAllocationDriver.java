package com.bt.zhangzy.network.entity;

import java.util.List;

/**
 * 司机的数据模型封装
 * 包括车辆信息（列表形式）
 * Created by ZhangZy on 2016-3-21.
 */
public class ResponseAllocationDriver extends JsonOrderHistory {

    JsonDriver driver;
    List<JsonCar> cars;

    public JsonDriver getDriver() {
        return driver;
    }

    public void setDriver(JsonDriver driver) {
        this.driver = driver;
    }

    public List<JsonCar> getCars() {
        return cars;
    }

    public void setCars(List<JsonCar> cars) {
        this.cars = cars;
    }
}
