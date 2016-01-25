package com.bt.zhangzy.tools;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 异步文件上传  鉴于6.0推荐使用HttpURLConnection
 * Created by ZhangZy on 2016-1-20.
 */
public class UploadFileTask extends AsyncTask<File, Integer, String> {

    private static final String TAG = UploadFileTask.class.getSimpleName();
    private static final int TIME_OUT = 10 * 10000000; //超时时间
    String requestURL;// = "http://192.168.208.13:8080/AndroidUploadFileWeb/FileImageUploadServlet";

    /**
     * 可变长的输入参数，与AsyncTask.exucute()对应
     */

    public UploadFileTask(String url) {
        requestURL = url;
    }

//    @Override
//    protected void onPostExecute(String result) {
//        // 返回HTML页面的内容
//    }


    @Override
    protected String doInBackground(File... params) {
//        File file = new File(params[0]);

        return postUseUrlConnection(requestURL, params[0]);
//        return uploadFile(params[0], requestURL);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    /**
     * 以POST方式上传文件
     *
     * @param requestUrl
     * @param file
     * @return
     */
    private String postUseUrlConnection(String requestUrl, File file) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String newName = file.getName();
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIME_OUT);
            connection.setConnectTimeout(TIME_OUT);
            // 允许Input、Output，不使用Cache
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            // 设置以POST方式进行传送
            connection.setRequestMethod("POST");
            // 设置RequestProperty
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            // 构造DataOutputStream流
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + end);
            outputStream.writeBytes("Content-Disposition: form-data; "
                    + "name=\"file\";filename=\"" + newName + "\"" + end);
            outputStream.writeBytes(end);
            // 构造要上传文件的FileInputStream流
            FileInputStream fileInputStream = new FileInputStream(file);
            // 设置每次写入1024bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            // 从文件读取数据至缓冲区
            while ((length = fileInputStream.read(buffer)) != -1) {
                // 将资料写入DataOutputStream中
                outputStream.write(buffer, 0, length);
            }
            outputStream.writeBytes(end);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + end);
            // 关闭流
            fileInputStream.close();
            outputStream.flush();
            // 获取响应流
            InputStream connectionInputStream = connection.getInputStream();
            int ch;
            while ((ch = connectionInputStream.read()) != -1) {
                sb.append((char) ch);
            }
            // 关闭DataOutputStream
            outputStream.close();
            connectionInputStream.close();

            /**
             * 获取响应码 200=成功
             * 当响应成功，获取响应的流
             */
            int res = connection.getResponseCode();
            if (res != 200) {
                Log.w(TAG, requestUrl+"==>response code:" + res + ";ResponseMessage:" + connection.getResponseMessage() + ": " + sb.toString());

            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "MalformedURLException", e);
        }
        return sb.toString();
    }


}
