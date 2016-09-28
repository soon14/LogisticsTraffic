package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.CallPhoneDialog;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.LicenceKeyboardPopupWindow;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.RequestAddCar;
import com.bt.zhangzy.network.entity.RequestBindCarDriver;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

/**
 * 车辆详情
 * Created by ZhangZy on 2016-8-26.
 */
public class CarDetailActivity extends BaseActivity {
    EditText licenceEd;
    Car car;//车辆信息;
    private boolean isFirstVerify;//标记是修改 还是新建车辆

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null) {

            setContentView(R.layout.activity_car_edit);
            setPageName("添加车辆");
            licenceEd = (EditText) findViewById(R.id.car_detail_num_tx);
            car = new Car();
            isFirstVerify = true;
            Location location = User.getInstance().getLocation();
            if (location != null) {
                String params = location.toText();
                car.setUsualResidence(params);
                setTextView(R.id.car_detail_address_tx, car.getUsualResidence());
            }
        } else {
            isFirstVerify = false;
            setContentView(R.layout.activity_car_detail);
            setPageName("车辆详情");
            Car car = getIntent().getExtras().getParcelable(AppParams.CAR_LIST_PAGE_CAR_KEY);
            if (car != null) {

                initView(car);
                this.car = car;
            }
        }


    }

    /**
     * 车辆信息初始化
     *
     * @param car
     */
    private void initView(Car car) {

        setTextView(R.id.car_detail_num_tx, car.getNumber());
        setTextView(R.id.car_detail_type_tx, car.getType());
        setTextView(R.id.car_detail_length_tx, car.getLength());
        setTextView(R.id.car_detail_weight_tx, car.getCapacity());
        setTextView(R.id.car_detail_address_tx, car.getUsualResidence());

        if (!isFirstVerify)
            switch (car.getPayStatus()) {
                case PaymentReceived:
                    setTextView(R.id.car_detail_pay_msg, getString(R.string.car_detail_pay_msg, Tools.toStringDate(car.getExpireDate())));

                    View viewById = findViewById(R.id.car_detail_pay_msg);
                    if (viewById != null)
                        viewById.setClickable(false);
                    break;
                case NonPayment:
                    setTextView(R.id.car_detail_pay_msg, getString(R.string.car_detail_un_pay_msg));
                    break;
            }
        else{

        }

        setImageUrl(R.id.car_detail_photo_img, car.getFrontalPhotoUrl1());
        setImageUrl(R.id.car_detail_license_img, car.getDrivingLicensePhotoUrl());


        //绑定的驾驶员
        if (car.getPilotId() > 0)
            setTextView(R.id.car_detail_bind_tx, car.getName() + " - " + car.getPhoneNumber());
//        setBindView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else {
            if (requestCode == AppParams.CAR_DETAIL_REQUEST_CODE) {
                // 选择司机列表返回
                if (data != null && data.getExtras().containsKey(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY)) {
                    //编辑页面 转换到 显示页面
                    Car car = data.getParcelableExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY);
                    if (isFirstVerify) {
                        isFirstVerify = false;
                        setContentView(R.layout.activity_car_detail);
                        setPageName("车辆详情");
                        this.car = car;
                        initView(car);
                    }
                    String json_str = data.getStringExtra(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY);
                    Log.d(TAG, "选择需要绑定的司机-返回 ： " + json_str);
                    JsonDriver jsonDriver = JsonDriver.ParseEntity(json_str, JsonDriver.class);
//                    setBindView(jsonDriver);
                    requestBindCarDriver(jsonDriver);
                }
            } else
                super.onActivityResult(requestCode, resultCode, data);
        }
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

                car.setNumber(string);

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
                        car.setLength(text.toString());
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
                        car.setCapacity(text.toString());
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
                        car.setType(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_type_items));
    }


    /**
     * 车辆位置选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(View view) {
        if (checkStauts())
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
                        car.setUsualResidence(params);
                    }
                }).show();
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
                        case R.id.car_detail_photo_img:
                            car.setFrontalPhotoUrl1(uploadImgURL);
                            break;
                        case R.id.car_detail_license_img:
                            car.setDrivingLicensePhotoUrl(uploadImgURL);

                            break;
                    }
            }
        });
    }


    /**
     * 绑定司机按钮
     *
     * @param view
     */
    public void onClick_Bind(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppParams.CAR_DETAIL_PAGE_CAR_KEY, car);
        startActivityForResult(DriverListActivity.class, bundle, AppParams.CAR_DETAIL_REQUEST_CODE);
    }

    /**
     * 解绑司机
     *
     * @param view
     */
    public void onClick_UnBind(View view) {
        if (car.getPilotId() > 0) {
            showUnBindDialog();
        } else {
            showToast("还没有驾驶员呢");
        }
    }

    /**
     * 删除按钮
     *
     * @param view
     */
    public void onClick_Delete(View view) {
        //删除车辆 跳转到客服电话；
        new CallPhoneDialog(this)
                .setInfoMessage(String.format(getString(R.string.service_tel_dialog), getString(R.string.app_phone)))
                .setPhoneNum(getString(R.string.app_phone))
                .show();

    }

    /**
     * 转换车辆修改页面
     *
     * @param view
     */
    public void onClick_Change(View view) {
        setContentView(R.layout.activity_car_edit);
        setPageName("修改车辆");
        initView(car);
        setTextView(R.id.car_detail_submit_bt, "保存");
    }

    /**
     * 确定按钮
     *
     * @param view
     */
    public void onClick_Submit(View view) {
        //字段检测
        if (car == null)
            return;
        if (Tools.isEmptyStrings(car.getNumber(), car.getType(), car.getLength(), car.getCapacity(), car.getUsualResidence())) {
            showToast("请检查车辆信息是否填充完整");
            return;
        }
        if (Tools.isEmptyStrings(car.getFrontalPhotoUrl1(), car.getDrivingLicensePhotoUrl())) {
            showToast("请检查照片是否全部上传");
            return;
        }

        requestAddCar();
    }


    /**
     * 去付费事件
     *
     * @param view
     */
    public void onClick_Pay(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppParams.CAR_DETAIL_PAGE_CAR_KEY, car);
        startActivity(PayActivity.class, bundle);
    }

    private void showUnBindDialog() {
        new ConfirmDialog(this)
                .setMessage("是否解绑？")
                .setCancel("否")
                .setConfirm("是")
                .setListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void onClick(boolean isConfirm) {
                        if (isConfirm) {
                            requestUnBindCarDriver(car.getPilotId());
                        }
                    }
                }).show();
    }

    /**
     * 添加车辆请求
     */
    private void requestAddCar() {
        RequestAddCar requestJson = new RequestAddCar();
        JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
        requestJson.setDriver(jsonDriver);
        car.setDriverId(jsonDriver.getId());
        JsonCar car = this.car.toJson();
//        car.setDriverId(jsonDriver.getId());
        requestJson.setCar(car);

        JsonCallback jsonCallback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
//                requestPublishCar();
                JsonCar car = JsonCar.ParseEntity(result, JsonCar.class);
                CarDetailActivity.this.car = new Car(car);
                if (isFirstVerify) {
                    showToast("添加车辆成功");
                    //自动跳转到绑定司机
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppParams.CAR_DETAIL_PAGE_CAR_KEY, CarDetailActivity.this.car);
                    startActivityForResult(DriverListActivity.class, bundle, AppParams.CAR_DETAIL_REQUEST_CODE);//添加车辆成功后 自动跳转到绑定司机页面
                } else {
                    showToast("修改车辆信息成功");
                }
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail) + str);
            }
        };
        if (isFirstVerify) {
            HttpHelper.getInstance().post(AppURL.PostDriversAddCar, requestJson, jsonCallback);
        } else {
            HttpHelper.getInstance().post(AppURL.PostDrviersUpdateCar, car, jsonCallback);
        }
    }


    /**
     * 绑定司机
     *
     * @param jsonDriver
     */
    private void requestBindCarDriver(final JsonDriver jsonDriver) {

        RequestBindCarDriver params = new RequestBindCarDriver();
        params.setDriverId(jsonDriver.getId());
        params.setCarId(car.getId());

        HttpHelper.getInstance().post(AppURL.PostBindCarDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTextView(R.id.car_detail_bind_tx, jsonDriver.getName() + "  -  " + jsonDriver.getPhoneNumber());
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showToast(str);
            }
        });
    }

    private void requestUnBindCarDriver(int driverId) {

        RequestBindCarDriver params = new RequestBindCarDriver();
        params.setDriverId(driverId);
        params.setCarId(car.getId());

        HttpHelper.getInstance().post(AppURL.PostUnBindCarDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setTextView(R.id.car_detail_bind_tx, "");
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showToast(str);
            }
        });
    }


}
