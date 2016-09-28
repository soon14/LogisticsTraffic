package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-9-26.
 */
public class RequestBindCarDriver extends BaseEntity {
    int carId;
    int driverId;

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
