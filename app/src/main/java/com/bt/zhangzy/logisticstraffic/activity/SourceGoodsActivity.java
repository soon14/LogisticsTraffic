package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.OrderType;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.fragment.SourceGoodsListFragment;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 货源信息
 * Created by ZhangZy on 2015/8/26.
 */
public class SourceGoodsActivity extends BaseActivity {


    //    private ListView listView;
//    private SourceGoodsListAdapter adapter;
    private OrderType currentType = OrderType.Empty;
    private ViewPager viewPager;
    private SourceGoodsListFragment publicFragment;
    private SourceGoodsListFragment motorcadesFragment;
    private SourceGoodsListFragment acceptFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_source_goods);
        setPageName("货源信息");
        initListView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //刷新列表
        requestOrderList(currentType);
    }

    class ResponseAcceptList extends JsonCallback {
        @Override
        public void onSuccess(String msg, String result) {
            List<JsonOrder> list = ParseJson_Array(result, JsonOrder.class);
            if (list == null && list.isEmpty()) {
                showToast("没有已抢货源");
                return;
            }

            acceptFragment.addList(list);
        }

        @Override
        public void onFailed(String str) {
            showToast("已抢订单列表获取失败 " + str);
        }
    }

    private void requestAcceptOrderList() {
        acceptFragment.clearAdapter();
        //已抢订单列表
        HashMap<String, String> params = new HashMap<>();

        params.put("orderType", String.valueOf(OrderType.MotorcadesType.ordinal()));
        /*订单分配中（物流 -> 司机）	3*/
        params.put("status", "3");
        params.put("role", String.valueOf(User.getInstance().getUserType().toRole()));
        params.put("roleId", String.valueOf(User.getInstance().getRoleId()));

        HttpHelper.getInstance().get(AppURL.GetOrderAcceptList, params, new ResponseAcceptList());
        //公共货源
        params = new HashMap<>(params);
        params.put("orderType", String.valueOf(OrderType.PublicType.ordinal()));
        HttpHelper.getInstance().get(AppURL.GetOrderAcceptList, params, new ResponseAcceptList());

    }

    private void requestOrderList(final OrderType type) {
        //过滤未加入车队的司机
        if (type == OrderType.MotorcadesType) {
            if (User.getInstance().getMotorcades() == null
                    || User.getInstance().getMotorcades().isEmpty()) {
                showToast("您还没有加入车队");
                return;
            }
        }
        //// TO DO: 2016-1-30  可抢订单列表获取失败
        //http://182.92.77.31:8080/orders/list?orderType=1&status=3
        HashMap<String, String> params = new HashMap<>();

        params.put("orderType", String.valueOf(type.ordinal()));
        /*订单分配中（物流 -> 司机）	3*/
        params.put("status", "3");
        params.put("role", String.valueOf(User.getInstance().getUserType().toRole()));
        params.put("roleId", String.valueOf(User.getInstance().getRoleId()));
        HttpHelper.getInstance().get(AppURL.GetOrderList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonOrder> list = ParseJson_Array(result, JsonOrder.class);
                if (list == null || list.isEmpty()) {
//                    showToast("货源列表为空 ->" + type.name());
                    if (type == OrderType.PublicType) {
                        publicFragment.setAdapter(null);
                    } else if (type == OrderType.MotorcadesType) {
                        motorcadesFragment.setAdapter(null);
                    }
                    return;
                }
                //排序
                Collections.sort(list);
                OrderType orderType = OrderType.parseOrderType(list.get(0).getOrderType());
                if (orderType == OrderType.PublicType) {
                    publicFragment.setAdapter(list);
                } else if (orderType == OrderType.MotorcadesType) {
                    motorcadesFragment.setAdapter(list);
                }
            }

            @Override
            public void onFailed(String str) {
                showToast("货源列表获取失败" + str);
            }
        });

    }

    private void initListView() {
        viewPager = (ViewPager) findViewById(R.id.source_list_viewpager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        publicFragment = new SourceGoodsListFragment();
        motorcadesFragment = new SourceGoodsListFragment();
        acceptFragment = new SourceGoodsListFragment();
        fragments.add(publicFragment);
        fragments.add(motorcadesFragment);
        fragments.add(acceptFragment);

        SourceGoodsListFragment.OnItemClickListener listener = new SourceGoodsListFragment.OnItemClickListener() {
            @Override
            public void OnItemClick(JsonOrder order) {
                gotoDetail(order, true);
            }
        };
        publicFragment.setListener(listener);
        motorcadesFragment.setListener(listener);
        acceptFragment.setListener(new SourceGoodsListFragment.OnItemClickListener() {
            @Override
            public void OnItemClick(JsonOrder order) {
                gotoDetail(order, false);
            }
        });


        FragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        onClick_ChangeSourceType(findViewById(R.id.source_goods_public_bt));
                        break;
                    case 1:
                        onClick_ChangeSourceType(findViewById(R.id.source_goods_motorcade_bt));
                        break;
                    case 2:
                        onClick_ChangeSourceType(findViewById(R.id.source_goods_accept_bt));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        onClick_ChangeSourceType(findViewById(R.id.source_goods_public_bt));
        requestOrderList(OrderType.PublicType);
        requestOrderList(OrderType.MotorcadesType);

        //增加一个小的延时任务，哪个列表有数据就显示哪个列表
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (currentType == OrderType.Empty) {
                    if (!publicFragment.isEmpty())
                        currentType = OrderType.PublicType;
                    else if (!motorcadesFragment.isEmpty())
                        currentType = OrderType.MotorcadesType;
                    else
                        currentType = OrderType.PublicType;

                    View view = findViewById(currentType == OrderType.PublicType ? R.id.source_goods_public_bt : R.id.source_goods_motorcade_bt);
                    view.setSelected(true);
                    lastSelectBtn = view;

                    viewPager.setCurrentItem(currentType == OrderType.PublicType ? 0 : 1);
                }
                return false;
            }
        }).sendEmptyMessageDelayed(1, 500);
    }


    private View lastSelectBtn;//记录上一次选中的按钮

    public void onClick_ChangeSourceType(View view) {
        if (view == null)
            return;
        if (lastSelectBtn != null && lastSelectBtn.getId() == view.getId())
            return;
        view.setSelected(true);
        if (lastSelectBtn != null) {
            lastSelectBtn.setSelected(false);
        }
        lastSelectBtn = view;
//        listView.setAdapter(new SourceGoodsListAdapter());
        if (view.getId() == R.id.source_goods_public_bt)
            currentType = OrderType.PublicType;
        else if (view.getId() == R.id.source_goods_motorcade_bt)
            currentType = OrderType.MotorcadesType;
        else if (view.getId() == R.id.source_goods_accept_bt) {
            requestAcceptOrderList();
            if (viewPager.getCurrentItem() != 2) {
                viewPager.setCurrentItem(2);
            }
            return;
        }

        requestOrderList(currentType);
        int currentPage = currentType == OrderType.PublicType ? 0 : 1;
        if (viewPager.getCurrentItem() != currentPage) {
            viewPager.setCurrentItem(currentPage);
        }
    }

    private void gotoDetail(JsonOrder order, boolean is_accept) {
        if (User.getInstance().isLogin()) {
            if (!User.getInstance().checkUserVipStatus(this)) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(AppParams.ORDER_DETAIL_KEY_TYPE, OrderDetailMode.UntreatedMode.ordinal());
            bundle.putParcelable(AppParams.ORDER_DETAIL_KEY_ORDER, order);
            bundle.putBoolean(AppParams.ORDER_CAN_ACCEPT, is_accept);
            startActivity(OrderDetailActivity.class, bundle);
        } else {
            gotoLogin();
        }
    }
}
