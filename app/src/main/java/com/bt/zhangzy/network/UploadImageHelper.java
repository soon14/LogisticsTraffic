package com.bt.zhangzy.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.PictureHelper;
import com.bt.zhangzy.tools.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        Log.i(TAG, "创建压缩图片任务");
//        final CustomProgress customProgress = CustomProgress.show(activity, "图片压缩中···");
        new AsyncTask<File, String, File>() {

            @Override
            protected File doInBackground(File... params) {
                File file = params[0];
                Log.d(TAG, "压缩前图片：" + file.getPath());
                // 压缩图片
//            compressImage(croppedFile, 500);
//            Bitmap compress = compress(croppedFile.getPath(), 300f, 300f);
//                imageFile = createImageFile();
                file = compressByte(file.getPath(), 500);
                Log.d(TAG, "压缩后图片：" + file.getPath());
                return file;
            }

            @Override
            protected void onPostExecute(File file) {
                if (file != null)
                    HttpHelper.uploadImagePost(AppURL.UpLoadImage, file, rspCallback);
//                super.onPostExecute(file);
//                customProgress.cancel();
//                if (callBack != null) {
//                    callBack.handlerImage(file);
//                }
            }
        }.execute(file);
        //图片上传不支持中文文件名
        // 这里做重命名操作
//        Log.d(TAG, "old file name = " + file.getName() + "  path=" + file.getAbsolutePath());
//        File newPath = null;//new File(file, "sys" + System.currentTimeMillis());
//        try {
//            newPath = AppParams.getInstance().createImageFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        file.renameTo(newPath);//这里会移动文件 抛弃此方法
//        Log.d(TAG, "newPath file name = " + newPath.getName() + "  path=" + newPath.getAbsolutePath() + " exists=" + newPath.exists());
        //  照片上传逻辑
//        UploadFileTask task = new UploadFileTask(AppURL.UpLoadImage);
//        task.execute(file);

//        HttpHelper.getInstance().postImage(AppURL.UpLoadImage, file, rspCallback);

//        HttpHelper.uploadImagePost(AppURL.UpLoadImage, newPath, rspCallback);
    }

    /**
     * 压缩图片质量
     *
     * @param imgPath 源文件路径
     * @param size    压缩目标大小 单位：Kb
     * @return
     */
    private File compressByte(String imgPath, int size) {
        try {
            Bitmap bitmap = readFile(imgPath);

//            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imgPath),null,opts);
            Log.d(TAG, "读取图片:" + imgPath + "->" + bitmap + " 目标压缩大小：" + size);
            if (bitmap == null) {
                Log.w(TAG, "压缩图片失败");
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int quality = 110;
            int currentSize;
            do {
                // 重置baos即清空baos
                baos.reset();
                // 每次都减少10
                if (quality > 10)
                    quality -= 10;
                else
                    quality -= 1;
                if (quality < 5) {
                    break;
                }
                // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                // 这里压缩options%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                currentSize = baos.toByteArray().length / 1024;
                Log.d(TAG, "当前大小：" + currentSize + "->" + quality);

                // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            } while (currentSize > size);
            baos.close();
            //新建一个文件 用于存放压缩后的图片
            File file = AppParams.getInstance().createImageFile();// 去掉创建图片  改为替换原有的图片
            OutputStream outputStream = new FileOutputStream(file);
            quality = Math.max(5, quality);
            Log.d(TAG, "最终压缩比率：" + quality);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream))
                Log.d(TAG, "写入压缩文件成功");
            outputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, e);
        }
        Log.d(TAG, "压缩失败");
        return null;
    }

    /**
     * 读取文件
     *
     * @param imgPath
     * @return
     */
    private Bitmap readFile(String imgPath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);
        if (bitmap == null) {
            opts.inJustDecodeBounds = false;
            try {
                bitmap = BitmapFactory.decodeFile(imgPath, opts);
            } catch (OutOfMemoryError e) {
                //如果内存不够用 则压缩图片
                opts.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath, opts);
            }
        }
        return bitmap;
    }


    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param out_w   target pixel of width
     * @param out_h   target pixel of height
     * @return
     */
    public Bitmap compress(String imgPath, float out_w, float out_h) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        if (newOpts.outWidth < 0 || newOpts.outHeight < 0) {
            Log.d(TAG, "边界读取失败!");
            newOpts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        }

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.d(TAG, "原始尺寸：" + w + "-" + h + " --> " + out_w + "-" + out_h);

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w >= h && w > out_w) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / out_w);
        } else if (w < h && h > out_h) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / out_h);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        Log.d(TAG, "inSampleSize=" + newOpts.inSampleSize);
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        Log.d(TAG, "压缩后尺寸：" + newOpts.outWidth + "-" + newOpts.outHeight + " --> " + out_w + "-" + out_h);
        if (bitmap == null)
            Log.w(TAG, "图片压缩失败");

        try {
            OutputStream outputStream = new FileOutputStream(imgPath);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream))
                Log.d(TAG, "覆盖源文件-成功");
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
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
