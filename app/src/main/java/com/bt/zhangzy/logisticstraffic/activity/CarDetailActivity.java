package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonCar;

/**
 * 车辆详情
 * Created by ZhangZy on 2016-8-26.
 */
public class CarDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null) {

            setContentView(R.layout.activity_car_edit);
        } else {
            setContentView(R.layout.activity_car_detail);
            String car_str = getIntent().getExtras().getString(AppParams.CAR_LIST_PAGE_CAR_KEY);
            if(!TextUtils.isEmpty(car_str)){
                JsonCar jsonCar = JsonCar.ParseEntity(car_str,JsonCar.class);

                initView(jsonCar);
            }
        }


        setPageName("车辆详情");
    }

    /**
     * 车辆信息初始化
     * @param jsonCar
     */
    private void initView(JsonCar jsonCar) {

        setTextView(R.id.car_detail_num_tx,jsonCar.getNumber());
        setTextView(R.id.car_detail_type_tx,jsonCar.getType());
        setTextView(R.id.car_detail_length_tx,jsonCar.getLength());
        setTextView(R.id.car_detail_weight_tx,jsonCar.getCapacity());
        setTextView(R.id.car_detail_address_tx,jsonCar.getUsualResidence());

        setImageUrl(R.id.car_detail_photo_img,jsonCar.getFrontalPhotoUrl1());
        setImageUrl(R.id.car_detail_license_img,jsonCar.getDrivingLicensePhotoUrl());



    }


}
