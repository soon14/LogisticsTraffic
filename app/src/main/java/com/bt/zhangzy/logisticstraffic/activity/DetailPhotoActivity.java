package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.PictureHelper;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.ImageHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;

import java.io.File;

/**
 * Created by ZhangZy on 2015/6/25.
 */
public class DetailPhotoActivity extends BaseActivity {


    private ImageView userImage;

    ImageView loadImage;

    String uploadImgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppParams.DEVICES_APP) {
            setContentView(R.layout.activity_detail_devices);
        } else {
            setContentView(R.layout.activity_detail_photo);
            loadImage = (ImageView) findViewById(R.id.load_img);
        }
        setPageName("详细信息");


        PictureHelper.getInstance().setCallBack(new PictureHelper.CallBack() {
            @Override
            public void handlerImage(File file) {
                Log.w(TAG, "图片路径：" + file.getAbsolutePath());
                uploadFile(file);


                if (userImage != null) {
//                    userImage.setImageURI(Uri.fromFile(file));

                    userImage.setImageDrawable(Drawable.createFromPath(file.getPath()));
                }
            }

        });
    }

    private void uploadFile(File file) {
        //  照片上传逻辑
//                UploadFileTask task = new UploadFileTask(DetailPhotoActivity.this, Url.UpLoadImage);
//                task.execute(file);
        HttpHelper.getInstance().postImage(Url.UpLoadImage, file, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("图片上传成功");
                uploadImgURL = result;
                loadUrlImg(result);
            }

            @Override
            public void onFailed(String str) {
                showToast("图片上传失败：" + str);
            }
        });
    }

    private void loadUrlImg(final String result) {
        if (!TextUtils.isEmpty(result)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadImage != null) {
                        ImageHelper.getInstance().load(Url.Host + result, loadImage);
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PictureHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_Submit(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的信息已经上传到后台，需要3-5天进行信息审核，请耐心等待。谢谢使用物流汇。").setNegativeButton("联系客服", null).setPositiveButton("确认", null);
        builder.create().show();
    }

    public void onClick_Photo(View view) {
        if (view instanceof ImageView)
            userImage = (ImageView) view;

        BaseDialog.showChooseItemsDialog(this, "照片选择", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.dialog_choose_item_1) {
                    PictureHelper.getInstance().pickFromGallery(DetailPhotoActivity.this);
                } else if (v.getId() == R.id.dialog_choose_item_2) {
                    PictureHelper.getInstance().startCamera(DetailPhotoActivity.this);
                }
            }
        }, "去图库选择", "启动相机");

    }

}
