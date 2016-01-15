package com.bt.zhangzy.network;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * todo 数据链接类  未测试
 * Created by ZhangZy on 2015/6/4.
 */
public class HttpHelper extends OkHttpClient {

    private static final String TAG = HttpHelper.class.getSimpleName();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    /**
     * 请求超时 秒
     */
    public static final int TIMEOUT = 40;

    /**
     * UUID
     */
    public static String BOUNDARY = java.util.UUID.randomUUID().toString();

    /**
     * 单例实体
     */
    private static HttpHelper mInstance;

    private HttpHelper() {
        this.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setWriteTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setRetryOnConnectionFailure(true);//连接超时重试

    }

    public static HttpHelper getInstance() {
        if (mInstance == null)
            mInstance = new HttpHelper();

        return mInstance;
    }


    private String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = this.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    private String post(String url, HashMap textParams) throws IOException {
        RequestBody requestBody = null;
//        MultipartBuilder multBuilder = new MultipartBuilder();
//        multBuilder.type(MediaType.parse("multipart/form-data"+ ";boundary=" + BOUNDARY));
        FormEncodingBuilder builder = new FormEncodingBuilder();
        //表单
        if (textParams != null && textParams.size() > 0) {
            Set<String> keySet = textParams.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                String name = it.next();
                String value = (String) textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
                builder.add(name, value);
            }
        }
//        multBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"jpg\""),RequestBody.create(MediaType.parse("image/jpg;charset=UTF-8"),data));
//        requestBody = multBuilder.build();
        requestBody = builder.build();

        return post(url, requestBody);
    }

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        return post(url, body);
    }

    private String post(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = this.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    public void post(String url, JSONObject json, NetCallback callback) {
        if(json == null)
            return;
        Log.i(TAG, "url = "+url+" json = "+json.toString());
        RequestBody body = RequestBody.create(JSON, json.toString());
        post(url, body, callback);
    }
    /**
     * 异步线程访问网络
     *
     * @param url
     * @param json     参数格式 JSON
     * @param callback
     */
    public void post(String url, String json, NetCallback callback) {
        Log.i(TAG, "url = "+url+" json = "+json);
        RequestBody body = RequestBody.create(JSON, json);
        post(url, body, callback);
    }

    /***
     * 异步线程访问网络
     *
     * @param url
     * @param textParams       参数格式Map
     * @param responseCallback
     * @throws IOException
     */
    public void post(String url, HashMap<String, String> textParams, NetCallback responseCallback) {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            //表单
            if (textParams != null && textParams.size() > 0) {
                Log.i(TAG, "url = "+url+" params = "+textParams.toString());
                Set<String> keySet = textParams.keySet();
                for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                    String name = it.next();
                    String value = (String) textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
                    builder.add(name, value);
                }
            }
            RequestBody body = builder.build();
            post(url, body, responseCallback);
        } catch (Exception e) {
            Log.w(TAG, "post(" + url + "," + textParams == null ? "" : textParams.toString() + ")", e);
        }
    }

    /***
     * 异步线程访问网络
     *
     * @param url
     * @param body
     * @param responseCallback
     * @throws IOException
     */
    private void post(String url, RequestBody body, NetCallback responseCallback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        enqueue(request, responseCallback);

    }

    /**
     * 文件上传 待测试
     *
     * @param url
     * @param file
     * @param rspCallback
     */
    public void postFile(String url, File file, NetCallback rspCallback) {

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        enqueue(request, rspCallback);
    }

    public void get(String url, NetCallback responseCallback) {
        Request request = new Request.Builder().url(url).build();
        this.newCall(request).enqueue(responseCallback);
    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Response execute(Request request) throws IOException {
        return this.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public void enqueue(Request request, NetCallback responseCallback) {
        this.newCall(request).enqueue(responseCallback);
    }


}
