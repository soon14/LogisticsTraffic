package com.bt.zhangzy.logisticstraffic.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.DetailCompany;
import com.bt.zhangzy.logisticstraffic.activity.PayActivity;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.receiver.MessageReceiver;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.CustomProgress;
import com.bt.zhangzy.network.ImageHelper;
import com.bt.zhangzy.network.AppURL;
import com.zhangzy.baidusdk.BaiduMapActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class BaseActivity extends FragmentActivity {

    public final String TAG;
    protected Context context;

    CustomProgress progress;
    Toast toast;//统一管理toast  防止一个页面上多个toast的显示；
    String toastMsg;


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
        getApp().setCurrentAct(this);
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


    /**
     * 显示进度条 用户不可取消
     *
     * @param msg
     */
    public void showProgress(CharSequence msg) {
        if (progress == null)
            progress = CustomProgress.show(context, msg);
        else {
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
     * 设置页面内容
     *
     * @param id     内容标识
     * @param string 内容
     */
    protected void setTextView(int id, String string) {
        if (TextUtils.isEmpty(string))
            return;
        TextView tx = (TextView) findViewById(id);
        if (tx != null)
            tx.setText(string);
    }

    /**
     * 设置网络图片
     *
     * @param id
     * @param url
     */
    protected void setImageUrl(int id, String url) {
        if (TextUtils.isEmpty(url) || !url.startsWith(AppURL.Host))
            return;
        ImageView img = (ImageView) findViewById(id);
        if (img != null)
            ImageHelper.getInstance().load(url, img);
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

    public void showDialogCallPhone(final String phoneNum) {
        Log.d(TAG, ">>>showDialogCallPhone " + phoneNum);
        if (AppParams.DEVICES_APP && !User.getInstance().isVIP()) {
            ConfirmDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PayActivity.class);
                }
            });
            return;
        }
        BaseDialog dialog = BaseDialog.CreateChoosePhoneDialog(this, phoneNum);
        dialog.setOnClickListener(R.id.dialog_btn_no, null).setOnClickListener(R.id.dialog_btn_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.dialog_btn_yes) {
                    getApp().callPhone(phoneNum);
                }
            }
        });
        dialog.show();

    }


}
