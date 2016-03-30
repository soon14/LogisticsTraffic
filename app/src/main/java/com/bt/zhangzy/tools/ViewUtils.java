package com.bt.zhangzy.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.network.AppURL;

/**
 * Created by ZhangZy on 2016-1-18.
 */
public final class ViewUtils {
    private static final String TAG = ViewUtils.class.getSimpleName();

    public static Bitmap drawableToBitmap(Drawable drawable) {

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
     * 设置网络图片
     * 做一些基本判断
     *
     * @param url
     */
    public static void setImageUrl(ImageView img, String url) {
        if (img == null)
            return;
        if (TextUtils.isEmpty(url)) {
            ImageHelper.getInstance().load(null, img);
        } else {
            if (url.startsWith("/")) {
                url = AppURL.Host + url;
            }
            if (url.startsWith("http://")) {
                ImageHelper.getInstance().load(url, img);
            }
        }
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
