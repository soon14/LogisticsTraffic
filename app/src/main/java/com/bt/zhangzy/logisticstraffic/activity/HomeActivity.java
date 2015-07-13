package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.fragment.BaseHomeFragment;
import com.bt.zhangzy.logisticstraffic.fragment.HappyFragment;
import com.bt.zhangzy.logisticstraffic.fragment.HomeFragment;
import com.bt.zhangzy.logisticstraffic.fragment.ServicesFragment;
import com.bt.zhangzy.logisticstraffic.fragment.UserFragment;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.FloatView;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class HomeActivity extends BaseActivity {

    private final static String Tag = HomeActivity.class.getSimpleName();
    public static final String BUNDLE_PAGE_KEY = "BUNDLE_PAGE_KEY";
    public static final String PAGE_HOME = "HomeFragment";
    public static final String PAGE_USER = "UserFragment";
    public static final String PAGE_HAPPY = "HappyFragment";
    public static final String PAGE_SERVICES = "ServicesFragment";
    private static final int INDEX_HOME = 0;
    private static final int INDEX_SERVICES = 1;
    private static final int INDEX_HAPPY = 2;
    private static final int INDEX_USER = 3;

    //浮动窗口
    private WindowManager windowManager = null;
    private FloatView floatView;
    private ViewPager contentViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

//        add(R.id.home_content, new HomeFragment(), TAG_HOME);
//
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createViewPage();
                createView();
            }
        });
    }


    private void createViewPage() {
        contentViewPager = (ViewPager) findViewById(R.id.home_content_pager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
        fragments.add(new ServicesFragment());
        fragments.add(new HappyFragment());
        fragments.add(new UserFragment());

        contentViewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        contentViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEvent(event);
            }
        });

        //默认页面处理
        contentViewPager.setCurrentItem(INDEX_HOME);
        setSelectBtn(findViewById(R.id.home_bottom_first_btn));
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey(BUNDLE_PAGE_KEY)){
                String value = bundle.getString(BUNDLE_PAGE_KEY);
                if(PAGE_HOME.equals(value)){
                    contentViewPager.setCurrentItem(INDEX_HOME);
                    setSelectBtn(findViewById(R.id.home_bottom_first_btn));
                }else if(PAGE_SERVICES.equals(value)){
                    contentViewPager.setCurrentItem(INDEX_SERVICES);
                    setSelectBtn(findViewById(R.id.home_bottom_services_btn));
                }else if(PAGE_HAPPY.equals(value)){
                    contentViewPager.setCurrentItem(INDEX_HAPPY);
                    setSelectBtn(findViewById(R.id.home_bottom_happy_btn));
                }else if(PAGE_USER.equals(value)){
                    contentViewPager.setCurrentItem(INDEX_USER);
                    setSelectBtn(findViewById(R.id.home_bottom_me_btn));
                }
            }
        }

    }


    /**
     * 浮窗 创建
     */
    private void createView() {
        Log.d(Tag, "浮窗 创建");
        // 获取WindowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        floatView = FloatView.CreateView(getApplicationContext(), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

                startActivity(OrderDetailActivity.class);
            }
        });
        floatView.setWindowManager(windowManager);
        floatView.setVisibility(View.VISIBLE);
        // 显示myFloatView图像
        windowManager.addView(floatView, floatView.getWindowManagerParams());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            getApp().Exit();
            onClick_SafeQuit(null);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (contentViewPager != null && contentViewPager.getAdapter() != null) {
            HomeFragmentPagerAdapter adapter = (HomeFragmentPagerAdapter) contentViewPager.getAdapter();
            BaseHomeFragment item = (BaseHomeFragment) adapter.getItem(contentViewPager.getCurrentItem());
            return item.onTouchEventFragment(event);
        }
        return super.onTouchEvent(event);
