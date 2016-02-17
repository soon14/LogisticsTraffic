package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.OrderStatus;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.fragment.OrderListFragment;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 我的订单页面 滑动切换功能
 * Created by ZhangZy on 2015/6/19.
 */
public class OrderListActivity extends BaseActivity {
    public static final int PAGE_UNTREATED = 0;
    public static final int PAGE_SUBMITTED = 1;
    public static final int PAGE_COMPLETED = 2;

    private ViewPager viewPager;
    OrderListFragment untreatedFragment, submittedFragment, completedFragment;
    ArrayList<JsonOrder> untreatedList = new ArrayList<JsonOrder>();
    ArrayList<JsonOrder> submittedList = new ArrayList<JsonOrder>();
    ArrayList<JsonOrder> completedList = new ArrayList<JsonOrder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderlist);
        setPageName("我的订单");
//        initTabHost();
        if (AppParams.DEVICES_APP) {
            findViewById(R.id.orderlist_tab_untreated).setVisibility(View.GONE);
        }
        initViewPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestOrderList();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.orderlist_viewpager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        if (!AppParams.DEVICES_APP) {
            untreatedFragment = new OrderListFragment().initTAG_INDEX(PAGE_UNTREATED);
            fragments.add(untreatedFragment);
        }
        submittedFragment = new OrderListFragment().initTAG_INDEX(PAGE_SUBMITTED);
        fragments.add(submittedFragment);
        completedFragment = new OrderListFragment().initTAG_INDEX(PAGE_COMPLETED);
        fragments.add(completedFragment);
        //// TO DO: 2016-1-30  列表切换时 数据丢失的问题 ???

        FragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        //解决方案 使用setOffscreenPageLimit来设置ViewPager的预加载页数，可以同样保存数据。 也可以更改Fragment里的页面初始化逻辑
//        viewPager.setOffscreenPageLimit(3);
        if (AppParams.DEVICES_APP) {
            onClick_SelectBtn(findViewById(R.id.orderlist_tab_submitted));
        } else {
            onClick_SelectBtn(findViewById(R.id.orderlist_tab_untreated));
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (AppParams.DEVICES_APP) {
                    position += 1;
                }
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

    //拆分订单
    private void setAdapter(List<JsonOrder> list) {
        if (list == null || list.isEmpty()) {
            showToast("数据列表为空");
            return;
        }
        //clear history
        untreatedList.clear();
        submittedList.clear();
        completedList.clear();
        //add new data
        for (JsonOrder order : list) {
            switch (OrderStatus.parseStatus(order.getStatus())) {
                case TempOrder:
                case UncommittedOrder:
                    untreatedList.add(order);
                    break;
                case CommitOrder:
                    submittedList.add(order);
                    break;
                case FinishedOrder:
                case DiscardOrder:
                    completedList.add(order);
                    break;
            }
        }
        if (untreatedFragment != null)
            untreatedFragment.setAdapter(untreatedList);
        submittedFragment.setAdapter(submittedList);
        completedFragment.setAdapter(completedList);

//        viewPager.getAdapter().notifyDataSetChanged();
    }

    private void requestOrderList() {
        User user = User.getInstance();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("role", String.valueOf(user.getUserType().toRole()));
        long roleId = user.getUserType() == Type.InformationType ? user.getCompanyID()
                : user.getUserType() == Type.EnterpriseType ? user.getEnterpriseID()
                : user.getUserType() == Type.DriverType ? user.getDriverID()
                : user.getId();
        params.put("roleId", String.valueOf(roleId));
//        params.put("orderStatus","0");
        HttpHelper.getInstance().get(AppURL.GetMyOrderList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("列表获取成功" + msg);
                List<JsonOrder> list = ParseJson_Array(result, JsonOrder.class);
                setAdapter(list);
                // // TODO: 2016-1-29  添加订单数据
//                adapter = new OrderListAdapter(list);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        listView.setAdapter(adapter);
//                    }
//                });
            }

            @Override
            public void onFailed(String str) {
                showToast("列表获取失败" + str);
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
        if (AppParams.DEVICES_APP) {
            currentPage -= 1;
        }
        if (currentPage >= 0 && viewPager != null) {
            if (viewPager.getCurrentItem() != currentPage)
                viewPager.setCurrentItem(currentPage);
//            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}
