package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.OrderStatus;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.InputDialog;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonOrder;

import java.util.List;


/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderDetailActivity extends BaseActivity {


    OrderDetailMode currentMode = OrderDetailMode.EmptyMode;
    private Product product;
    private List<People> selectedDrivers;
    JsonOrder jsonOrder = new JsonOrder();//如果没有则创建一个订单
    OrderStatus orderStatus = OrderStatus.Empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);
        setPageName("订单详情");

        initData();

        initView();
        initView_JsonOrder();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppParams.BUNDLE_PRODUCT_KEY)) {
                product = (Product) bundle.get(AppParams.BUNDLE_PRODUCT_KEY);
            }
            if (bundle.containsKey(AppParams.ORDER_DETAIL_KEY_ORDER)) {
                jsonOrder = bundle.getParcelable(AppParams.ORDER_DETAIL_KEY_ORDER);
                orderStatus = OrderStatus.parseStatus(jsonOrder.getStatus());
            }
            if (bundle.containsKey(AppParams.ORDER_DETAIL_KEY_TYPE)) {
                //根据传入的参数判断显示模式
                int type = bundle.getInt(AppParams.ORDER_DETAIL_KEY_TYPE);
                currentMode = OrderDetailMode.parse(type);
//                if (currentMode == OrderDetailMode.UntreatedMode) {
//                    setCurrentModeForUser();
//                }

            }
        }
        if (currentMode == OrderDetailMode.EmptyMode) {
//            setCurrentModeForUser();
            showToast("订单状态为空");
        }
    }

    private void initView_JsonOrder() {
        if (jsonOrder == null)
            return;
        setTextView(R.id.order_detail_number_tx, String.valueOf(jsonOrder.getId()));
        setTextView(R.id.order_detail_enterprise_tx, "企业ID=" + jsonOrder.getEnterpriseId());
        setTextView(R.id.order_detail_company_tx, "物流ID=" + jsonOrder.getCompanyId());
        setTextView(R.id.order_detail_start_city_tx, jsonOrder.getStartCity());
        setTextView(R.id.order_detail_stop_city_tx, jsonOrder.getStopCity());
        setTextView(R.id.order_detail_type_tx, jsonOrder.getGoodsType());
        setTextView(R.id.order_detail_goods_name_tx, jsonOrder.getGoodsName());
        setTextView(R.id.order_detail_goods_weight_tx, jsonOrder.getGoodsWeight());
        setTextView(R.id.order_detail_volume_tx, jsonOrder.getGoodsVolume());
        setTextView(R.id.order_detail_receiver_name, jsonOrder.getReceiverName());
        setTextView(R.id.order_detail_receiver_phone_tx, jsonOrder.getReceiverPhone());
        if (jsonOrder.getDriverCount() > 0)
            setTextView(R.id.order_detail_driver_size_ed, jsonOrder.getDriverCount() + "辆");
//        setTextView(R.id.order_detail_volume_tx,jsonOrder.getGoodsVolume());


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppParams.RESULT_CODE_SELECT_DEVICES) {
            //车队选择返回
            if (data != null) {
                List<People> peoples = data.getParcelableArrayListExtra(AppParams.RESULT_CODE_KEY);
                if (peoples != null) {
                    selectedDrivers = peoples;
                }
//                People people = data.getParcelableExtra(AppParams.RESULT_CODE_KEY);
//                if (people != null) {
//                    Log.d(TAG, ">>>>>>>>>>>>>>>>" + people.getPhoneNumber() + "," + people.getName());
////                Toast.makeText(this,people.getPhoneNumber(),Toast.LENGTH_SHORT).show();
//                    EditText edit = (EditText) findViewById(R.id.order_detail_phone);
//                    edit.setText(people.getPhoneNumber());
//                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void initView() {
        Type userType = User.getInstance().getUserType();
        switch (currentMode) {
            case CreateMode:
                //企业模式
                if (product != null) {
                    setTextView(R.id.order_detail_submit, "下单");
                    setTextView(R.id.order_detail_company_tx, product.getName());
                    setTextView(R.id.order_detail_enterprise_tx, User.getInstance().getUserName());
                } else {
                    findViewById(R.id.order_detail_submit).setVisibility(View.INVISIBLE);
                }

                JsonEnterprise enterprise = User.getInstance().getJsonTypeEntity();
                setTextView(R.id.order_detail_enterprise_tx, enterprise.getName());
                break;
            case UntreatedMode://信息部模式
                setTextView(R.id.order_detail_submit, "提交订单");
                if (userType == Type.DriverType) {
                    findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                    setTextView(R.id.order_detail_submit, "抢单");
                }
                break;
            case CompletedMode:
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "订单已完成");
                break;
            case SubmittedMode:
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "订单已提交");
                break;
        }
    }

    private void showChooseCallDialog() {
//        new AlertDialog.Builder(this).setNegativeButton("呼叫车队",null)
//                .setPositiveButton("Call车",null).show();

//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_call, null);
//        final AlertDialog progress = new AlertDialog.Builder(this).setView(view).create();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progress.cancel();
                if (v.getId() == R.id.dialog_call_me_btn) {
                    gotoFleet();
                } else {
                    finish();
                }
            }
        };
