package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.LicenceKeyboardPopupWindow;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.RequestAddCar;
import com.bt.zhangzy.tools.ViewUtils;

/**
 * 发布车源信息
 * Created by ZhangZy on 2015/7/23.
 */
public class PublishCarActivity extends BaseActivity {

    TextView licenceEd;
//    private ImageView userImage;
    JsonCar requestCarJson = new JsonCar();
    boolean isAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        setPageName("发布车辆");
        licenceEd = (TextView) findViewById(R.id.publish_licence_ed);

        //// TO DO: 2016-2-26  发布车辆不能填写信息，只能修改备注信息，页面需要做限制
        initView();


    }


    private void initView() {
        User user = User.getInstance();
        JsonDriver driver = user.getJsonTypeEntity();
        JsonCar car = user.getJsonCar().get(0);
        setTextView(R.id.publish_name_tx, user.getUserName());
        setTextView(R.id.publish_tel_tx, user.getPhoneNum());
        if (car != null) {
            isAuth = true;
            requestCarJson = car;
            setTextView(R.id.publish_licence_ed, car.getNumber());
            setTextView(R.id.publish_length_ed, car.getLength());
            setTextView(R.id.publish_weight_ed, car.getCapacity());
            setTextView(R.id.publish_type_ed, car.getType());
//            setTextView(R.id.publish_status_ed, car.getSituation());
            setTextView(R.id.publish_address_ed, car.getUsualResidence());
            setTextView(R.id.publish_remark_tx, car.getRemark());

            setImageUrl(R.id.publish_car_img, car.getFrontalPhotoUrl1());
            setImageUrl(R.id.publish_car_2_img, car.getFrontalPhotoUrl2());
            setImageUrl(R.id.publish_licence_img, car.getDrivingLicensePhotoUrl());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }


    public void onClick_ShowLicence(View view) {
        if (isAuth)
            return;
        //虚拟键盘
        LicenceKeyboardPopupWindow.create(this).setListener(new LicenceKeyboardPopupWindow.ConfirmListener() {
            @Override
            public void confirm(String string) {
                licenceEd.setText(string);

            }
        }).showAsDropDown(view, 0, -view.getHeight());

    }

    public void onClick_Length(View view) {
        if (isAuth)
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆长度", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
                        setTextView(R.id.publish_length_ed, text.toString());
                        requestCarJson.setLength(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_length_items));
    }

    public void onClick_Weight(View view) {
        if (isAuth)
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆载重", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
                        setTextView(R.id.publish_weight_ed, text.toString());
                        requestCarJson.setCapacity(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_weight_items));
    }

    public void onClick_Type(View view) {
        if (isAuth)
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆类型", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
                        setTextView(R.id.publish_type_ed, text.toString());
                        requestCarJson.setType(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_type_items));
    }

    public void onClick_Status(View view) {
        if (isAuth)
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆状况", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
//                        setTextView(R.id.publish_status_ed, text.toString());
                        requestCarJson.setSituation(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.car_status_items));
    }

    public void onClick_ChangeLocation(final View view) {
        if (isAuth)
            return;
        if (view == null || !(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;
        String string = ViewUtils.getStringFromTextView(textView);
        Location location;
        if (TextUtils.isEmpty(string)) {
            location = User.getInstance().getLocation();
        } else {
            location = Location.Parse(string);
        }
        LocationView.createDialog(this)
                .setCurrentLocation(location)
                .setListener(new LocationView.ChangingListener() {
                    @Override
                    public void onChanged(Location loc) {
                    }

                    public void onCancel(Location loc) {
                        if (loc == null)
                            return;

                        String params = loc.toText();
                        textView.setText(params);
                        if (requestCarJson != null) {
                            requestCarJson.setUsualResidence(params);
                        }
                    }
                }).show();
    }

    public void onClick_Photo(View view) {
        if (isAuth)
            return;
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView imageView, String url) {
                switch (imageView.getId()) {
                    case R.id.publish_car_img:
                        requestCarJson.setFrontalPhotoUrl1(url);
                        break;
                    case R.id.publish_car_2_img:
                        requestCarJson.setFrontalPhotoUrl2(url);
                        break;
                    case R.id.publish_licence_img:
                        requestCarJson.setDrivingLicensePhotoUrl(url);
                        break;
                }
            }
        });


    }


    public void onClick_Publish(View view) {
        if (TextUtils.isEmpty(licenceEd.getText())) {
            showToast("请输入车牌号");
            return;
        }
        requestCarJson.setNumber(licenceEd.getText().toString().trim());
        if (isAuth) {
            requestPublishCar();
        } else {
            requestAddCar();
        }

    }



    private void requestAddCar() {
        RequestAddCar requestJson = new RequestAddCar();
        JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
        requestJson.setDriver(jsonDriver);
        requestJson.setCar(requestCarJson);
        requestCarJson.setDriverId(jsonDriver.getId());
        EditText editText = (EditText) findViewById(R.id.publish_remark_tx);
        if (!TextUtils.isEmpty(editText.getText())) {
            requestCarJson.setRemark(editText.getText().toString());
        }

        HttpHelper.getInstance().post(AppURL.PostDriversAddCar, requestJson, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                requestPublishCar();
            }

            @Override
            public void onFailed(String str) {
                showToast("发布失败");
            }
        });
    }

    private void requestPublishCar() {
        JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
        requestCarJson.setDriverId(jsonDriver.getId());
        EditText editText = (EditText) findViewById(R.id.publish_remark_tx);
        if (!TextUtils.isEmpty(editText.getText())) {
            requestCarJson.setRemark(editText.getText().toString());
        }
        HttpHelper.getInstance().post(AppURL.PostDriversPublishCar, requestCarJson, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("发布成功");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("发布失败");
            }
        });
    }
}
