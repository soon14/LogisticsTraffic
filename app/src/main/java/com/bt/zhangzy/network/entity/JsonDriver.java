package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-1-21.
 */
public class JsonDriver extends BaseEntity {

    int id , userId;
    String licensePhotoUrl;//驾驶证照片URL
    String specialQualificationsPhotoUrl;//特殊资质图片URL
    String personLicensePhotoUrl;//本人手持驾驶证照片URL
    String myWealth;//我的财富
    int star;//星级
    int commentsCount;//评论数
    int orderCount;//接单数
    int totalMileage;//总里程
    int status;
    //需要后期自己添加的数据,
    String name,phone;


    public JsonDriver() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getLicensePhotoUrl() {
        return licensePhotoUrl;
    }

    public void setLicensePhotoUrl(String licensePhotoUrl) {
        this.licensePhotoUrl = licensePhotoUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSpecialQualificationsPhotoUrl() {
        return specialQualificationsPhotoUrl;
    }

    public void setSpecialQualificationsPhotoUrl(String specialQualificationsPhotoUrl) {
        this.specialQualificationsPhotoUrl = specialQualificationsPhotoUrl;
    }

    public String getPersonLicensePhotoUrl() {
        return personLicensePhotoUrl;
    }

    public void setPersonLicensePhotoUrl(String personLicensePhotoUrl) {
        this.personLicensePhotoUrl = personLicensePhotoUrl;
    }

    public String getMyWealth() {
        return myWealth;
    }

    public void setMyWealth(String myWealth) {
        this.myWealth = myWealth;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(int totalMileage) {
        this.totalMileage = totalMileage;
    }
}
