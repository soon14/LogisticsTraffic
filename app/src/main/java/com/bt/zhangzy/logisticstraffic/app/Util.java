package com.bt.zhangzy.logisticstraffic.app;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ZhangZy on 2015/7/22.
 */
public final class Util {

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
