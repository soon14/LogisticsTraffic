package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.OrderListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * TODO: 滑动切换功能
 * Created by ZhangZy on 2015/6/19.
 */
public class OrderListActivity extends BaseActivity {

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderlist);
        setPageName("我的订单");
        initTabHost();
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(R.id.orderlist_tabHost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        tab1.setContent(R.id.orderlist_tab_list1);
        tab1.setIndicator(getTabView("未确认"));
        tabHost.addTab(tab1);
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setContent(R.id.orderlist_tab_list2).setIndicator(getTabView("交易中")));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setContent(R.id.orderlist_tab_list3).setIndicator(getTabView("已完成")));
        tabHost.setCurrentTab(0);

        View view = tabHost.getTabContentView().findViewById(R.id.orderlist_tab_list1);
        if(view != null){
            ListView list = (ListView) view;
            list.setAdapter(new OrderListAdapter(false));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(OrderDetailActivity.class);
                }
            });
        }

        view = tabHost.getTabContentView().findViewById(R.id.orderlist_tab_list2);
        if(view != null){
            ListView list = (ListView) view;
            list.setAdapter(new OrderListAdapter(false));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    startActivity(OrderDetailActivity.class,bundle);
                }
            });
        }
        view = tabHost.getTabContentView().findViewById(R.id.orderlist_tab_list3);
        if(view != null){
            ListView list = (ListView) view;
            list.setAdapter(new OrderListAdapter(false));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(EvaluationActivity.class);
                }
            });
        }
//        view = tabHost.getTabContentView().findViewById(R.id.orderlist_tab_list4);
//        if(view != null){
//            ListView list = (ListView) view;
//            list.setAdapter(new OrderListAdapter(true));
//        }

    }

    private View getTabView(String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.orderlist_tab,null);
        TextView textView = (TextView) view.findViewById(R.id.tab_name);
        textView.setText(name);
        return  view;
    }

//    public void onClick_Back(View view ){
//        finish();
//    }
}
