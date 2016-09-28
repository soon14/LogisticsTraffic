package com.bt.zhangzy.logisticstraffic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.activity.DriverDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.CarListDriverAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.RequestDriverList;
import com.bt.zhangzy.network.entity.RequestSaveDriver;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.app.AppToast;

import java.util.List;

/**
 * 驾驶员列表管理
 * Created by ZhangZy on 2016-8-24.
 */
public class DriverListFragment extends Fragment {

    final static String TAG = DriverListFragment.class.getSimpleName();
    private View layoutView;
    private ListView listView;
    private ListAdapter adapter;
    boolean isFiristOpen = false;

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
        } else {
//            initListView(view);

            return layoutView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFiristOpen)
            requestDriverList();
    }

    /**
     * 页面初始化
     *
     * @param view
     */
    private void initListView(View view) {
        isFiristOpen = true;
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null && adapter instanceof CarListDriverAdapter) {
                    CarListDriverAdapter driverAdapter = (CarListDriverAdapter) adapter;
                    JsonDriver driver = driverAdapter.getItem(position);
                    showDelDriverDialog(driver);
                    return true;
                }
                return false;
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
//                requestAddDriver(phoneNumber);
                showAddDriverDialog();
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


    private void showDelDriverDialog(final JsonDriver driver) {
        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setMessage("删除司机" + driver.getName() + "？")
                .setConfirm("删除")
                .setCancel("取消")
                .setListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void onClick(boolean isConfirm) {
                        if (isConfirm) {
                            requestDelDriver(driver.getPhoneNumber());
                        }
                    }
                })
                .show();
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
     * 数据列表请求；
     */
    public void requestDriverList() {

        RequestDriverList params = new RequestDriverList();
        params.setOwnerId((int) User.getInstance().getId());
        HttpHelper.getInstance().post(AppURL.GetDriverList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

//                Log.d(TAG, msg + "==>" + result);

                List<JsonDriver> list = JsonDriver.ParseArray(result, JsonDriver.class);
                if (list == null || list.isEmpty()) {
                    AppToast.getInstance().showToast(getActivity(), "您还没有添加过司机哦！");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAdapter(null);
                        }
                    });
                    return;
                }
                User.getInstance().setJsonDriverList(list);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(new CarListDriverAdapter(User.getInstance().getJsonDriverList(), false));
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
     * 删除司机
     *
     * @param phoneNumber
     */
    public void requestDelDriver(String phoneNumber) {

        RequestSaveDriver params = new RequestSaveDriver();
        params.setCarOnwerId((int) User.getInstance().getId());
        params.setDirverPhone(phoneNumber);
        HttpHelper.getInstance().del(AppURL.PostDeleteDriver, params, new JsonCallback() {
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


}
