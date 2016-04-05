package com.bt.zhangzy.logisticstraffic.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.ResponseAllocationDriver;

import java.io.Serializable;

/**
 * 联系人数据封装
 * Created by ZhangZy on 2015/7/10.
 */
public class People implements Parcelable, Serializable {
    private int id,role;
    private int userId, driverId, motorcadeId,orderHistoryId;
    private String name;
    private String phoneNumber;
    private String jsonString;

    public People() {
    }

    public People(ResponseAllocationDriver json){
        if (json.getCars() != null && !json.getCars().isEmpty()) {
            setMotorcadeId(json.getCars().get(0).getMotocardesDriverId());
        }
        //这里的 roleId = driverId
        setDriverId(json.getDriver().getId());
        setRole(json.getRole());
        setId(json.getRoleId());
        setName(json.getName());
        setPhoneNumber(json.getPhoneNumber());
        setUserId(json.getDriver().getUserId());
        setOrderHistoryId(json.getId());
    }

    public People ( JsonCar jsonCar){
        setMotorcadeId(jsonCar.getMotocardesDriverId());
        setDriverId(jsonCar.getDriverId());
        setId(jsonCar.getDriverId());
        setName(jsonCar.getName());
        setPhoneNumber(jsonCar.getPhoneNumber());
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getOrderHistoryId() {
        return orderHistoryId;
    }

    public void setOrderHistoryId(int orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getMotorcadeId() {
        return motorcadeId;
    }

    public void setMotorcadeId(int motorcadeId) {
        this.motorcadeId = motorcadeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public People setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public People setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(jsonString);
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(driverId);
        dest.writeInt(motorcadeId);
        dest.writeInt(orderHistoryId);
        dest.writeInt(role);
    }

    //实例化静态内部对象CREATOR实现接口Parcelable.Creator
    public static final Parcelable.Creator<People> CREATOR = new Creator<People>() {

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }

        //将Parcel对象反序列化为ParcelableDate
        @Override
        public People createFromParcel(Parcel source) {
            People people = new People();
            people.setName(source.readString());
            people.setPhoneNumber(source.readString());
            people.setJsonString(source.readString());
            people.setId(source.readInt());
            people.setUserId(source.readInt());
            people.setDriverId(source.readInt());
            people.setMotorcadeId(source.readInt());
            people.setOrderHistoryId(source.readInt());
            people.setRole(source.readInt());
            return people;
        }
    };
}
