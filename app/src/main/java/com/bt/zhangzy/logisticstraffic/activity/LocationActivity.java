package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonLocationCity;

/**
 * Created by ZhangZy on 2015/6/12.
 */
public class LocationActivity extends BaseActivity {

//    private View listLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

//        listLayout = findViewById(R.id.location_list_ly);

        LocationView.getInstance().requestLocation(this, new LocationView.LocationCallback() {
            @Override
            public void networkLocation(Location location) {
                setCityName(location.getCityName());
            }

            @Override
            public void chooseLocation(Location location) {
                setCityName(location.getCityName());
            }
        });

    }

    private void setCityName(String cityname) {
        TextView textView = (TextView) findViewById(R.id.location_info);
        textView.setText("定位成功");
        textView = (TextView) findViewById(R.id.location_city_name_btn);
        textView.setText(cityname);
        requestCityInfo(cityname);
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(HomeActivity.class);
                finish();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    JsonLocationCity cityInfo;

    private void requestCityInfo(String cityname) {
        HttpHelper.getInstance().get(AppURL.GetCityInfo + cityname, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                cityInfo = ParseJson_Object(result, JsonLocationCity.class);
                refreshView();
            }

            @Override
            public void onFailed(String str) {
                showToast("城市信息获取失败");
            }
        });
    }

    private void refreshView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView city_name = (TextView) findViewById(R.id.location_city_tx);
                city_name.setText(cityInfo.getProvince() + "·" + cityInfo.getCity());
                TextView city_info = (TextView) findViewById(R.id.location_city_info_tx);
                city_info.setText(cityInfo.getIntroduction());
                if (!TextUtils.isEmpty(cityInfo.getPictureUrl())) {
                    setImageUrl(R.id.location_city_img, cityInfo.getPictureUrl());
                }
            }
        });
    }

    public void onClick_OpenCityList(View view) {

//        getApp().showLoacaitonList(view);
        LocationView.getInstance().showLoacaitonList(this, view);

    }


    public void onClick_JumpToHome(View view) {
//        startActivity(new Intent(this,HomeActivity.class));
        startActivity(HomeActivity.class);
        finish();
    }


}
