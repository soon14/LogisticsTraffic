package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.LicenceKeyboardPopupWindow;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCar;

/**
 * 车辆详情
 * Created by ZhangZy on 2016-8-26.
 */
public class CarDetailActivity extends BaseActivity {
    EditText licenceEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null) {

            setContentView(R.layout.activity_car_edit);
            licenceEd = (EditText) findViewById(R.id.car_edit_licence_ed);
        } else {
            setContentView(R.layout.activity_car_detail);
            String car_str = getIntent().getExtras().getString(AppParams.CAR_LIST_PAGE_CAR_KEY);
            if (!TextUtils.isEmpty(car_str)) {
                JsonCar jsonCar = JsonCar.ParseEntity(car_str, JsonCar.class);

                initView(jsonCar);
            }
        }


        setPageName("车辆详情");
    }

    /**
     * 车辆信息初始化
     *
     * @param jsonCar
     */
    private void initView(JsonCar jsonCar) {

        setTextView(R.id.car_detail_num_tx, jsonCar.getNumber());
        setTextView(R.id.car_detail_type_tx, jsonCar.getType());
        setTextView(R.id.car_detail_length_tx, jsonCar.getLength());
        setTextView(R.id.car_detail_weight_tx, jsonCar.getCapacity());
        setTextView(R.id.car_detail_address_tx, jsonCar.getUsualResidence());

        setImageUrl(R.id.car_detail_photo_img, jsonCar.getFrontalPhotoUrl1());
        setImageUrl(R.id.car_detail_license_img, jsonCar.getDrivingLicensePhotoUrl());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @return
     */
    private boolean checkStauts() {
//        if (!editable) {
//            showToast("当前状态不可编辑");
//            return true;
//        }
        return false;
    }

    /**
     * 车牌号选择
     *
     * @param view
     */
    public void onClick_ShowLicence(View view) {
        if (checkStauts())
            return;
        //虚拟键盘
        LicenceKeyboardPopupWindow.create(this).setListener(new LicenceKeyboardPopupWindow.ConfirmListener() {
            @Override
            public void confirm(String string) {
                if (licenceEd != null)
                    licenceEd.setText(string);

            }
        }).showAsDropDown(view, 0, -view.getHeight());

    }

    /**
     * 车辆长度
     *
     * @param view
     */
    public void onClick_ChangeLength(final View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆长度", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
                        ((TextView) view).setText(text);
//                        setTextView(R.id.detail_car_length, text.toString());
//                        requestJsonCar.setLength(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_length_items));
    }


    /**
     * 车辆载重
     *
     * @param view
     */
    public void onClick_ChangeWeight(final View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆载重", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
                        ((TextView) view).setText(text);
//                        setTextView(R.id.detail_car_weight, text.toString());
//                        requestJsonCar.setCapacity(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_weight_items));
    }

    /**
     * 车辆类型
     *
     * @param view
     */
    public void onClick_ChangeType(final View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆类型", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
                        ((TextView) view).setText(text);
//                        setTextView(R.id.detail_car_type, text.toString());
//                        requestJsonCar.setType(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_type_items));
    }


    /**
     * 照片上传事件
     *
     * @param view
     */
    public void onClick_Photo(View view) {
        if (checkStauts())
            return;
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView userImage, String uploadImgURL) {
                if (userImage != null)
                    switch (userImage.getId()) {
                        case R.id.photo_yyzz_img:
//                            yyzzUrl = uploadImgURL;
                            break;
                    }
            }
        });
    }


}
