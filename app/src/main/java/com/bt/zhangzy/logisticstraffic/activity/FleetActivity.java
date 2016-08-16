package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.AppTextListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListForDevicesAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.SourceCarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonMotocardesDriver;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.RequestOrderAccept_Reject;
import com.bt.zhangzy.network.entity.ResponseAllocationDriver;
import com.bt.zhangzy.network.entity.ResponseMotorcades;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.http.BaseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetActivity extends BaseActivity {

    ListView listView;
    FleetListAdapter adapter;
    SourceCarListAdapter adapterDrivers;
    int currentDriverIndex = -1;
    boolean isSelectDriver = false;// 选择司机
    boolean isShowLoadingDriver = false;//显示司机的运输状态
    boolean isShowLoadingDriverEdit = false;//标记是否可以确认装货
    int motorcadeId = -1;
    private int needSelectDriverSize;//需要选择的司机数量
    private ArrayList<ResponseAllocationDriver> selectDriverListFromOrder;
    private ArrayList<People> selectedDrivers;


    String motorcade_name;
    JsonCompany company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initData();

        setContentView(R.layout.activity_fleet);
        listView = (ListView) findViewById(R.id.fleet_list);

        //TO DO 接口 更新车队信息；
        if (AppParams.DRIVER_APP) {
            initDriverView_MotorcadesList();

        } else {
            initCompanyView();

        }

    }

    private void initCompanyView() {
        if (!isSelectDriver)
            findViewById(R.id.fleet_finish_bt).setVisibility(View.GONE);
        if (isSelectDriver || isShowLoadingDriver || User.getInstance().getUserType() != Type.EnterpriseType) {
            findViewById(R.id.fleet_create_order_bt).setVisibility(View.GONE);
        }
        if (selectDriverListFromOrder == null) {
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
        } else {
            if (isSelectDriver)
                setPageName("接单司机列表");
            else if (isShowLoadingDriver)
                setPageName("司机运输状态");
            initSelectDriverModel();
        }
    }

    /**
     * 司机端初始化车队列表，这里做判断，显示哪个页面
     * 司机端的车队 有两个页面：我加入的车队列表；车队列表
     */
    private void initDriverView_MotorcadesList() {
        if (motorcadeId < 0) {
            if (User.getInstance().getJsonTypeEntity() == null) {
                showToast("请先完善信息");
                finish();
                return;
            }
            findViewById(R.id.fleet_create_order_bt).setVisibility(View.GONE);
            findViewById(R.id.fleet_button_ly).setVisibility(View.GONE);
            setPageName("我加入的车队");
            JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
            requestGetMotorcadesList(jsonDriver);
        } else {
//                setContentView(R.layout.activity_fleet_devices);
            //因为企业没有门头照片等信息，所以取消照片显示
//            View headView = getLayoutInflater().inflate(R.layout.activity_fleet_devices, null);
//            listView.addHeaderView(headView);
            setPageName("车队详情");
            findViewById(R.id.fleet_finish_bt).setVisibility(View.GONE);
            findViewById(R.id.fleet_create_order_bt).setVisibility(View.GONE);
            Button button = (Button) findViewById(R.id.fleet_add_bt);
            button.setText("退出车队");
            button.setVisibility(View.VISIBLE);
            requestGetMotorcades(motorcadeId);
        }
    }

    private void initSelectDriverModel() {
        findViewById(R.id.fleet_add_bt).setVisibility(View.GONE);
//                if (adapter == null)
//                    adapter = new FleetListAdapter(AppParams.DRIVER_APP ? true : isSelectDriver, needSelectDriverSize);
//                adapter.setPeoples(selectDriverListFromOrder);
//                initView();

        ArrayList<JsonCar> array = new ArrayList<>();
//        ResponseAllocationDriver json;
        JsonCar jsonCar;
        for (ResponseAllocationDriver json : selectDriverListFromOrder) {
//            json = BaseEntity.ParseEntity(people.getJsonString(), ResponseAllocationDriver.class);
            if (json != null)
                if (json.getCars() != null && !json.getCars().isEmpty()) {
                    jsonCar = json.getCars().get(0);
                    jsonCar.setName(json.getName());
                    jsonCar.setPhoneNumber(json.getPhoneNumber());
                    jsonCar.setReceiveStatus(json.getReceiveStatus());
                    array.add(jsonCar);
                }
        }

        adapterDrivers = new SourceCarListAdapter(array);
        if (isSelectDriver) {
            adapterDrivers.initSelsect(needSelectDriverSize);
            if (selectedDrivers != null) {
                ResponseAllocationDriver allocationDriver;
                for (People people : selectedDrivers) {
                    for (int k = 0; k < selectDriverListFromOrder.size(); k++) {
                        allocationDriver = selectDriverListFromOrder.get(k);
                        if (people.getId() == allocationDriver.getRoleId()) {
                            adapterDrivers.selectPosition(k, true);
                            break;
                        }
                    }
                }
            }
        }
        if (isShowLoadingDriver) {
            adapterDrivers.initLoadinStatus(new SourceCarListAdapter.BtnClickListener() {

                @Override
                public void onClick(int position) {
                    if (isShowLoadingDriverEdit) {
                        if (position > -1 && position < selectDriverListFromOrder.size()) {
                            ResponseAllocationDriver driver = selectDriverListFromOrder.get(position);
                            if (driver != null)
                                requestLoadingAccept(driver);
                        }
                    } else {
                        showToast("您无权更改此状态");
                    }
                }
            });
        }

        listView.setAdapter(adapterDrivers);
        if (isSelectDriver) {
            adapterDrivers.initSelectDriver();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    gotoDriverDetail(position, (JsonCar) parent.getAdapter().getItem(position), true);
                }
            });
        }
    }


    private void initData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            //标记是否为选择模式  只有在信息部call车时才有可能触发
            if (bundle.containsKey(AppParams.RESULT_CODE_KEY)) {
                if (bundle.getInt(AppParams.RESULT_CODE_KEY) == AppParams.RESULT_CODE_SELECT_DEVICES) {
                    isSelectDriver = true;
                    needSelectDriverSize = bundle.getInt(AppParams.SELECT_DEVICES_SIZE_KEY);
                    selectedDrivers = bundle.getParcelableArrayList(AppParams.SELECTED_DRIVERS_LIST);
                } else if (bundle.getInt(AppParams.RESULT_CODE_KEY) == AppParams.RESULT_CODE_ACCEPT_DRIVERS) {
                    //展示司机 的 运输状态
                    isShowLoadingDriver = true;
                    isShowLoadingDriverEdit = bundle.getBoolean(AppParams.SELECTED_DRIVERS_EDIT, false);
                }
                if (bundle.containsKey(AppParams.SELECT_DRIVES_LIST_KEY)) {
                    ArrayList<String> list = bundle.getStringArrayList(AppParams.SELECT_DRIVES_LIST_KEY);
                    selectDriverListFromOrder = BaseEntity.ParseArrayToEntity(list, ResponseAllocationDriver.class);
//                    selectDriverListFromOrder = bundle.getParcelableArrayList(AppParams.SELECT_DRIVES_LIST_KEY);
                }
            }
            // 标记是否为车队详情页
            if (bundle.containsKey(AppParams.BUNDLE_MOTOCARDE_ID)) {
                motorcadeId = bundle.getInt(AppParams.BUNDLE_MOTOCARDE_ID);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppParams.RESULT_CODE_CONFIRM_DRIVER) {
            if (currentDriverIndex > -1) {
                if (data != null) {
                    adapterDrivers.selectPosition(currentDriverIndex, data.getBooleanExtra(AppParams.SOURCE_PAGE_RESULT_SELECT_KEY, false));
                }
            }
        }
    }


    AdapterView.OnItemClickListener onItemSelectClickListener;
    FleetListAdapter.DelBtnListener delBtnListener;

    private void initView() {

        if (!TextUtils.isEmpty(motorcade_name)) {
            setPageName(motorcade_name);
        }


        if (adapterDrivers == null) {
            return;
        }
        listView.setAdapter(adapterDrivers);
        if (User.getInstance().getUserType() != Type.DriverType) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                onclick_DelDriver(id, people);
                    showDelDialog(adapterDrivers.getItem(position));
                    return true;
                }
            });
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                position -=1;
//                gotoDriverDetail(position, adapterDrivers.getItem(position), isSelectDriver);
                gotoDriverDetail(position, (JsonCar) parent.getAdapter().getItem(position), isSelectDriver);

            }

        });
