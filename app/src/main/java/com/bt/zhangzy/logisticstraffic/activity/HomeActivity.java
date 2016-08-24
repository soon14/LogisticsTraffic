package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.UpgradeApp;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.data.UserStatus;
import com.bt.zhangzy.logisticstraffic.fragment.BaseHomeFragment;
import com.bt.zhangzy.logisticstraffic.fragment.HomeFragment;
import com.bt.zhangzy.logisticstraffic.fragment.UserFragment;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.FloatView;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.tools.ContextTools;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/4.
 */
public class HomeActivity extends BaseActivity {


    private static final int INDEX_HOME = 0;
    //    private static final int INDEX_SERVICES = 1;
    //    private static final int INDEX_HAPPY = 2;
    private static final int INDEX_USER = 1;//3;

    //浮动窗口
    private WindowManager windowManager = null;
    private FloatView floatView;
    private ViewPager contentViewPager;

    //中间的按钮
    private ImageButton customBtn;
//    boolean lastLogin = User.getInstance().isLogin();

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
                init();
            }
        });


        new UpgradeApp(this).checkApp();


    }

    @Override
    public void onClick_Back(View view) {
//        super.onClick_Back(view);
        onClick_Quit(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onClick_Quit(null);
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
            Log.d(TAG, "浮窗 删除");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initCustomBtn();
        //判断用户是否登陆，并对页面进行相应的更新

        if (floatView != null) {
            floatView.setVisibility(View.VISIBLE);
            Log.d(TAG, "浮窗 显示");
        } else {
            //防止 oppo类的手机 浮窗点击失效的问题 增加延时任务
            new Handler(getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    createView();
                    return true;
                }
            }).sendEmptyMessageDelayed(0, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatView != null) {
            floatView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "浮窗 隐藏");
        }
        //保存用户数据
        getApp().saveUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新读取用户数据
        if (User.getInstance().getId() == 0)
            getApp().LoadAppData();
        //重新配置软件网关 防止oppo类的手机适配问题
        getApp().reloadAppParams();
    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    private void createViewPage() {
        contentViewPager = (ViewPager) findViewById(R.id.home_content_pager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment());
//        if (User.getInstance().getUserType() == Type.CompanyInformationType) {
//            fragments.add(new SourceListFragment());
//        } else {
//            fragments.add(new ServicesFragment());
//        }
//        fragments.add(new HappyFragment());
        fragments.add(new UserFragment());

        contentViewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        contentViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEvent(event);
            }
        });
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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
        };
