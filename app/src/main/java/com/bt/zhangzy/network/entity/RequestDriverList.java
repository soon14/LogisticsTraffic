package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * 请求司机列表
 * Created by ZhangZy on 2016-9-26.
 */
public class RequestDriverList extends BaseEntity {
    int ownerId;

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
