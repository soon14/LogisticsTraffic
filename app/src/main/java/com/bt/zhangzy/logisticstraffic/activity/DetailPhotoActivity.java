package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.LicenceKeyboardPopupWindow;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.RequestAddCar;
import com.bt.zhangzy.tools.ViewUtils;

/**
 * Created by ZhangZy on 2015/6/25.
 */
public class DetailPhotoActivity extends BaseActivity {


    String yyzzUrl, swdjzUrl, mtzpUrl, frsfzUrl;
    private boolean isFirstVerify = true;
    private boolean editable = true;//标记资料是否可以编辑;

    JsonDriver requestJsonDriver = new JsonDriver();
    JsonCar requestJsonCar = new JsonCar();
    JsonCompany requestJsonCompany = new JsonCompany();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstVerify = User.getInstance().getJsonTypeEntity() == null;
        if (AppParams.DRIVER_APP) {
            setContentView(R.layout.activity_detail_devices);
            initDriverView();
        } else {
            setContentView(R.layout.activity_detail_photo);
            initCompanyView();
        }
        setPageName("详细信息");

    }

    private void initUserStatus(int status) {
//        JsonUser jsonUser = User.getInstance().getJsonUser();
        String tost = null;
        /*用户状态 未审核	-1 已审核	0 冻结	1 删除	2 已付费	3 已提交资料	4*/
        editable = status < 0;
        switch (status) {
            case -1:
                tost = "未审核";
                break;
            case 0:
                tost = "已审核";
                break;
            case 1:
                tost = "冻结";
                break;
            case 2:
                tost = "删除";
                break;
            case 3:
                tost = "已付费";
                break;
            case 4:
                tost = "已提交资料";
                break;
        }
        showToast(tost);
        if (!editable) {
            findViewById(R.id.detail_submit_bt).setVisibility(View.INVISIBLE);
        }
    }

    private void initDriverView() {
        User user = User.getInstance();
        setTextView(R.id.driver_name_ed, user.getUserName());
        setTextView(R.id.driver_phone_ed, user.getPhoneNum());
        JsonUser jsonUser = user.getJsonUser();
        setImageUrl(R.id.driver_sfz_img, jsonUser.getIdCardPhotoUrl());
//        setImageUrl(R.id.devices_jsz_img, jsonUser.getPersonPhotoUrl());
        if (user.getJsonTypeEntity() != null) {
            JsonDriver jsonDriver = user.getJsonTypeEntity();
            initUserStatus(jsonDriver.getStatus());
            setImageUrl(R.id.devices_jsz_img, jsonDriver.getLicensePhotoUrl());
            setImageUrl(R.id.devices_tszz_img, jsonDriver.getSpecialQualificationsPhotoUrl());

            requestJsonDriver = jsonDriver;
            //to do 车的信息更新
            if (user.getJsonCar() != null) {
                JsonCar jsonCar = user.getJsonCar();
                setImageUrl(R.id.devices_xxz_img, jsonCar.getDrivingLicensePhotoUrl());
                setImageUrl(R.id.devices_clzp_img, jsonCar.getFrontalPhotoUrl1());
                setImageUrl(R.id.devices_clzp_two_img, jsonCar.getFrontalPhotoUrl2());

                setTextView(R.id.detail_car_type, jsonCar.getType());
                setTextView(R.id.detail_car_length, jsonCar.getLength());
                setTextView(R.id.detail_car_weight, jsonCar.getCapacity());
//                setTextView(R.id.detail_car_status, jsonCar.getSituation());
                setTextView(R.id.detail_car_location, jsonCar.getUsualResidence());

                requestJsonCar = jsonCar;
            }
        }

    }

    //初始化 企业相关view
    private void initCompanyView() {
        User user = User.getInstance();
        setTextView(R.id.photo_name_ed, user.getPhoneNum());

//        ImageView yyzzImg = (ImageView) findViewById(R.id.photo_yyzz_img);
//        ImageView frsfzImg = (ImageView) findViewById(R.id.photo_frsfz_img);
//        ImageView mtzpImg = (ImageView) findViewById(R.id.photo_mtzp_img);
//        ImageView swdjzImg = (ImageView) findViewById(R.id.photo_swdjz_img);
        JsonUser jsonUser = user.getJsonUser();
        frsfzUrl = jsonUser.getIdCardPhotoUrl();
//        ImageHelper.getInstance().load(frsfzUrl, frsfzImg);
        setImageUrl(R.id.photo_frsfz_img, frsfzUrl);
        if (user.getJsonTypeEntity() != null) {
            if (user.getUserType() == Type.EnterpriseType) {
                JsonEnterprise enterprise = user.getJsonTypeEntity();
                initUserStatus(enterprise.getStatus());
                setTextView(R.id.photo_address_ed, enterprise.getAddress());
                yyzzUrl = enterprise.getBusinessLicenseUrl();
                swdjzUrl = enterprise.getTaxRegistrationCertificateUrl();
                mtzpUrl = enterprise.getPhotoUrl();
            } else if (user.getUserType() == Type.CompanyInformationType) {
                JsonCompany company = user.getJsonTypeEntity();
                requestJsonCompany = company;
                initUserStatus(company.getStatus());
                setTextView(R.id.photo_address_ed, company.getAddress());
                yyzzUrl = company.getBusinessLicenseUrl();
                swdjzUrl = company.getTaxRegistrationCertificateUrl();
                mtzpUrl = company.getPhotoUrl();
            }
//            ImageHelper.getInstance().load(yyzzUrl, yyzzImg);
//            ImageHelper.getInstance().load(swdjzUrl, swdjzImg);
//            ImageHelper.getInstance().load(mtzpUrl, mtzpImg);
            setImageUrl(R.id.photo_yyzz_img, yyzzUrl);
            setImageUrl(R.id.photo_swdjz_img, swdjzUrl);
            setImageUrl(R.id.photo_mtzp_img, mtzpUrl);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_ChangeLength(View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆长度", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
                        setTextView(R.id.detail_car_length, text.toString());
                        requestJsonCar.setLength(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_length_items));
    }

    private boolean checkStauts() {
        if (!editable) {
            showToast("当前状态不可编辑");
            return true;
        }
        return false;
    }

    public void onClick_ChangeWeight(View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆载重", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
                        setTextView(R.id.detail_car_weight, text.toString());
                        requestJsonCar.setCapacity(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_weight_items));
    }

    public void onClick_ChangeType(View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆类型", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
                        setTextView(R.id.detail_car_type, text.toString());
                        requestJsonCar.setType(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_type_items));
    }

    public void onClick_ChangeStatus(View view) {
        if (checkStauts())
            return;
        ChooseItemsDialog.showChooseItemsDialog(this, "请选择车辆状况", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
//                        ((TextView)view).setText(text);
//                        setTextView(R.id.detail_car_status, text.toString());
                        requestJsonCar.setSituation(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.car_status_items));
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
                        if (requestJsonCar != null) {
                            requestJsonCar.setUsualResidence(params);
                        }
                        requestJsonCompany.setArea(params);
                    }
                }).show();
    }

    public void onClick_ShowLicence(View view) {
        if (checkStauts())
            return;
        //虚拟键盘
        LicenceKeyboardPopupWindow.create(this).setListener(new LicenceKeyboardPopupWindow.ConfirmListener() {
            @Override
            public void confirm(String string) {
//                licenceEd.setText(string);
                setTextView(R.id.detail_car_num, string);
                requestJsonCar.setNumber(string);
            }
        }).showAsDropDown(findViewById(R.id.page_top_name), 0, 0);

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
                        case R.id.photo_frsfz_img:
                        case R.id.driver_sfz_img:
                            frsfzUrl = uploadImgURL;
                            requestChangeUserInfo();
                            break;
                        case R.id.photo_mtzp_img:
                            mtzpUrl = uploadImgURL;
                            break;
                        case R.id.photo_swdjz_img:
                            swdjzUrl = uploadImgURL;
                            break;
                        //司机的相关图片
                        case R.id.devices_jsz_img:
                            requestJsonDriver.setLicensePhotoUrl(uploadImgURL);
                            break;
                        case R.id.devices_tszz_img:
                            requestJsonDriver.setSpecialQualificationsPhotoUrl(uploadImgURL);
                            break;
                        case R.id.devices_xxz_img:
                            requestJsonCar.setDrivingLicensePhotoUrl(uploadImgURL);
                            break;
                        case R.id.devices_clzp_img:
                            requestJsonCar.setFrontalPhotoUrl1(uploadImgURL);
                            break;
                        case R.id.devices_clzp_two_img:
                            requestJsonCar.setFrontalPhotoUrl2(uploadImgURL);
                            break;

                    }
            }
        });


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
            if (type == Type.EnterpriseType)
                requestVerifyEnterprise(nameEd.getText().toString(), addressEd.getText().toString());
            else if (type == Type.CompanyInformationType)
                requestVerifyInformation(nameEd.getText().toString(), addressEd.getText().toString());
        } else if (type == Type.DriverType) {
            EditText nameEd = (EditText) findViewById(R.id.driver_name_ed);
            EditText phoneEd = (EditText) findViewById(R.id.driver_phone_ed);
            if (TextUtils.isEmpty(nameEd.getText()) || TextUtils.isEmpty(phoneEd.getText())) {
                showToast("请填写" + nameEd.getHint() + "和" + phoneEd.getHint());
                return;
            }
            requestJsonCar.setType(getStringFromTextView(R.id.detail_car_type));
            requestJsonCar.setLength(getStringFromTextView(R.id.detail_car_length));
            requestJsonCar.setCapacity(getStringFromTextView(R.id.detail_car_weight));
//            requestJsonCar.setSituation(getStringFromTextView(R.id.detail_car_status));
            requestJsonCar.setUsualResidence(getStringFromTextView(R.id.detail_car_location));

            requestVerifyDriver();
        }

//        showSuccessDialog();
    }

    private void requestChangeUserInfo() {
        JsonUser jsonUser = User.getInstance().getJsonUser();
        jsonUser.setIdCardPhotoUrl(frsfzUrl);

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
                //// TODO: 2016-2-26  这里要对添加车辆做判断，如果已经添加过车辆则需要修改车辆信息的接口
                requestAddCar();

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


    private void requestAddCar() {
        RequestAddCar requestJson = new RequestAddCar();
//        JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
        requestJson.setDriver(requestJsonDriver);
        requestJson.setCar(requestJsonCar);
        requestJsonCar.setDriverId(requestJsonDriver.getId());

        JsonCallback jsonCallback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
//                requestPublishCar();
//                showToast(getString(R.string.information_upload_fail) + str);
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail) + str);
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(AppURL.PostDriversAddCar, requestJson, jsonCallback);
        else
            HttpHelper.getInstance().put(AppURL.PutDrviersCar + String.valueOf(requestJsonCar.getId()), requestJson, jsonCallback);
    }

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
        company.setTaxRegistrationCertificateUrl(swdjzUrl);
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
        enterprise.setTaxRegistrationCertificateUrl(swdjzUrl);
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
