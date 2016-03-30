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
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.adapter.FleetListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListForDevicesAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.SourceCarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonMotocardesDriver;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.ResponseAllocationDriver;
import com.bt.zhangzy.network.entity.ResponseMotorcades;

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
    boolean isSelectDriver = false;
    int motorcadeId = -1;
    private int needSelectDriverSize;//需要选择的司机数量
    private ArrayList<People> selectDriverListFromOrder;
    private ArrayList<People> selectedDrivers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initData();

        setContentView(R.layout.activity_fleet);
        listView = (ListView) findViewById(R.id.fleet_list);

        //TODO 接口 更新车队信息；
        if (AppParams.DRIVER_APP) {
            if (motorcadeId < 0) {
                if (User.getInstance().getJsonTypeEntity() == null) {
                    showToast("请先完善信息");
                    finish();
                    return;
                }
                findViewById(R.id.fleet_button_ly).setVisibility(View.GONE);
                setPageName("我加入的车队");
                JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
                requestGetMotorcadesList(jsonDriver);
            } else {
//                setContentView(R.layout.activity_fleet_devices);
                View headView = getLayoutInflater().inflate(R.layout.activity_fleet_devices, null);
                listView.addHeaderView(headView);
                setPageName("车队详情");
                Button button = (Button) findViewById(R.id.fleet_add_bt);
                button.setText("退出车队");
                button.setVisibility(View.VISIBLE);
                requestGetMotorcades(motorcadeId);
            }
        } else {
            if (!isSelectDriver)
                findViewById(R.id.fleet_finish_bt).setVisibility(View.GONE);
            if (selectDriverListFromOrder == null) {
                setPageName("我的车队");
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
            } else {
                setPageName("接单司机列表");
                initSelectDriverModel();
            }

        }

    }

    private void initSelectDriverModel() {
        findViewById(R.id.fleet_add_bt).setVisibility(View.GONE);
//                if (adapter == null)
//                    adapter = new FleetListAdapter(AppParams.DRIVER_APP ? true : isSelectDriver, needSelectDriverSize);
//                adapter.setPeoples(selectDriverListFromOrder);
//                initView();

        ArrayList<JsonCar> array = new ArrayList<>();
        ResponseAllocationDriver json;
        JsonCar jsonCar;
        for (People people : selectDriverListFromOrder) {
            json = BaseEntity.ParseEntity(people.getJsonString(), ResponseAllocationDriver.class);
            if (json != null)
                if (json.getCars() != null && !json.getCars().isEmpty()) {
                    jsonCar = json.getCars().get(0);
                    jsonCar.setName(people.getName());
                    jsonCar.setPhoneNumber(people.getPhoneNumber());
                    array.add(jsonCar);
                }
        }

        adapterDrivers = new SourceCarListAdapter(array);
        adapterDrivers.initSelsect(needSelectDriverSize);
        if (selectedDrivers != null) {
            People people;
            for (People sp : selectedDrivers) {
                for (int k = 0; k < selectDriverListFromOrder.size(); k++) {
                    people = selectDriverListFromOrder.get(k);
                    if (sp == people || sp.getDriverId() == people.getDriverId()) {
                        adapterDrivers.selectPosition(k, true);
                        break;
                    }
                }
            }
        }

        listView.setAdapter(adapterDrivers);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                gotoDriverDetail(position, adapterDrivers.getItem(position), true);
            }
        });
    }

    private void initData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            //标记是否为选择模式  只有在信息部call车时才有可能触发
            if (bundle.containsKey(AppParams.RESULT_CODE_KEY)) {
                if (bundle.getInt(AppParams.RESULT_CODE_KEY) == AppParams.RESULT_CODE_SELECT_DEVICES) {
                    isSelectDriver = true;
                }
                needSelectDriverSize = bundle.getInt(AppParams.SELECT_DEVICES_SIZE_KEY);
                if (bundle.containsKey(AppParams.SELECT_DRIVES_LIST_KEY)) {
                    selectDriverListFromOrder = bundle.getParcelableArrayList(AppParams.SELECT_DRIVES_LIST_KEY);
                }
                selectedDrivers = bundle.getParcelableArrayList(AppParams.SELECTED_DRIVERS_LIST);
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
        if (adapterDrivers == null) {
            return;
        }
        listView.setAdapter(adapterDrivers);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                onclick_DelDriver(id, people);
                showDelDialog(adapterDrivers.getItem(position));
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                gotoDriverDetail(position, adapterDrivers.getItem(position), isSelectDriver);
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
        ResponseAllocationDriver json;
        for (int index : selectedList) {
            if (selectDriverListFromOrder == null) {
                JsonCar jsonCar = adapterDrivers.getItem(index);
                people = new People();
                people.setMotorcadeId(jsonCar.getMotocardesDriverId());
                people.setDriverId(jsonCar.getDriverId());
                people.setId(jsonCar.getDriverId());
                people.setName(jsonCar.getName());
                people.setPhoneNumber(jsonCar.getPhoneNumber());
            } else {
                people = selectDriverListFromOrder.get(index);
                json = BaseEntity.ParseEntity(people.getJsonString(), ResponseAllocationDriver.class);
                if (people.getId() == 0)
                    people.setId(json.getRoleId());
                if (people.getDriverId() == 0) {
                    people.setDriverId(json.getDriver().getId());
                }
                if (people.getUserId() == 0) {
                    people.setUserId(json.getDriver().getUserId());
                }
            }
            peoples.add(people);
        }
        returnOrderDetial(peoples);
    }

    //选择完成
    private void returnOrderDetial(ArrayList<People> peoples) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppParams.RESULT_CODE_KEY, peoples);