//        view.findViewById(R.id.dialog_call_me_btn).setOnClickListener(listener);
//        view.findViewById(R.id.dialog_call_all_btn).setOnClickListener(listener);
//        progress.show();

        BaseDialog baseDialog = new BaseDialog(this);
        baseDialog.setView(R.layout.dialog_call);
        baseDialog.setOnClickListener(R.id.dialog_call_me_btn, listener);
        baseDialog.setOnClickListener(R.id.dialog_call_all_btn, listener);
        baseDialog.show();
    }

    private void gotoFleet() {
        startActivity(FleetActivity.class);
    }

    /**
     * 地址选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(final View view) {
        LocationView locationView = LocationView.createDialog(this).setListener(new LocationView.ChangingListener() {
            @Override
            public void onChanged(String province, String city) {
                if (TextUtils.isEmpty(city))
                    return;

                if (view != null && view instanceof TextView) {
                    String params = province + "·" + city;
                    ((TextView) view).setText(params);
                    if (jsonOrder != null) {
                        if (view.getId() == R.id.order_detail_start_city_tx)
                            jsonOrder.setStartCity(params);
                        else if (view.getId() == R.id.order_detail_stop_city_tx) {
                            jsonOrder.setStopCity(params);
                        }
                    }
                }
            }
        });
        locationView.setCurrentLocation(User.getInstance().getLocation());
        locationView.show();
    }

    /**
     * 货物类型 选择事件
     *
     * @param view
     */
    public void onClick_ChangeType(View view) {
        final TextView textView = (TextView) view;

        BaseDialog.showChooseItemsDialog(this, getString(R.string.order_change_type_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
                        textView.setText(text);
                        jsonOrder.setGoodsType(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_type_items));

    }

    public void onClick_InputGoodsName(View view) {
        InputDialog dialog = new InputDialog(this);
        dialog.setEditHintString("请输入货物名称");
        dialog.setCallback(new InputDialog.Callback() {
            @Override
            public void inputCallback(String string) {
                jsonOrder.setGoodsName(string);
                setTextView(R.id.order_detail_goods_name_tx, string);
            }
        });
        dialog.show();
    }

    /**
     * 车型 选择事件
     *
     * @param view
     */
    public void onClick_InputVolume(View view) {
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setEditHintString("请输入货物体积")
                .setSuffixString("立方米")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        jsonOrder.setGoodsVolume(string);
                        setTextView(R.id.order_detail_volume_tx, string+"立方米");
                    }
                }).show();
