package com.bt.zhangzy.logisticstraffic.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonMotocardesDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据模型
 * 司机用户
 * <p/>
 * Created by ZhangZy on 2016-9-22.
 */
public class Driver implements Parcelable {

    int id, userId, mototcadeDriverId;
    int orderHistoryId;
    String licensePhotoUrl;//驾驶证照片URL
    String specialQualificationsPhotoUrl;//特殊资质图片URL
    String personLicensePhotoUrl;//本人手持驾驶证照片URL
    String myWealth;//我的财富
    int star;//星级
    int commentsCount;//评论数
    int orderCount;//接单数
    int totalMileage;//总里程
    int status;

    //车主需求中添加的字段
    int carId;//正在驾驶车辆id
    int drivingState;//驾驶状态
    String idCard;//身份证号码


    //需要后期自己添加的数据,
    String name, phoneNumber;
    int ownCarCount;
    int selectCarNum;

    List<Car> listCar;
    List<Car> selectCars;

    public Driver() {

    }

    public Driver(JsonDriver jsonDriver) {
        this.id = jsonDriver.getId();
        this.userId = jsonDriver.getUserId();
        this.licensePhotoUrl = jsonDriver.getLicensePhotoUrl();
        this.status = jsonDriver.getStatus();
        this.carId = jsonDriver.getCarId();
        this.drivingState = jsonDriver.getDrivingState();
        this.idCard = jsonDriver.getIdCard();
        this.name = jsonDriver.getName();
        this.phoneNumber = jsonDriver.getPhoneNumber();
//        this.ownCarCount = jsonDriver.getOwnCarCount();
//        this.selectCarNum = jsonDriver.getSelectCarNum();

    }

    public Driver(JsonMotocardesDriver jsonMotocardesDriverdriver) {
        this.id = jsonMotocardesDriverdriver.getDriverId();
        this.userId = jsonMotocardesDriverdriver.getUserId();
        this.mototcadeDriverId = jsonMotocardesDriverdriver.getId();
        this.name = jsonMotocardesDriverdriver.getName();
        this.phoneNumber = jsonMotocardesDriverdriver.getPhoneNumber();

    }

    public void copy(Driver d) {
        this.id = d.id;
        this.userId = d.userId;
        this.mototcadeDriverId = d.mototcadeDriverId;
        this.orderHistoryId = d.orderHistoryId;
        this.licensePhotoUrl = d.licensePhotoUrl;
        this.specialQualificationsPhotoUrl = d.specialQualificationsPhotoUrl;
        this.personLicensePhotoUrl = d.personLicensePhotoUrl;
        this.myWealth = d.myWealth;
        this.star = d.star;
        this.commentsCount = d.commentsCount;
        this.orderCount = d.orderCount;
        this.totalMileage = d.totalMileage;
        this.status = d.status;
        this.carId = d.carId;
        this.drivingState = d.drivingState;
        this.idCard = d.idCard;
        this.name = d.name;
        this.phoneNumber = d.phoneNumber;
        this.ownCarCount = d.ownCarCount;
        this.selectCarNum = d.selectCarNum;
        this.listCar = d.listCar;
        this.selectCars = d.selectCars;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getListCar() {
        return listCar;
    }

    public void addCar(Car car) {
        if (listCar == null)
            listCar = new ArrayList<Car>();

        listCar.add(car);
        ownCarCount = listCar.size();
    }

    public void addAllCar(List<Car> list) {
        if (listCar == null)
            listCar = new ArrayList<Car>();

        listCar.addAll(list);
        ownCarCount = listCar.size();
    }

    public int getOwnCarCount() {
        return ownCarCount;
    }

    public int getSelectCarNum() {
        return selectCarNum;
    }

    public List<Car> getSelectCars() {
        return selectCars;
    }

    public void setSelectCars(List<Car> selectCars) {
        this.selectCars = selectCars;
        selectCarNum = selectCars == null ? 0 : selectCars.size();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getLicensePhotoUrl() {
        return licensePhotoUrl;
    }

    public int getStatus() {
        return status;
    }

    public int getCarId() {
        return carId;
    }

    public int getDrivingState() {
        return drivingState;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getOrderHistoryId() {
        return orderHistoryId;
    }

    public void setOrderHistoryId(int orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Driver) {
            return this.id == ((Driver) o).id;
        }
        return super.equals(o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeInt(this.mototcadeDriverId);
        dest.writeInt(this.orderHistoryId);
        dest.writeString(this.licensePhotoUrl);
        dest.writeString(this.specialQualificationsPhotoUrl);
        dest.writeString(this.personLicensePhotoUrl);
        dest.writeString(this.myWealth);
        dest.writeInt(this.star);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.orderCount);
        dest.writeInt(this.totalMileage);
        dest.writeInt(this.status);
        dest.writeInt(this.carId);
        dest.writeInt(this.drivingState);
        dest.writeString(this.idCard);
        dest.writeString(this.name);
        dest.writeString(this.phoneNumber);
        dest.writeInt(this.ownCarCount);
        dest.writeInt(this.selectCarNum);
        dest.writeTypedList(listCar);
        dest.writeTypedList(selectCars);
    }

    protected Driver(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readInt();
        this.mototcadeDriverId = in.readInt();
        this.orderHistoryId = in.readInt();
        this.licensePhotoUrl = in.readString();
        this.specialQualificationsPhotoUrl = in.readString();
        this.personLicensePhotoUrl = in.readString();
        this.myWealth = in.readString();
        this.star = in.readInt();
        this.commentsCount = in.readInt();
        this.orderCount = in.readInt();
        this.totalMileage = in.readInt();
        this.status = in.readInt();
        this.carId = in.readInt();
        this.drivingState = in.readInt();
        this.idCard = in.readString();
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.ownCarCount = in.readInt();
        this.selectCarNum = in.readInt();
        this.listCar = in.createTypedArrayList(Car.CREATOR);
        this.selectCars = in.createTypedArrayList(Car.CREATOR);

    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        public Driver createFromParcel(Parcel source) {
            return new Driver(source);
        }

        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };
}
