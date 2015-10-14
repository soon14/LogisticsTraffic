package com.bt.zhangzy.logisticstraffic.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangzy on 15/2/9.
 */
public class PictureHelper {

    private static final String TAG = PictureHelper.class.getSimpleName();

    public interface CallBack {
        public void handlerImage(String path);
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
            if (data == null || croppedFile == null)
                return false;
            if (callBack != null) {
                callBack.handlerImage(croppedFile.getPath());
                return true;
            }
//            String path = galleryManager.selectImage(data);
//            connectUpLoadImage(path);
            //头像上传成功后在设置图像
//            if(userImage !=null){
//                userImage.setImageURI(Uri.fromFile(new File(path)));
//            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            // 从照相机返回，去剪裁页面
            cropPicture(activity, Uri.fromFile(photoFile));
            return true;
        } else if (requestCode == SELECT_PHOTO) {// 来自头像页面 从相册返回，去剪裁页面
            if (data == null) {
                return false;
            } else {
                Uri selectedImage = data.getData();
                cropPicture(activity, selectedImage);
                return true;
            }
        }
        return false;
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
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        try {
            croppedFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedFile));
        ctx.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.i(TAG, "storageDir:" + storageDir.getPath());
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // / mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;

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
                photoFile = createImageFile();
            } catch (IOException ex) {
//                CommonUtility.showMiddleToast(this, "create file failed");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
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
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }


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
