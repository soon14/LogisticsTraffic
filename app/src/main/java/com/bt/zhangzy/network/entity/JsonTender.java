package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-3-14.
 */
public class JsonTender extends BaseEntity {
    int id;
    int enterpriseId;
    String name;
    String pictureBigUrl;
    String pictureSmallUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureBigUrl() {
        return pictureBigUrl;
    }

    public void setPictureBigUrl(String pictureBigUrl) {
        this.pictureBigUrl = pictureBigUrl;
    }

    public String getPictureSmallUrl() {
        return pictureSmallUrl;
    }

    public void setPictureSmallUrl(String pictureSmallUrl) {
        this.pictureSmallUrl = pictureSmallUrl;
    }
}