//        if (delBtnListener == null)
//            delBtnListener = new FleetListAdapter.DelBtnListener() {
//                @Override
//                public void onClick(int id, People people) {
//                    onclick_DelDriver(id, people);
//                }
//            };
//        adapter.setDelBtnListener(delBtnListener);


    }

    /**
     * @param position
     * @param car
     * @param selectDriverModel 是否为选择司机模式
     */
    private void gotoDriverDetail(int position, JsonCar car, boolean selectDriverModel) {
        if (car == null) {
            Log.w(TAG, "gotoDriverDetail car=" + car);
            return;
        }
        currentDriverIndex = position;
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.SOURCE_PAGE_CAR_KEY, car.toString());
        bundle.putBoolean(AppParams.SOURCE_PAGE_SELECT_DRIVER_KEY, selectDriverModel);
        bundle.putBoolean(AppParams.SOURCE_PAGE_RESULT_SELECT_KEY, adapterDrivers.isSelect(position));

        startActivityForResult(SourceCarDetailActivity.class, bundle, AppParams.RESULT_CODE_CONFIRM_DRIVER);
    }

    public void onClick_FinishSelect(View view) {
        int select_size = adapterDrivers.getSelectedListSize();
//        ArrayList<People> peoples = adapter.getSelectedList();
        if (select_size != needSelectDriverSize) {
            int tmp = needSelectDriverSize - select_size;
            showToast(getString(R.string.need_driver_size, tmp));
            return;
        }
        ArrayList<Integer> selectedList = adapterDrivers.getSelectedList();
        ArrayList<People> peoples = new ArrayList<>();
        People people;
        for (int index : selectedList) {
            if (selectDriverListFromOrder == null) {
                JsonCar jsonCar = adapterDrivers.getItem(index);
                people = new People(jsonCar);
            } else {
                ResponseAllocationDriver json;
                json = selectDriverListFromOrder.get(index);
                people = new People(json);

            }
            peoples.add(people);
        }
        returnOrderDetial(peoples);
    }

    //选择完成
    private void returnOrderDetial(ArrayList<People> peoples) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppParams.SELECTED_DRIVERS_LIST, peoples);
