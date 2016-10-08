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
import com.bt.zhangzy.logisticstraffic.adapter.DriverListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListForDevicesAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.SourceCarListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
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
import com.bt.zhangzy.network.entity.ResponseMotorcades;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.app.AppProgress;
import com.zhangzy.base.http.BaseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 负责给司机显示 加入的车队列表
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetActivity extends BaseActivity {

    ListView listView;
    FleetListAdapter adapter;
    SourceCarListAdapter adapterDrivers;
    DriverListAdapter driverListAdapter;
    int currentDriverIndex = -1;
    //    boolean isSelectDriver = false;// 选择司机
//    boolean isShowLoadingDriver = false;//显示司机的运输状态
//    boolean isShowLoadingDriverEdit = false;//标记是否可以确认装货
    int motorcadeId = -1;
//    private int needSelectDriverSize;//需要选择的司机数量
//    private ArrayList<ResponseAllocationDriver> selectDriverListFromOrder;// 待选司机列表
//    private ArrayList<People> selectedDrivers;


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
//            initCompanyView();

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


    private void initData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
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
                gotoDriverDetail(position, (JsonCar) parent.getAdapter().getItem(position), false);

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
    }

    private void requestGetMotorcadesList(JsonDriver jsonDriver) {
        AppProgress.getInstance().showProgress(this, "正在获取车队列表...");
        HttpHelper.getInstance().get(AppURL.GetDriversListMotorcade, jsonDriver, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                //// TO DO: 2016-1-26  司机所属的车队列表更新
                List<JsonMotorcades> list = ParseJson_Array(result, JsonMotorcades.class);
                initDriverView_MotorcadesList(list);
                AppProgress.getInstance().cancelProgress();
            }

            @Override
            public void onFailed(String str) {
                showToast("司机所属车队列表获取失败");
                AppProgress.getInstance().cancelProgress();
                finish();
            }
        });
    }


    private void requestGetMotorcades(int motorcadeId) {
//        AppProgress.getInstance().showProgress(this, "正在获取车队列表...");
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
                                    car.setMotocardesDriverId(driver.getId());
                                    car.setName(driver.getName());
                                    car.setPhoneNumber(driver.getPhoneNumber());
                                    break;
                                }
                            }
                        }
                        adapterDrivers = new SourceCarListAdapter(carList);
                        adapterDrivers.initSelsect(0);
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

                        ArrayList<People> list = new ArrayList<People>();
                        People p;
                        for (JsonMotocardesDriver driver : motorcadeDrivers) {
                            p = new People();
                            p.setName(driver.getName());
                            p.setPhoneNumber(driver.getPhoneNumber());
                            p.setId(driver.getId());
                            list.add(p);
                        }

                        final AppTextListAdapter adapter = new AppTextListAdapter(list);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(motorcade_name)) {
                                    setPageName(motorcade_name);
                                }
                                listView.setAdapter(adapter);

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
                AppProgress.getInstance().cancelProgress();
            }

            @Override
            public void onFailed(String str) {

                AppProgress.getInstance().cancelProgress();
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

    //删除车队司机
    private void requestDelDriver(int driverId) {

        JsonCallback jsonCallback = new JsonCallback() {
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
        };

        HttpHelper.getInstance().del(HttpHelper.toString(AppURL.DeleteMotorcadeDriver.toString(), new String[]{"id=" + driverId}), jsonCallback);
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
                if (listView.getAdapter() != null && listView.getAdapter() instanceof AppTextListAdapter) {
                    AppTextListAdapter adp = (AppTextListAdapter) listView.getAdapter();
                    Iterator<People> iterator = adp.getList().iterator();
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


}
