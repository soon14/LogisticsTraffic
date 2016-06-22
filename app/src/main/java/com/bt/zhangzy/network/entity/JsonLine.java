package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-5-18.
 */
public class JsonLine extends BaseEntity {

    int id;
    int userId;
    int isDefault;
    String lineName;
    Date createDate;
    Date modifyDate;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
