package com.bt.zhangzy.network;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.bt.zhangzy.network.entity.BaseEntity;

import java.util.List;

/**
 * f
 * 网络回调接口  处理返回的json字符串
 * Created by ZhangZy on 2016-1-15.
 */
public abstract class JsonCallback extends NetCallback {
    public abstract void onSuccess(String msg, String result);

    @Override
    public void onSuccess(String str) {
        BaseEntity entity = JSON.parseObject(str, BaseEntity.class);
        if (entity != null) {
            if (entity.getCode() == 200) {
                try {
                    onSuccess(entity.getMessage(), entity.getResult());

                    return;
                } catch (JSONException ex) {
                    Log.w(JsonCallback.class.getSimpleName(), ex);
                }
            }
            onFailed(entity.getMessage());
            return;
        }
//        Json json = Json.ToJson(str);
//        if (json != null) {
//            if (json.has("code")) {
//                int code = json.getInt("code");
//                String message = json.getString("message");
//                if (code == 200) {
//                    Json result = json.getJson("result");
//                    if (result != null) {
//                        onSuccess(message, result);
//                        return;
//                    }
//                }
//                onFailed(message);
//                return;
//            }
//        }
        onFailed(str);
    }

    public static <T extends BaseEntity> T ParseJson_Object(String json, Class<T> tClass) {
        return JSON.parseObject(json, tClass);
    }

    public static <T extends BaseEntity> List<T> ParseJson_Array(String json, Class<T> tClass) {
        return JSON.parseArray(json, tClass);
    }

    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

}
