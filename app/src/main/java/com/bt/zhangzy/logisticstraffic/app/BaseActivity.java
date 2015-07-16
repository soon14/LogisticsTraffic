package com.bt.zhangzy.logisticstraffic.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.zhangzy.baidusdk.BaiduMapActivity;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class BaseActivity extends FragmentActivity {

    public final String TAG;
    protected Context context;

    protected BaseActivity() {
        TAG = getClass().getSimpleName();
        context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApp().setCurrentAct(this);
    }

    public LogisticsTrafficApplication getApp() {
        return (LogisticsTrafficApplication) getApplication();
    }

    TextView pageTopName;

    public void setPageName(String name) {
        if (pageTopName == null) {
            pageTopName = (TextView) findViewById(R.id.page_top_name);
        }
        pageTopName.setText(name);

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
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
    }
}
