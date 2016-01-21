package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-21.
 */
public class JsonDriver extends BaseEntity {

    int id;
    String licensePhotoUrl;//驾驶证照片URL

    public JsonDriver() {
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
}
