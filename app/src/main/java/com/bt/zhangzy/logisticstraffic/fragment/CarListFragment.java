package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.activity.CarDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.CarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.CarPayStatus;
import com.bt.zhangzy.logisticstraffic.data.CarRunStatus;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆管理
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListFragment extends Fragment {

    final static String TAG = CarListFragment.class.getSimpleName();
    private View layoutView;
    private ListView listView;
    private CarListAdapter adapter;
    boolean isCheckMode;
    ArrayList<Car> carArrayList;//查看订单相关车辆

    public CarListFragment() {
        super();
        this.isCheckMode = false;
    }

    /**
     * 此方法 必须在fragment初始化之前调用；
     *
     * @param checkMode
     */
    public void setCheckMode(boolean checkMode) {
        isCheckMode = checkMode;
    }

    public void setCarArrayList(ArrayList<Car> carArrayList) {
        this.carArrayList = carArrayList;
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
        if (isCheckMode) {
            ViewUtils.setText(view, R.id.car_list_add_bt, "完成");
        } else if (carArrayList != null) {
            view.findViewById(R.id.car_list_add_bt).setVisibility(View.GONE);
        }
        listView = (ListView) view.findViewById(R.id.car_list_listView);
//        listView.setAdapter(new OrderListAdapter(false));
        if (carArrayList == null|| User.getInstance().getUserType()== Type.DriverType)
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Car jsonCar = (Car) adapter.getItem(position);
                    if (jsonCar != null) {
                        BaseActivity activity = (BaseActivity) getActivity();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AppParams.CAR_LIST_PAGE_CAR_KEY, jsonCar);
                        activity.startActivity(CarDetailActivity.class, bundle);
                    }


                }
            });
        if (adapter != null)
            listView.setAdapter(adapter);
        else
            requestCarList();

        if (isCheckMode) {
            ViewUtils.setText(view, R.id.car_list_add_bt, "完成");
        } else {
            ViewUtils.setText(view, R.id.car_list_add_bt, "添加新车辆");
        }

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
    void setAdapter(CarListAdapter adapter) {
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

    /**
     * 返回选中的车辆列表
     *
     * @return
     */
    public ArrayList<Car> getCheckList() {
        return adapter == null ? null : adapter.getCheckList();
    }

    /**
     * 设置全选
     */
    public void setAllCheck() {
        adapter.allCheck();
    }


    public void requestCarList() {
        if (carArrayList != null) {
            CarListAdapter carListAdapter = new CarListAdapter(carArrayList);
            carListAdapter.setLook(true);
            setAdapter(carListAdapter);
        } else
            User.getInstance().requestCarInfo(new JsonCallback() {
                @Override
                public void onSuccess(String msg, String result) {
                    //车辆管理数据设置
                    List<JsonCar> jsonCar = User.getInstance().getJsonCar();
                    if (jsonCar != null) {
                        ArrayList<Car> list = parseJsonCar(jsonCar);
                        if (list != null) {
                            CarListAdapter carListAdapter = new CarListAdapter(list);
                            carListAdapter.setCheckMode(isCheckMode);
                            setAdapter(carListAdapter);
                        }
                    }
                }

                @Override
                public void onFailed(String str) {

                }
            });

    }

    public ArrayList<Car> parseJsonCar(List<JsonCar> list) {
//        this.list = list;
        if (list != null) {
            ArrayList<Car> ls = new ArrayList<>();
            for (JsonCar jsonCar : list) {
                //司机抢单 时 隐藏 非付费车辆
                Car car = new Car(jsonCar);
                if (isCheckMode) {
                    //过滤车辆的状态
                    if (car.getPayStatus() == CarPayStatus.PaymentReceived) {
                        if (car.getRunStatus() == CarRunStatus.Leisure)
                            //过滤掉没有驾驶员的
//                            if (car.getPilotId() > 0) {
                                ls.add(car);
//                            }

                    }
                } else {
                    ls.add(car);
                }
            }
            return ls;
        }
        return null;
    }

}
