package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.SourceGoodsListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.OrderType;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.fragment.SourceGoodsListFragment;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZhangZy on 2015/8/26.
 */
public class SourceGoodsActivity extends BaseActivity {


    //    private ListView listView;
//    private SourceGoodsListAdapter adapter;
    private OrderType currentType = OrderType.Empty;
    private ViewPager viewPager;
    private SourceGoodsListFragment publicFragment;
    private SourceGoodsListFragment motorcadesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_source_goods);
        setPageName("货源信息");
        initListView();

    }

    private void requestOrderList(OrderType type) {
        //// TO DO: 2016-1-30  可抢订单列表获取失败
        //http://182.92.77.31:8080/orders/list?orderType=1&status=3
        HashMap<String, String> params = new HashMap<>();

        params.put("orderType", String.valueOf(type.ordinal()));
        /*订单分配中（物流 -> 司机）	3*/
        params.put("status", "3");
        HttpHelper.getInstance().get(AppURL.GetOrderList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonOrder> list = ParseJson_Array(result, JsonOrder.class);
                if (list == null && list.isEmpty()) {
                    showToast("数据列表为空");
                    return;
                }
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
        fragments.add(publicFragment);
        fragments.add(motorcadesFragment);

        SourceGoodsListFragment.OnItemClickListener listener = new SourceGoodsListFragment.OnItemClickListener() {
            @Override
            public void OnItemClick(JsonOrder order) {
                gotoDetail(order);
            }
        };
        publicFragment.setListener(listener);
        motorcadesFragment.setListener(listener);

        FragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        onClick_ChangeSourceType(findViewById(R.id.source_goods_public_bt));
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

        requestOrderList(currentType);
        int currentPage = currentType == OrderType.PublicType ? 0 : 1;
        if (viewPager.getCurrentItem() != currentPage) {
            viewPager.setCurrentItem(currentPage);
        }
    }

    private void gotoDetail(JsonOrder order) {
        if (User.getInstance().getLogin()) {
            if (AppParams.DRIVER_APP && !User.getInstance().isVIP()) {
                ConfirmDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(PayActivity.class);
                    }
                });
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(AppParams.ORDER_DETAIL_KEY_TYPE, OrderDetailMode.UntreatedMode.ordinal());
            bundle.putParcelable(AppParams.ORDER_DETAIL_KEY_ORDER, order);
            startActivity(OrderDetailActivity.class, bundle);
        } else {
            ConfirmDialog.showConfirmDialog(this, "您还没有登录，是否登录？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(LoginActivity.class);
                }
            });
        }
    }
}
