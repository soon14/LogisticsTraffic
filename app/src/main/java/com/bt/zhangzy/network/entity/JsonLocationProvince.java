package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.tools.Tools;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-27.
 */
public class JsonLocationProvince extends BaseEntity {
    public JsonLocationProvince() {
    }

    char firstLetter;
    String province;
    List<JsonLocationCity> city;

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

    public List<JsonLocationCity> getCity() {
        return city;
    }

    public void setCity(List<JsonLocationCity> city) {
        this.city = city;
    }

    //根据控件需求 覆盖tostring方法
    @Override
    public String toString() {
//        return super.toString();
        return province;
    }
}
