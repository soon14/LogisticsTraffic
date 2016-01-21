package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * Created by ZhangZy on 2016-1-21.
 *
 */
@Deprecated
public class WaitingDialog extends ProgressDialog {
    public WaitingDialog(Activity context) {
        super(context);

        setCanceledOnTouchOutside(false);
        // 设置进度条风格，风格为圆形，旋转的
        setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // 设置ProgressDialog 标题
        setTitle("提示");

        // 设置ProgressDialog 提示信息
//        setMessage("这是一个圆形进度条对话框");

        // 设置ProgressDialog 标题图标
        setIcon(R.mipmap.ic_launcher);

        // 设置ProgressDialog 的进度条是否不明确
        setIndeterminate(false);

        // 设置ProgressDialog 是否可以按退回按键取消
        setCancelable(false);

        // 设置ProgressDialog 的一个Button
//        m_pDialog.setButton("确定", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int i)
//            {
//                //点击“确定按钮”取消对话框
//                dialog.cancel();
//            }
//        });
    }


//    public static showProgressDialog(Activity activity){
//        //创建ProgressDialog对象
//        m_pDialog = new ProgressDialog(PDialog.this);
//
//        // 设置进度条风格，风格为圆形，旋转的
//        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//
//        // 设置ProgressDialog 标题
//        m_pDialog.setTitle("提示");
//
//        // 设置ProgressDialog 提示信息
//        m_pDialog.setMessage("这是一个圆形进度条对话框");
//
//        // 设置ProgressDialog 标题图标
//        m_pDialog.setIcon(R.drawable.img1);
//
//        // 设置ProgressDialog 的进度条是否不明确
//        m_pDialog.setIndeterminate(false);
//
//        // 设置ProgressDialog 是否可以按退回按键取消
//        m_pDialog.setCancelable(true);
//
//        // 设置ProgressDialog 的一个Button
//        m_pDialog.setButton("确定", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int i)
//            {
//                //点击“确定按钮”取消对话框
//                dialog.cancel();
//            }
//        });
//
//        // 让ProgressDialog显示
//        m_pDialog.show();
//    }
}
