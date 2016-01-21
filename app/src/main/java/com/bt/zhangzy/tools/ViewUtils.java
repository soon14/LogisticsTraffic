package com.bt.zhangzy.tools;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * 设置textView的内容
     * @param textView
     * @param string
     */
    public static void setText(TextView textView , String string){
        if(textView == null)
            return;
        textView.setText(TextUtils.isEmpty(string)?"":string);
    }

    /**
     * 找到textview 并设置内容
     * @param group
     * @param id
     * @param string
     */
    public static void setText(ViewGroup group ,int id,String string){
        if(group == null)
            return;
        View view = group.findViewById(id);
        if(view !=null && view instanceof  TextView){
            setText((TextView) view,string);
        }
    }

}
