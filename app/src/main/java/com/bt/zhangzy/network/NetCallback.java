package com.bt.zhangzy.network;

/**
 * 网络请求异步回调类
 * Created by ZhangZy on 2016-1-5.
 */

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class NetCallback implements Callback {
    private static final String TAG = NetCallback.class.getSimpleName();

    public abstract void onFailed(String str);

    public abstract void onSuccess(String str);

    @Override
    public void onFailure(Request request, IOException e) {
        try {
            onFailed(request.toString());
            Log.w(TAG, request.urlString(), e);
        } catch (Exception ex) {
            Log.w(TAG, "onFailure", ex);
        }
    }

    @Override
    public void onResponse(Response response) throws IOException {
        try {
            String responseBody = response.body().string();
            onSuccess(responseBody);
            Log.d(TAG, response.toString() + "==>>" + responseBody);
        } catch (Exception e) {
            Log.w(TAG, "onResponse", e);
        }
    }
}