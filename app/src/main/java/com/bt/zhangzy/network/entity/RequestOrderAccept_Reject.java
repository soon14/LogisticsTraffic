package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-2-19.
 */
public class RequestOrderAccept_Reject extends BaseEntity {
    int orderId, role, roleId;
    String carIds;

    public String getCarIds() {
        return carIds;
    }

    public void setCarIds(String carIds) {
        this.carIds = carIds;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getRole() {
        return role;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
