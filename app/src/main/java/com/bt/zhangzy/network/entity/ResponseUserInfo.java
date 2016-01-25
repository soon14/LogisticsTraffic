package com.bt.zhangzy.network.entity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class ResponseUserInfo extends BaseEntity{
    private JsonCompany company;
    private List<JsonMotorcades> motorcades;
    private JsonEnterprise enterprise;
    private JsonDriver driver;

    public ResponseUserInfo() {
    }

    public JsonCompany getCompany() {
        return company;
    }

    public void setCompany(JsonCompany company) {
        this.company = company;
    }

    public List<JsonMotorcades> getMotorcades() {
        return motorcades;
    }

    public void setMotorcades(List<JsonMotorcades> motorcades) {
        this.motorcades = motorcades;
    }

    public JsonEnterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(JsonEnterprise enterprise) {
        this.enterprise = enterprise;
    }

    public JsonDriver getDriver() {
        return driver;
    }

    public void setDriver(JsonDriver driver) {
        this.driver = driver;
    }
}
