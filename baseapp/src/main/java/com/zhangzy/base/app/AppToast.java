package com.zhangzy.base.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * app内的toast的统一管理，显示；
 * 界面统一
 * Created by ZhangZy on 2016-8-19.
 */
public class AppToast {

    final String TAG;
    static AppToast instance = new AppToast();
    Handler handler;

    private AppToast() {
        TAG = getClass().getSimpleName();
        handler = new Handler(Looper.getMainLooper());
    }

    public static AppToast getInstance() {
        return instance;
    }


    Toast toast;//统一管理toast  防止一个页面上多个toast的显示；
    String toastMsg;
    Context context;
    Runnable showToast = new Runnable() {
        @Override
        public void run() {
            showToastOnUI();
        }
    };

    /**
     * 显示提示信息
     *
     * @param msg
     */
    public void showToast(Context context, String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        toastMsg = msg;
        this.context = context;
        Log.d(TAG, "Toast:" + msg);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToastOnUI();
        } else {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
            handler.post(showToast);
        }
    }

    private void showToastOnUI() {
        if (context == null)
            return;
        if (toast == null) {
            toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
        }
//        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(toastMsg);
        toast.show();
    }

}
