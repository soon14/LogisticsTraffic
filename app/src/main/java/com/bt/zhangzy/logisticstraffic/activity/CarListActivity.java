package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.adapter.CarListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.CarListDriverAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.fragment.CarListFragment;
import com.bt.zhangzy.network.entity.JsonDriver;

import java.util.ArrayList;

/**
 * 司机端的车辆管理
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListActivity extends BaseActivity {

    private ViewPager viewPager;//页面适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_list);
        setPageName("我的车辆");
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.list_viewpager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        CarListFragment carListFragment = new CarListFragment();
        //车辆管理数据设置
        CarListAdapter carListAdapter = new CarListAdapter(User.getInstance().getJsonCar());
        carListFragment.setAdapter(carListAdapter);
        fragments.add(carListFragment);

        //驾驶员列表数据设置

        CarListFragment driverListFragment = new CarListFragment();
        //test data
        ArrayList<JsonDriver> driverArrayList = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            JsonDriver jsonDriver = new JsonDriver();
            jsonDriver.setName("张三");
            jsonDriver.setPhone("15001230123");
            driverArrayList.add(jsonDriver);
        }
        driverListFragment.setAdapter(new CarListDriverAdapter(driverArrayList));
        fragments.add(driverListFragment);

        FragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        selectBtn(findViewById(R.id.car_list_tab_car));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        selectBtn(findViewById(R.id.car_list_tab_car));
                        break;
                    case 1:
                        selectBtn(findViewById(R.id.car_list_tab_driver));
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
        if (!selectBtn(view)) return;

        setPageCurrentItem(view.getId());
    }

    private boolean selectBtn(View view) {
        if (view == null)
            return false;
        if (lastSelectBtn != null && lastSelectBtn.getId() == view.getId())
            return false;
        view.setSelected(true);
        if (lastSelectBtn != null) {
            lastSelectBtn.setSelected(false);
        }
        lastSelectBtn = view;
        return true;
    }

    private void setPageCurrentItem(int viewID) {
        int currentPage = -1;
        switch (viewID) {
            case R.id.car_list_tab_car:
                currentPage = 0;
                break;
            case R.id.car_list_tab_driver:
                currentPage = 1;
                break;
        }
        if (currentPage >= 0 && viewPager != null) {
            if (viewPager.getCurrentItem() != currentPage)
                viewPager.setCurrentItem(currentPage);
//            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}
