package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.OrderStatus;
import com.bt.zhangzy.logisticstraffic.data.OrderType;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.InputDialog;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.JsonOrder;
import com.bt.zhangzy.network.entity.JsonOrderHistory;
import com.bt.zhangzy.network.entity.RequestCallDriver;
import com.bt.zhangzy.network.entity.RequestOrderAccept_Reject;
import com.bt.zhangzy.network.entity.RequestOrderAllocation;
import com.bt.zhangzy.network.entity.RequestOrderHistroy;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderDetailActivity extends BaseActivity {

    private OrderDetailMode currentMode = OrderDetailMode.EmptyMode;
    private Product product;
    private List<People> selectedDrivers;
    private JsonOrder jsonOrder;
    private OrderStatus orderStatus = OrderStatus.Empty;
    private boolean updateJsonOrder = false;//更新订单的标记
    private ArrayList<People> allocationList;//接单成功的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);
        setPageName("订单详情");

        initData();

        initView();

        if (jsonOrder == null)
            jsonOrder = new JsonOrder();//如果没有则创建一个订单
        else
            initView_JsonOrder();

        if (User.getInstance().getUserType() == Type.CompanyInformationType) {
            //物流公司看到企业派送的订单后自动调用 同意接单的接口
            if (orderStatus == OrderStatus.UncommittedOrder) {
                requestAccept_Reject(true);
            } else if (orderStatus == OrderStatus.AllocationOrder) {
                requestListOrderHistroy();
            }
        }

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
//        setTextView(R.id.order_detail_enterprise_tx, "企业ID=" + jsonOrder.getEnterpriseId());
//        setTextView(R.id.order_detail_company_tx, "物流ID=" + jsonOrder.getCompanyId());
        setTextView(R.id.order_detail_enterprise_tx, jsonOrder.getEnterpriseName());
        setTextView(R.id.order_detail_company_tx, jsonOrder.getCompanyName());
        setTextView(R.id.order_detail_start_city_tx, jsonOrder.getStartCity());
        setTextView(R.id.order_detail_stop_city_tx, jsonOrder.getStopCity());
        setTextView(R.id.order_detail_type_tx, jsonOrder.getGoodsType());
//        setTextView(R.id.order_detail_goods_name_tx, jsonOrder.getGoodsName());
        setTextView(R.id.order_detail_goods_weight_tx, jsonOrder.getGoodsWeight());
        setTextView(R.id.order_detail_volume_tx, jsonOrder.getGoodsVolume());
        setTextView(R.id.order_detail_receiver_name, jsonOrder.getReceiverName());
        setTextView(R.id.order_detail_receiver_phone_tx, jsonOrder.getReceiverPhone());
        if (jsonOrder.getDriverCount() > 0)
            setTextView(R.id.order_detail_driver_size_ed, String.valueOf(jsonOrder.getDriverCount()));
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
        if (userType == Type.EnterpriseType) {
            initView_Enterprise();
        } else if (userType == Type.CompanyInformationType) {
            initView_Company();
        } else if (userType == Type.DriverType) {
            initView_Drivers();

        }
    }

    private void initView_Drivers() {
        switch (currentMode) {
            case CreateMode:

                break;
            case UntreatedMode://未提交订单
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "抢单");
                break;
            case SubmittedMode:
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "验证收货");
                break;
            case CompletedMode:
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
//                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "评价");
                break;
        }
    }

    private void initView_Company() {
        switch (currentMode) {
            case CreateMode:
                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "下单");
                //                    setTextView(R.id.order_detail_company_tx, User.getInstance().getUserName());
                JsonCompany company = User.getInstance().getJsonTypeEntity();
                if (company != null) {
                    setTextView(R.id.order_detail_company_tx, company.getName());
                }
                break;
            case UntreatedMode://未提交订单
                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, "提交订单");
                break;
            case SubmittedMode:
//                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                //修改为定位图标
                findViewById(R.id.order_detail_select_driver_bt).setBackgroundResource(R.drawable.location_btn);
