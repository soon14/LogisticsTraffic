package com.bt.zhangzy.logisticstraffic.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.bt.zhangzy.network.entity.JsonCar;

import java.util.Date;

/**
 * 数据模型
 * 车辆
 * Created by ZhangZy on 2016-9-22.
 */
public class Car implements Parcelable {

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
    int runStatus = 1;//运行状态
    int orderId;//订单主键
    int pilotId;//驾驶员主键

    public Car() {
    }

    public Car(JsonCar json) {
        this.name = json.getName();
        this.phoneNumber = json.getPhoneNumber();
        this.id = json.getId();
        this.driverId = json.getDriverId();
        this.number = json.getNumber();
        this.type = json.getType();
        this.status = json.getStatus();
        this.length = json.getLength();
        this.capacity = json.getCapacity();
        this.payStatus = json.getPayStatus();
        this.runStatus = json.getRunStatus();
        this.orderId = json.getOrderId();
        this.pilotId = json.getPilotId();
        this.situation = json.getSituation();
        this.usualResidence = json.getUsualResidence();
        this.currentLocation = json.getCurrentLocation();
        this.drivingLicensePhotoUrl = json.getDrivingLicensePhotoUrl();
        this.frontalPhotoUrl1 = json.getFrontalPhotoUrl1();
        this.frontalPhotoUrl2 = json.getFrontalPhotoUrl2();
        this.payDate = json.getPayDate();
        this.expireDate = json.getExpireDate();
    }

    /**
     * json数据转换
     *
     * @return
     */
    public JsonCar toJson() {
        JsonCar json = new JsonCar();
        json.setName(name);
        json.setPhoneNumber(phoneNumber);
        json.setId(id);
        json.setDriverId(driverId);
        json.setNumber(number);
        json.setType(type);
        json.setStatus(status);
        json.setLength(length);
        json.setCapacity(capacity);
        json.setPayStatus(payStatus);
        json.setRunStatus(runStatus);
        json.setOrderId(orderId);
        json.setPilotId(pilotId);
        json.setSituation(situation);
        json.setUsualResidence(usualResidence);
        json.setCurrentLocation(currentLocation);
        json.setDrivingLicensePhotoUrl(drivingLicensePhotoUrl);
        json.setFrontalPhotoUrl1(frontalPhotoUrl1);
        json.setFrontalPhotoUrl2(frontalPhotoUrl2);
        json.setPayDate(payDate);
        json.setExpireDate(expireDate);


        return json;
    }

    public String getFrontalPhotoUrl1() {
        return frontalPhotoUrl1;
    }

    public void setFrontalPhotoUrl1(String frontalPhotoUrl1) {
        this.frontalPhotoUrl1 = frontalPhotoUrl1;
    }

    public String getDrivingLicensePhotoUrl() {
        return drivingLicensePhotoUrl;
    }

    public void setDrivingLicensePhotoUrl(String drivingLicensePhotoUrl) {
        this.drivingLicensePhotoUrl = drivingLicensePhotoUrl;
    }

    public void setUsualResidence(String usualResidence) {
        this.usualResidence = usualResidence;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getId() {
        return id;
    }

    public int getDriverId() {
        return driverId;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public String getLength() {
        return length;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getSituation() {
        return situation;
    }

    public CarStatus getStatus() {
        return CarStatus.Parse(status);
    }

    public String getStatusName() {
        switch (getStatus()) {
            case UN_CHECKED:
                return "未提交";
            case COMMITED:
                return "已提交资料";
            case CHECKED:
                return "已审核";
            case LOCK:
                return "冻结";
        }
        return "";
    }

    public CarPayStatus getPayStatus() {
        return CarPayStatus.Parse(payStatus);
    }

    public String getPayStatusName() {
        switch (getPayStatus()) {
            case NonPayment:
            case Payment:
                return "未支付";
            case PaymentReceived:
                return "已支付";
            case Overdue:
                return "已过期";
        }
        return "";
    }

    public CarRunStatus getRunStatus() {
        return CarRunStatus.Parse(runStatus);
    }

    public String getRunStatusName() {
        switch (getRunStatus()) {
            case Leisure:
                return "空闲";
            case Busy:
                return "运输中";
            case OffStream:
                return "停运";

        }
        return "";
    }

    public int getOrderId() {
        return orderId;
    }

    public int getPilotId() {
        return pilotId;
    }

    public String getUsualResidence() {
        return usualResidence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.phoneNumber);
        dest.writeInt(this.id);
        dest.writeInt(this.driverId);
        dest.writeInt(this.motocardesDriverId);
        dest.writeString(this.drivingLicensePhotoUrl);
        dest.writeString(this.frontalPhotoUrl1);
        dest.writeString(this.frontalPhotoUrl2);
        dest.writeString(this.type);
        dest.writeString(this.number);
        dest.writeString(this.length);
        dest.writeString(this.capacity);
        dest.writeString(this.situation);
        dest.writeString(this.usualResidence);
        dest.writeString(this.startCity);
        dest.writeString(this.stopCity);
        dest.writeString(this.currentLocation);
        dest.writeString(this.remark);
        dest.writeInt(this.status);
        dest.writeInt(this.receiveStatus == null ? -1 : this.receiveStatus.ordinal());
        dest.writeInt(this.payStatus);
        dest.writeLong(this.payDate != null ? this.payDate.getTime() : -1);
        dest.writeLong(this.expireDate != null ? this.expireDate.getTime() : -1);
        dest.writeInt(this.runStatus);
        dest.writeInt(this.orderId);
        dest.writeInt(this.pilotId);
    }

    protected Car(Parcel in) {
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.id = in.readInt();
        this.driverId = in.readInt();
        this.motocardesDriverId = in.readInt();
        this.drivingLicensePhotoUrl = in.readString();
        this.frontalPhotoUrl1 = in.readString();
        this.frontalPhotoUrl2 = in.readString();
        this.type = in.readString();
        this.number = in.readString();
        this.length = in.readString();
        this.capacity = in.readString();
        this.situation = in.readString();
        this.usualResidence = in.readString();
        this.startCity = in.readString();
        this.stopCity = in.readString();
        this.currentLocation = in.readString();
        this.remark = in.readString();
        this.status = in.readInt();
        int tmpReceiveStatus = in.readInt();
        this.receiveStatus = tmpReceiveStatus == -1 ? null : OrderReceiveStatus.values()[tmpReceiveStatus];
        this.payStatus = in.readInt();
        long tmpPayDate = in.readLong();
        this.payDate = tmpPayDate == -1 ? null : new Date(tmpPayDate);
        long tmpExpireDate = in.readLong();
        this.expireDate = tmpExpireDate == -1 ? null : new Date(tmpExpireDate);
        this.runStatus = in.readInt();
        this.orderId = in.readInt();
        this.pilotId = in.readInt();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel source) {
            return new Car(source);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };


    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getPayDate() {
        return payDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }
}
