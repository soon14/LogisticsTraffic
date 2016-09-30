package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.DriverListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.CarPayStatus;
import com.bt.zhangzy.logisticstraffic.data.CarRunStatus;
import com.bt.zhangzy.logisticstraffic.data.Driver;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonMotocardesDriver;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.ResponseAllocationDriver;
import com.bt.zhangzy.network.entity.ResponseMotorcades;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.app.AppProgress;
import com.zhangzy.base.http.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 车队页面
 * <p/>
 * 拆分FleetActivity
 * 负责给物流公司 和企业显示车队成员及车辆
 * Created by ZhangZy on 2016-9-22.
 */
public class MotorcadeActivity extends BaseActivity {


    private int motorcadeId;//车队id
    private ListView listView;

    DriverListAdapter driverListAdapter;
    boolean isSelectDriver = false;// 选择司机
    boolean isShowLoadingDriver = false;//显示司机的运输状态
    private ArrayList<ResponseAllocationDriver> selectDriverListFromOrder;// 待选司机列表
    private ArrayList<Driver> selectedDrivers;
    private int needSelectDriverSize; //需要选择的司机数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        setContentView(R.layout.activity_motorcade);
        listView = (ListView) findViewById(R.id.motorcade_list);

        initCompany();

    }

    private void initData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            //标记是否为选择模式  只有在信息部call车时才有可能触发
            if (bundle.containsKey(AppParams.RESULT_CODE_KEY)) {
                if (bundle.getInt(AppParams.RESULT_CODE_KEY) == AppParams.RESULT_CODE_SELECT_DEVICES) {
                    isSelectDriver = true;
                    //需要选择的司机数量
                    needSelectDriverSize = bundle.getInt(AppParams.SELECT_DEVICES_SIZE_KEY);
                    selectedDrivers = bundle.getParcelableArrayList(AppParams.SELECTED_DRIVERS_LIST);
                } else if (bundle.getInt(AppParams.RESULT_CODE_KEY) == AppParams.RESULT_CODE_ACCEPT_DRIVERS) {
                    //展示司机 的 运输状态
                    isShowLoadingDriver = true;
                    //标记是否可以确认装货  新版中去掉确认装货的过程
//                    isShowLoadingDriverEdit = bundle.getBoolean(AppParams.SELECTED_DRIVERS_EDIT, false);
                }
                if (bundle.containsKey(AppParams.SELECT_DRIVES_LIST_KEY)) {
                    ArrayList<String> list = bundle.getStringArrayList(AppParams.SELECT_DRIVES_LIST_KEY);
                    selectDriverListFromOrder = BaseEntity.ParseArrayToEntity(list, ResponseAllocationDriver.class);
//                    selectDriverListFromOrder = bundle.getParcelableArrayList(AppParams.SELECT_DRIVES_LIST_KEY);
                }
            }

        }
    }


    private void initCompany() {
//        if (isSelectDriver || isShowLoadingDriver) {
//            findViewById(R.id.motorcade_create_order_bt).setVisibility(View.GONE);
//            findViewById(R.id.motorcade_add_bt).setVisibility(View.GONE);
//
//        }
        if (User.getInstance().getUserType() == Type.CompanyInformationType) {
            findViewById(R.id.motorcade_create_order_bt).setVisibility(View.GONE);
        }
        if (!isSelectDriver)
            findViewById(R.id.motorcade_finish_bt).setVisibility(View.GONE);

        if (selectDriverListFromOrder == null) {
            setPageName("我的车队");
            findViewById(R.id.motorcade_add_bt).setVisibility(View.VISIBLE);
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
        } else {
            if (isSelectDriver)
                setPageName("接单司机列表");
            else if (isShowLoadingDriver)
                setPageName("司机运输状态");

            findViewById(R.id.motorcade_add_bt).setVisibility(View.GONE);
            initSelectDriverModel();
        }

    }

    private void initSelectDriverModel() {
        driverListAdapter = new DriverListAdapter(paraseJson(selectDriverListFromOrder), isSelectDriver);
        listView.setAdapter(driverListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Driver driver = driverListAdapter.getItem(position);
                gotoCarListAct(driver);
            }
        });

    }

    private void gotoCarListAct(Driver driver) {
        if (driver != null) {
            if (driver.getListCar() == null || driver.getListCar().isEmpty()) {
                showToast("没有可以查看的车辆");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppParams.ORDER_SELECT_MODE, isSelectDriver);
            bundle.putParcelable(AppParams.ORDER_SELECT_DRIVER, driver);
            startActivityForResult(MotorcadeCarAct.class, bundle, AppParams.ORDER_SERECT_DRIVER_CODE);
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
                    gotoCarListAct(driver);
                }
            });
        }
    }

    /**
     * 将传入的json数据解析成 模型数据
     *
     * @param selectDriverListFromOrder
     */
    private ArrayList<Driver> paraseJson(ArrayList<ResponseAllocationDriver> selectDriverListFromOrder) {
        ArrayList<Driver> driverList = new ArrayList<>();
        Driver driver;
        for (ResponseAllocationDriver json : selectDriverListFromOrder) {
            driver = new Driver(json.getDriver());
            driver.setOrderHistoryId(json.getId());
            driver.setName(json.getName());
            driver.setPhoneNumber(json.getPhoneNumber());
            for (JsonCar jsoncar : json.getCars()) {
                driver.addCar(new Car(jsoncar));
            }

            driverList.add(driver);
        }
        return driverList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppParams.ORDER_SERECT_DRIVER_CODE) {
            if (data != null && data.hasExtra(AppParams.ORDER_SELECT_DRIVER)) {
                Driver driver = data.getParcelableExtra(AppParams.ORDER_SELECT_DRIVER);
                driverListAdapter.repalceDriver(driver);
                if (selectedDrivers == null) {
                    selectedDrivers = new ArrayList<>();
                    selectedDrivers.add(driver);
                } else {
                    for (Driver d : selectedDrivers) {
                        if (d.getId() == driver.getId()) {
                            selectedDrivers.remove(d);
                            break;
                        }
                    }
                    selectedDrivers.add(driver);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 企业给车队下单
     *
     * @param view
     */
    public void onClick_CreateOrder(View view) {
        //企业没有车队无法下单
//        if (.getUserType() == Type.EnterpriseType) {
//        List<JsonMotorcades> motorcades = User.getInstance().getMotorcades();
        if (listView.getAdapter() == null) {
            showToast("请先添加车队成员！");
            return;
        }
//        }
        Bundle bundle = new Bundle();
        bundle.putInt(AppParams.ORDER_DETAIL_KEY_TYPE, OrderDetailMode.CreateMode.ordinal());
        startActivity(OrderDetailActivity.class, bundle, 0, false);
    }


    /**
     * 完成选择
     *
     * @param view
     */
    public void onClick_FinishSelect(View view) {
        if (selectedDrivers == null) {
            showToast(getString(R.string.need_driver_size, needSelectDriverSize));
            return;
        }
        int select_size = 0;
        for (Driver driver : selectedDrivers) {
            select_size += driver.getSelectCarNum();
        }
        if (select_size != needSelectDriverSize) {
            int tmp = needSelectDriverSize - select_size;
            if (tmp > 0)
                showToast(getString(R.string.need_driver_size, tmp));
            else
                showToast(getString(R.string.need_driver_overtop_size, -tmp));

            return;
        }
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(AppParams.SELECTED_DRIVERS_LIST, selectedDrivers);
//                    intent.putExtra(Constant.RESULT_CODE_KEY,people);
        setResult(AppParams.RESULT_CODE_SELECT_DEVICES, intent);
        finish();
    }

    /**
     * 添加司机
     *
     * @param view
     */
    public void onclick_AddDriver(View view) {
        showAddDriverDialog();
    }

    private void showAddDriverDialog() {
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.dialog_add_driver);
        dialog.setTitle("请输入已注册司机用户的手机号");
        dialog.setOnClickListener(R.id.add_driver_dl_cancel_bt, null).setOnClickListenerForDialog(R.id.add_driver_dl_confirm_bt, new BaseDialog.DialogClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
//                EditText name = (EditText) dialog.findViewById(R.id.add_driver_dl_name_ed);
                EditText phone = (EditText) dialog.findViewById(R.id.add_driver_dl_phone_ed);
                if (TextUtils.isEmpty(phone.getText())) {
                    showToast("请检查填写内容");
                    return;
                }
                if (!Tools.IsPhoneNum(phone.getText().toString())) {
                    showToast("手机号格式错误");
                    return;
                }

                dialog.cancel();
                requestAddDriver(phone.getText().toString());
            }


        }).show();
    }

    /**
     * 添加车队司机
     *
     * @param phone
     */
    private void requestAddDriver(final String phone) {

        HttpHelper.getInstance().post(AppURL.PostAddMotorcadeDriverPhone, motorcadeId + "/" + phone, new BaseEntity(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("司机添加成功");
//                notifyListAdapter();
                //重新请求车队的成员列表
                requestGetMotorcades(motorcadeId);
            }

            @Override
            public void onFailed(String str) {
                showToast("司机添加失败" + str);
            }
        });
    }

    /**
     * 车队列表
     *
     * @param motorcadeId
     */
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
                        Driver driver;
                        for (JsonMotocardesDriver jsonMotocardesDriverdriver : motorcadeDrivers) {
                            driver = new Driver(jsonMotocardesDriverdriver);
                            for (JsonCar jsonCar : json.getCars()) {
                                if (driver.getId() == jsonCar.getDriverId()) {
                                    Car car = new Car(jsonCar);
                                    //过滤车辆的状态
                                    if (car.getPayStatus() == CarPayStatus.PaymentReceived) {
                                        if (car.getRunStatus() == CarRunStatus.Leisure)
                                            //过滤掉没有驾驶员的
                                            if (car.getPilotId() > 0) {
                                                driver.addCar(car);
                                            }
                                    }
                                }
                            }
                            driverList.add(driver);
                        }


                        driverListAdapter = new DriverListAdapter(driverList, isSelectDriver);
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
