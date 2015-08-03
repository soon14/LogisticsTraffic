package com.bt.zhangzy.logisticstraffic.data;

import java.io.Serializable;

/**
 * Created by ZhangZy on 2015/7/3.
 */
public class Location implements Serializable {

    private String cityName;
    private String latitude;//纬度
    private String langitude;//经度


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLangitude() {
        return langitude;
    }

    public void setLangitude(String langitude) {
        this.langitude = langitude;
    }
}
