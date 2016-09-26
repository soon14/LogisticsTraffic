package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.activity.CarDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.CarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;

import java.util.List;

/**
 * 车辆管理
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListFragment extends Fragment {

    final static String TAG = CarListFragment.class.getSimpleName();
    private View layoutView;
    private ListView listView;
    private ListAdapter adapter;


    public CarListFragment() {
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

    @Override
    public void onResume() {
        super.onResume();
        requestCarList();
    }

    private void initListView(View view) {
        listView = (ListView) view.findViewById(R.id.car_list_listView);
//        listView.setAdapter(new OrderListAdapter(false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonCar jsonCar = (JsonCar) adapter.getItem(position);
                if (jsonCar != null) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppParams.CAR_LIST_PAGE_CAR_KEY, jsonCar.toString());
                    activity.startActivity(CarDetailActivity.class, bundle);
                }


            }
        });
        if (adapter != null)
            listView.setAdapter(adapter);
        else
            requestCarList();


        Button addBtn = (Button) view.findViewById(R.id.car_list_add_bt);

        addBtn.setText("添加新车辆");

    }

//    /**
//     * 设置列表项的点击 事件
//     * @param onItemClickListener
//     */
//    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
//        if (onItemClickListener == null)
//            return;
//
//        if (listView != null) {
//            listView.setOnItemClickListener(onItemClickListener);
//        }
//
//
//    }


    /**
     * 设置页面数据
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        if (listView != null) {
            if (Looper.myLooper() == Looper.getMainLooper())
                listView.setAdapter(adapter);
            else
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(CarListFragment.this.adapter);
                    }
                });
        }
    }


    public void requestCarList() {
        User.getInstance().requestCarInfo(new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                //车辆管理数据设置
                List<JsonCar> jsonCar = User.getInstance().getJsonCar();
                if (jsonCar != null) {
                    CarListAdapter carListAdapter = CarListAdapter.Init(jsonCar);
                    setAdapter(carListAdapter);
                }
            }

            @Override
            public void onFailed(String str) {

            }
        });

    }

}
