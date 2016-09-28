package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * 请求添加驾驶员（新增）
 * Created by ZhangZy on 2016-9-26.
 */
public class RequestSaveDriver extends BaseEntity {
    String dirverPhone ;
    int carOnwerId;

    public String getDirverPhone() {
        return dirverPhone;
    }

    public void setDirverPhone(String dirverPhone) {
        this.dirverPhone = dirverPhone;
    }

    public int getCarOnwerId() {
        return carOnwerId;
    }

    public void setCarOnwerId(int carOnwerId) {
        this.carOnwerId = carOnwerId;
    }
}