//                setTextView(R.id.order_detail_submit, "订单已提交");
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                break;
            case CompletedMode:
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
//                setTextView(R.id.order_detail_submit, "订单已完成");
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                break;
        }
    }

    //初始化企业页面
    private void initView_Enterprise() {
        switch (currentMode) {
            case CreateMode:
                setTextView(R.id.order_detail_submit, "下单");
                if (product != null) {
                    setTextView(R.id.order_detail_company_tx, product.getName());
                    setTextView(R.id.order_detail_enterprise_tx, User.getInstance().getUserName());
                } else {
                    findViewById(R.id.order_detail_submit).setVisibility(View.INVISIBLE);
                }
                JsonEnterprise enterprise = User.getInstance().getJsonTypeEntity();
                if (enterprise != null)
                    setTextView(R.id.order_detail_enterprise_tx, enterprise.getName());
                break;
            case UntreatedMode://未提交订单

                findViewById(R.id.order_detail_submit).setVisibility(View.INVISIBLE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                break;
            case SubmittedMode:
//                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                //修改为定位图标
                findViewById(R.id.order_detail_select_driver_bt).setBackgroundResource(R.drawable.location_btn);
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                break;
            case CompletedMode:
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
//                setTextView(R.id.order_detail_submit, "订单已完成");
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
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
//                    gotoFleet();
                    //to do call motorcade
                    requestCallMotorcade();
                } else {
//                    finish();
                    //to do  call public
                    requestCallPublic();
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


    /**
     * 检查编辑状态
     *
     * @return 是否 不可以编辑
     */
    private boolean cannotEditStatus() {
        //司机只能查看 不能编辑
        if (AppParams.DRIVER_APP)
            return true;
        if (orderStatus != OrderStatus.Empty
                && orderStatus != OrderStatus.TempOrder
                && orderStatus != OrderStatus.UncommittedOrder)
            return true;
        return false;
    }

    /**
     * 地址选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(View view) {
        if (cannotEditStatus())
            return;

        if (view == null || !(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;
        String string = ViewUtils.getStringFromTextView(textView);
        Location location;
        if (TextUtils.isEmpty(string)) {
            location = User.getInstance().getLocation();
        } else {
            location = Location.Parse(string);
        }
        LocationView.createDialog(this)
                .setCurrentLocation(location)
                .setListener(new LocationView.ChangingListener() {
                    @Override
                    public void onChanged(Location loc) {
                    }

                    public void onCancel(Location loc) {
                        if (loc == null)
                            return;

                        String params = loc.toText();
                        textView.setText(params);
                        if (jsonOrder != null) {
                            updateJsonOrder = true;
                            if (textView.getId() == R.id.order_detail_start_city_tx)
                                jsonOrder.setStartCity(params);
                            else if (textView.getId() == R.id.order_detail_stop_city_tx) {
                                jsonOrder.setStopCity(params);
                            }
                        }
                    }
                }).show();
    }

    /**
     * 货物类型 选择事件
     *
     * @param view
     */
    public void onClick_ChangeType(View view) {
        if (cannotEditStatus())
            return;
        final TextView textView = (TextView) view;

        ChooseItemsDialog.showChooseItemsDialog(this, getString(R.string.order_change_type_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (!TextUtils.isEmpty(text)) {
                        textView.setText(text);
                        updateJsonOrder = true;
                        jsonOrder.setGoodsType(text.toString());
                    }
                }
            }
        }, getResources().getStringArray(R.array.order_change_type_items));

    }


    public void onClick_InputDriverCount(View view) {
        if (cannotEditStatus())
            return;
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setEditHintString("请输入需要的司机数量")
//                .setSuffixString("立方米")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        setTextView(R.id.order_detail_driver_size_ed, string);
                        jsonOrder.setDriverCount(Integer.valueOf(string));
                    }
                }).show();
    }

    public void onClick_InputReceiverName(View view) {
        if (cannotEditStatus())
            return;
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setEditHintString("请输入货主姓名")
//                .setSuffixString("立方米")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        jsonOrder.setReceiverName(string);
                        setTextView(R.id.order_detail_receiver_name, string);
                    }
                }).show();
    }

    public void onClick_InputPhone(View view) {
        if (cannotEditStatus())
            return;
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_PHONE)
                .setEditHintString("请输入货主电话号码")
