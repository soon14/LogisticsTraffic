package com.bt.zhangzy.logisticstraffic.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.WebViewActivity;
import com.bt.zhangzy.logisticstraffic.view.CustomProgress;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.ImageHelper;
import com.bt.zhangzy.network.Url;
import com.zhangzy.baidusdk.BaiduMapActivity;

import org.w3c.dom.Text;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class BaseActivity extends FragmentActivity {

    public final String TAG;
    protected Context context;

    CustomProgress progress;

    protected BaseActivity() {
        TAG = getClass().getSimpleName();
        context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApp().setCurrentAct(this);
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

    public BaseActivity getActivity(){
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
        TextView tx = (TextView) findViewById(id);
        tx.setText(string);
    }

    /**
     * 显示提示信息
     *
     * @param msg
     */
    protected void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
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
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    /**
     * 地址选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(final View view) {
        LocationView.createDialog(this).setListener(new LocationView.ChangingListener() {
            @Override
            public void onChanged(String province, String city) {
                if (TextUtils.isEmpty(city))
                    return;
                if (view != null && view instanceof TextView) {
                    ((TextView) view).setText(city);
                }
            }
        }).show();
    }
}
