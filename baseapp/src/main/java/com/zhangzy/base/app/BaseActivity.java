package com.zhangzy.base.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangzy.base.R;
import com.zhangzy.base.view.CustomProgress;


/**
 * Created by ZhangZy on 2015/6/18.
 */
public class BaseActivity extends Activity {

    public final String TAG;
    protected Context context;

    CustomProgress progress;
    Toast toast;//统一管理toast  防止一个页面上多个toast的显示；
    String toastMsg;


    protected BaseActivity() {
        TAG = getClass().getSimpleName();
        context = this;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //避免弹出的键盘遮挡输入框的问题
        //这样会让屏幕整体上移。如果加上的 是 android:windowSoftInputMode="adjustPan"这样键盘就会覆盖屏幕。
        //AndroidManifest.xml的Activity设置属性：android:windowSoftInputMode = "adjustResize"
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 显示进度条 用户不可取消
     *
     * @param msg
     */
    public void showProgress(CharSequence msg) {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress(msg);
            }
        });
    }

    /**
     * 取消精度条
     */
    public void cancelProgress() {
        if (progress == null)
            return;
        progress.cancel();
    }

    public BaseActivity getActivity() {
        return this;
    }


    /**
     * 返回页面上一个TextView的内容
     *
     * @param id
     * @return
     */
    protected String getStringFromTextView(int id) {
        View view = findViewById(id);
        if (view != null && view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (!TextUtils.isEmpty(text))
                return text.toString();
        }
        return null;
    }

    /**
     * 设置页面内容
     *
     * @param id     内容标识
     * @param string 内容
     */
    protected void setTextView(int id, String string) {
        View viewById = findViewById(id);
        if (viewById == null || !(viewById instanceof TextView))
            return;
        TextView tx = (TextView) viewById;

        if (TextUtils.isEmpty(string))
            tx.setText("");
        else
            tx.setText(string);
    }


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
    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        toastMsg = msg;
        Log.d(TAG, "Toast:" + msg);
        if (Looper.myLooper() == getMainLooper()) {
            showToastOnUI();
        } else {
            runOnUiThread(showToast);
        }
    }

    private void showToastOnUI() {
        if (toast == null) {
            toast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
        } else {
//            toast.cancel();
        }
//        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(toastMsg);
        toast.show();
    }


    public void onClick_Back(View view) {
        finish();
    }

    /**
     * activity 跳转封装
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * activity 跳转封装 带参数
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        startActivity(cls, bundle, false);
    }

    public void startActivity(Class<?> cls, Bundle bundle, boolean istop) {
        startActivity(cls, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP, istop);
    }

    public void startNewActivity(Class<?> cls, Bundle bundle) {
        startActivity(cls, bundle, Intent.FLAG_ACTIVITY_NEW_TASK, false);
    }

    private void startActivity(Class<?> cls, Bundle bundle, int flags, boolean istop) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(flags);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (istop) {
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_stop);
        } else {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    /**
     * 跳转封装
     *
     * @param cls         跳转目标
     * @param bundle      请求参数
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
