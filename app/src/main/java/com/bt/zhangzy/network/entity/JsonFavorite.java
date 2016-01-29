package com.bt.zhangzy.network.entity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-1-28.
 */
public class JsonFavorite extends BaseEntity {
    public JsonFavorite() {
    }

    int id;
    int role,roleId,favoritedRole,favoritedRoleId;
    Date date;
    String remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getFavoritedRole() {
        return favoritedRole;
    }

    public void setFavoritedRole(int favoritedRole) {
        this.favoritedRole = favoritedRole;
    }

    public int getFavoritedRoleId() {
        return favoritedRoleId;
    }

    public void setFavoritedRoleId(int favoritedRoleId) {
        this.favoritedRoleId = favoritedRoleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
