package com.bt.zhangzy.logisticstraffic.data;

import android.text.TextUtils;

import com.bt.zhangzy.tools.Tools;

import java.io.Serializable;

/**
 * Created by ZhangZy on 2015/7/3.
 */
public class Location implements Serializable {

    static final char SEPARATOR = '·';//定义分隔符
    private String cityName;//市
    private String provinceName;//省
    private String district;
    private String latitude;//纬度
    private String longitude;//经度
    private char fistLatter;

    public Location() {
    }

    public Location(String provinceName, String cityName) {
        this.provinceName = provinceName;
        this.cityName = cityName;
        if (!TextUtils.isEmpty(provinceName))
            fistLatter = Tools.getFirstLetter(provinceName.charAt(0));
    }

    public static Location Parse(String text) {
        if (TextUtils.isEmpty(text) || text.indexOf(SEPARATOR) < 0)
            return null;
        String[] split = text.split(String.valueOf(SEPARATOR));
        if (split == null || split.length <= 1)
            return null;
        else
            return new Location(split[0], split[1]);
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public char getFistLatter() {
        return fistLatter;
    }

    public String toText() {
        if (TextUtils.isEmpty(provinceName) && TextUtils.isEmpty(cityName))
            return "";
        else
            return provinceName + SEPARATOR + cityName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + provinceName + "->" + cityName + "(" + longitude + "," + latitude + ")]";
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
