package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

/**
 * 角色资料提交
 * Created by ZhangZy on 2015/6/25.
 */
public class DetailPhotoActivity extends BaseActivity {


    String yyzzUrl, mtzpUrl;
    private boolean isFirstVerify = true;
    private boolean editable = true;//标记资料是否可以编辑;
    private String currentStautsStr;

    JsonDriver requestJsonDriver = new JsonDriver();
//    JsonCar requestJsonCar = new JsonCar();
    JsonCompany requestJsonCompany = new JsonCompany();
    JsonEnterprise requestJsonEnterprise = new JsonEnterprise();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initUserStatus();
        if (AppParams.DRIVER_APP) {
            setContentView(R.layout.activity_detail_devices);
            setPageName("完善司机详情");
            initDriverView();
        } else {
            setContentView(R.layout.activity_detail_photo);
            setPageName("完善详情");
            initCompanyView();
        }

    }

    private void initUserStatus() {
        isFirstVerify = User.getInstance().getJsonTypeEntity() == null;
//        JsonUser jsonUser = User.getInstance().getJsonUser();
        String tost = null;
        /*用户状态 未审核	-1 已审核	0 冻结	1 删除	2 已付费	3 已提交资料	4*/
        switch (User.getInstance().getUserStatus()) {
            case UN_CHECKED:
                tost = "未审核";
                editable = true;
                break;
            case CHECKED:
                tost = "已审核";
                editable = false;
                break;
            case LOCK:
                tost = "冻结";
                editable = false;
                break;
            case DELETE:
                tost = "删除";
                editable = false;
                break;
            case PAID:
                tost = "已付费";
                editable = false;
                break;
            case COMMITED:
                tost = "已提交资料";
                editable = false;
                break;
        }
        currentStautsStr = tost;
        showToast(tost);
    }

    private void initDriverView() {
        if (!editable) {
//            findViewById(R.id.detail_submit_bt).setVisibility(View.INVISIBLE);
            findViewById(R.id.driver_name_ed).setEnabled(false);
            findViewById(R.id.driver_phone_ed).setEnabled(false);
            findViewById(R.id.detail_submit_bt).setEnabled(false);
            setTextView(R.id.detail_submit_bt, currentStautsStr);
        }
        User user = User.getInstance();
        setTextView(R.id.driver_name_ed, user.getUserName());
        setTextView(R.id.driver_phone_ed, user.getPhoneNum());
        JsonUser jsonUser = user.getJsonUser();
        if (!TextUtils.isEmpty(jsonUser.getIdCardPhotoUrl())) {
            setImageUrl(R.id.driver_certificate_img, jsonUser.getIdCardPhotoUrl());
            //行驶证
//            requestJsonCar.setDrivingLicensePhotoUrl(jsonUser.getIdCardPhotoUrl());
            //驾驶证
            requestJsonDriver.setLicensePhotoUrl(jsonUser.getIdCardPhotoUrl());
        }
        //手持身份证
//        if (!Tools.isEmptyStrings(jsonUser.getPersonPhotoUrl())) {
//            setImageUrl(R.id.driver_sfz_sc_img, jsonUser.getPersonPhotoUrl());
//        }
//        setImageUrl(R.id.devices_jsz_img, jsonUser.getPersonPhotoUrl());10
        if (user.getJsonTypeEntity() != null) {
            JsonDriver jsonDriver = user.getJsonTypeEntity();
//            setImageUrl(R.id.devices_car_img, jsonDriver.getLicensePhotoUrl());
//            setImageUrl(R.id.devices_tszz_img, jsonDriver.getSpecialQualificationsPhotoUrl());

//            setImageUrl(R.id.devices_jsz_sc_img, jsonDriver.getPersonLicensePhotoUrl());


            requestJsonDriver = jsonDriver;
            //to do 车的信息更新
//            if (user.getJsonCar() != null) {
//                JsonCar jsonCar = user.getJsonCar().get(0);
//                setImageUrl(R.id.devices_car_img, jsonCar.getFrontalPhotoUrl1());
////                setImageUrl(R.id.devices_xxz_img, jsonCar.getDrivingLicensePhotoUrl());
////                setImageUrl(R.id.devices_clzp_img, jsonCar.getFrontalPhotoUrl1());
////                setImageUrl(R.id.devices_clzp_two_img, jsonCar.getFrontalPhotoUrl2());
//
//                setTextView(R.id.detail_car_type, jsonCar.getType());
//                setTextView(R.id.detail_car_length, jsonCar.getLength());
//                setTextView(R.id.detail_car_weight, jsonCar.getCapacity());
////                setTextView(R.id.detail_car_status, jsonCar.getSituation());
//                setTextView(R.id.detail_car_location, jsonCar.getUsualResidence());
//                setTextView(R.id.detail_car_num, jsonCar.getNumber());
//
//                requestJsonCar = jsonCar;
//            }
        }

//        //默认位置
//        String locStr = getStringFromTextView(R.id.detail_car_location);
//        if (TextUtils.isEmpty(locStr)) {
//            Location location = User.getInstance().getLocation();
//            if (location != null) {
//                setTextView(R.id.detail_car_location, location.toText());
//            }
//        }

    }

    //初始化 企业相关view
    private void initCompanyView() {
        if (!editable) {
//            findViewById(R.id.detail_submit_bt).setVisibility(View.INVISIBLE);
            findViewById(R.id.photo_name_ed).setEnabled(false);
            findViewById(R.id.photo_address_ed).setEnabled(false);
            findViewById(R.id.detail_submit_bt).setEnabled(false);
            setTextView(R.id.detail_submit_bt, currentStautsStr);
        }
        User user = User.getInstance();
//        setTextView(R.id.photo_name_ed, user.getPhoneNum());
        JsonUser jsonUser = user.getJsonUser();
//        frsfzUrl = jsonUser.getIdCardPhotoUrl();
//        ImageHelper.getInstance().load(frsfzUrl, frsfzImg);
//        setImageUrl(R.id.photo_frsfz_img, frsfzUrl);
        if (user.getJsonTypeEntity() != null) {
            if (user.getUserType() == Type.EnterpriseType) {
                JsonEnterprise enterprise = user.getJsonTypeEntity();
                requestJsonEnterprise = enterprise;
                setTextView(R.id.photo_name_ed, enterprise.getName());

                String address = enterprise.getAddress();
                String[] strings = Tools.splitAddress(address, ",");
                setTextView(R.id.photo_city_tx, strings[0]);
                setTextView(R.id.photo_address_ed, strings[1]);
                yyzzUrl = enterprise.getBusinessLicenseUrl();
//                swdjzUrl = enterprise.getTaxRegistrationCertificateUrl();
                mtzpUrl = enterprise.getPhotoUrl();
            } else if (user.getUserType() == Type.CompanyInformationType) {
                JsonCompany company = user.getJsonTypeEntity();
                requestJsonCompany = company;
                setTextView(R.id.photo_name_ed, company.getName());
                String address = company.getAddress();
                String[] strings = Tools.splitAddress(address, ",");
                setTextView(R.id.photo_city_tx, strings[0]);
                setTextView(R.id.photo_address_ed, strings[1]);
//                setTextView(R.id.photo_address_ed, address);
//                setTextView(R.id.photo_city_tx, company.getArea());
                yyzzUrl = company.getBusinessLicenseUrl();
//                swdjzUrl = company.getTaxRegistrationCertificateUrl();
                mtzpUrl = company.getPhotoUrl();
            }
//            ImageHelper.getInstance().load(yyzzUrl, yyzzImg);
//            ImageHelper.getInstance().load(swdjzUrl, swdjzImg);
//            ImageHelper.getInstance().load(mtzpUrl, mtzpImg);
            setImageUrl(R.id.photo_yyzz_img, yyzzUrl);
//            setImageUrl(R.id.photo_swdjz_img, swdjzUrl);
            setImageUrl(R.id.photo_mtzp_img, mtzpUrl);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean checkStauts() {
        if (!editable) {
            showToast("当前状态不可编辑");
            return true;
        }
        return false;
    }




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
//                        if (requestJsonCar != null) {
//                            requestJsonCar.setUsualResidence(params);
//                        }
                        requestJsonCompany.setArea(params);
                    }
                }).show();
    }


    public void onClick_Photo(View view) {
        if (checkStauts())
            return;
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView userImage, String uploadImgURL) {
                if (userImage != null)
                    switch (userImage.getId()) {
                        case R.id.photo_yyzz_img:
                            yyzzUrl = uploadImgURL;
                            break;
//                        case R.id.photo_frsfz_img:
                        case R.id.driver_certificate_img:
//                            frsfzUrl = uploadImgURL;
                            //行驶证
//                            requestJsonCar.setDrivingLicensePhotoUrl(uploadImgURL);
                            //驾驶证
                            requestJsonDriver.setLicensePhotoUrl(uploadImgURL);
                            requestChangeUserInfo(true, uploadImgURL);
                            break;
//                        case R.id.driver_sfz_sc_img:
//                            //手持身份证
//                            requestChangeUserInfo(false, uploadImgURL);
//                            break;
                        case R.id.photo_mtzp_img:
                            mtzpUrl = uploadImgURL;
                            break;
//                        case R.id.photo_swdjz_img:
//                            swdjzUrl = uploadImgURL;
//                            break;

                    }
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onClick_Back(null);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick_Back(View view) {
        startActivity(HomeActivity.class);
        finish();
    }

    public void onClick_Submit(View view) {
        if (checkStauts())
            return;

        isFirstVerify = User.getInstance().getJsonTypeEntity() == null;
        Type type = User.getInstance().getUserType();
        if (type == Type.EnterpriseType || type == Type.CompanyInformationType) {
            EditText nameEd = (EditText) findViewById(R.id.photo_name_ed);
            EditText addressEd = (EditText) findViewById(R.id.photo_address_ed);
            if (TextUtils.isEmpty(nameEd.getText()) || TextUtils.isEmpty(addressEd.getText())) {
                showToast("请填写" + nameEd.getHint() + "和" + addressEd.getHint());
                return;
            }
            if (Tools.isEmptyStrings(yyzzUrl, mtzpUrl)) {
                showToast("图片信息不完整");
                return;
            }

            String p_str = getStringFromTextView(R.id.photo_city_tx);
            String loc_str = getStringFromTextView(R.id.photo_address_ed);

            if (type == Type.EnterpriseType)
                requestVerifyEnterprise(nameEd.getText().toString(), p_str + "," + loc_str);
            else if (type == Type.CompanyInformationType)
                requestVerifyInformation(nameEd.getText().toString(), p_str + "," + loc_str);
        } else if (type == Type.DriverType) {
            EditText nameEd = (EditText) findViewById(R.id.driver_name_ed);
            EditText phoneEd = (EditText) findViewById(R.id.driver_phone_ed);
            if (TextUtils.isEmpty(nameEd.getText()) || TextUtils.isEmpty(phoneEd.getText())) {
                showToast("请填写" + nameEd.getHint() + "和" + phoneEd.getHint());
                return;
            }

            if (Tools.isEmptyStrings(User.getInstance().getJsonUser().getIdCardPhotoUrl()/*, User.getInstance().getJsonUser().getPersonPhotoUrl()*/)) {
                showToast("图片信息不完整");
                return;
            }

            if (Tools.isEmptyStrings(requestJsonDriver.getLicensePhotoUrl()/*, requestJsonDriver.getSpecialQualificationsPhotoUrl(),*/
//                    requestJsonCar.getDrivingLicensePhotoUrl(), requestJsonCar.getFrontalPhotoUrl1()/*, requestJsonCar.getFrontalPhotoUrl2()*/
                   /* , requestJsonDriver.getPersonLicensePhotoUrl()*/)) {
                showToast("图片信息不完整");
                return;
            }
//            requestJsonCar.setType(getStringFromTextView(R.id.detail_car_type));
//            requestJsonCar.setLength(getStringFromTextView(R.id.detail_car_length));
//            requestJsonCar.setCapacity(getStringFromTextView(R.id.detail_car_weight));
////            requestJsonCar.setSituation(getStringFromTextView(R.id.detail_car_status));
//            requestJsonCar.setUsualResidence(getStringFromTextView(R.id.detail_car_location));
//
//            if (Tools.isEmptyStrings(requestJsonCar.getLength(), requestJsonCar.getCapacity(), requestJsonCar.getType(),
//                    requestJsonCar.getNumber(), requestJsonCar.getUsualResidence())) {
//                showToast("车辆信息不完整");
//                return;
//            }

            requestVerifyDriver();
        }

//        showSuccessDialog();
    }

    private void requestChangeUserInfo(boolean isidcard, String frsfzUrl) {
        JsonUser jsonUser = User.getInstance().getJsonUser();
        if (isidcard)
            jsonUser.setIdCardPhotoUrl(frsfzUrl);
        else
            jsonUser.setPersonPhotoUrl(frsfzUrl);

        HttpHelper.getInstance().put(AppURL.PutUserInfo, String.valueOf(jsonUser.getId()), jsonUser, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("身份证照修改失败");
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("身份证照修改成功");

            }
        });
    }


    private void requestVerifyDriver() {

        requestJsonDriver.setUserId((int) User.getInstance().getId());

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showSuccessDialog();
                //跟新driver信息
                requestJsonDriver = ParseJson_Object(result, JsonDriver.class);
                //// T ODO: 2016-2-26  这里要对添加车辆做判断，如果已经添加过车辆则需要修改车辆信息的接口
//                requestAddCar();

            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail) + str);
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(AppURL.PostVerifyDrivers, requestJsonDriver, callback);
        else
            HttpHelper.getInstance().put(AppURL.PutDrivers + String.valueOf(requestJsonDriver.getId()), requestJsonDriver, callback);
    }


