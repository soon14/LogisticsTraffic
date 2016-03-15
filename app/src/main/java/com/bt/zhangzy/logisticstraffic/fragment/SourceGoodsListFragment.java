package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.adapter.SourceGoodsListAdapter;
import com.bt.zhangzy.network.entity.JsonOrder;

import java.util.List;

/**
 * 未提交订单， 已提交订单，已完成订单
 * Created by ZhangZy on 2015/7/16.
 */
public class SourceGoodsListFragment extends Fragment {
    public interface OnItemClickListener {
        void OnItemClick(JsonOrder order);
    }

    final String TAG = SourceGoodsListFragment.class.getSimpleName();

    private ListView listView;
    private SourceGoodsListAdapter adapter;
    private View layoutView;
    private OnItemClickListener listener;

    public SourceGoodsListFragment() {
        super();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
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
        if (layoutView != null) {
//            initListView(view);
            return layoutView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initListView(View view) {
        listView = (ListView) view.findViewById(R.id.orderlist_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null && position < adapter.getCount()) {
//                    gotoDetail((JsonOrder) adapter.getItem(position));
                    if (listener != null) {
                        listener.OnItemClick((JsonOrder) adapter.getItem(position));
                    }
                }
            }
        });
    }

    public void setAdapter(List<JsonOrder> list) {
        if (list == null || list.isEmpty())
            return;
        adapter = new SourceGoodsListAdapter(list);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }
}
