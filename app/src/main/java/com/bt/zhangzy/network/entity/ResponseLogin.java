package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class ResponseLogin extends BaseEntity {
    public ResponseLogin() {
    }

    JsonUser user;
    JsonDriver driver;
    JsonCompany company;
    JsonEnterprise enterprise;

    public JsonUser getUser() {
        return user;
    }

    public void setUser(JsonUser user) {
        this.user = user;
    }

    public JsonDriver getDriver() {
        return driver;
    }

    public void setDriver(JsonDriver driver) {
        this.driver = driver;
    }

    public JsonCompany getCompany() {
        return company;
    }

    public void setCompany(JsonCompany company) {
        this.company = company;
    }

    public JsonEnterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(JsonEnterprise enterprise) {
        this.enterprise = enterprise;
    }
}