//        return detector.onTouchEvent(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在程序退出(Activity销毁）时销毁悬浮窗口
        windowManager.removeView(floatView);
        Log.d(Tag, "浮窗 删除");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (floatView != null) {
            floatView.setVisibility(View.VISIBLE);
            Log.d(Tag, "浮窗 显示");
        } else {
            createView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatView != null) {
            floatView.setVisibility(View.INVISIBLE);
            Log.d(Tag, "浮窗 隐藏");
        }
    }


    public void gotoDetail() {
//        startActivity(new Intent(this,DetailCompany.class));
        startActivity(DetailCompany.class);
    }

    public void showDialogCallPhone(final String phoneNum) {
        Log.d(Tag, ">>>showDialogCallPhone " + phoneNum);
        BaseDialog dialog = BaseDialog.CreateDialog(HomeActivity.this);
        dialog.setPhoneNum(phoneNum);
        dialog.setListener(new BaseDialog.ConfirmListener() {
            @Override
            public void onClick(View view, boolean isConfirm) {
                if (isConfirm) {
                    getApp().callPhone(phoneNum);
                }
            }
        });
        dialog.show();

    }


    public void onClick_gotoDetail(View view) {
        gotoDetail();
    }

    public void onClick_CityList(View view) {
        getApp().showLoacaitonList(view);

    }

    private View lastSelectBtn;//记录上一次选中的按钮

    private void setSelectBtn(View view) {
        if (view == null)
            return;
        view.setSelected(true);
        if (lastSelectBtn != null) {
            lastSelectBtn.setSelected(false);
        }
        lastSelectBtn = view;
    }

    public void onClick_Me(View view) {
//        startActivity(LoginActivity.class);
        //标签页切换
//        replace(R.id.home_content, new UserFragment(), TAG_USER);
        if (!User.getInstance().getLogin()) {
            startActivity(LoginActivity.class);
        } else {
            contentViewPager.setCurrentItem(INDEX_USER);
            setSelectBtn(view);
        }
    }


    public void onClick_Home(View view) {
//        replace(R.id.home_content,new HomeFragment(),TAG_HOME);
        contentViewPager.setCurrentItem(INDEX_HOME);
        setSelectBtn(view);
    }

    public void onClick_Happy(View view) {
//        startActivity(PlayActivity.class);
//        replace(R.id.home_content,new HappyFragment(),TAG_HAPPY);
        contentViewPager.setCurrentItem(INDEX_HAPPY);
        setSelectBtn(view);
    }

    public void onClick_Services(View view) {
//        startActivity(ServicesActivity.class);
//        replace(R.id.home_content, new ServicesFragment(), TAG_SERVICES);
        contentViewPager.setCurrentItem(INDEX_SERVICES);
        setSelectBtn(view);
    }

    public void onClick_gotoHistory(View view) {
        startActivity(HistoryActivity.class);
    }


    public void onClick_Search(View view) {
        startActivity(SearchActivity.class);
    }


    public void onClick_gotoOrderList(View view) {
//       startActivity(new Intent(this,OrderListActivity.class));
//        Toast.makeText(this,">>>>>>>>>>>>>>>",Toast.LENGTH_LONG).show();
        startActivity(OrderListActivity.class);
    }

    public void onClick_SafeQuit(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("是否退出?").setNegativeButton("取消", null).setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getApp().Exit();
            }
        });
        builder.create().show();
    }

    public void onClick_SettingShare(View view) {
        startActivity(SettingShareActivity.class);
    }

    public void onClick_SettingShop(View view) {
        startActivity(SettingShopActivity.class);
    }


    public void onClick_Fleet(View view) {
        startActivity(FleetActivity.class);
    }

    public void onClick_gotoDetailPhoto(View view) {
        startActivity(DetailPhotoActivity.class);
    }


    public void onClick_Collect(View view) {
        startActivity(CollectActivity.class);
    }

    public void onClick_InfoLocation(View view) {

        String pageName = null;
        switch (view.getId()) {
            case R.id.services_qiye_btn:
                pageName = "信息部";
                break;
            case R.id.services_wuliu_btn:
                pageName = "物流园区";
                break;
            case R.id.services_xinxibu_btn:
                pageName = "仓储";
                break;
            case R.id.services_jiayouzhan_btn:
                pageName = "加油站";
                break;
        }
        if (pageName != null) {
            Bundle bundle = new Bundle();
            bundle.putString("pageName", pageName);
            startActivity(LocationListActivity.class, bundle);
        }
    }
}
