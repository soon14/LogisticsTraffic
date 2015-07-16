package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.OrderDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.OrderListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * 未提交订单， 已提交订单，已完成订单
 * Created by ZhangZy on 2015/7/16.
 */
public class OrderListFragment extends Fragment {

    final String TAG = OrderListFragment.class.getSimpleName();
    private int TAG_INDEX;
    private ListView list;

    public OrderListFragment(int tag) {
        super();
        TAG_INDEX = tag;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        if (view != null) {
            initListView(view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initListView(View view) {
        list = (ListView) view.findViewById(R.id.orderlist_listview);
        list.setAdapter(new OrderListAdapter(false));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(OrderDetailActivity.class);
            }
        });
    }

    void startActivity(Class<?> cls) {
        getBaseActivity().startActivity(cls);
    }

    private BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

}
