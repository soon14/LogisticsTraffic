package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.CarListDriverAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.RequestBindCarDriver;
import com.bt.zhangzy.network.entity.RequestDriverList;
import com.bt.zhangzy.network.entity.RequestSaveDriver;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.app.AppToast;

import java.util.List;

/**
 * 司机列表
 * Created by ZhangZy on 2016-9-7.
 */
public class DriverListActivity extends BaseActivity {


    private ListView listView;
    private CarListDriverAdapter adapter;
    private Car bindCar;
    private int openPosition;
    private boolean isAddCar;//标记为 添加车辆页面跳转

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_list);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(AppParams.CAR_DETAIL_PAGE_CAR_KEY)) {
                bindCar = getIntent().getParcelableExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY);

                Log.d(TAG, "选择需要绑定的司机 : " + bindCar.getNumber());
            }
            if (getIntent().hasExtra(AppParams.CAR_DETAIL_PAGE_ADD_KEY)) {
                isAddCar = getIntent().getBooleanExtra(AppParams.CAR_DETAIL_PAGE_ADD_KEY, false);
            }
            setPageName("选择需要绑定的司机");

        } else
            setPageName("司机列表");

        initListView();
    }

    /**
     * 页面初始化
     */
    private void initListView() {
        if (isAddCar) {
            setTextView(R.id.car_list_finsh_bt, "下一步");
        } else {
            findViewById(R.id.car_list_jump_bt).setVisibility(View.GONE);
        }

        listView = (ListView) findViewById(R.id.car_list_listView);
//        listView.setAdapter(new OrderListAdapter(false));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter != null) {
                    JsonDriver driver = adapter.getItem(position);
                    if (driver != null) {
                        openPosition = position;
                        Bundle bundle = new Bundle();
                        bundle.putString(AppParams.CAR_LIST_PAGE_DRIVER_KEY, driver.toString());
                        bundle.putBoolean(AppParams.CAR_LIST_PAGE_SELECT_MODE, true);
                        startActivityForResult(DriverDetailActivity.class, bundle, AppParams.CAR_DETAIL_REQUEST_CODE);
                    }

                }
            }
        });
        if (adapter != null)
            listView.setAdapter(adapter);
        else {
            requestDriverList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppParams.CAR_DETAIL_REQUEST_CODE) {
            if (openPosition > -1) {
                adapter.setSelectDriver(openPosition);
            }
        }
    }

    /**
     * 完成按钮
     *
     * @param view
     */
    public void onClick_Finish(View view) {
        if (adapter == null)
            return;

        JsonDriver driver = adapter.getSelectDriver();
        if (driver == null) {
            showToast("请选择需要绑定的司机");
        } else {
            requestBindCarDriver(driver);

        }

    }

    /**
     * 跳过按钮
     *
     * @param view
     */
    public void onClick_Jump(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppParams.CAR_DETAIL_PAGE_CAR_KEY, bindCar);
        startActivity(PayActivity.class, bundle);
        finish();
    }

    public void onClick_Add(View view) {
        showAddDriverDialog();
    }

    private void showAddDriverDialog() {
        BaseDialog dialog = new BaseDialog(getActivity());
        dialog.setView(R.layout.dialog_add_driver);
        dialog.setTitle("请输入已注册司机用户的手机号");
        dialog.setOnClickListener(R.id.add_driver_dl_cancel_bt, null).setOnClickListenerForDialog(R.id.add_driver_dl_confirm_bt, new BaseDialog.DialogClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
//                EditText name = (EditText) dialog.findViewById(R.id.add_driver_dl_name_ed);
                EditText phone = (EditText) dialog.findViewById(R.id.add_driver_dl_phone_ed);
                if (TextUtils.isEmpty(phone.getText())) {
                    AppToast.getInstance().showToast(getActivity(), "请检查填写内容");
                    return;
                }
                if (!Tools.IsPhoneNum(phone.getText().toString())) {
                    AppToast.getInstance().showToast(getActivity(), "手机号格式错误");
                    return;
                }

                dialog.cancel();
                requestAddDriver(phone.getText().toString());
            }


        }).show();
    }

    /**
     * 添加司机
     *
     * @param phoneNumber
     */
    public void requestAddDriver(String phoneNumber) {

        RequestSaveDriver params = new RequestSaveDriver();
        params.setCarOnwerId((int) User.getInstance().getId());
        params.setDirverPhone(phoneNumber);
        HttpHelper.getInstance().post(AppURL.PostSaveDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                AppToast.getInstance().showToast(getActivity(), msg);
                requestDriverList();
            }

            @Override
            public void onFailed(String str) {
                AppToast.getInstance().showToast(getActivity(), str);
            }
        });
    }

    /**
     * 数据列表请求；
     */
    public void requestDriverList() {

        RequestDriverList params = new RequestDriverList();
        params.setOwnerId((int) User.getInstance().getId());
        HttpHelper.getInstance().post(AppURL.GetDriverList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

                List<JsonDriver> list = JsonDriver.ParseArray(result, JsonDriver.class);
                if (list == null || list.isEmpty()) {
                    AppToast.getInstance().showToast(getActivity(), "您还没有添加过司机哦！");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(null);
                        }
                    });
                    return;
                }
                User.getInstance().setJsonDriverList(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CarListDriverAdapter(User.getInstance().getJsonDriverList(), bindCar != null);
                        listView.setAdapter(adapter);
                        adapter.setDefaultSelect(bindCar.getPilotId());
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                Log.d(TAG, str);
                AppToast.getInstance().showToast(getActivity(), "数据加载失败");
            }
        });
    }


    /**
     * 绑定司机
     *
     * @param jsonDriver
     */
    private void requestBindCarDriver(final JsonDriver jsonDriver) {

        RequestBindCarDriver params = new RequestBindCarDriver();
        params.setDriverId(jsonDriver.getId());
        params.setCarId(bindCar.getId());

        HttpHelper.getInstance().post(AppURL.PostBindCarDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                if (isAddCar) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppParams.CAR_DETAIL_PAGE_CAR_KEY, bindCar);
                    startActivity(PayActivity.class, bundle);
                    finish();
                } else {
                    Intent data = new Intent();
                    data.putExtra(AppParams.CAR_DETAIL_PAGE_DRIVER_KEY, jsonDriver.toString());
                    data.putExtra(AppParams.CAR_DETAIL_PAGE_CAR_KEY, bindCar);
                    setResult(AppParams.CAR_DETAIL_REQUEST_CODE, data);
                    finish();
                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setTextView(R.id.car_detail_bind_tx, jsonDriver.getName() + "  -  " + jsonDriver.getPhoneNumber());
//                    }
//                });
            }

            @Override
            public void onFailed(String str) {
                showToast(str);
            }
        });
    }
}
