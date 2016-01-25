package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.PictureHelper;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.ImageHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonUser;

import java.io.File;

/**
 * Created by ZhangZy on 2015/6/25.
 */
public class DetailPhotoActivity extends BaseActivity {


    private ImageView userImage;
    String yyzzUrl, swdjzUrl, mtzpUrl, frsfzUrl;
    private boolean isFirstVerify = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstVerify = User.getInstance().getJsonTypeEntity() == null;
        if (AppParams.DEVICES_APP) {
            setContentView(R.layout.activity_detail_devices);
            initDriverView();
        } else {
            setContentView(R.layout.activity_detail_photo);
            initCompanyView();
        }
        setPageName("详细信息");


        PictureHelper.getInstance().setCallBack(new PictureHelper.CallBack() {
            @Override
            public void handlerImage(File file) {
                Log.w(TAG, "图片路径：" + file.getAbsolutePath());
                uploadFile(file);

                if (userImage != null) {
                    userImage.setImageURI(Uri.fromFile(file));
//                    userImage.setImageDrawable(Drawable.createFromPath(file.getPath()));
                }
            }

        });
    }

    private void initDriverView() {
        User user = User.getInstance();
        setTextView(R.id.devices_name_ed, user.getUserName());
        setTextView(R.id.devices_phone_ed, user.getPhoneNum());
        JsonUser jsonUser = user.getJsonUser();
        setImageUrl(R.id.devices_sfz_img, jsonUser.getIdCardPhotoUrl());
//        setImageUrl(R.id.devices_jsz_img, jsonUser.getPersonPhotoUrl());
        if(user.getJsonTypeEntity() != null){
            JsonDriver jsonDriver = user.getJsonTypeEntity();
            setImageUrl(R.id.devices_jsz_img,jsonDriver.getLicensePhotoUrl());
            setImageUrl(R.id.devices_tszz_img,jsonDriver.getSpecial_qualifications_photo_url());

            requestJsonDriver = jsonDriver;
            //todo 车的信息更新
        }

    }

    //初始化 企业相关view
    private void initCompanyView() {
        User user = User.getInstance();
        setTextView(R.id.photo_address_ed, user.getAddress());
        setTextView(R.id.photo_name_ed, user.getPhoneNum());

        ImageView yyzzImg = (ImageView) findViewById(R.id.photo_yyzz_img);
        ImageView frsfzImg = (ImageView) findViewById(R.id.photo_frsfz_img);
        ImageView mtzpImg = (ImageView) findViewById(R.id.photo_mtzp_img);
        ImageView swdjzImg = (ImageView) findViewById(R.id.photo_swdjz_img);
        JsonUser jsonUser = user.getJsonUser();
        frsfzUrl = jsonUser.getIdCardPhotoUrl();
        ImageHelper.getInstance().load(frsfzUrl, frsfzImg);

        if (user.getUserType() == Type.EnterpriseType) {
            JsonEnterprise enterprise = user.getJsonTypeEntity();
            yyzzUrl = enterprise.getBusinessLicenseUrl();
            swdjzUrl = enterprise.getTaxRegistrationCertificateUrl();
            mtzpUrl = enterprise.getPhotoUrl();
        } else if (user.getUserType() == Type.InformationType) {
            JsonCompany company = user.getJsonTypeEntity();
            yyzzUrl = company.getBusinessLicenseUrl();
            swdjzUrl = company.getTaxRegistrationCertificateUrl();
            mtzpUrl = company.getPhotoUrl();
        }
        ImageHelper.getInstance().load(yyzzUrl, yyzzImg);
        ImageHelper.getInstance().load(swdjzUrl, swdjzImg);
        ImageHelper.getInstance().load(mtzpUrl, mtzpImg);
    }


    private void uploadFile(File file) {
        showProgress("图片上传中...");
        //  照片上传逻辑
//        UploadFileTask task = new UploadFileTask(Url.UpLoadImage);
//        task.execute(file);
        JsonCallback rspCallback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("图片上传成功");
                String uploadImgURL = Url.Host + result;
                ImageHelper.getInstance().loadImgOnUiThread(getActivity(), uploadImgURL, userImage);
                if (userImage != null)
                    switch (userImage.getId()) {
                        case R.id.photo_yyzz_img:
                            yyzzUrl = uploadImgURL;
                            break;
                        case R.id.photo_frsfz_img:
                        case R.id.devices_sfz_img:
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
                            requestJsonDriver.setSpecial_qualifications_photo_url(uploadImgURL);
                            break;
                        case R.id.devices_xxz_img:
                            requestJsonCar.setDriving_license_photo_url(uploadImgURL);
                            break;
                        case R.id.devices_clzp_img:
                            requestJsonCar.setFrontal_photo_url_1(uploadImgURL);
                            break;
                        case R.id.devices_clzp_two_img:
                            requestJsonCar.setFrontal_photo_url_2(uploadImgURL);
                            break;

                    }
                cancelProgress();
            }

            @Override
            public void onFailed(String str) {
                showToast("图片上传失败：" + str);
                cancelProgress();
            }
        };
