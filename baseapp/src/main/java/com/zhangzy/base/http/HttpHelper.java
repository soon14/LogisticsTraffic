package com.zhangzy.base.http;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
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
 * 数据链接类
 * 1、POST    /url      创建
 * 2、DELETE  /url/xxx  删除
 * 3、PUT     /url/xxx  更新
 * 4、GET     /url/xxx  查看
 * Created by ZhangZy on 2015/6/4.
 */
public class HttpHelper extends OkHttpClient {

    private static final String TAG = HttpHelper.class.getSimpleName();

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/png");

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
    private static HttpHelper instance;

    HttpHelper() {
        this.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setWriteTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setRetryOnConnectionFailure(true);//连接超时重试

    }

    public static HttpHelper getInstance() {
        if (instance == null)
            instance = new HttpHelper();

        return instance;
    }

    /**
     * 拼接口参数  如：http://182.92.77.31:8080/motorcades/params1/params2
     *
     * @param params
     * @return
     */
    public static String toString(String... params) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String p : params) {
            stringBuffer.append(p).append("/");
        }
        //删除最后一个 /
        if (stringBuffer.charAt(stringBuffer.length() - 1) == '/')
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    /**
     * 拼接口参数  如：http://www.baidu.com?param1&param2&....
     *
     * @param url
     * @param textParams
     * @return
     */
    public static String toString(String url, HashMap textParams) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url);
        stringBuffer.append("?");
        Iterator<String> it = textParams.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = it.next();
            stringBuffer.append(key);
            stringBuffer.append('=');
            stringBuffer.append(textParams.get(key));
            if (it.hasNext())
                stringBuffer.append('&');
        }
        //删除最后一个 &
        if (stringBuffer.charAt(stringBuffer.length() - 1) == '&')
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    /**
     * 拼接口参数  如：http://www.baidu.com?param1&param2&....
     *
     * @param url
     * @param params
     * @return
     */
    public static String toString(String url, String[] params) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url).append("?");
        for (String p : params) {
            stringBuffer.append(p).append("&");
        }
        //删除最后一个 &
        if (stringBuffer.charAt(stringBuffer.length() - 1) == '&')
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    /**
     * 拼接口参数  如：http://www.baidu.com?param1&param2&....
     *
     * @param url
     * @param json
     * @return
     */
    public static String toString(String url, JSONObject json) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url);
        stringBuffer.append("?");
        try {
            Iterator<String> keys = json.keys();
            String key, value;
            while (keys.hasNext()) {
                key = keys.next();
                value = json.getString(key);
                stringBuffer.append(key + "=" + value).append("&");
            }
            //删除最后一个 &
            if (stringBuffer.charAt(stringBuffer.length() - 1) == '&')
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    /**
     * 拼接口参数  如：http://www.baidu.com?param1&param2&....
     *
     * @param url
     * @param entity
     * @return
     */
    public static String toString(String url, BaseEntity entity) {
        com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) JSON.toJSON(entity);
//        JSON.parseObject(JSON.toJSONString(entity));
//        get(url, jsonObject, callback);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url);
        stringBuffer.append("?");
        for (String key : jsonObject.keySet()) {
            stringBuffer.append(key).append("=").append(jsonObject.getString(key)).append("&");
        }

        return stringBuffer.toString();
    }


    /*=================== 单线程请求 ===========================*/
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
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
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
    /*=======================================*/

    /**
     * @param url
     * @param entity   fastJSON实体类
     * @param callback
     */
    public void post(String url, BaseEntity entity, NetCallback callback) {
        post(url, entity.toString(), callback);
    }

    public void put(String url, BaseEntity entity, NetCallback callback) {
        put(url, entity.toString(), callback);
    }

    public void del(String url, BaseEntity entity, NetCallback callback) {
        del(url, entity.toString(), callback);
    }

    public void get(String url, BaseEntity entity, NetCallback callback) {
        get(toString(url, entity), callback);
    }


    /**
     * 异步线程访问网络
     *
     * @param url
     * @param json     参数格式 JSON
     * @param callback
     */
    public void post(String url, String json, NetCallback callback) {
        Log.i(TAG, "post url = " + url + " json = " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
//        post(url, body, callback);
        enqueue(new Request.Builder().url(url).post(body).build(), callback);
    }

    public void get(String url, NetCallback callback) {
        Log.i(TAG, "get url = " + url);
        enqueue(new Request.Builder().url(url).get().build(), callback);
    }

    public void put(String url, String json, NetCallback callback) {
        Log.i(TAG, "put url = " + url + " json = " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
//        put(url, body, callback);
        enqueue(new Request.Builder().url(url).put(body).build(), callback);
    }

    public void del(String url, String json, NetCallback callback) {
        Log.i(TAG, "del url = " + url + " json = " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
//        del(url, body, callback);
        enqueue(new Request.Builder().url(url).delete(body).build(), callback);
    }

    public void del(String url, NetCallback callback) {
        enqueue(new Request.Builder().url(url).delete().build(), callback);
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
                Log.i(TAG, "post url = " + url + " params = " + textParams.toString());
                Set<String> keySet = textParams.keySet();
                for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                    String name = it.next();
                    String value = (String) textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
                    builder.add(name, value);
                }
            }
            RequestBody body = builder.build();
//            post(url, body, responseCallback);
            enqueue(new Request.Builder().url(url).post(body).build(), responseCallback);
        } catch (Exception e) {
            Log.w(TAG, "post(" + url + "," + textParams == null ? "" : textParams.toString() + ")", e);
        }
    }

    public void get(String url, HashMap textParams, NetCallback responseCallback) {
        //表单
        if (textParams != null && textParams.size() > 0) {
            Log.i(TAG, "get url = " + url + " params = " + textParams.toString());
            String stringBuffer = toString(url, textParams);

            enqueue(new Request.Builder().url(stringBuffer).get().build(), responseCallback);
        }
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


    /**
     * 基于okhttp上传文件
     *
     * @param url
     * @param file
     * @param rspCallback
     */
    public void postImage(String url, File file, NetCallback rspCallback) {
        /* MediaType FORM = MediaType.parse("multipart/form-data");
        *  MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/png");
        * */
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
//        builder.addPart(RequestBody.create(MEDIA_TYPE_IMAGE, file));
        /* MediaType.parse("media/type") */
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));

//        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\""),
//                RequestBody.create(MediaType.parse("image/jpeg"), file));

        //遍历map中所有参数到builder
//        for (String key : map.keySet()) {
//            builder.addFormDataPart(key, map.get(key));
//        }

        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
//        for (String path : paths) {
//            builder.addFormDataPart("upload", null, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
//        }


        //构建请求体
//        RequestBody requestBody = builder.build();

//        Headers handler = Headers.Builder.;
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        enqueue(request, rspCallback);
    }



    /**
     * 不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Response execute(Request request) throws IOException {
        return this.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     * 底层封装
     *
     * @param request
     * @param responseCallback
     */
    public void enqueue(Request request, NetCallback responseCallback) {
        try {
            this.newCall(request).enqueue(responseCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "网络请求异常url=" + request.toString(), e);
        }
    }


}
