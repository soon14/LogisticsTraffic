package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.fragment.OrderListFragment;

import java.util.ArrayList;

/**
 * 我的订单页面 滑动切换功能
 * Created by ZhangZy on 2015/6/19.
 */
public class OrderListActivity extends BaseActivity {
    public static final int PAGE_UNTREATED = 0;
    public static final int PAGE_SUBMITTED = 1;
    public static final int PAGE_COMPLETED = 2;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderlist);
        setPageName("我的订单");
//        initTabHost();
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.orderlist_viewpager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new OrderListFragment(PAGE_UNTREATED));
        fragments.add(new OrderListFragment(PAGE_SUBMITTED));
        fragments.add(new OrderListFragment(PAGE_COMPLETED));
        FragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        onClick_SelectBtn(findViewById(R.id.orderlist_tab_untreated));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case PAGE_UNTREATED:
                        onClick_SelectBtn(findViewById(R.id.orderlist_tab_untreated));
                        break;
                    case PAGE_SUBMITTED:
                        onClick_SelectBtn(findViewById(R.id.orderlist_tab_submitted));
                        break;
                    case PAGE_COMPLETED:
                        onClick_SelectBtn(findViewById(R.id.orderlist_tab_completed));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private View lastSelectBtn;//记录上一次选中的按钮

    public void onClick_SelectBtn(View view) {
        if (view == null)
            return;
        if (lastSelectBtn != null && lastSelectBtn.getId() == view.getId())
            return;
        view.setSelected(true);
        if (lastSelectBtn != null) {
            lastSelectBtn.setSelected(false);
        }
        lastSelectBtn = view;
        setPageCurrentItem(view.getId());
    }

    private void setPageCurrentItem(int viewID) {
        int currentPage = -1;
        switch (viewID) {
            case R.id.orderlist_tab_untreated:
                currentPage = PAGE_UNTREATED;
                break;
            case R.id.orderlist_tab_submitted:
                currentPage = PAGE_SUBMITTED;
                break;
            case R.id.orderlist_tab_completed:
                currentPage = PAGE_COMPLETED;
                break;
        }
        if (currentPage > 0 && viewPager != null) {
            if (viewPager.getCurrentItem() != currentPage)
                viewPager.setCurrentItem(currentPage);
        }
    }
}