//                .setSuffixString("立方米")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        jsonOrder.setReceiverPhone(string);
                        setTextView(R.id.order_detail_receiver_phone_tx, string);
                    }
                }).show();
    }

    /**
     * 车型 选择事件
     *
     * @param view
     */
    public void onClick_InputVolume(View view) {
        if (cannotEditStatus())
            return;
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setEditHintString("请输入货物体积")
                .setSuffixString("立方米")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        jsonOrder.setGoodsVolume(string);
                        setTextView(R.id.order_detail_volume_tx, string + "立方米");
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
        if (cannotEditStatus())
            return;
        final TextView textView = (TextView) view;
        ChooseItemsDialog.showChooseItemsDialog(this, getString(R.string.order_change_truck_length_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    CharSequence text = tx.getText();
                    if (TextUtils.isEmpty(text))
                        return;
                    textView.setText(text);
                    updateJsonOrder = true;
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
        if (cannotEditStatus())
            return;
        new InputDialog(this)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setEditHintString("请输入货物重量")
                .setSuffixString("吨")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        jsonOrder.setGoodsWeight(string);
                        setTextView(R.id.order_detail_goods_weight_tx, string + "吨");
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

    /**
     * 车队页面跳转
     *
     * @param needDriverNum
     * @param list          待选司机列表；如果是从车队选择填null
     */
    private void gotoSelectDriver(int needDriverNum, ArrayList<People> list) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppParams.RESULT_CODE_KEY, AppParams.RESULT_CODE_SELECT_DEVICES);
        bundle.putInt(AppParams.SELECT_DEVICES_SIZE_KEY, needDriverNum);
        if (list != null && !list.isEmpty())
            bundle.putParcelableArrayList(AppParams.SELECT_DRIVES_LIST_KEY, list);
        startActivityForResult(FleetActivity.class, bundle, AppParams.RESULT_CODE_SELECT_DEVICES);
    }

    //选择 司机 call车
    public void onClick_SelectDriverBtn(View view) {
//        if (cannotEditStatus())
//            return;
        if (orderStatus == OrderStatus.CommitOrder) {
            gotoMap();
        } else if (orderStatus == OrderStatus.AllocationOrder) {
            //to do 订单分配中 这里需要请求 抢单成功的司机列表
//            showToast("订单分配中 这里需要请求 抢单成功的司机列表");
            gotoSelectDriver(jsonOrder.getDriverCount(), allocationList);
            return;
        }
        int needDriverNum = getNeedDriverNum();
        if (needDriverNum < 0) {
            showToast("请先填写车辆数");
            return;
        }
        gotoSelectDriver(needDriverNum, null);
//        Bundle bundle = new Bundle();
//        bundle.putInt(AppParams.RESULT_CODE_KEY, AppParams.RESULT_CODE_SELECT_DEVICES);
//        bundle.putInt(AppParams.SELECT_DEVICES_SIZE_KEY, needDriverNum);
//        startActivityForResult(FleetActivity.class, bundle, AppParams.RESULT_CODE_SELECT_DEVICES);
    }

    public void gotoMap() {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "运输司机位置");
        bundle.putString(AppParams.WEB_PAGE_URL, String.format(AppURL.LOCATION_MAP_ORDER.toString(), jsonOrder.getId()));
        startActivity(WebViewActivity.class, bundle);
    }

    public void onClick_CallPhone(View view) {

        new ConfirmDialog(this)
                .setMessage("是否拨打客服电话")
                .setConfirm("拨打")
                .setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getApp().callPhone("400");
                    }
                }).show();
    }

    //提交订单按钮
    public void onClick_SubmitOrder(View view) {
        User user = User.getInstance();
//        TextView editText = (TextView) findViewById(R.id.order_detail_open_ed);
        switch (currentMode) {
            case CreateMode://创建订单
                //企业 向 信息部下单
                requestCreateOrder();
                break;
            case UntreatedMode://未提交订单
                if (jsonOrder == null) {
                    showToast("订单内容为空");
                    return;
                }
                //司机抢单
                if (user.getUserType() == Type.DriverType) {
                    submitOrder_Drivers();
                } else if (user.getUserType() == Type.CompanyInformationType) {
                    submitOrder_Company();
                } else {
                    showToast("无权操作订单");
                }
                break;
            case SubmittedMode://已提交订单
                if (user.getUserType() == Type.DriverType) {
                    //验证收货
                    Bundle bundle = new Bundle();
                    bundle.putString(AppParams.BUNDLE_VERIFICATION_ORDER_PHONE_KEY, jsonOrder.getReceiverPhone());
                    bundle.putInt(AppParams.BUNDLE_VERIFICATION_ORDER_ID_KEY, jsonOrder.getId());
                    startActivity(VerificationActivity.class, bundle);

                    finish();
                }
                break;
            case CompletedMode:// 已完成订单
                //跳转评价
                if (user.getUserType() == Type.DriverType) {
                    Bundle bundle = new Bundle();
//                    bundle.putInt(AppParams.BUNDLE_EVALUATION_ROLE,Type.CompanyInformationType.toRole());
                    bundle.putString(AppParams.BUNDLE_EVALUATION_ORDER, jsonOrder.toString());
                    startActivity(EvaluationActivity.class, bundle);
                }
                break;
            default:
                Log.w(TAG, "提交订单 当前模式:" + currentMode);
                break;
        }

    }

    //提交订单 --物流公司
    private void submitOrder_Company() {
        TextView need_driver_size = (TextView) findViewById(R.id.order_detail_driver_size_ed);
        if (TextUtils.isEmpty(need_driver_size.getText())) {
            showToast("司机数量未填写");
            return;
        }

        if (updateJsonOrder) {
            //先修改订单内容
            requestUpdateOrder();
        }
        submitOrder_Company_Call();

    }

    private void submitOrder_Company_Call() {
        //                //先判断有没有选司机
        if (selectedDrivers == null) {
            if (orderStatus == OrderStatus.AllocationOrder
                    && allocationList != null) {
                showToast("请从已抢单司机列表中选择司机");
                // 如果已经从 抢单司机列表中选择了，则直接走callDriver接口进行通知；callDriver成功后会自动调用allocation
            } else {
//                  没有选择司机，或者没有可选的接单列表 弹出CALL车选项
                showChooseCallDialog();
            }
        } else {
            //已经选择了司机 直接进行call车
            requestCallDriver();
//                        requestAllocation();
        }
    }

    // 提交订单 -- 司机
    private void submitOrder_Drivers() {
        new ConfirmDialog(this)
                .setMessage("是否同意接单？")
                .setConfirm("同意")
                .setCancel("拒绝")
                .setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderType type = OrderType.parseOrderType(jsonOrder.getOrderType());
                        //todo 公共订单 抢单 用save_order_history
                        if (type == OrderType.PublicType) {
                            requestSaveOrderHistory();
                        }
                        ///车队货源
                        else if (type == OrderType.MotorcadesType) {
                            requestAccept_Reject(true);
                        }
                    }
                })
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //暂时不发送拒绝接单的请求；
//                                    requestAccept_Reject(false);
                    }
                })
                .show();
