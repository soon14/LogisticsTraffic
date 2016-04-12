package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-4-12.
 */
public class ResponseComment extends BaseEntity {

    int role;
    int commentRole;
    JsonComment comment;
    JsonUser roleObject;
    JsonUser commentRoleObject;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getCommentRole() {
        return commentRole;
    }

    public void setCommentRole(int commentRole) {
        this.commentRole = commentRole;
    }

    public JsonComment getComment() {
        return comment;
    }

    public void setComment(JsonComment comment) {
        this.comment = comment;
    }

    public JsonUser getRoleObject() {
        return roleObject;
    }

    public void setRoleObject(JsonUser roleObject) {
        this.roleObject = roleObject;
    }

    public JsonUser getCommentRoleObject() {
        return commentRoleObject;
    }

    public void setCommentRoleObject(JsonUser commentRoleObject) {
        this.commentRoleObject = commentRoleObject;
    }
}
