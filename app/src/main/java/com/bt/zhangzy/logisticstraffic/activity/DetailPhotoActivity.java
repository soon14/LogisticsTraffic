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
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonEnterprise;

import java.io.File;

/**
 * Created by ZhangZy on 2015/6/25.
 */
public class DetailPhotoActivity extends BaseActivity {


    private ImageView userImage;
    String yyzzUrl, swdjzUrl, mtzpUrl, frsfzUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppParams.DEVICES_APP) {
            setContentView(R.layout.activity_detail_devices);
        } else {
            setContentView(R.layout.activity_detail_photo);
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


    private void uploadFile(File file) {
        showProgress("图片上传中...");
        //  照片上传逻辑
//                UploadFileTask task = new UploadFileTask(DetailPhotoActivity.this, Url.UpLoadImage);
//                task.execute(file);
        HttpHelper.getInstance().postImage(Url.UpLoadImage, file, new JsonCallback() {
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
                            frsfzUrl = uploadImgURL;
                            break;
                        case R.id.photo_mtzp_img:
                            mtzpUrl = uploadImgURL;
                            break;
                        case R.id.photo_swdjz_img:
                            swdjzUrl = uploadImgURL;
                            break;

                    }
                cancelProgress();
            }

            @Override
            public void onFailed(String str) {
                showToast("图片上传失败：" + str);
                cancelProgress();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PictureHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_Submit(View view) {
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
        }else if(type == Type.DriverType){
            EditText nameEd = (EditText) findViewById(R.id.devices_name_ed);
            EditText phoneEd = (EditText) findViewById(R.id.devices_phone_ed);
            requestVerifyDriver();
        }

//        showSuccessDialog();
    }

    private void requestVerifyDriver() {
        JsonDriver driver = new JsonDriver();

        HttpHelper.getInstance().post(Url.VerifyDrivers, driver, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.upload_verify_info).setNegativeButton("联系客服", null).setPositiveButton("确认", null);
        builder.create().show();
    }

    private void requestVerifyInformation(String name, String address) {
        JsonCompany company = new JsonCompany();
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

        HttpHelper.getInstance().post(Url.VerifyCompanies, company, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showSuccessDialog();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        });
    }

    private void requestVerifyEnterprise(String name, String address) {
        JsonEnterprise enterprise = new JsonEnterprise();
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

        HttpHelper.getInstance().post(Url.VerifyEnterprises, enterprise, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showSuccessDialog();
            }

            @Override
            public void onFailed(String str) {
                showToast(getString(R.string.information_upload_fail));
            }
        });
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
