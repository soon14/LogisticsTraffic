package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.app.ContextTools;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.fragment.BaseHomeFragment;
import com.bt.zhangzy.logisticstraffic.fragment.HappyFragment;
import com.bt.zhangzy.logisticstraffic.fragment.HomeFragment;
import com.bt.zhangzy.logisticstraffic.fragment.ServicesFragment;
import com.bt.zhangzy.logisticstraffic.fragment.SourceListFragment;
import com.bt.zhangzy.logisticstraffic.fragment.UserFragment;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.FloatView;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class HomeActivity extends BaseActivity {

    private final static String Tag = HomeActivity.class.getSimpleName();

    private static final int INDEX_HOME = 0;
    private static final int INDEX_SERVICES = 1;
    //    private static final int INDEX_HAPPY = 2;
    private static final int INDEX_USER = 2;//3;

    //浮动窗口
    private WindowManager windowManager = null;
    private FloatView floatView;
    private ViewPager contentViewPager;

    //中间的按钮
    private Button customBtn;
    boolean lastLogin = User.getInstance().getLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //避免弹出的键盘遮挡输入框的问题
        //这样会让屏幕整体上移。如果加上的 是 android:windowSoftInputMode="adjustPan"这样键盘就会覆盖屏幕。
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /*关于android:windowSoftInputMode
    　　activity主窗口与软键盘的交互模式，可以用来避免输入法面板遮挡问题，Android1.5后的一个新特性。
    　　这个属性能影响两件事情：
　　　　【一】当有焦点产生时，软键盘是隐藏还是显示
　　　　【二】是否减少活动主窗口大小以便腾出空间放软键盘*/

        setContentView(R.layout.activity_home);

//        add(R.id.home_content, new HomeFragment(), TAG_HOME);
//
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createViewPage();
                init();
            }
        });
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
            BaseHomeFragment item = getCurrentFragment();
            return item.onTouchEventFragment(event);
        }
        return super.onTouchEvent(event);
