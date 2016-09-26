package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.DriverListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.Driver;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonMotocardesDriver;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.ResponseMotorcades;
import com.zhangzy.base.app.AppProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * 车队页面
 * 拆分FleetActivity
 * Created by ZhangZy on 2016-9-22.
 */
public class MotorcadeActivity extends BaseActivity {



    private int motorcadeId;//车队id
    private ListView listView;

    DriverListAdapter driverListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_motorcade);

        listView = (ListView) findViewById(R.id.motorcade_list);

        initCompany();

    }

    private void initCompany() {

        setPageName("我的车队");
        try {
            List<JsonMotorcades> motorcades = User.getInstance().getMotorcades();
            if (motorcades != null && !motorcades.isEmpty()) {
                //信息部只能有一个车队，所以直接取第一个元素
                motorcadeId = motorcades.get(0).getId();
                requestGetMotorcades(motorcadeId);
            } else {
                showToast("没有车队");
                finish();
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "JsonMotorcades 错误", e);
            e.printStackTrace();
        }

    }

    //init listView
    private void initView() {
        if (driverListAdapter != null) {
            listView.setAdapter(driverListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Driver driver = driverListAdapter.getItem(position);
                    if(driver!=null ) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AppParams.REQUEST_PAGE_KEY,driver);
                        startActivityForResult(MotorcadeCarAct.class, bundle, AppParams.REQUEST_CODE);
                    }
                }
            });
        }
    }


    private void requestGetMotorcades(int motorcadeId) {
        AppProgress.getInstance().showProgress(this, "正在获取车队列表...");
        //获取车队信息

        HttpHelper.getInstance().get(AppURL.GetMotorcades, String.valueOf(motorcadeId), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

                ResponseMotorcades json = ParseJson_Object(result, ResponseMotorcades.class);


                List<JsonMotocardesDriver> motorcadeDrivers = json.getMotorcadeDrivers();
//                List<JsonCar> carList = json.getCars();
                if (motorcadeDrivers != null && !motorcadeDrivers.isEmpty()) {
                    //// TO DO: 2016-1-25  车队列表信息更新；
                    List<Driver> driverList = new ArrayList<Driver>();
                    if (User.getInstance().getUserType() != Type.DriverType) {
                        // company
                        JsonCar car;
                        Driver driver;
                        for (JsonMotocardesDriver jsonMotocardesDriverdriver : motorcadeDrivers) {
                            driver = new Driver(jsonMotocardesDriverdriver);
                            for (JsonCar jsonCar : json.getCars()) {
                                if (driver.getId() == jsonCar.getDriverId()) {
                                    driver.addCar(new Car(jsonCar));
                                }
                            }
                            driverList.add(driver);
                        }


                        driverListAdapter = new DriverListAdapter(driverList, false);
//                        adapterDrivers.initSelsect(needSelectDriverSize);
                        showToast("车队信息获取成功");
//                    notifyListAdapter();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initView();

                            }
                        });
                    } else {
                        //driver

                    }


                } else {
                    showToast("车队还没有添加过成员哦！");
                    if (listView.getAdapter() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(null);
                            }
                        });
                    }
                }
                AppProgress.getInstance().cancelProgress();
            }

            @Override
            public void onFailed(String str) {

                AppProgress.getInstance().cancelProgress();
                showToast("车队信息获取失败");
            }
        });

    }


}
