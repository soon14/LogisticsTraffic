package com.zhangzy.base.app;

import android.content.Context;
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
    public void showProgress(Context context, CharSequence msg) {
        if (progress == null) {
            progress = CustomProgress.show(context, msg);
        } else {
            progress.setMessage(msg);
            progress.show();
        }
    }


    /**
     * 显示进度条 用户不可取消
     * 并且放到UI线程中执行
     *
     * @param msg
     */
    public void showProgressOnUI(final CharSequence msg) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showProgress(msg);
//            }
//        });
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