//    private void requestAddCar() {
//        RequestAddCar requestJson = new RequestAddCar();
////        JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
//        requestJson.setDriver(requestJsonDriver);
//        requestJson.setCar(requestJsonCar);
//        requestJsonCar.setDriverId(requestJsonDriver.getId());
//
//        JsonCallback jsonCallback = new JsonCallback() {
//            @Override
//            public void onSuccess(String msg, String result) {
////                requestPublishCar();
////                showToast(getString(R.string.information_upload_fail) + str);
//                //更新用户信息；
//                User.getInstance().requestUserInfo();
//            }
//
//            @Override
//            public void onFailed(String str) {
//                showToast(getString(R.string.information_upload_fail) + str);
//            }
//        };
//        if (isFirstVerify)
//            HttpHelper.getInstance().post(AppURL.PostDriversAddCar, requestJson, jsonCallback);
//        else {
//            HttpHelper.getInstance().post(AppURL.PostDrviersUpdateCar, requestJsonCar, jsonCallback);
//        }
//    }

    private void showSuccessDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.upload_verify_info).setNegativeButton("联系客服", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //todo 拨打客服电话
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(HomeActivity.class);
                        finish();
                    }
                });
                builder.create().show();
            }
        });

    }

    private void requestVerifyInformation(String name, String address) {
//        company = new JsonCompany();
        JsonCompany company = requestJsonCompany;
        company.setId(User.getInstance().getCompanyID());
        company.setUserId((int) User.getInstance().getId());
        company.setName(name);
        company.setAddress(address);
        //坐标
        Location location = User.getInstance().getLocation();
        if (location != null) {
            company.setLatitude(Float.valueOf(location.getLatitude()));
            company.setLongitude(Float.valueOf(location.getLongitude()));
//            company.setArea(location.toText());
        }
        //门头照片
        company.setPhotoUrl(mtzpUrl);
        //营业执照
        company.setBusinessLicenseUrl(yyzzUrl);
        //税务登记证
//        company.setTaxRegistrationCertificateUrl(swdjzUrl);
        //法人照 frsfzUrl

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showSuccessDialog();
                //更新用户信息；
                User.getInstance().requestUserInfo();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(AppURL.PostVerifyCompanies, company, callback);
        else
            HttpHelper.getInstance().put(AppURL.PutCompaniesInfo, String.valueOf(User.getInstance().getCompanyID()), company, callback);
    }

    private void requestVerifyEnterprise(String name, String address) {
        JsonEnterprise enterprise = new JsonEnterprise();
        enterprise.setId(User.getInstance().getEnterpriseID());
        enterprise.setUserId((int) User.getInstance().getId());
        enterprise.setName(name);
        enterprise.setAddress(address);
        //门头照片
        enterprise.setPhotoUrl(mtzpUrl);
        //营业执照
        enterprise.setBusinessLicenseUrl(yyzzUrl);
        //税务登记证
//        enterprise.setTaxRegistrationCertificateUrl(swdjzUrl);
        //法人照 frsfzUrl

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showSuccessDialog();
                //更新用户信息；
                User.getInstance().requestUserInfo();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(AppURL.PostVerifyEnterprises, enterprise, callback);
        else
            HttpHelper.getInstance().put(AppURL.PutEnterprisesInfo, String.valueOf(User.getInstance().getEnterpriseID()), enterprise, callback);
    }


}
