package com.bt.zhangzy.logisticstraffic.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;

import java.util.HashMap;

/**
 * Created by ZhangZy on 2015/7/10.
 */
public class AlertDialogApp extends AlertDialog.Builder implements View.OnClickListener {

    public interface DialogClickListener {
        public void onClick(AlertDialog dialog, View view);
    }

    private HashMap<Integer, DialogClickListener> listenerMap = new HashMap<>();
    private AlertDialog dialog;
    private int layoutId;

    public AlertDialogApp(Context context) {
        super(context);
    }

    public AlertDialogApp(Context context, int layoutId) {
        super(context);
        setView(layoutId);
    }

    /** 常用对话框方法封装  二次确认对话框 */
    public static AlertDialogApp showConfirm(Context context, String msg, DialogClickListener listener) {
        AlertDialogApp dialog = new AlertDialogApp(context, R.layout.dialog_confirm);
        dialog.create().show();
        TextView tx = (TextView) dialog.dialog.findViewById(R.id.confirm_dl_msg_tx);
        tx.setText(msg);
        dialog.addClickListener(R.id.dl_cancel_bt, null).addClickListener(R.id.dl_confirm_bt,listener );
        return dialog;
    }


    @Override
    public AlertDialog.Builder setView(int layoutResId) {
        this.layoutId = layoutResId;
        return this;
    }

    @NonNull
    @Override
    public AlertDialog create() {
        View layout = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setView(layout);
        dialog = super.create();

        View view;
        if (!listenerMap.isEmpty()) {
            for (int id : listenerMap.keySet()) {
                view = dialog.findViewById(id);
                if (view != null) {
                    view.setOnClickListener(this);
                }
            }

        }
        return dialog;
    }


    /**
     * 必须在show方法之后调用  费解
     */
    public AlertDialogApp addClickListener(int id, DialogClickListener listener) {
        listenerMap.put(id, listener);
        if (dialog != null) {
            View view = dialog.findViewById(id);
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
        return this;
    }


    @Override
    public void onClick(View v) {
        for (int id : listenerMap.keySet()) {
            if (v.getId() == id) {
                if (listenerMap.get(id) != null) {
                    listenerMap.get(id).onClick(dialog, v);
                    return;
                }
            }
        }
        dialog.cancel();
    }
}
