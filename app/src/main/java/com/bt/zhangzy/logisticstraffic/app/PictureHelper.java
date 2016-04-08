package com.bt.zhangzy.logisticstraffic.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by zhangzy on 15/2/9.
 */
public class PictureHelper {

    private static final String TAG = PictureHelper.class.getSimpleName();
    static final boolean IS_CUT_PHOTO = true;//标记 是否需要切图的步骤

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
            if (croppedFile == null)
                return false;
            // 压缩图片
//            compressImage(croppedFile, 500);
            Bitmap compress = compress(croppedFile.getPath(), 300, 300);
//                imageFile = createImageFile();
            saveBitmap2file(compress, croppedFile.getPath());
            Log.d(TAG, "压缩后图片：" + croppedFile.getPath());
            if (callBack != null) {
                callBack.handlerImage(croppedFile);
                return true;
            }
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
                if (callBack != null) {
                    callBack.handlerImage(photoFile);
                    return true;
                }
            }
            return true;
        } else if (requestCode == SELECT_PHOTO) {// 来自头像页面 从相册返回，去剪裁页面
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
                        callBack.handlerImage(photoFile);
                    }
                }
                return true;
            }
        }
        return false;
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
            croppedFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedFile));
        ctx.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    public File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(AppParams.getInstance().getCacheImageDir());
        Log.i(TAG, "storageDir:" + storageDir.getPath());
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        String imageFileName = "Android_Crop_JPEG";
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

    /**
     * 将图片image压缩成大小为 size的图片（size表示图片大小，单位是KB）
     *
     * @param imageFile 图片资源
     * @param size      图片大小
     * @return Bitmap
     */
    private Bitmap compressImage(File imageFile, int size) {

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int quality = 100;
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while (baos.toByteArray().length / 1024 > size) {
                Log.d(TAG, "当前大小：" + baos.toByteArray().length / 1024);
                // 重置baos即清空baos
                baos.reset();
                // 每次都减少10
                quality -= 10;
                // 这里压缩options%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

            }
            // 把压缩后的数据baos存放到ByteArrayInputStream中
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            // 把ByteArrayInputStream数据生成图片
            bitmap = BitmapFactory.decodeStream(isBm, null, null);

            //替换源文件
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            //            获取要保存到的文件的文件流
//            OutputStream stream = new FileOutputStream(imageFile.getPath());
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
//            stream.close();

            baos.flush();
            baos.close();
        } catch (IOException e) {
            Log.w(TAG, e);
            e.printStackTrace();
        }
        return bitmap;
    }

    static boolean saveBitmap2file(Bitmap bmp, String path) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 50;
        OutputStream stream = null;
        try {
//            获取要保存到的文件的文件流
            stream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
            e.printStackTrace();
        }
//把指定的bitmp压缩到文件中 就是保存在指定文件中 format是文件格式（Bitmap.CompressFormat.JPEG jpeg） quality 是品质（100 就是原质量）
        return bmp.compress(format, quality, stream);
    }

//    public void compress(File imageFile, int size) {
//        ThumbnailUtils.extractThumbnail()
//    }


    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @return
     */
    public Bitmap compress(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
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
