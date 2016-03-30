package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.http.BaseEntity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-2-1.
 */
public class ResponseOpenCity extends BaseEntity {
    public ResponseOpenCity() {
    }

    String province;
    List<String> city;
    char firstLetter;

    public char getFirstLetter() {
        return firstLetter;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
        firstLetter = Tools.getFirstLetter(province.charAt(0));
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(List<String> city) {
        this.city = city;
    }
}
