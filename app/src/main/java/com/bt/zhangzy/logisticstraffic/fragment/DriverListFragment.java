package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.activity.DriverDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.CarListDriverAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonDriver;

import java.util.ArrayList;

/**
 * 驾驶员列表管理
 * Created by ZhangZy on 2016-8-24.
 */
public class DriverListFragment extends Fragment {

    final static String TAG = DriverListFragment.class.getSimpleName();
    private View layoutView;
    private ListView listView;
    private ListAdapter adapter;

    public DriverListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutView = getActivity().getLayoutInflater().inflate(R.layout.fragment_car_list, null, false);
        if (layoutView != null) {
            initListView(layoutView);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        if (layoutView == null) {
            layoutView = getActivity().getLayoutInflater().inflate(R.layout.fragment_car_list, null, false);
            initListView(layoutView);
        }
        if (layoutView != null) {
//            initListView(view);
            return layoutView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 页面初始化
     *
     * @param view
     */
    private void initListView(View view) {
        listView = (ListView) view.findViewById(R.id.car_list_listView);
//        listView.setAdapter(new OrderListAdapter(false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter != null && adapter instanceof CarListDriverAdapter) {
                    CarListDriverAdapter driverAdapter = (CarListDriverAdapter) adapter;
                    JsonDriver driver = driverAdapter.getItem(position);
                    if (driver != null) {
                        BaseActivity activity = (BaseActivity) getActivity();
                        Bundle bundle = new Bundle();
                        bundle.putString(AppParams.CAR_LIST_PAGE_DRIVER_KEY, driver.toString());
                        activity.startActivity(DriverDetailActivity.class, bundle);
                    }

                }
            }
        });
        if (adapter != null)
            listView.setAdapter(adapter);
        else {
            requestDriverList();
        }


        Button button = (Button) view.findViewById(R.id.car_list_add_bt);
        button.setText("添加新司机");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 设置页面数据
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        if (listView != null)
            listView.setAdapter(adapter);
    }


    public void requestDriverList() {
        //test data
        ArrayList<JsonDriver> driverArrayList = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            JsonDriver jsonDriver = new JsonDriver();
            jsonDriver.setName("张三");
            jsonDriver.setPhone("15001230123");
            driverArrayList.add(jsonDriver);
        }
        setAdapter(new CarListDriverAdapter(driverArrayList));
    }


}
