package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-5-18.
 */
public class JsonLine extends BaseEntity {

    //出发城市 目的城市
    String consignorCity, receiverCity;
    //收货人姓名  收货人电话  收货人地址
    String receiverName, receiverPhone, receiverAddress;

    String consignorName;//发货人名字
    String consignorPhone;//电话
    String consignorAddress;

    public String getConsignorCity() {
        return consignorCity;
    }

    public void setConsignorCity(String consignorCity) {
        this.consignorCity = consignorCity;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String stopCity) {
        this.receiverCity = stopCity;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getConsignorName() {
        return consignorName;
    }

    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    public String getConsignorPhone() {
        return consignorPhone;
    }

    public void setConsignorPhone(String consignorPhone) {
        this.consignorPhone = consignorPhone;
    }

    public String getConsignorAddress() {
        return consignorAddress;
    }

    public void setConsignorAddress(String consignorAddress) {
        this.consignorAddress = consignorAddress;
    }
}
