package com.bt.zhangzy.network.entity;

import com.alibaba.fastjson.JSON;

/**
 * Created by ZhangZy on 2016-1-15.
 */
public class BaseEntity {

    int code = -1;
    String message;
    String result;

    public BaseEntity() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
