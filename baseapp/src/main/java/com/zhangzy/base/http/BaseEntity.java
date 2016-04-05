package com.zhangzy.base.http;


import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * 将数据 解析成 List< String >
     *方便 bundle 中的数据传递
     * @param list
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> ArrayList<String> ParseArrayToString(List<T> list) {
        if (list == null || list.isEmpty())
            return null;
        ArrayList<String> arrayList = new ArrayList<String>();
        for (T t : list) {
            arrayList.add(t.toString());
        }
        return arrayList;
    }

    /**
     * 将 string list 数据解析成 list json数据
     *
     * @param arrayList
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> ArrayList<T> ParseArrayToEntity(ArrayList<String> arrayList, Class<T> tClass) {
        if (arrayList == null || arrayList.isEmpty())
            return null;
        ArrayList<T> list = new ArrayList<T>();
        for (String string : arrayList) {
            list.add(BaseEntity.ParseEntity(string, tClass));
        }
        return list;
    }
}
