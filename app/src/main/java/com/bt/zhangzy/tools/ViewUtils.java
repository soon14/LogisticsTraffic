package com.bt.zhangzy.tools;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by ZhangZy on 2016-1-18.
 */
public final class ViewUtils {
    private static final String TAG = ViewUtils.class.getSimpleName();


    /**
     * 设置TextView的内容 如果没有内容则跳过；避免报错
     * @param textView
     * @param sequence
     * @return 有没有设置成功
     */
    public static boolean setTextView(TextView textView, CharSequence sequence) {
        if (TextUtils.isEmpty(sequence) || textView == null)
            return false;
        else
            textView.setText(sequence);
        return true;
    }

}
