package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.logisticstraffic.data.People;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangZy on 2016-2-16.
 */
public class RequestCallDriver extends BaseEntity {
    public RequestCallDriver() {
    }

    int orderId;
    int motorcadeId;
    List<JsonMotocardesDriver> drivers;

    public int getMotorcadeId() {
        return motorcadeId;
    }

    public void setMotorcadeId(int motorcadeId) {
        this.motorcadeId = motorcadeId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<JsonMotocardesDriver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<JsonMotocardesDriver> drivers) {
        this.drivers = drivers;
    }

    public void setDrivesFromPeople(List<People> selectedDrivers) {
        List<JsonMotocardesDriver> drives = new ArrayList<>();
        for (People people : selectedDrivers) {
            drives.add(new JsonMotocardesDriver(people));
        }
        this.drivers = drives;
    }
}
