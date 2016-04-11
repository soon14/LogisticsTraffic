package com.zhangzy.base.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ZhangZy on 2016-1-18.
 */
public final class ViewUtils {
    private static final String TAG = ViewUtils.class.getSimpleName();

    /**
     * bitmap 转 uri
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static Uri BitmapToUri(Context context, Bitmap bitmap) {
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
    }

    public static Bitmap UriToBitmap(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    static File BitmapToFile(Bitmap bmp, String path) {
        File file = null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 50;
        OutputStream stream = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
//            获取要保存到的文件的文件流
            stream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
            e.printStackTrace();
        }
//把指定的bitmp压缩到文件中 就是保存在指定文件中 format是文件格式（Bitmap.CompressFormat.JPEG jpeg） quality 是品质（100 就是原质量）
        bmp.compress(format, quality, stream);
        return file;
    }

    public static Bitmap DrawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    /**
     * 返回textview里的text 字段
     *
     * @param textView
     * @return
     */
    public static String getStringFromTextView(TextView textView) {
        if (textView != null) {
            CharSequence text = textView.getText();
            if (!TextUtils.isEmpty(text))
                return text.toString();
        }
        return null;
    }


    /**
     * 设置textView的内容
     *
     * @param textView
     * @param string
     */
    public static boolean setText(TextView textView, String string) {
        if (textView == null)
            return false;
        textView.setText(TextUtils.isEmpty(string) ? "" : string);
        return true;
    }

    /**
     * 找到textview 并设置内容
     *
     * @param group
     * @param id
     * @param string
     */
    public static void setText(View group, int id, String string) {
        if (group == null)
            return;
        View view = group.findViewById(id);
        if (view != null && view instanceof TextView) {
            setText((TextView) view, string);
        }
    }

}
