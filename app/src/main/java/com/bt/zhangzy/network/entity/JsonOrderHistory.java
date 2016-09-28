package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.logisticstraffic.data.OrderReceiveStatus;
import com.zhangzy.base.http.BaseEntity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-2-19.
 */
public class JsonOrderHistory extends BaseEntity {
    int id, orderId, role, roleId, status;
    Date createData, modifyData;
    String name, phoneNumber;
    String carIds;

    public String getCarIds() {
        return carIds;
    }

    public void setCarIds(String carIds) {
        this.carIds = carIds;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getStatus() {
        return status;
    }

    public OrderReceiveStatus getReceiveStatus(){
        return OrderReceiveStatus.parseStatus(status);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateData() {
        return createData;
    }

    public void setCreateData(Date createData) {
        this.createData = createData;
    }

    public Date getModifyData() {
        return modifyData;
    }

    public void setModifyData(Date modifyData) {
        this.modifyData = modifyData;
    }
}
