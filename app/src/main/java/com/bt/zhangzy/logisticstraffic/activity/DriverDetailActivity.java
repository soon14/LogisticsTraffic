package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Driver;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.RequestSaveDriver;
import com.zhangzy.base.app.AppToast;

/**
 * 司机详情展示
 * Created by ZhangZy on 2016-8-30.
 */
public class DriverDetailActivity extends BaseActivity {


    Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_detail);

        setPageName("司机详情");

        if (getIntent().hasExtra(AppParams.CAR_LIST_PAGE_DRIVER_KEY)) {
            String driver_str = getIntent().getStringExtra(AppParams.CAR_LIST_PAGE_DRIVER_KEY);
            JsonUser jsonDriver = JsonUser.ParseEntity(driver_str, JsonUser.class);
            initView(jsonDriver);
        }
        //选择模式下 屏蔽删除按钮
        if (getIntent().hasExtra(AppParams.CAR_LIST_PAGE_SELECT_MODE)) {
            if (getIntent().getBooleanExtra(AppParams.CAR_LIST_PAGE_SELECT_MODE, false)) {
                View delBt = findViewById(R.id.driver_detail_del_bt);
                if (delBt != null) {
                    delBt.setVisibility(View.GONE);
                }
            }
        }

    }

    private void initView(JsonUser user) {
        setTextView(R.id.driver_detail_name_tx, user.getName());
//        setTextView(R.id.driver_detail_num_tx,jsonDriver.getId());
        setTextView(R.id.driver_detail_num_tx, user.getPhoneNumber());
        setImageUrl(R.id.driver_detail_photo_img, user.getPersonPhotoUrl());
        setImageUrl(R.id.driver_detail_id_card_photo_img, user.getIdCardPhotoUrl());

        driver = new Driver();
        driver.setUserId(user.getId());
        driver.setPhoneNumber(user.getPhoneNumber());
        driver.setName(user.getName());
    }

    /**
     * 确认按钮
     *
     * @param view
     */
    public void onClick_Submit(View view) {
        Intent data = new Intent();
        setResult(AppParams.CAR_DETAIL_REQUEST_CODE, data);
        finish();
    }

    /**
     * 删除按钮
     *
     * @param view
     */
    public void onClick_Delete(View view) {
        showDelDriverDialog(driver);
    }

    private void showDelDriverDialog(Driver driver) {
        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setMessage("删除司机" + driver.getName() + "？")
                .setConfirm("删除")
                .setCancel("取消")
                .setListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void onClick(boolean isConfirm) {
                        if (isConfirm) {
                            requestDelDriver(DriverDetailActivity.this.driver.getPhoneNumber());
                        }
                    }
                })
                .show();
    }

    /**
     * 删除司机
     *
     * @param phoneNumber
     */
    public void requestDelDriver(String phoneNumber) {

        RequestSaveDriver params = new RequestSaveDriver();
        params.setCarOnwerId((int) User.getInstance().getId());
        params.setDirverPhone(phoneNumber);
        HttpHelper.getInstance().del(AppURL.PostDeleteDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                AppToast.getInstance().showToast(getActivity(), msg);
                finish();
            }

            @Override
            public void onFailed(String str) {
                AppToast.getInstance().showToast(getActivity(), str);
            }
        });
    }
}
