package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.logisticstraffic.data.People;

/**
 * Created by ZhangZy on 2016-1-25.
 */
public class JsonMotocardesDriver extends BaseEntity {
    public JsonMotocardesDriver() {
    }

    public JsonMotocardesDriver(People people) {
        id = people.getId();
        driverId = people.getDriverId();
        motorcadeId = people.getMotorcadeId();
        userId = people.getUserId();
        name = people.getName();
        phoneNumber = people.getPhoneNumber();
    }
    int id,userId;
    int motorcadeId;
    int driverId;
    String name, phoneNumber;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMotorcadeId() {
        return motorcadeId;
    }

    public void setMotorcadeId(int motorcadeId) {
        this.motorcadeId = motorcadeId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
