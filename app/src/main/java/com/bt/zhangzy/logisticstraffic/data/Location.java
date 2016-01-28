package com.bt.zhangzy.logisticstraffic.data;

import com.bt.zhangzy.tools.Tools;

import java.io.Serializable;

/**
 * Created by ZhangZy on 2015/7/3.
 */
public class Location implements Serializable {

    private String cityName;//市
    private String provinceName;//省
    private String latitude;//纬度
    private String langitude;//经度
    private char fistLatter;

    public Location() {
    }

    public Location(String provinceName, String cityName) {
        this.provinceName = provinceName;
        this.cityName = cityName;
        fistLatter = Tools.getFirstLetter(provinceName.charAt(0));
    }

    public char getFistLatter() {
        return fistLatter;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + provinceName + "->" + cityName + "(" + langitude + "," + latitude + ")]";
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

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
