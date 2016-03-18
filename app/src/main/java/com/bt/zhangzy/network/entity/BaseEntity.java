package com.bt.zhangzy.network.entity;


import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

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


    @Override
    public String toString() {
//        return super.toString();
        return JSON.toJSONString(this);
    }

    public static <T extends BaseEntity> T ParseEntity(String str, Class<T> tClass) {
        return JSON.parseObject(str, tClass);
    }

    public static <T extends BaseEntity> List<T> ParseArray(String str, Class<T> tClass) {
        return JSON.parseArray(str, tClass);
    }

//    public static String toStringArray(){
//        return JSON.toJSONString()
//    }
}
