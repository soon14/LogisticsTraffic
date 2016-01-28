package com.bt.zhangzy.network.entity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-27.
 */
public class JsonLocationCity extends BaseEntity {
    public JsonLocationCity() {
    }

    String province;
    String pictureUrl;
    String introduction;
    String city;
    List<String> town;

    @Override
    public String toString() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getTown() {
        return town;
    }

    public void setTown(List<String> town) {
        this.town = town;
    }
}
