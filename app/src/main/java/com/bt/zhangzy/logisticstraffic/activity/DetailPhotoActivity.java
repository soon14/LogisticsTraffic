package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.app.PictureHelper;
import com.bt.zhangzy.network.HttpHelper;

import java.io.File;

/**
 * Created by ZhangZy on 2015/6/25.
 */
public class DetailPhotoActivity extends BaseActivity {


    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DEVICES_APP) {
            setContentView(R.layout.activity_detail_devices);
        } else {
            setContentView(R.layout.activity_detail_photo);
        }
        setPageName("详细信息");

        PictureHelper.getInstance().setCallBack(new PictureHelper.CallBack() {
            @Override
            public void handlerImage(String path) {
                File file = new File(path);
                // todo 照片上传逻辑
//                HttpHelper.getInstance().postFile(url,file,new NetCallback);
                if (userImage != null)
                    userImage.setImageURI(Uri.fromFile(file));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的信息已经上传到后台，需要3-5天进行信息审核，请耐心等待。谢谢使用物流汇。").setNegativeButton("联系客服", null).setPositiveButton("确认", null);
        builder.create().show();
    }

    public void onClick_Photo(View view) {
        if (view instanceof ImageView)
            userImage = (ImageView) view;
        PictureHelper.getInstance().startCamera(this);
    }

}
