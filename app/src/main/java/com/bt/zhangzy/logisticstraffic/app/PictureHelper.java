package com.bt.zhangzy.logisticstraffic.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by zhangzy on 15/2/9.
 */
public class PictureHelper {

    private static final String TAG = PictureHelper.class.getSimpleName();
    static final boolean IS_CUT_PHOTO = false;//标记 是否需要切图的步骤

    public interface CallBack {
        void handlerImage(File file);
    }

    static final int REQUEST_TAKE_PHOTO = 1;// 去照相
    static final int SELECT_PHOTO = 2; // 去选择照片
    static final int PHOTO_REQUEST_CUT = 3;// 去裁剪照片
    private File croppedFile;
    private File photoFile;

    private CallBack callBack;
    static PictureHelper instance = new PictureHelper();

    private PictureHelper() {

    }

    public static PictureHelper getInstance() {
        return instance;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public boolean onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪裁页面返回
            return callback(activity, croppedFile);
//            String path = galleryManager.selectImage(data);
//            connectUpLoadImage(path);
            //头像上传成功后在设置图像
//            if(userImage !=null){
//                userImage.setImageURI(Uri.fromFile(new File(path)));
//            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (IS_CUT_PHOTO) {
                // 从照相机返回，去剪裁页面
                cropPicture(activity, Uri.fromFile(photoFile));
            } else {
                return callback(activity, photoFile);
            }
            return true;
        } else if (requestCode == SELECT_PHOTO) {// 从相册返回，去剪裁页面
            if (data == null) {
                return false;
            } else {
                Uri selectedImage = data.getData();
                if (IS_CUT_PHOTO) {
                    cropPicture(activity, selectedImage);
                } else {
                    if (callBack != null) {
                        try {
                            photoFile = new File(new URI(selectedImage.toString()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            Log.w(TAG, e);
                            photoFile = new File(getRealPathFromURI(activity, selectedImage));
                        }
                        return callback(activity, photoFile);
//                        callBack.handlerImage(photoFile);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean callback(Context context, File file) {
        if (file == null)
            return false;
        if (callBack != null) {
            callBack.handlerImage(file);
        }

        return true;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    // 去剪裁图片
    public void cropPicture(Activity ctx, Uri input) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(input, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        /*在很多博客中都把“return-data”设置为了true然后在onActivityResult中通过data.getParcelableExtra("data")来获取数据，不过这样的话dp这个变量的值就不能太大了，不然你的程序就挂了。这里也就是我遇到问题的地方了，在大多数高配手机上这样用是没有问题的，不过很多低配手机就有点hold不住了，直接就异常了，包括我们的国产神机米3也没能hold住，所以我建议大家不要通过return data 大数据，小数据还是没有问题的，说以我们在剪切图片的时候就尽量使用Uri这个东东来帮助我们。*/
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        try {
            croppedFile = AppParams.getInstance().createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedFile));
        ctx.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }




    /**
     * 启动相机
     *
     * @param ctx
     */
    public void startCamera(Activity ctx) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(ctx.getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = AppParams.getInstance().createImageFile();
            } catch (IOException ex) {
//                CommonUtility.showMiddleToast(this, "create file failed");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //下面这句指定调用相机拍照后的照片存储的路径
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                ctx.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }


    /**
     * 去图库挑选
     *
     * @param activity
     */
    public void pickFromGallery(Activity activity) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }







   /* public void startImg(){
        Intent intent = new Intent();
                *//* 开启Pictures画面Type设定为image *//*
        intent.setType("image*//*");
                *//* 使用Intent.ACTION_GET_CONTENT这个Action *//*
        intent.setAction(Intent.ACTION_GET_CONTENT);
                *//* 取得相片后返回本画面 *//*
        startActivityForResult(intent, 1);
    }*/


   /* public static void setImageViewCircleBorder(View view){
        if(view != null){
            if(view instanceof ImageViewCircle){
                ImageViewCircle circle = (ImageViewCircle) view;
                circle.setBorderColor(0x33FFFFFF);
                circle.setBorderWidth(6);
            }
        }
    }*/
}