//        return detector.onTouchEvent(event);
    }

    /**
     * 返回当前页的Fragment
     *
     * @return
     */
    public BaseHomeFragment getCurrentFragment() {
        if (contentViewPager == null || contentViewPager.getAdapter() == null)
            return null;
        HomeFragmentPagerAdapter adapter = (HomeFragmentPagerAdapter) contentViewPager.getAdapter();
        return (BaseHomeFragment) adapter.getItem(contentViewPager.getCurrentItem());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) {
            // 在程序退出(Activity销毁）时销毁悬浮窗口
            windowManager.removeView(floatView);
            Log.d(Tag, "浮窗 删除");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initCustomBtn();
        //判断用户是否登陆，并对页面进行相应的更新
        if (lastLogin != User.getInstance().getLogin()) {
            lastLogin = User.getInstance().getLogin();
//            createViewPage();
            if (User.getInstance().getUserType() == Type.InformationType) {
                HomeFragmentPagerAdapter adapter = (HomeFragmentPagerAdapter) contentViewPager.getAdapter();
                adapter.updateFragment(INDEX_SERVICES, new SourceListFragment());
//            ArrayList<Fragment> fragments = new ArrayList<Fragment>();
//            fragments.add(new HomeFragment());
//            fragments.add(new SourceListFragment());
//            fragments.add(new UserFragment());
//            adapter.setFragments(fragments);
            }
        }
        if (floatView != null) {
            floatView.setVisibility(View.VISIBLE);
            Log.d(Tag, "浮窗 显示");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createView();
                }
            });
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


    private void createViewPage() {
        contentViewPager = (ViewPager) findViewById(R.id.home_content_pager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
        if (User.getInstance().getUserType() == Type.InformationType) {
            fragments.add(new SourceListFragment());
        } else {
            fragments.add(new ServicesFragment());
        }
//        fragments.add(new HappyFragment());
        fragments.add(new UserFragment());

        contentViewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        contentViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEvent(event);
            }
        });
        contentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void init() {


        //默认页面处理
        setPage(INDEX_HOME);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constant.BUNDLE_PAGE_KEY)) {
                String value = bundle.getString(Constant.BUNDLE_PAGE_KEY);

                if (Constant.PAGE_HOME.equals(value)) {
                    setPage(INDEX_HOME);
                } else if (Constant.PAGE_SERVICES.equals(value)) {
                    setPage(INDEX_SERVICES);
//                } else if (Constant.PAGE_HAPPY.equals(value)) {
//                    setPage(INDEX_HAPPY);
                } else if (Constant.PAGE_USER.equals(value)) {
                    setPage(INDEX_USER);
                }
            }
        }
    }

    /**
     * 初始化自定义按键
     */
    private void initCustomBtn() {
        customBtn = (Button) findViewById(R.id.home_bottom_services_btn);
        if (User.getInstance().getUserType() == Type.InformationType) {
            customBtn.setText("空车信息");
        } else if (User.getInstance().getUserType() == Type.DriverType) {
            customBtn.setText("货源");
        }
    }

    private void setPage(int page) {
        setPage(page, null);
    }

    private void setPage(int page, View view) {
        //登陆判断
        if (page == INDEX_USER && !User.getInstance().getLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.BUNDLE_PAGE_KEY, Constant.PAGE_USER);
            startActivity(LoginActivity.class, bundle);
//            finish();
            if (contentViewPager.getCurrentItem() == INDEX_USER) {
                contentViewPager.setCurrentItem(INDEX_SERVICES);
            }

            return;
        }
        if (view == null) {
            switch (page) {
                case INDEX_HOME:
                    setSelectBtn(findViewById(R.id.home_bottom_first_btn));
                    break;
//                case INDEX_HAPPY:
//                    setSelectBtn(findViewById(R.id.home_bottom_happy_btn));
//                    break;
                case INDEX_SERVICES:
                    setSelectBtn(findViewById(R.id.home_bottom_services_btn));
                    break;
                case INDEX_USER:
                    setSelectBtn(findViewById(R.id.home_bottom_me_btn));
                    break;

                default:
                    //没有该页面，跳出
                    return;
            }
        }
        if (contentViewPager != null && contentViewPager.getCurrentItem() != page) {
            contentViewPager.setCurrentItem(page);
        }
    }


    /**
     * 浮窗 创建
     */
    private void createView() {
        //TODO 浮窗音效 小汽车下单加入音效
        if (!User.getInstance().getLogin() || User.getInstance().getUserType() != Type.InformationType) {
            Log.w(Tag, "用户 不符合浮窗创建条件");
            floatView = null;
            return;
        }
        Log.d(Tag, "浮窗 创建");
        // 获取WindowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        floatView = FloatView.CreateView(getApplicationContext(), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

//                startActivity(OrderDetailActivity.class);
                if (User.getInstance().getUserType() == Type.DriverType) {
                    startActivity(PublishActivity.class);
                } else {
                    startActivity(OrderDetailActivity.class);
                }
            }
        });
        floatView.setWindowManager(windowManager);
        floatView.setVisibility(View.VISIBLE);
        // 显示myFloatView图像
        windowManager.addView(floatView, floatView.getWindowManagerParams());
    }


    public void gotoDetail() {
//        startActivity(new Intent(this,DetailCompany.class));
        startActivity(DetailCompany.class);
    }

    public void showDialogCallPhone(final String phoneNum) {
        Log.d(Tag, ">>>showDialogCallPhone " + phoneNum);
        BaseDialog dialog = BaseDialog.CreateChoosePhoneDialog(HomeActivity.this, phoneNum);
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
        if (lastSelectBtn != null && lastSelectBtn.getId() == view.getId())
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
        setPage(INDEX_USER, view);
    }


    public void onClick_Home(View view) {
//        replace(R.id.home_content,new HomeFragment(),TAG_HOME);
        setPage(INDEX_HOME, view);
    }

    public void onClick_Happy(View view) {
//        startActivity(PlayActivity.class);
//        replace(R.id.home_content,new HappyFragment(),TAG_HAPPY);
//        setPage(INDEX_HAPPY, view);
    }

    public void onClick_Services(View view) {
//        startActivity(ServicesActivity.class);
//        replace(R.id.home_content, new ServicesFragment(), TAG_SERVICES);
        setPage(INDEX_SERVICES, view);
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

        BaseDialog.showConfirmDialog(this, "是否退出?", "返回", "退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApp().Exit();
            }
        });
    }

    public void onClick_Server(View view) {
        // 从用户信息中点击服务按钮
        startActivity(ServicesActivity.class);
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

    //搜索类型选择
    public void onClick_SearchType(View view) {

        BaseHomeFragment f = getCurrentFragment();
        if(f != null && f instanceof SourceListFragment){
            SourceListFragment slf = (SourceListFragment) f;
            ((SourceListFragment) f).onClick_SearchType(view);
        }
    }

    /**
     * 推荐给好友
     *
     * @param view
     */
    public void onClick_SendFriend(View view) {
        ContextTools.showContacts(this, Constant.REQUEST_CODE_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_CODE_CONTACT) {
            String[] str = ContextTools.OnActivityRsultForContacts(this, data);
            if (str != null && str.length>1 && !TextUtils.isEmpty(str[1])) {
                //给拿到的电话发送短信
                ContextTools.SendSMS(this, str[1], "测试短信：推荐内容   快来使用易运通吧！！");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