//        contentViewPager.addOnPageChangeListener(onPageChangeListener);
        contentViewPager.setOnPageChangeListener(onPageChangeListener);


    }

    private void init() {


        //默认页面处理
        setPage(INDEX_HOME);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppParams.BUNDLE_PAGE_KEY)) {
                String value = bundle.getString(AppParams.BUNDLE_PAGE_KEY);

                if (AppParams.PAGE_HOME.equals(value)) {
                    setPage(INDEX_HOME);
//                } else if (Constant.PAGE_SERVICES.equals(value)) {
//                    setPage(INDEX_SERVICES);
//                } else if (Constant.PAGE_HAPPY.equals(value)) {
//                    setPage(INDEX_HAPPY);
                } else if (AppParams.PAGE_USER.equals(value)) {
                    setPage(INDEX_USER);
                }
            }
        }

        LocationView.getInstance().requestLocation(this, new LocationView.LocationCallback() {
            @Override
            public void networkLocation(Location location) {
                setCityName(location.getCityName());
            }

            @Override
            public void chooseLocation(Location location) {
                setCityName(location.getCityName());
            }
        });
        Location location = User.getInstance().getLocation();
        if (location != null)
            setCityName(location.getCityName());
    }

    private void setCityName(String cityName) {
        if (TextUtils.isEmpty(cityName))
            return;
        if (contentViewPager != null && contentViewPager.getCurrentItem() == INDEX_HOME) {
            Button btn = (Button) findViewById(R.id.home_location_btn);
            if (btn != null) {
                btn.setText(cityName);
            }
        }
    }

    /**
     * 初始化自定义按键
     */
    private void initCustomBtn() {
        customBtn = (ImageButton) findViewById(R.id.home_bottom_custom_btn);
        switch (User.getInstance().getUserType()) {
            case DriverType:
                customBtn.setImageResource(R.drawable.home_source_btn_selector);
                break;
            case CompanyInformationType:
                customBtn.setImageResource(R.drawable.home_source_car_btn_selector);
                break;
            case EnterpriseType:
                customBtn.setImageResource(R.drawable.home_services_btn_selector);
                break;
        }
//        if (!AppParams.DRIVER_APP) {
//            customBtn.setImageResource(R.drawable.home_source_car_btn_selector);
////            customBtn.setText("车源");
////            customBtn.setCompoundDrawables(null, getDrawable(R.drawable.home_source_btn_selector), null, null);
//            // 使用代码设置drawableleft
////            Drawable drawable = getResources().getDrawable(R.drawable.home_source_btn_selector);
////            // / 这一步必须要做,否则不会显示.
////            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
////            customBtn.setCompoundDrawables(null, drawable, null, null);
//        } else {
//            customBtn.setImageResource(R.drawable.home_source_btn_selector);
////            customBtn.setText("货源");
//        }
    }

    private void setPage(int page) {
        setPage(page, null);
    }

    private void setPage(int page, View view) {
        //登陆判断
        if (page == INDEX_USER && !User.getInstance().isLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString(AppParams.BUNDLE_PAGE_KEY, AppParams.PAGE_USER);
            startActivity(LoginActivity.class, bundle);
            finish();
            if (contentViewPager.getCurrentItem() == INDEX_USER) {
                contentViewPager.setCurrentItem(INDEX_HOME);
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
//                case INDEX_SERVICES:
//                    setSelectBtn(findViewById(R.id.home_bottom_services_btn));
//                    break;
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
     * 点击浮窗
     *
     * @param view
     */
    public void onClick_FloatView(View view) {
//                startActivity(OrderDetailActivity.class);
        if (User.getInstance().getUserType() == Type.DriverType) {
            if (User.getInstance().checkUserVipStatus(this)) {
                startActivity(PublishCarActivity.class);
            }
        } else {
            if (User.getInstance().checkUserStatus(this)) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(AppParams.ORDER_DETAIL_KEY_TYPE, OrderDetailMode.CreateMode.ordinal());
            startActivity(OrderDetailActivity.class, bundle);
        }
    }

    /**
     * 浮窗 创建
     */
    private void createView() {
        //TODO 浮窗音效 小汽车下单加入音效
        if (!User.getInstance().isLogin() || User.getInstance().getUserType() == Type.EnterpriseType) {
            Log.w(TAG, "用户 不符合浮窗创建条件");
            floatView = null;
            return;
        }
        Log.d(TAG, "浮窗 创建");
        // 获取WindowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        floatView = FloatView.CreateView(getApplicationContext(), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(HomeActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                onClick_FloatView(v);

            }
        });
        floatView.setWindowManager(windowManager);
        floatView.setVisibility(View.VISIBLE);
        // 显示myFloatView图像
        windowManager.addView(floatView, floatView.getWindowManagerParams());

    }


    /**
     * 城市列表 按钮
     * @param view
     */
    public void onClick_CityList(View view) {
//        getApp().showLoacaitonList(view);
        View topView = findViewById(R.id.home_top_ly);
        LocationView.getInstance().showLoacaitonList(this, topView);
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

    public void onClick_Custom(View view) {
//        startActivity(ServicesActivity.class);
//        replace(R.id.home_content, new ServicesFragment(), TAG_SERVICES);
//        setPage(INDEX_SERVICES, view);
        if (!User.getInstance().isLogin()) {
            gotoLogin();
            return;
        }
        if (User.getInstance().getUserType() == Type.DriverType) {
            if (User.getInstance().getUserStatus() == UserStatus.UN_CHECKED) {
                showToast("用户资料不完善，无法使用此功能");
                return;
            }
            if (User.getInstance().checkUserVipStatus(this)) {
                startActivity(SourceGoodsActivity.class, null, true);
            }
        } else if (User.getInstance().getUserType() == Type.CompanyInformationType) {
            startActivity(SourceCarActivity.class, null, true);
        } else {
            startActivity(ServicesActivity.class, null, true);
        }
    }

    public void onClick_gotoHistory(View view) {
        if (User.getInstance().isLogin())
            startActivity(HistoryActivity.class);
        else
            showToast("请先登录！");
    }


    public void onClick_Search(View view) {
        startActivity(SearchActivity.class);
    }


    public void onClick_gotoOrderList(View view) {
//       startActivity(new Intent(this,OrderListActivity.class));
//        Toast.makeText(this,">>>>>>>>>>>>>>>",Toast.LENGTH_LONG).show();
        if (User.getInstance().checkUserStatus(this)) {
            return;
        }
        startActivity(OrderListActivity.class);
    }

    public void onClick_Quit(View view) {

        ConfirmDialog.showConfirmDialog(this, "是否退出?", "返回", "退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApp().Exit(HomeActivity.this, User.getInstance().isSave());
            }
        });
    }

    public void onClick_SafeQuit(View view) {

        ConfirmDialog.showConfirmDialog(this, "是否注销并退出程序?", "返回", "退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApp().Exit(HomeActivity.this, false);
            }
        });
    }

    public void onClick_Server(View view) {
        // 从用户信息中点击服务按钮
        startActivity(ServicesActivity.class);
    }

    public void onClick_Lines(View view) {
        // 地址管理
        startActivity(LinesListActivity.class);
    }

    public void onClick_SettingShare(View view) {
        startActivity(SettingShareActivity.class);
    }

    public void onClick_SettingShop(View view) {
        startActivity(SetCompanyActivity.class);
    }


    /**
     * 我的车辆页面入口
     * @param view
     */
    public void onClick_CarList(View view){
        if(User.getInstance().getUserType() != Type.DriverType)
            return;
        startActivity(CarListActivity.class);
    }

    /**
     * 我的车队
     *
     * @param view
     */
    public void onClick_Fleet(View view) {
        if (User.getInstance().checkUserStatus(this)) {
            return;
        }
        startActivity(FleetActivity.class);
    }

    /**
     * 我的标书
     *
     * @param view
     */
    public void onClick_Tender(View view) {
        Bundle bundle = new Bundle();
        JsonEnterprise company = User.getInstance().getJsonTypeEntity();
        if (company == null) {
            showToast("请先完善信息");
            return;
        }
        bundle.putString(AppParams.BUNDLE_TENDER_COMPANY_JSON, company.toString());
        startActivity(TenderListActivity.class, bundle);
    }

    /**
     * 完善信息
     *
     * @param view
     */
    public void onClick_gotoDetailPhoto(View view) {
        startActivity(DetailPhotoActivity.class);
    }


    public void onClick_Collect(View view) {
        startActivity(CollectActivity.class);
    }

    /**
     * 修改密码
     *
     * @param view
     */
    public void onClick_ChangePassword(View view) {
        startActivity(SetPasswordActivity.class);
    }

    /**
     * 推荐给好友
     *
     * @param view
     */
    public void onClick_SendFriend(View view) {
        ContextTools.showContacts(this, AppParams.REQUEST_CODE_CONTACT);
    }

    /**
     * 意见反馈页面跳转
     *
     * @param view
     */
    public void onClick_Feedback(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "意见反馈");
        bundle.putString(AppParams.WEB_PAGE_URL, AppURL.APP_FEEDBACK.toString());
        startActivity(WebViewActivity.class, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppParams.REQUEST_CODE_CONTACT) {
            String[] str = ContextTools.OnActivityRsultForContacts(this, data);
            if (str != null && str.length > 1 && !TextUtils.isEmpty(str[1])) {
                //给拿到的电话发送短信
                ContextTools.SendSMS(this, str[1], String.format(getString(R.string.app_recommend_sms), getString(R.string.app_name), AppURL.DOWNLOAD_APP.toString()));
            }

        } else if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_Detail(View view) {
        if (getCurrentFragment() instanceof HomeFragment) {
            gotoDetail(null);
        }
    }

    public void onClick_HeadImg(View view) {
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView imageView, String url) {
                requestChangeUserInfo(url);
            }
        });
    }

    private void requestChangeUserInfo(String url) {
        JsonUser jsonUser = User.getInstance().getJsonUser();
        jsonUser.setPortraitUrl(url);

        HttpHelper.getInstance().put(AppURL.PutUserInfo, String.valueOf(jsonUser.getId()), jsonUser, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("头像修改失败");
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("头像修改成功");

            }
        });
    }
}
