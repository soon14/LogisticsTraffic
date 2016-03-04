package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.OrderDetailActivity;
import com.bt.zhangzy.logisticstraffic.activity.OrderListActivity;
import com.bt.zhangzy.logisticstraffic.adapter.OrderListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonOrder;

import java.util.HashMap;
import java.util.List;

/**
 * 未提交订单， 已提交订单，已完成订单
 * Created by ZhangZy on 2015/7/16.
 */
public class OrderListFragment extends Fragment {

    final String TAG = OrderListFragment.class.getSimpleName();
    private int TAG_INDEX;
    private ListView listView;
    private OrderListAdapter adapter;
    private View layoutView;

    public OrderListFragment() {
        super();
    }

    public OrderListFragment initTAG_INDEX(int TAG_INDEX) {
        this.TAG_INDEX = TAG_INDEX;
        return this;
    }

    public int getTAG_INDEX() {
        return TAG_INDEX;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 为什么要在onCreate初始化layoutView？？？？
        ////http://blog.csdn.net/bob1993_dev/article/details/46491993
        /*当我们使用Viewpager搭配FragmentPagerAdapter进行滑动的时候，某些情况下会发现前边几页自己手动产生的数据会被清空掉，对于这个碎片的滑动：
        它的出现和消失只是和Activity一样，是一个是否在栈顶的关系，当非栈顶碎片出现再栈顶的时候，它就会从碎片的onCreateView这个生命周期开始走到onDestroyView之前，
        如果你把adapter这种装数据的对象放在了onCreateView及以下的生命节点里，当碎片重新回到栈顶之后，adapter一定会被重新创建，所以数据丢失。。。。
        解决方案就是将adapter的实例化放在onCreate里。*/
        layoutView = getActivity().getLayoutInflater().inflate(R.layout.fragment_list, null, false);
        if (layoutView != null) {
            initListView(layoutView);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        if (layoutView == null) {
            layoutView = getActivity().getLayoutInflater().inflate(R.layout.fragment_list, null, false);
            initListView(layoutView);
        }
        if (layoutView != null) {
//            initListView(view);
            return layoutView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initListView(View view) {
        listView = (ListView) view.findViewById(R.id.orderlist_listview);
//        listView.setAdapter(new OrderListAdapter(false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null)
                    openOrderDetail(adapter.getItem(position));
            }
        });
        if (adapter != null)
            listView.setAdapter(adapter);
        if (TAG_INDEX == OrderListActivity.PAGE_UNTREATED) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    showDelDialog(adapter.getItem(position));
                    return true;
                }
            });
        }
    }


    private void openOrderDetail(JsonOrder item) {

        int ordinal = -1;
        switch (TAG_INDEX) {
            case OrderListActivity.PAGE_UNTREATED:
                ordinal = OrderDetailMode.UntreatedMode.ordinal();
                break;
            case OrderListActivity.PAGE_SUBMITTED:
                ordinal = OrderDetailMode.SubmittedMode.ordinal();
                break;
            case OrderListActivity.PAGE_COMPLETED:
                ordinal = OrderDetailMode.CompletedMode.ordinal();
                break;
        }
        if (ordinal > 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(AppParams.ORDER_DETAIL_KEY_TYPE, ordinal);
            bundle.putParcelable(AppParams.ORDER_DETAIL_KEY_ORDER, item);
            getBaseActivity().startActivity(OrderDetailActivity.class, bundle);
        }
    }


    private BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public void setAdapter(List<JsonOrder> list) {
        if (list == null || list.isEmpty())
            return;
//        if (listView == null) {
//            initListView(layoutView);
//        }
        //// TO DO: 2016-3-3  为什么 listView 为空?????
        //解释，setAdapter方法是网络回调执行的，而listFragment对象是有缓冲机制的，所以不一定创建了实例，这里需要先缓存adapter对象，等初始化的时候再赋值；
        adapter = new OrderListAdapter(list);
        if (listView != null)
            listView.setAdapter(adapter);

    }

    private void showDelDialog(final JsonOrder jsonOrder) {
        new ConfirmDialog(getActivity())
                .setMessage("是否删除订单：" + jsonOrder.getId())
                .setConfirm("删除")
                .setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestDelOrder(jsonOrder.getId());
                        adapter.delItem(jsonOrder);
                    }
                }).show();
    }


    private void requestDelOrder(int id) {

        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(id));
        HttpHelper.getInstance().get(AppURL.GetOrderDel, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                getBaseActivity().showToast("订单删除成功");

            }

            @Override
            public void onFailed(String str) {
                getBaseActivity().showToast("订单删除失败");
            }
        });
    }
}
