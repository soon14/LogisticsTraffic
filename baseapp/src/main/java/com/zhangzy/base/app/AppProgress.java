package com.zhangzy.base.app;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.zhangzy.base.view.CustomProgress;

/**
 * 统一管理进度条；
 * 单例模式
 * Created by ZhangZy on 2016-8-19.
 */
public class AppProgress {
    final static String TAG = AppProgress.class.getSimpleName();
    static AppProgress instance = new AppProgress();
    Handler handler;

    private AppProgress() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static AppProgress getInstance() {
        return instance;
    }

    CustomProgress progress;

    /**
     * 显示进度条 用户不可取消
     *
     * @param msg
     */
    public void showProgress(Activity context, CharSequence msg) {
        if (progress == null) {
            progress = CustomProgress.show(context, msg);
        } else {
            progress.setMessage(msg);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                progress.show();
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.show();
                    }
                });
            }
        }
    }


    /**
     * 取消精度条
     */
    public void cancelProgress() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progress == null)
                    return;
                progress.cancel();
            }
        });

    }
}