//        final TextView textView = (TextView) view;
//        BaseDialog.showChooseItemsDialog(this, getString(R.string.order_change_truck_type_title), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v != null && v instanceof TextView) {
//                    TextView tx = (TextView) v;
//                    CharSequence text = tx.getText();
//                    if (TextUtils.isEmpty(text))
//                        return;
//                    textView.setText(text);
//                    jsonOrder.setNeedCarType(text.toString());
//                }
//            }
//        }, getResources().getStringArray(R.array.order_change_truck_type_items));
    }

    /**
     * 车长选择
     *
     * @param view
     */
    public void onClick_ChangeTruckLength(View view) {

        final TextView textView = (TextView) view;
        BaseDialog.showChooseItemsDialog(this, getString(R.string.order_change_truck_length_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (TextUtils.isEmpty(text))
                        return;
                    textView.setText(text);
                    jsonOrder.setNeedCarLength(text.toString());
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_length_items));
    }

    /**
     * 货物重量选择
     *
     * @param view
     */
    public void onClick_ChangeWeight(View view) {
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setEditHintString("请输入货物重量")
                .setSuffixString("吨")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        jsonOrder.setGoodsWeight(string);
                        setTextView(R.id.order_detail_goods_weight_tx, string+"吨");
                    }
                }).show();

//        final TextView textView = (TextView) view;
//        BaseDialog dialog = new BaseDialog(this);
//        dialog.setView(R.layout.order_dialog_weight).setOnClickListener(R.id.order_dialog_weight_btn, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView tx = (TextView) v;
//                CharSequence text = tx.getText();
//                if (TextUtils.isEmpty(text))
//                    return;
//                textView.setText(text);
//                jsonOrder.setGoodsWeight(text.toString());
//            }
//        });
//        dialog.show();
    }

    //提交订单按钮
    public void onClick_SubmitOrder(View view) {
//        TextView editText = (TextView) findViewById(R.id.order_detail_open_ed);
        switch (currentMode) {
            case CreateMode:
                //企业 向 信息部下单
                requestCreateOrder();
                break;
            case UntreatedMode:
                if (jsonOrder == null) {
                    showToast("订单内容为空");
                    return;
                }

                TextView need_driver_size = (TextView) findViewById(R.id.order_detail_driver_size_ed);
                if (TextUtils.isEmpty(need_driver_size.getText())) {
                    showToast("司机数量未填写");
                    return;
                }
                jsonOrder.setDriverCount(Integer.valueOf(need_driver_size.getText().toString()));
                //先修改订单内容
                requestUpdateOrder();
                //先判断有没有选司机
                if (selectedDrivers == null) {
                    showToast("请先选择司机");
                    return;
                }

                showChooseCallDialog();
                break;
        }

    }

    private void requestUpdateOrder() {
        //信息部修改订单
        HttpHelper.getInstance().put(AppURL.PutOrder, jsonOrder, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("订单更新失败" + str);
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("订单更新成功" + msg);
            }
        });
    }


    private void requestCreateOrder() {
//        showProgress("下单中...");
        //企业向信息部下单，生成的临时订单会推送的信息部，由信息部完善订单信息

//        JsonOrder jsonOrder = new JsonOrder();
        jsonOrder.setEnterpriseId(User.getInstance().getEnterpriseID());
        jsonOrder.setCompanyId(product.getID());
//        jsonOrder.setPublishDate(new Date(2015,));

        HttpHelper.getInstance().post(AppURL.PostCreatOrders, jsonOrder, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("下单成功" + msg);
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("下单失败" + str);
            }
        });
    }

    private int getNeedDriverNum() {
        EditText needDriverNum = (EditText) findViewById(R.id.order_detail_driver_size_ed);
        if (TextUtils.isEmpty(needDriverNum.getText())) {
            return -1;
        }
        return Integer.valueOf(needDriverNum.getText().toString());
    }

    //选择 司机 call车
    public void onClick_SelectDriverBtn(View view) {
        int needDriverNum = getNeedDriverNum();
        if (needDriverNum < 0) {
            showToast("请先填写车辆数");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(AppParams.RESULT_CODE_KEY, AppParams.RESULT_CODE_SELECT_DEVICES);
        bundle.putInt(AppParams.SELECT_DEVICES_SIZE_KEY, needDriverNum);
        startActivityForResult(FleetActivity.class, bundle, AppParams.RESULT_CODE_SELECT_DEVICES);
    }
}
