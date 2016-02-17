package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * Created by ZhangZy on 2016-2-17.
 */
public class ConfirmDialog extends BaseDialog {

    TextView message;
    Button confirmBt, cancelBt;

    public ConfirmDialog(Activity context) {
        super(context);
        setView(R.layout.dialog_confirm);
        message = (TextView) findViewById(R.id.confirm_dl_msg_tx);
        confirmBt = (Button) findViewById(R.id.dl_confirm_bt);
        cancelBt = (Button) findViewById(R.id.dl_cancel_bt);
        cancelBt.setOnClickListener(this);
        confirmBt.setOnClickListener(this);
    }

    public ConfirmDialog setMessage(String msg) {
        message.setText(msg);
        return this;
    }

    public ConfirmDialog setConfirm(String str) {
        if (!TextUtils.isEmpty(str))
            confirmBt.setText(str);
        return this;
    }

    public ConfirmDialog setCancel(String str) {
        if (!TextUtils.isEmpty(str))
            cancelBt.setText(str);
        return this;
    }

    public ConfirmDialog setConfirmListener(View.OnClickListener listener) {
        setOnClickListener(R.id.dl_confirm_bt, listener);
        return this;
    }

    public ConfirmDialog setCancelListener(View.OnClickListener listener) {
        setOnClickListener(R.id.dl_cancel_bt, listener);
        return this;
    }


    /**
     * 常用对话框方法封装  二次确认对话框
     *
     * @param act      上下文
     * @param msg      提示内容
     * @param listener 确认按钮执行的事件
     */
    public static void showConfirmDialog(Activity act, String msg, View.OnClickListener listener) {
        showConfirmDialog(act, msg, null, null, listener);
    }

    /**
     * 常用对话框方法封装  二次确认对话框
     *
     * @param act      上下文
     * @param msg      提示内容
     * @param cancel   取消按钮描述
     * @param confirm  确认按钮描述
     * @param listener 执行事件
     */
    public static void showConfirmDialog(Activity act, String msg, String cancel, String confirm, View.OnClickListener listener) {

        new ConfirmDialog(act)
                .setMessage(msg)
                .setConfirm(confirm)
                .setCancel(cancel)
                .setConfirmListener(listener)
                .show();
//        BaseDialog dialog = new BaseDialog(act);
//        dialog.setView(R.layout.dialog_confirm);
//        dialog.setTextView(R.id.confirm_dl_msg_tx, msg);
//        if (!TextUtils.isEmpty(cancel)) {
//            dialog.setTextView(R.id.dl_cancel_bt, cancel);
//        }
//        if (!TextUtils.isEmpty(confirm)) {
//            dialog.setTextView(R.id.dl_confirm_bt, confirm);
//        }
//        dialog.setOnClickListener(R.id.dl_cancel_bt, null).setOnClickListener(R.id.dl_confirm_bt, listener);
//        dialog.show();
    }
}
