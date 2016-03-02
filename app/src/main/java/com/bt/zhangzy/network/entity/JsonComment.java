package com.bt.zhangzy.network.entity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-3-2.
 */
public class JsonComment extends BaseEntity {

    int id, role, roleId;//发起人角色类型  发起人ID
    int commentedRole, commentedRoleId;//被评论角色类型  被评论角色ID
    Date date;//评论时间
    double rate;//评级;
    String content;//评论内容

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

    public int getCommentedRole() {
        return commentedRole;
    }

    public void setCommentedRole(int commentedRole) {
        this.commentedRole = commentedRole;
    }

    public int getCommentedRoleId() {
        return commentedRoleId;
    }

    public void setCommentedRoleId(int commentedRoleId) {
        this.commentedRoleId = commentedRoleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
