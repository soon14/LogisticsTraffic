package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.CallPhoneDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.zhangzy.base.http.BaseEntity;

import java.util.List;

/**
 * 车源详情
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceCarDetailActivity extends BaseActivity {

    JsonCar jsonCar;
    boolean isSelectDriverModel;//选择司机页面
    boolean isSelectDriver;
    boolean fromFleetPage = false;//标记是否从车源列表中来

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_source_car_detail);
        setPageName("车源详情");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppParams.SOURCE_PAGE_CAR_KEY)) {
                jsonCar = BaseEntity.ParseEntity(bundle.getString(AppParams.SOURCE_PAGE_CAR_KEY), JsonCar.class);
            }
            fromFleetPage = bundle.containsKey(AppParams.SOURCE_PAGE_SELECT_DRIVER_KEY);
            isSelectDriverModel = bundle.getBoolean(AppParams.SOURCE_PAGE_SELECT_DRIVER_KEY);
            isSelectDriver = bundle.getBoolean(AppParams.SOURCE_PAGE_RESULT_SELECT_KEY);

        }

        initView();
    }

    private void initView() {
        if (jsonCar == null)
            return;
        setImageUrl(R.id.car_img, jsonCar.getDrivingLicensePhotoUrl());
        setTextView(R.id.car_type_tx, jsonCar.getType());
        setTextView(R.id.car_length_tx, jsonCar.getLength());
        setTextView(R.id.car_weight_tx, jsonCar.getCapacity());
        setTextView(R.id.car_num_tx, jsonCar.getNumber());
//        setTextView(R.id.car_situation_tx, jsonCar.getSituation());
        setTextView(R.id.car_name_tx, jsonCar.getName());
//        setTextView(R.id.car_star_city_tx, jsonCar.getConsignorCity());
//        setTextView(R.id.car_stop_city_tx, jsonCar.getStopCity());
//        setTextView(R.id.car_location_tx, jsonCar.getCurrentLocation());
        setTextView(R.id.car_residence_tx, jsonCar.getUsualResidence());
        setTextView(R.id.car_remark_tx, jsonCar.getRemark());

        Button confirmBt = (Button) findViewById(R.id.car_confirm_bt);
        confirmBt.setVisibility(isSelectDriverModel ? View.VISIBLE : View.GONE);
        if (isSelectDriverModel) {

            confirmBt.setText(isSelectDriver ? "取消选择" : "确认选择");
        }
        if (fromFleetPage)
            findViewById(R.id.car_join_bt).setVisibility(View.GONE);
    }

    public void onClick_Join(View view) {
        //加入车队 // TODO: 2016-1-28  车源详情中 加入车队接口
//        People devices = new People();
//        devices.setName("卢爱华");
//        devices.setPhoneNumber("18202268826");
//        User.getInstance().getDriverList().add(devices);
//        showToast("添加成功");
        requestAddMotorcade();
    }

    public void onClick_CallPhone(View view) {
//        ContextTools.callPhone(this, "10010");
        if (!User.getInstance().checkUserVipStatus(this)) {
            return;
        }
        new CallPhoneDialog(this)
                .setPhoneNum(jsonCar.getPhoneNumber())
                .show();
    }

    private void requestAddMotorcade() {
        int motorcadeId = 0;
        List<JsonMotorcades> motorcades = User.getInstance().getMotorcades();
        if (motorcades != null && !motorcades.isEmpty()) {
            //信息部只能有一个车队，所以直接取第一个元素
            motorcadeId = motorcades.get(0).getId();
        }

        HttpHelper.getInstance().post(AppURL.PostAddMotorcadeDriverPhone, motorcadeId + "/" + jsonCar.getPhoneNumber(), new BaseEntity(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("司机添加成功");
//                notifyListAdapter();
                //重新请求车队的成员列表
//                requestGetMotorcades(motorcadeId);
            }

            @Override
            public void onFailed(String str) {
                showToast("司机添加失败" + str);
            }
        });
    }

    public void onClick_ConfirmDrvier(View view) {
        Intent intent = new Intent();
        intent.putExtra(AppParams.SOURCE_PAGE_RESULT_SELECT_KEY, !isSelectDriver);
        setResult(AppParams.RESULT_CODE_CONFIRM_DRIVER, intent);
        finish();
    }
}
