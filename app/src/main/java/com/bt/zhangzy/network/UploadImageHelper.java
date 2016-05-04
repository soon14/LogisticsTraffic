package com.bt.zhangzy.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.PictureHelper;
import com.bt.zhangzy.tools.ViewUtils;

import java.io.File;
import java.io.IOException;

/**
 * 负责处理图片的选择 和 上传 功能
 * Created by ZhangZy on 2016-3-9.
 */
public class UploadImageHelper {
    private static final String TAG = UploadImageHelper.class.getSimpleName();
    static UploadImageHelper instance = new UploadImageHelper();
    private ImageView userImage;
    private BaseActivity activity;
    private Listener listener;

    private UploadImageHelper() {
        PictureHelper.getInstance().setCallBack(new PictureHelper.CallBack() {
            @Override
            public void handlerImage(File file) {
                Log.w(TAG, "图片路径：" + file.getAbsolutePath());
                uploadFile(file);

                if (userImage != null) {
                    userImage.setImageURI(Uri.fromFile(file));
//                    userImage.setImageURI(Uri.parse(file.getAbsolutePath()));

//                    userImage.setImageDrawable(Drawable.createFromPath(file.getPath()));
                }
            }

        });
    }

    public static UploadImageHelper getInstance() {
        return instance;
    }

    //activity 回调
    public boolean onActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
        return PictureHelper.getInstance().onActivityResult(act, requestCode, resultCode, data);
    }

    JsonCallback rspCallback = new JsonCallback() {
        @Override
        public void onSuccess(String msg, String result) {
            if (activity != null) {
                activity.showToast("图片上传成功" + msg);
                activity.cancelProgress();
            }
            final String uploadImgURL = /*AppURL.HostDebug + */result;
            Log.i(TAG, "上传图片地址：" + uploadImgURL);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtils.setImageUrl(userImage, uploadImgURL);
                    }
                });
            }
            if (listener != null) {
                listener.handler(userImage, uploadImgURL);
            }
        }

        @Override
        public void onFailed(String str) {
            if (activity != null) {
                activity.showToast("图片上传失败：" + str);
                activity.cancelProgress();
            }
        }
    };

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void uploadFile(File file) {
        if (activity != null)
            activity.showProgress("图片上传中...");
        //图片上传不支持中文文件名
        // 这里做重命名操作
        Log.d(TAG, "old file name = " + file.getName() + "  path=" + file.getAbsolutePath());
        File newPath = null;//new File(file, "sys" + System.currentTimeMillis());
        try {
            newPath = PictureHelper.getInstance().createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.renameTo(newPath);
        Log.d(TAG, "newPath file name = " + newPath.getName() + "  path=" + newPath.getAbsolutePath() + " exists=" + newPath.exists());
        //  照片上传逻辑
//        UploadFileTask task = new UploadFileTask(AppURL.UpLoadImage);
//        task.execute(file);

//        HttpHelper.getInstance().postImage(AppURL.UpLoadImage, file, rspCallback);

        HttpHelper.uploadImagePost(AppURL.UpLoadImage, newPath, rspCallback);
    }

    @NonNull
    public void onClick_Photo(BaseActivity act, View view, Listener listener) {
        if (view instanceof ImageView)
            userImage = (ImageView) view;
        else {
            Log.d(TAG, "not ImageView");
            return;
        }

        activity = act;
        this.listener = listener;
        new AlertDialog.Builder(act).setTitle("请选择路径").setItems(new String[]{"去图库选择", "启动相机"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    PictureHelper.getInstance().pickFromGallery(activity);
                } else if (which == 1) {
                    PictureHelper.getInstance().startCamera(activity);
                }
            }
        }).create().show();
        return;
    }

    public interface Listener {
        void handler(ImageView imageView, String url);
    }
}
