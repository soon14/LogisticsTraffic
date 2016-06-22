package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.HttpHelper;
import com.zhangzy.base.http.BaseEntity;
import com.zhangzy.base.http.NetCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZhangZy on 2016-2-24.
 */
public class TestActivity extends BaseActivity {

    class JsonParams extends BaseEntity {
        double firstName, lastName;

        public double getFirstName() {
            return firstName;
        }

        public void setFirstName(double firstName) {
            this.firstName = firstName;
        }

        public double getLastName() {
            return lastName;
        }

        public void setLastName(double lastName) {
            this.lastName = lastName;
        }
    }

    class ArrayParams extends BaseEntity {
        List<JsonParams> list;

        public List<JsonParams> getList() {
            return list;
        }

        public void setList(List<JsonParams> list) {
            this.list = list;
        }
    }

    TextView paramsTx, responseTx;
    EditText urlTx, longitudeEd, latitudeEd;
    ArrayParams params = new ArrayParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        urlTx = (EditText) findViewById(R.id.test_url);
        paramsTx = (TextView) findViewById(R.id.test_params);
        responseTx = (TextView) findViewById(R.id.test_response);
        longitudeEd = (EditText) findViewById(R.id.test_add_longitude);
        latitudeEd = (EditText) findViewById(R.id.test_add_latitude);

        urlTx.setText(url);
        longitudeEd.setText("116.417854");
        latitudeEd.setText("39.921988");
    }

    //    String url = "http://192.168.1.114:8080/freight/html/ditu.html?id=1";
//    String url = "http://192.168.1.112:8080/freight/html/software.html";
    String url = "http://192.168.1.112:8080/freight/html/company.html";

    String response;

    public void onClick_AddEntity(View v) {
        String url = "http://api.map.baidu.com/trace/v2/entity/add";
        HashMap<String, String> params = new HashMap<>();
        params.put("ak", "Usl0hVgcUs82XUojFiQPaFtI");
        params.put("service_id", "119300");
        params.put("entity_name", getStringFromTextView(R.id.test_entity_name));

        HttpHelper.getInstance().post(url, params, new NetCallback() {
            @Override
            public void onFailed(String str) {
                setResponse(str);
            }

            @Override
            public void onSuccess(String str) {
                setResponse(str);
            }
        });
    }

    private void setResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseTx.setText(response);
            }
        });
    }

    public void onClick_AddPoint(View v) {
        String url = "http://api.map.baidu.com/trace/v2/track/addpoint";
        HashMap<String, String> params = new HashMap<>();
        params.put("ak", "Usl0hVgcUs82XUojFiQPaFtI");
        params.put("service_id", "119300");
        params.put("latitude", latitudeEd.getText().toString());
        params.put("longitude", longitudeEd.getText().toString());
        params.put("coord_type", "1");
        params.put("loc_time", String.valueOf(System.currentTimeMillis()/1000));
        params.put("entity_name", getStringFromTextView(R.id.test_entity_name));
        HttpHelper.getInstance().post(url, params, new NetCallback() {
            @Override
            public void onFailed(String str) {
                setResponse(str);
            }

            @Override
            public void onSuccess(String str) {
                setResponse(str);
            }
        });

    }

    public void onClick_Request(View view) {
        if (TextUtils.isEmpty(urlTx.getText())) {
            showToast("请求地址为空");
            return;
        }
        url = urlTx.getText().toString();

        HttpHelper.getInstance().post(url, params, new NetCallback() {

            @Override
            public void onFailed(String str) {
                response = "failed:" + str;
                setResponse();

            }

            @Override
            public void onSuccess(String str) {
                response = str;
                setResponse();
            }
        });

    }

    public void onClick_OpenWeb(View view) {
        if (TextUtils.isEmpty(urlTx.getText())) {
            showToast("请求地址为空");
            return;
        }
        url = urlTx.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "测试页面");
        bundle.putString(AppParams.WEB_PAGE_URL, url);
        startActivity(WebViewActivity.class, bundle);
    }

    private void setResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseTx.setText(response);
                Bundle bundle = new Bundle();
                bundle.putString(AppParams.WEB_PAGE_NAME, "测试页面");
                bundle.putString(AppParams.WEB_PAGE_HTML_DATA, response);
                startActivity(WebViewActivity.class, bundle);
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response));
//                startActivity(intent);
            }
        });
    }


    public void onClick_AddParams(View view) {
        if (TextUtils.isEmpty(longitudeEd.getText()) || TextUtils.isEmpty(latitudeEd.getText())) {
            showToast("参数为空");
            return;
        }
        String params_1 = longitudeEd.getText().toString();
        String params_2 = latitudeEd.getText().toString();
//        ArrayList<JsonParams> list = new ArrayList<>();
        if (params.getList() == null)
            params.setList(new ArrayList<JsonParams>());

        JsonParams json = new JsonParams();
        json.setFirstName(Double.valueOf(params_1));
        json.setLastName(Double.valueOf(params_2));
        params.getList().add(json);
        paramsTx.setText(JSON.toJSONString(params));

    }
}
