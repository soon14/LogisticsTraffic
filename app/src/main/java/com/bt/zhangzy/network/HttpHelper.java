package com.bt.zhangzy.network;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * 请求超时 秒
     */
    public static final int TIMEOUT = 40;

    /**
     * UUID
     */
    public static String BOUNDARY = java.util.UUID.randomUUID ( ).toString ( ) ;

    /**
     *  单例实体
     */
    private static HttpHelper mInstance;

    private HttpHelper(){
        this.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setWriteTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setRetryOnConnectionFailure(true);//连接超时重试

    }

    public static HttpHelper getInstance(){
        if(mInstance == null)
            mInstance = new HttpHelper();

        return mInstance;
    }


    public String get(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = this.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    public String post(String url,HashMap textParams) throws IOException{
        RequestBody requestBody = null;
//        MultipartBuilder multBuilder = new MultipartBuilder();
//        multBuilder.type(MediaType.parse("multipart/form-data"+ ";boundary=" + BOUNDARY));
        FormEncodingBuilder builder = new FormEncodingBuilder();
        //表单
        if(textParams != null && textParams.size() > 0){
            Set<String> keySet = textParams.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
                String name = it.next();
                String value = (String)textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
                builder.add(name,value);
            }
        }
//        multBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"jpg\""),RequestBody.create(MediaType.parse("image/jpg;charset=UTF-8"),data));
//        requestBody = multBuilder.build();
        requestBody = builder.build();

        return post(url,requestBody);
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        return post(url,body);
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

    /**
     * 该不会开启异步线程。
     * @param request
     * @return
     * @throws IOException
     */
    public Response execute(Request request) throws IOException{
        return this.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public void enqueue(Request request, Callback responseCallback){
        this.newCall(request).enqueue(responseCallback);
    }


}