//                    intent.putExtra(Constant.RESULT_CODE_KEY,people);
        intent.putExtras(bundle);
        setResult(AppParams.RESULT_CODE_SELECT_DEVICES, intent);
        finish();
    }

    private void initDriverView(List<JsonMotorcades> list) {
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
                //// TODO: 2016-1-26  司机所属的车队列表更新
                List<JsonMotorcades> list = ParseJson_Array(result, JsonMotorcades.class);
                initDriverView(list);
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
                cancelProgress();
                ResponseMotorcades json = ParseJson_Object(result, ResponseMotorcades.class);
                json.getCompany();
                json.getMotorcade();
                List<JsonMotocardesDriver> motorcadeDrivers = json.getMotorcadeDrivers();
                List<JsonCar> carList = json.getCars();
                if (motorcadeDrivers != null && !motorcadeDrivers.isEmpty()) {
                    //// TO DO: 2016-1-25  车队列表信息更新；
                    if (carList.size() != motorcadeDrivers.size()) {
                        Log.w(TAG, "数据不统一! car size =" + carList.size() + " motorcade drivers size = " + motorcadeDrivers.size());
                    }
                    JsonCar car;
                    JsonMotocardesDriver driver;
                    for (int k = 0; k < carList.size(); k++) {
                        car = carList.get(k);
                        driver = motorcadeDrivers.get(k);
                        car.setMotocardesDriverId(driver.getId());
                        car.setName(driver.getName());
                        car.setPhoneNumber(driver.getPhoneNumber());
                    }
                   /* ArrayList<People> list = new ArrayList<People>();
                    People people;
                    for (JsonMotocardesDriver driver : motorcadeDrivers) {
                        people = new People();
                        people.setName(driver.getName());
                        people.setId(driver.getId());
                        people.setUserId(driver.getUserId());
                        people.setDriverId(driver.getDriverId());
                        people.setMotorcadeId(driver.getMotorcadeId());
                        people.setPhoneNumber(driver.getPhoneNumber());
                        list.add(people);
                    }
//                    User.getInstance().setDriverList(list);
                    if (adapter == null)
                        adapter = new FleetListAdapter(AppParams.DRIVER_APP ? true : isSelectDriver, needSelectDriverSize);
                    adapter.setPeoples(list);
//                    adapter.addPeople(User.getInstance().getDriverList());*/

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
                    showToast("车队还没有添加过成员哦！");
                }

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
                //todo 接口 删除车队 队员
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
                    Toast.makeText(context, "请检查填写内容", Toast.LENGTH_SHORT).show();
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

    public void onclick_QuitFleet(View view) {
        showToast("正在申请退出！");
    }

}
