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

/**
 * 未提交订单， 已提交订单，已完成订单
 * Created by ZhangZy on 2015/7/16.
 */
public class OrderListFragment extends Fragment {

    final String TAG = OrderListFragment.class.getSimpleName();
    private int TAG_INDEX;
    private ListView list;

    public OrderListFragment() {
        super();
    }

    public OrderListFragment initTAG_INDEX(int TAG_INDEX) {
        this.TAG_INDEX = TAG_INDEX;
        return  this;
    }



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
                    getBaseActivity().startActivity(OrderDetailActivity.class, bundle);
                }
            }
        });
    }


    private BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

}
