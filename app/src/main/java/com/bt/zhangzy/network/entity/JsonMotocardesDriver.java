package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.Driver;
import com.zhangzy.base.http.BaseEntity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-25.
 */
public class JsonMotocardesDriver extends BaseEntity {
    public JsonMotocardesDriver() {
    }

    public JsonMotocardesDriver(Driver driver) {
        id = driver.getId();
        driverId = driver.getId();
//        motorcadeId = driver.getMotorcadeId();
        userId = driver.getUserId();
        name = driver.getName();
        phoneNumber = driver.getPhoneNumber();
        carIds = paraseCarIds(driver.getSelectCars());

    }
    int id,userId;
    int motorcadeId;
    int driverId;
    String name, phoneNumber;
    String carIds;

    /**
     * 将车辆列表解析为 car 的id的字符串
     * @param carArrayList
     * @return
     */
    private String paraseCarIds(List<Car> carArrayList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Car car : carArrayList) {
            stringBuffer.append(car.getId());
            stringBuffer.append(',');
        }
        return stringBuffer.substring(0, stringBuffer.length() - 1);
    }
    public String getCarIds() {
        return carIds;
    }

    public void setCarIds(String carIds) {
        this.carIds = carIds;
    }

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
