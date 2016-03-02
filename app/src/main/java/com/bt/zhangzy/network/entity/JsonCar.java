package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class JsonCar extends BaseEntity {

    String name, phoneNumber;
    int id, driverId;
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

    public JsonCar() {
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
