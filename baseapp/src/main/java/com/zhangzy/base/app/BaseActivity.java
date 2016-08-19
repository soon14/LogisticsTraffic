package com.zhangzy.base.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zhangzy.base.R;


/**
 * Created by ZhangZy on 2015/6/18.
 */
public class BaseActivity extends Activity {

    public final String TAG;
    protected Context context;



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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
