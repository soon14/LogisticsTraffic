package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class JsonCar extends BaseEntity {
    int id,driver_id;
    //行驶证照片URL  汽车正面照片URL
    String driving_license_photo_url,frontal_photo_url_1,frontal_photo_url_2;
    //汽车类型 车牌号
    String type,number;
    //车长 载重
    String length,capacity;
    //车辆状况  车辆常住地  始发地  目的地 当前位置
    String situation,usual_residence ,start_city ,stop_city , current_location;

    String remark;//备注

    public JsonCar() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriving_license_photo_url() {
        return driving_license_photo_url;
    }

    public void setDriving_license_photo_url(String driving_license_photo_url) {
        this.driving_license_photo_url = driving_license_photo_url;
    }

    public String getFrontal_photo_url_1() {
        return frontal_photo_url_1;
    }

    public void setFrontal_photo_url_1(String frontal_photo_url_1) {
        this.frontal_photo_url_1 = frontal_photo_url_1;
    }

    public String getFrontal_photo_url_2() {
        return frontal_photo_url_2;
    }

    public void setFrontal_photo_url_2(String frontal_photo_url_2) {
        this.frontal_photo_url_2 = frontal_photo_url_2;
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

    public String getUsual_residence() {
        return usual_residence;
    }

    public void setUsual_residence(String usual_residence) {
        this.usual_residence = usual_residence;
    }

    public String getStart_city() {
        return start_city;
    }

    public void setStart_city(String start_city) {
        this.start_city = start_city;
    }

    public String getStop_city() {
        return stop_city;
    }

    public void setStop_city(String stop_city) {
        this.stop_city = stop_city;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
