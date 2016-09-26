package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.logisticstraffic.data.OrderReceiveStatus;
import com.zhangzy.base.http.BaseEntity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class JsonCar extends BaseEntity {

    String name, phoneNumber;
    int id, driverId;
    int motocardesDriverId;//车队的对象id  仅在删除时用
    //行驶证照片URL  汽车正面照片URL
    String drivingLicensePhotoUrl, frontalPhotoUrl1, frontalPhotoUrl2;
    //汽车类型 车牌号
    String type, number;
    //车长 载重
    String length, capacity;
    //车辆状况  车辆常住地  始发地  目的地 当前位置
    String situation, usualResidence, startCity, stopCity, currentLocation;

    String remark;//备注
    int status;
    OrderReceiveStatus receiveStatus;

    //车主需求需要添加的字段
    int payStatus;//车辆付费状态
    Date payDate;
    Date expireDate;
    int runStatus;//运行状态
    int orderId;//订单主键
    int pilotId;//驾驶员主键


    public JsonCar() {
    }


    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public int getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(int runStatus) {
        this.runStatus = runStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPilotId() {
        return pilotId;
    }

    public void setPilotId(int pilotId) {
        this.pilotId = pilotId;
    }

    public OrderReceiveStatus getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(OrderReceiveStatus receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public int getMotocardesDriverId() {
        return motocardesDriverId;
    }

    public void setMotocardesDriverId(int motocardesDriverId) {
        this.motocardesDriverId = motocardesDriverId;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDrivingLicensePhotoUrl() {
        return drivingLicensePhotoUrl;
    }

    public void setDrivingLicensePhotoUrl(String drivingLicensePhotoUrl) {
        this.drivingLicensePhotoUrl = drivingLicensePhotoUrl;
    }

    public String getFrontalPhotoUrl1() {
        return frontalPhotoUrl1;
    }

    public void setFrontalPhotoUrl1(String frontalPhotoUrl1) {
        this.frontalPhotoUrl1 = frontalPhotoUrl1;
    }

    public String getFrontalPhotoUrl2() {
        return frontalPhotoUrl2;
    }

    public void setFrontalPhotoUrl2(String frontalPhotoUrl2) {
        this.frontalPhotoUrl2 = frontalPhotoUrl2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getUsualResidence() {
        return usualResidence;
    }

    public void setUsualResidence(String usualResidence) {
        this.usualResidence = usualResidence;
    }

    public String getStartCity() {
        return startCity;
    }

    public void setStartCity(String startCity) {
        this.startCity = startCity;
    }

    public String getStopCity() {
        return stopCity;
    }

    public void setStopCity(String stopCity) {
        this.stopCity = stopCity;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