//                    intent.putExtra(Constant.RESULT_CODE_KEY,people);
        intent.putExtras(bundle);
        setResult(AppParams.RESULT_CODE_SELECT_DEVICES, intent);
        finish();
    }

    /**
     * 初始化 车队列表
     *
     * @param list
     */
    private void initDriverView_MotorcadesList(List<JsonMotorcades> list) {
        final FleetListForDevicesAdapter adapter = new FleetListForDevicesAdapter(list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JsonMotorcades motorcades = (JsonMotorcades) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt(AppParams.BUNDLE_MOTOCARDE_ID, motorcades.getId());
                startNewActivity(FleetActivity.class, bundle);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppParams.DRIVER_APP) {

        } else {
//            User.getInstance().setDriverList(((FleetListAdapter) listView.getAdapter()).getList());
        }
    }

    private void requestGetMotorcadesList(JsonDriver jsonDriver) {
        showProgress("正在获取车队列表...");
        HttpHelper.getInstance().get(AppURL.GetDriversListMotorcade, jsonDriver, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                //// TO DO: 2016-1-26  司机所属的车队列表更新
                List<JsonMotorcades> list = ParseJson_Array(result, JsonMotorcades.class);
                initDriverView_MotorcadesList(list);
                cancelProgress();
            }

            @Override
            public void onFailed(String str) {
                showToast("司机所属车队列表获取失败");
                cancelProgress();
                finish();
            }
        });
    }

    private void reuqestGetMotorcadesList() {
        //按角色和id获取车队列表
        String params = "" + User.getInstance().getId();
        HttpHelper.getInstance().get(AppURL.GetMotorcadesList + params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {
                showToast("车队列表获取失败");
            }
        });
    }

    private void requestGetMotorcades(int motorcadeId) {
        showProgressOnUI("正在获取车队列表...");
        //获取车队信息

        HttpHelper.getInstance().get(AppURL.GetMotorcades, String.valueOf(motorcadeId), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

                ResponseMotorcades json = ParseJson_Object(result, ResponseMotorcades.class);


                List<JsonMotocardesDriver> motorcadeDrivers = json.getMotorcadeDrivers();
                List<JsonCar> carList = json.getCars();
                if (motorcadeDrivers != null && !motorcadeDrivers.isEmpty()) {
                    //// TO DO: 2016-1-25  车队列表信息更新；
                    if (carList.size() != motorcadeDrivers.size()) {
                        Log.w(TAG, "数据不统一! car size =" + carList.size() + " motorcade drivers size = " + motorcadeDrivers.size());
                    }
                    if (User.getInstance().getUserType() != Type.DriverType) {
                        // company
                        JsonCar car;
                        for (int k = 0; k < carList.size(); k++) {
                            car = carList.get(k);
                            for (JsonMotocardesDriver driver : motorcadeDrivers) {
//                            driver = motorcadeDrivers.get(k);
                                if (driver.getDriverId() == car.getDriverId()) {
                                    car.setMotocardesDriverId(driver.getMotorcadeId());
                                    car.setName(driver.getName());
                                    car.setPhoneNumber(driver.getPhoneNumber());
                                    break;
                                }
                            }
                        }
                        adapterDrivers = new SourceCarListAdapter(carList);
                        adapterDrivers.initSelsect(needSelectDriverSize);
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
                        motorcade_name = json.getMotorcade().getName();
                        company = json.getCompany();

                        ArrayList<String> list = new ArrayList<String>();
                        for (JsonMotocardesDriver driver : motorcadeDrivers) {
                            list.add(driver.getName());
                        }

                        final AppTextListAdapter adapter = new AppTextListAdapter(list);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(motorcade_name)) {
                                    setPageName(motorcade_name);
                                }
                                listView.setAdapter(adapter);

                                //数据返回中 只有company的信息，没有 enterprise的信息，所以取消门头照片等信息的显示；后期优化；
                                /*if (company != null) {
                                    setTextView(R.id.fleet_devices_cp_name_tx, company.getName());
                                    setTextView(R.id.fleet_devices_cp_address_tx, company.getAddress());

                                    //距离
                                    Location location = User.getInstance().getLocation();
                                    if (location != null) {
                                        float latitude = Float.valueOf(location.getLatitude());
                                        float longitude = Float.valueOf(location.getLongitude());
                                        String distance = null;
                                        if (company.getLatitude() != 0 && company.getLongitude() != 0)
                                            distance = Tools.ComputeDistance(latitude, longitude, company.getLatitude(), company.getLongitude());
                                        if (TextUtils.isEmpty(distance)) {
                                            setTextView(R.id.fleet_devices_cp_location, "距离未知");
                                        } else {
                                            String str = getString(R.string.location_distance_km);
                                            str = String.format(str, distance);
                                            setTextView(R.id.fleet_devices_cp_location, str);
                                        }
                                    }
                                    //照片初始化
                                    AdViewFlipper imgFlipper = (AdViewFlipper) findViewById(R.id.fleet_devices_flipper);
                                    if (imgFlipper != null) {
                                        imgFlipper.addView(company.getPhotoUrl());
                                        boolean is_stop_flipping = true;
                                        if (!TextUtils.isEmpty(company.getPhotoUrl2())) {
                                            imgFlipper.addView(company.getPhotoUrl2());
                                            is_stop_flipping = false;
                                        }
                                        if (!TextUtils.isEmpty(company.getPhotoUrl3())) {
                                            imgFlipper.addView(company.getPhotoUrl3());
                                            is_stop_flipping = false;
                                        }

                                        if (is_stop_flipping)
                                            imgFlipper.stopFlipping();
                                    }
                                }*/
                            }
                        });
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
                cancelProgress();
            }

            @Override
            public void onFailed(String str) {

                cancelProgress();
                showToast("车队信息获取失败");
            }
        });

    }


    private void showDelDialog(JsonCar jsonCar) {
        //删除是用 motocardes对象的id；
        final int driverId = jsonCar.getMotocardesDriverId();
        ConfirmDialog.showConfirmDialog(this, "确认删除车队司机", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FleetListAdapter adp = (FleetListAdapter) listView.getAdapter();
//                adp.removePeople(id);
                //to do 接口 删除车队 队员
                requestDelDriver(driverId);
            }
        });
    }

    Runnable notifyRunable;

    private void notifyListAdapter() {
        if (notifyRunable == null)
            notifyRunable = new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            };
        runOnUiThread(notifyRunable);
    }

    //删除车队司机
    private void requestDelDriver(int driverId) {

        HttpHelper.getInstance().del(HttpHelper.toString(AppURL.DeleteMotorcadeDriver.toString(), new String[]{"id=" + driverId}), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("删除成功");
//                notifyListAdapter();
                requestGetMotorcades(motorcadeId);
            }

            @Override
            public void onFailed(String str) {
                showToast("删除失败" + str);
            }
        });
    }

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

    public void onClick_Map(View view) {
        if (company == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "物流公司位置");
        bundle.putString(AppParams.WEB_PAGE_URL, String.format(AppURL.LOCATION_MAP_COMPANY.toString(), company.getId()));
        startActivity(WebViewActivity.class, bundle);
    }


    public void onclick_AddDriver(View view) {
        if (AppParams.DRIVER_APP) {
            showQuitDialog();
            return;
        }
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

    private void showQuitDialog() {
        ConfirmDialog.showConfirmDialog(this, "确认退出车队？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    ArrayList<People> list = adapter.getList();
                Iterator<People> iterator = adapter.getList().iterator();
                String phoneNum = User.getInstance().getPhoneNum();
                People people;
                int id = -1;
                while (iterator.hasNext()) {
                    people = iterator.next();
                    if (phoneNum.equals(people.getPhoneNumber())) {
                        id = people.getId();
                        iterator.remove();
                        break;
                    }
                }
                if (id > 0)
                    requestDelDriver(id);
                else
                    showToast("您不在车队里");
            }
        });
    }

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


    private void requestLoadingAccept(ResponseAllocationDriver driver) {
        // 企业 确认司机装货
        RequestOrderAccept_Reject params = new RequestOrderAccept_Reject();
        params.setOrderId(driver.getOrderId());
        params.setRole(driver.getRole());
        params.setRoleId(driver.getRoleId());
//        params.setRole(User.getInstance().getUserType().toRole());
//        params.setRoleId(User.getInstance().getRoleId());
        HttpHelper.getInstance().post(AppURL.PostConfirmLoading, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("确认装货成功");
                // TO DO: 2016-4-1 这里需要从新请求数据，刷新页面
                setResult(AppParams.RESULT_CODE_ACCEPT_DRIVERS);
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("确认装货失败");
            }
        });
    }

    public void onclick_QuitFleet(View view) {
        showToast("正在申请退出！");
    }

}
