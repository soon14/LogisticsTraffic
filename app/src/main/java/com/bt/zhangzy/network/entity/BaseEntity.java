package com.bt.zhangzy.network.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by ZhangZy on 2016-1-15.
 */
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

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
