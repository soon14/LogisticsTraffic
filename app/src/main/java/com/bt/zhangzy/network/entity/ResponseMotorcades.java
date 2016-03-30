package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

import java.util.List;

/**
 * 车队返回信息
 * Created by ZhangZy on 2016-1-25.
 */
public class ResponseMotorcades extends BaseEntity {
    public ResponseMotorcades() {

    }

    JsonMotorcades motorcade;
    JsonCompany company;
    private List<JsonMotocardesDriver> motorcadeDrivers;
    List<JsonCar> cars;

    public List<JsonCar> getCars() {
        return cars;
    }

    public void setCars(List<JsonCar> cars) {
        this.cars = cars;
    }

    public JsonMotorcades getMotorcade() {
        return motorcade;
    }

    public void setMotorcade(JsonMotorcades motorcade) {
        this.motorcade = motorcade;
    }

    public JsonCompany getCompany() {
        return company;
    }

    public void setCompany(JsonCompany company) {
        this.company = company;
    }

    public List<JsonMotocardesDriver> getMotorcadeDrivers() {
        return motorcadeDrivers;
    }

    public void setMotorcadeDrivers(List<JsonMotocardesDriver> motorcadeDrivers) {
        this.motorcadeDrivers = motorcadeDrivers;
    }
}