//                    requestAccept_Reject(true);
    }

    //物流公司选择接受订单的司机并分配订单
    private void requestAllocation(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            Log.w(TAG, "requestAllocation  list=null");
            return;
        }

        RequestOrderAllocation params = new RequestOrderAllocation();
        params.setOrderId(jsonOrder.getId());
        params.setOrderHistory(list);

        HttpHelper.getInstance().post(AppURL.PostAllocation, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("分配成功");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("分配失败" + str);
            }
        });
    }

    //抢单司机列表
    private void requestListOrderHistroy() {
        RequestOrderHistroy params = new RequestOrderHistroy();
        //这里的role是要显示的role，role=3是物流公司，所以只有一条
        params.setRole(Type.DriverType.toRole());
        params.setOrderId(jsonOrder.getId());
         /*接单状态 未接单=0	已接单=1	已拒绝=2	已同意接单=3	物流公司同意司机接单*/
        params.setStauts(3);

        HttpHelper.getInstance().post(AppURL.PostAllocationDriverList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonOrderHistory> list = ParseJson_Array(result, JsonOrderHistory.class);
                if (list == null || list.isEmpty())
                    return;

                if (allocationList == null)
                    allocationList = new ArrayList<People>();
                if (allocationList.size() > 0)
                    allocationList.clear();
                //筛选 接单成功的列表
                People people;
                for (JsonOrderHistory json : list) {
                    if (json.getStatus() == 1) {
                        people = new People();
                        people.setId(json.getRoleId());
                        people.setDriverId(json.getRoleId());
                        people.setName(json.getName());
                        people.setPhoneNumber(json.getPhoneNumber());
                        allocationList.add(people);
                    }
                }
//
            }

            @Override
            public void onFailed(String str) {
                showToast("抢单司机列表 获取失败" + str);
            }
        });
    }

    private void requestSaveOrderHistory() {
        JsonOrderHistory params = new JsonOrderHistory();
        params.setOrderId(jsonOrder.getId());
        params.setRole(User.getInstance().getUserType().toRole());
        params.setRoleId(User.getInstance().getRoleId());
        params.setStatus(1);/*接单状态 未接单	0	已接单	1 已拒绝	2 已同意接单	3	物流公司同意司机接单*/

        HttpHelper.getInstance().post(AppURL.PostSaveOrderHistory, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("接单成功");
            }

            @Override
            public void onFailed(String str) {
                showToast("接单失败" + str);
            }
        });
    }

    // accept or reject
    private void requestAccept_Reject(boolean accept) {

        RequestOrderAccept_Reject params = new RequestOrderAccept_Reject();
        params.setOrderId(jsonOrder.getId());
        params.setRole(User.getInstance().getUserType().toRole());
        params.setRoleId(User.getInstance().getRoleId());
        HttpHelper.getInstance().post(accept ? AppURL.PostAccept : AppURL.PostReject, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("接单成功");
            }

            @Override
            public void onFailed(String str) {
                showToast("接单失败" + str);
            }
        });
    }

    private void requestCallDriver() {
        RequestCallDriver params = new RequestCallDriver();
        params.setOrderId(jsonOrder.getId());
        params.setDrivesFromPeople(selectedDrivers);
        HttpHelper.getInstance().post(AppURL.PostCallDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("CALL车成功");
                List<JsonOrderHistory> list_order = ParseJson_Array(result, JsonOrderHistory.class);
                ArrayList<Integer> list = new ArrayList<Integer>();
                for (JsonOrderHistory json : list_order) {
                    list.add(json.getId());
                }
                requestAllocation(list);
//                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("CALL车失败" + str);
            }
        });
    }

    private void requestCallPublic() {
        RequestCallDriver params = new RequestCallDriver();
        params.setOrderId(jsonOrder.getId());
//        HashMap<String ,String > params = new HashMap<String ,String >();
//        params.put("orderId",String.valueOf(jsonOrder.getId()));
        HttpHelper.getInstance().post(AppURL.PostCallPublic, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("CALL车成功");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("CALL车失败" + str);
            }
        });
    }

    private void requestCallMotorcade() {
        List<JsonMotorcades> motorcades = User.getInstance().getMotorcades();
        if (motorcades == null || motorcades.isEmpty()) {
            showToast("没有车队");
            return;
        }
        int motorcadeId = motorcades.get(0).getId();
        RequestCallDriver params = new RequestCallDriver();
        params.setOrderId(jsonOrder.getId());
        params.setMotorcadeId(motorcadeId);

        HttpHelper.getInstance().post(AppURL.PostCallMotorcade, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("CALL车成功");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("CALL车失败" + str);
            }
        });
    }

    private void requestUpdateOrder() {
        //信息部修改订单
        HttpHelper.getInstance().put(AppURL.PutOrder, String.valueOf(jsonOrder.getId()), jsonOrder, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("订单更新失败" + str);
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("订单更新成功" + msg);
                //重置跟新标记
                updateJsonOrder = false;
            }
        });
    }


    /**
     * 创建订单
     */
    private void requestCreateOrder() {
//        showProgress("下单中...");
        //企业向信息部下单，生成的临时订单会推送的信息部，由信息部完善订单信息
        User user = User.getInstance();
//        JsonOrder jsonOrder = new JsonOrder();
        if (user.getUserType() == Type.EnterpriseType) {
            jsonOrder.setEnterpriseId(User.getInstance().getEnterpriseID());
            if (product != null)
                jsonOrder.setCompanyId(product.getID());
        } else if (user.getUserType() == Type.CompanyInformationType) {
            //信息部直接下单
            jsonOrder.setCompanyId(user.getCompanyID());
        }
//        jsonOrder.setPublishDate(new Date(2015,));

        HttpHelper.getInstance().post(AppURL.PostCreatOrders, jsonOrder, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("下单成功" + msg);
                requestSendToCompany(result);
                if (User.getInstance().getUserType() == Type.CompanyInformationType) {
                    requestOrder(result);
                }
//                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("下单失败" + str);
            }
        });
    }

    //创建订单后 获取订单
    private void requestOrder(String orderId) {

        HttpHelper.getInstance().get(AppURL.GetOrder + orderId, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                JsonOrder tmp_json = ParseJson_Object(result, JsonOrder.class);
                jsonOrder = tmp_json;
                if (User.getInstance().getUserType() == Type.CompanyInformationType) {
                    submitOrder_Company_Call();
                }
            }

            @Override
            public void onFailed(String str) {
                Log.w(TAG, "订单获取失败" + str);
            }
        });
    }

    //企业将订单指派到物流公司
    private void requestSendToCompany(String orderId) {
        User user = User.getInstance();
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        if (user.getUserType() == Type.EnterpriseType) {
            if (product != null)
                params.put("companyId", String.valueOf(product.getID()));
        } else if (user.getUserType() == Type.CompanyInformationType) {
            params.put("companyId", String.valueOf(user.getCompanyID()));
        }

        HttpHelper.getInstance().post(AppURL.PostSendToCompany, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "通知信息部成功" + msg + " ; result=" + result);
//                showToast("指派成功" + msg);
                //退出下单页面
//                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("通知信息部失败" + str);
            }
        });
    }

    private int getNeedDriverNum() {
        String num_str = getStringFromTextView(R.id.order_detail_driver_size_ed);
        if (TextUtils.isEmpty(num_str)) {
            return -1;
        }
        return Integer.valueOf(num_str);
    }


}
