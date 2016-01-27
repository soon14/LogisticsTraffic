package com.bt.zhangzy.network.entity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-25.
 */
public class ResponseMotorcades extends BaseEntity {
    public ResponseMotorcades() {

    }

    JsonMotorcades motorcade;
    JsonCompany company;
    private List<JsonMotocardesDriver> motorcadeDrivers;

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