//        HttpHelper.getInstance().postImage(Url.UpLoadImage, file, rspCallback);

        HttpHelper.uploadImagePost(Url.UpLoadImage, file, rspCallback);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PictureHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_Submit(View view) {

        isFirstVerify = User.getInstance().getJsonTypeEntity() == null;
        Type type = User.getInstance().getUserType();
        if (type == Type.EnterpriseType || type == Type.InformationType) {
            EditText nameEd = (EditText) findViewById(R.id.photo_name_ed);
            EditText addressEd = (EditText) findViewById(R.id.photo_address_ed);
            if (TextUtils.isEmpty(nameEd.getText()) || TextUtils.isEmpty(addressEd.getText())) {
                showToast("请填写" + nameEd.getHint() + "和" + addressEd.getHint());
                return;
            }
            if (type == Type.EnterpriseType)
                requestVerifyEnterprise(nameEd.getText().toString(), addressEd.getText().toString());
            else if (type == Type.InformationType)
                requestVerifyInformation(nameEd.getText().toString(), addressEd.getText().toString());
        } else if (type == Type.DriverType) {
            EditText nameEd = (EditText) findViewById(R.id.devices_name_ed);
            EditText phoneEd = (EditText) findViewById(R.id.devices_phone_ed);
            if (TextUtils.isEmpty(nameEd.getText()) || TextUtils.isEmpty(phoneEd.getText())) {
                showToast("请填写" + nameEd.getHint() + "和" + phoneEd.getHint());
                return;
            }
            requestVerifyDriver(nameEd.getText().toString(), phoneEd.getText().toString());
        }

//        showSuccessDialog();
    }

    private void requestChangeUserInfo() {
        JsonUser jsonUser = User.getInstance().getJsonUser();
        jsonUser.setIdCardPhotoUrl(frsfzUrl);

        HttpHelper.getInstance().put(Url.PutUserInfo + jsonUser.getId(), jsonUser, new JsonCallback() {
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

    JsonDriver requestJsonDriver = new JsonDriver();
    JsonCar requestJsonCar = new JsonCar();

    private void requestVerifyDriver(String name, String phoneNum) {

        requestJsonDriver.setUserId((int) User.getInstance().getId());

        JsonCallback callback = new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showSuccessDialog();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(Url.PostVerifyDrivers, requestJsonDriver, callback);
        else
            HttpHelper.getInstance().put(Url.PutDrivers, requestJsonDriver, callback);
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
        JsonCompany company = new JsonCompany();
        company.setId(User.getInstance().getCompanyID());
        company.setUserId((int) User.getInstance().getId());
        company.setName(name);
        company.setAddress(address);
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
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(Url.PostVerifyCompanies, company, callback);
        else
            HttpHelper.getInstance().put(Url.PutCompaniesInfo + User.getInstance().getCompanyID(), company, callback);
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
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        };
        if (isFirstVerify)
            HttpHelper.getInstance().post(Url.PostVerifyEnterprises, enterprise, callback);
        else
            HttpHelper.getInstance().put(Url.PutEnterprisesInfo + User.getInstance().getEnterpriseID(), enterprise, callback);
    }

    public void onClick_Photo(View view) {
        if (view instanceof ImageView)
            userImage = (ImageView) view;

        new AlertDialog.Builder(getActivity()).setTitle("请选择路径").setItems(new String[]{"去图库选择", "启动相机"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    PictureHelper.getInstance().pickFromGallery(getActivity());
                } else if (which == 1) {
                    PictureHelper.getInstance().startCamera(getActivity());
                }
            }
        }).create().show();
//        BaseDialog.showChooseItemsDialog(this, "照片选择", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.dialog_choose_item_1) {
//                    PictureHelper.getInstance().pickFromGallery(getActivity());
//                } else if (v.getId() == R.id.dialog_choose_item_2) {
//                    PictureHelper.getInstance().startCamera(getActivity());
//                }
//            }
//        }, "去图库选择", "启动相机");

    }


}
