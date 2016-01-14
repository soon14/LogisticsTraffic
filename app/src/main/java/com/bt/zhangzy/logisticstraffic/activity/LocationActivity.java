package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.LogisticsTrafficApplication;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;

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

        getApp().requestLocation(new LogisticsTrafficApplication.LocationCallback() {
            @Override
            public void networkLocation(Location location) {
                User.getInstance().setLocation(location);
                setCityName(location.getCityName());
            }

            @Override
            public void chooseLocation(Location location) {
                User.getInstance().setLocation(location);
                setCityName(location.getCityName());
            }
        });

    }

    private void setCityName(String cityname) {
        TextView textView = (TextView) findViewById(R.id.location_info);
        textView.setText("定位成功");
        textView = (TextView) findViewById(R.id.location_city_name_btn);
        textView.setText(cityname);

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(HomeActivity.class);
                finish();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    public void onClick_OpenCityList(View view) {

        getApp().showLoacaitonList(view);


    }


    public void onClick_JumpToHome(View view) {
//        startActivity(new Intent(this,HomeActivity.class));
        startActivity(HomeActivity.class);
        finish();
    }


}
