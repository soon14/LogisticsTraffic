package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.NetCallback;
import com.bt.zhangzy.tools.Json;
import com.squareup.okhttp.Request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZhangZy on 2015/7/30.
 */
public class ServicesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_services);
        setPageName("服务");

        if (AppParams.DRIVER_APP) {
            findViewById(R.id.services_check_ly).setVisibility(View.GONE);
        }
    }

    public void onClick_SearchID(View view) {
        EditText editText = (EditText) findViewById(R.id.services_id_ed);
        if (TextUtils.isEmpty(editText.getText()))
            return;

        reuqestSearchID(editText.getText().toString());
    }

    private void setResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) findViewById(R.id.services_search_result_tx);
                textView.setText(result);
            }
        });
    }

    private void reuqestSearchID(String idNum) {

        String url = "http://apis.baidu.com/chazhao/idcard/idcard";
        String httpArg = "idcard=" + idNum;
//        new AsyncTask<String, String, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                String url = "http://apis.baidu.com/apistore/idservice/id";
//                String httpArg = "idcard=152824198610280317";
//                String request = request(url, httpArg);
//                Log.w(TAG, "身份证查询信息：" + request);
//                return "";
//            }
//        }.execute("");

        url = url + "?" + httpArg;
        Request requestBody = new Request.Builder()
                .url(url)
                .addHeader("apikey", "9dbe96867ce071f9717cfa9c3d9e2484")
                .get().build();
        HttpHelper.getInstance().enqueue(requestBody, new NetCallback() {

            @Override
            public void onFailed(String str) {

            }

            @Override
            public void onSuccess(String str) {
//                str = str.replaceAll("/r/n", "");

                StringBuffer stringBuffer = new StringBuffer();
                Json json = Json.ToJson(str);
                String msg = json.getString("msg");
                Log.w(TAG, "查询结果" + json.toString());
                Json data = json.getJson("data");
                String address = data.getString("address");
                String gender = data.getString("gender");
                String zodiac = data.getString("zodiac");
                stringBuffer.append("身份证号：").append(data.getString("idcard"));
                stringBuffer.append("\n");
                stringBuffer.append("地址：").append(address);
                stringBuffer.append("\n");
                stringBuffer.append("性别：").append(gender);
                stringBuffer.append("\n");
                stringBuffer.append("生日：").append(data.getString("birthday"));
                setResult(stringBuffer.toString());


            }
        });
    }

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "9dbe96867ce071f9717cfa9c3d9e2484");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void onClick_InfoLocation(View view) {

        String pageName = null;
        String url = null;
        Location location = User.getInstance().getLocation();
        switch (view.getId()) {
            case R.id.services_qiye_btn:
                pageName = "信息部";
                url = "xxb";
                break;
            case R.id.services_jiayouzhan_btn:
                pageName = "加油站";
                url = "jyz";
                break;
            case R.id.services_wuliu_btn:
                pageName = "物流园区";
                url = "wly";
                break;
            case R.id.services_cangchu_btn:
                pageName = "仓储";
                url = "jd";
                break;
            case R.id.services_canyin_btn:
                pageName = "餐饮";
                url = "cy";
                break;
            case R.id.services_jiudian_btn:
                pageName = "酒店";
                url = "cc";
                break;
            case R.id.services_xiyu_btn:
                pageName = "洗浴";
                url = "xy";
                break;
            case R.id.services_chaoshi_btn:
                pageName = "超市";
                url = "cs";
                break;
        }

        if (pageName != null) {
            url = String.format(AppURL.LOCATION_MAP_SERVERS.toString(), Float.valueOf(location.getLangitude()), Float.valueOf(location.getLatitude()), url);
            Bundle bundle = new Bundle();
            bundle.putString(AppParams.WEB_PAGE_NAME, pageName);
            bundle.putString(AppParams.WEB_PAGE_URL, url);
            startActivity(WebViewActivity.class, bundle);
//            startActivity(LocationListActivity.class, bundle);
        }
    }
}
