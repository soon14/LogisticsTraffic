package com.bt.zhangzy.logisticstraffic.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.activity.DetailCompany;
import com.bt.zhangzy.logisticstraffic.activity.LoginActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.receiver.MessageReceiver;
import com.bt.zhangzy.logisticstraffic.view.CallPhoneDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.tools.ViewUtils;
import com.zhangzy.baidusdk.BaiduMapActivity;
import com.zhangzy.base.app.AppToast;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class BaseActivity extends FragmentActivity {

    public final String TAG;
    protected Context context;


    protected BaseActivity() {
        TAG = getClass().getSimpleName();
        context = this;

        if (TextUtils.isEmpty(User.getInstance().getRegistrationID())) {
            String registrationID = JPushInterface.getRegistrationID(context);
            User.getInstance().setRegistrationID(registrationID);
            Log.d(TAG, "JPUSH registrationID=" + registrationID);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "当前Activity=" + this.TAG);
        //避免弹出的键盘遮挡输入框的问题
        //这样会让屏幕整体上移。如果加上的 是 android:windowSoftInputMode="adjustPan"这样键盘就会覆盖屏幕。
        //AndroidManifest.xml的Activity设置属性：android:windowSoftInputMode = "adjustResize"
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //JPUSH  数据统计
        JPushInterface.onPause(this);
        MessageReceiver.unregisterReceiver(getActivity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //JPUSH 数据统计
        JPushInterface.onResume(this);
        MessageReceiver.registerMessageReceiver(getActivity());  // used for receive msg
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


    public LogisticsTrafficApplication getApp() {
        return (LogisticsTrafficApplication) getApplication();
    }

    public BaseActivity getActivity() {
        return this;
    }


    /**
     * 设置页面名称
     *
     * @param name
     */
    public void setPageName(String name) {
        TextView pageTopName = (TextView) findViewById(R.id.page_top_name);
        if (pageTopName != null) {
            pageTopName.setText(name);
        }
        View pagetoply = findViewById(R.id.page_top_ly);
        if (pagetoply != null) {
            pagetoply.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
        }

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

    /**
     * 设置网络图片
     *
     * @param id
     * @param url
     */
    protected void setImageUrl(int id, String url) {
        View viewById = findViewById(id);
        if (viewById != null && viewById instanceof ImageView)
            ViewUtils.setImageUrl((ImageView) viewById, url);
    }


    /**
     * 显示提示信息
     *
     * @param msg
     */
    public void showToast(String msg) {
        AppToast.getInstance().showToast(this, msg);

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

    protected void startActivity(Class<?> cls, Bundle bundle, int flags, boolean istop) {
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

        startActivityForResult(cls, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP, requestCode);
    }

    public void startActivityForResult(Class<?> cls, Bundle bundle, int flags, int requestCode) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(flags);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    //Fragment 功能

    /**
     * 添加Fragment
     */
    public void add(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }

    /**
     * 替换Fragment
     */
    public void replace(int containerViewId, Fragment fragment, String tag) {
//        getFragmentManager().beginTransaction().replace(containerViewId,fragment,tag).addToBackStack(tag).commit();
        getSupportFragmentManager().beginTransaction().replace(containerViewId, fragment, tag).addToBackStack(tag).commit();
    }

    public void onClick_gotoMap(View view) {
        startActivity(BaiduMapActivity.class);
        //todo  测试地图web页
//        startActivity(WebViewActivity.class);
    }

    public void gotoDetail(Product product) {
//        startActivity(new Intent(this,DetailCompany.class));
        if (product != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppParams.BUNDLE_PRODUCT_KEY, product);
            startActivity(DetailCompany.class, bundle);
        } else {
            startActivity(DetailCompany.class);
        }
    }

    /**
     * 登录页面跳转
     */
    public void gotoLogin() {
        ConfirmDialog.showConfirmDialog(this, "您还没有登陆，是否登陆？", "返回", "登陆", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LoginActivity.class);
                finish();
            }
        });
    }



    public void showDialogCallPhone(String phoneNum, int companyId) {
        Log.d(TAG, ">>>showDialogCallPhone " + phoneNum);
        if (!User.getInstance().checkUserVipStatus(this)) {
            return;
        }
        new CallPhoneDialog(this)
                .setCompanyId(companyId)
                .setPhoneNum(phoneNum)
                .show();

    }


}
