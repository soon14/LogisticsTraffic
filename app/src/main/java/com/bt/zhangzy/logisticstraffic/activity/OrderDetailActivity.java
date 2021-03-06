package com.bt.zhangzy.logisticstraffic.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.OrderReceiveStatus;
import com.bt.zhangzy.logisticstraffic.data.OrderStatus;
import com.bt.zhangzy.logisticstraffic.data.OrderType;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.CallPhoneDialog;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.InputDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCarTrack;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonLine;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.JsonOrder;
import com.bt.zhangzy.network.entity.JsonOrderHistory;
import com.bt.zhangzy.network.entity.RequestCallDriver;
import com.bt.zhangzy.network.entity.RequestOrderAccept_Reject;
import com.bt.zhangzy.network.entity.RequestOrderAllocation;
import com.bt.zhangzy.network.entity.RequestOrderHistroy;
import com.bt.zhangzy.network.entity.ResponseAllocationDriver;
import com.bt.zhangzy.network.entity.ResponseCompany;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;
import com.zhangzy.base.http.BaseEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderDetailActivity extends BaseActivity {

    private OrderDetailMode currentMode = OrderDetailMode.EmptyMode;
    //    private Product product;
    private ArrayList<People> selectedDrivers;
    private JsonOrder jsonOrder;
    private OrderStatus currentOrderStatus = OrderStatus.Empty;
    private boolean updateJsonOrder = false;//更新订单的标记
    private ArrayList<ResponseAllocationDriver> allocationList;//接单成功的列表
    private ArrayList<ResponseAllocationDriver> acceptList;//接单成功的司机列表 用于展示司机的运输过程
    private boolean canAccept;//此订单是否可抢，从货源列表中传过来
    private OrderReceiveStatus currentDriverLoadingStatus = null;//当前司机的 运输状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);
        setPageName("订单详情");

        // 获取订单数据
        initData();

        // 针对用户角色 对页面进行修改
        initView();

        //针对订单数据 对页面进行修改
        //如果没有订单数据，也就是创建订单，这里会进行初始化的操作
        initJsonOrder();


    }

    private void parseJsonLine(JsonLine jsonLine) {
        if (jsonLine == null)
            return;
        jsonOrder.setStartCity(jsonLine.getConsignorCity() + ',' + jsonLine.getConsignorAddress());
        jsonOrder.setStopCity(jsonLine.getReceiverCity() + ',' + jsonLine.getReceiverAddress());
        jsonOrder.setReceiverName(jsonLine.getReceiverName());
        jsonOrder.setReceiverPhone(jsonLine.getReceiverPhone());
        //数据长度不一致，不能保存此字段
//        jsonOrder.setReceiverAddress(jsonLine.getReceiverAddress());
        jsonOrder.setConsignorName(jsonLine.getConsignorName());
        jsonOrder.setConsignorPhone(jsonLine.getConsignorPhone());
//        jsonOrder.setcon
        //init view
        initLineView();

    }

    private void initLineView() {
        View lineLy = findViewById(R.id.order_detail_line_ly);
        if (lineLy != null) {
            findViewById(R.id.order_detail_line_empty).setVisibility(View.GONE);
            lineLy.setVisibility(View.VISIBLE);
            lineLy.findViewById(R.id.line_item_edit_bt).setVisibility(View.GONE);
            ViewUtils.setText(lineLy, R.id.line_item_consignor_name, jsonOrder.getConsignorName());
            ViewUtils.setText(lineLy, R.id.line_item_consignor_phone, jsonOrder.getConsignorPhone());
            ViewUtils.setText(lineLy, R.id.line_item_consignor_address, jsonOrder.getStartCity());
            ViewUtils.setText(lineLy, R.id.line_item_receiver_name, jsonOrder.getReceiverName());
            ViewUtils.setText(lineLy, R.id.line_item_receiver_phone, jsonOrder.getReceiverPhone());
            ViewUtils.setText(lineLy, R.id.line_item_receiver_address, jsonOrder.getStopCity());
        }
    }

    private void initJsonOrder() {
        User user = User.getInstance();
        if (jsonOrder == null) {
            jsonOrder = createDefaultOrder();
            if (User.getInstance().getLastUseLine() == null)
                findViewById(R.id.order_detail_line_ly).setVisibility(View.GONE);
            else {
                parseJsonLine(User.getInstance().getLastUseLine());
            }
        } else {
            findViewById(R.id.order_detail_line_empty).setVisibility(View.GONE);
            //同步订单数据
            requestOrder(String.valueOf(jsonOrder.getId()), false);
        }
        initView_JsonOrder();

        if (user.getUserType() == Type.CompanyInformationType) {
            //物流公司看到企业派送的订单后自动调用 同意接单的接口
            if (currentOrderStatus == OrderStatus.UncommittedOrder) {
                requestAccept_Reject(true);
            }
        }
        if (currentOrderStatus.ordinal() >= OrderStatus.AllocationOrder.ordinal()) {
            //获取已抢单的司机列表
            requestListOrderHistroy();
        }
    }

    private JsonOrder createDefaultOrder() {
        Log.d(TAG, "create default JsonOrder");
        User user = User.getInstance();
        JsonOrder jsonOrder = new JsonOrder();//如果没有则创建一个订单
        //填写默认的订单数据
//        Location loc = user.getLocation();
//        if (loc != null && !TextUtils.isEmpty(loc.getCityName()))
//            jsonOrder.setStartCity(loc.toText());
        jsonOrder.setDriverCount(1);
        jsonOrder.setStatus(OrderStatus.TempOrder);
        jsonOrder.setDeliverDate(new Date());
        if (user.getUserType() == Type.CompanyInformationType) {
            JsonCompany company = user.getJsonTypeEntity();
            if (company != null) {
                jsonOrder.setCompanyId(company.getId());
                jsonOrder.setCompanyName(company.getName());
            }
        } else if (user.getUserType() == Type.EnterpriseType) {
            JsonEnterprise enterprise = user.getJsonTypeEntity();
            if (enterprise != null) {
                jsonOrder.setEnterpriseId(enterprise.getId());
                jsonOrder.setEnterpriseName(enterprise.getName());
            }
        }
        Log.d(TAG, "订单 默认数据" + jsonOrder);
        return jsonOrder;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppParams.BUNDLE_PRODUCT_KEY)) {
                //只有从 物流详情页进来 才会传 product
                Product product = (Product) bundle.get(AppParams.BUNDLE_PRODUCT_KEY);

                if (product != null && product.getCompany() != null) {
                    ResponseCompany company = product.getCompany();
                    if (company.getCompany() != null) {
                        JsonCompany jsonCompany = company.getCompany();
                        //user data
                        JsonOrder jsonOrder = createDefaultOrder();
                        // company data
                        jsonOrder.setCompanyId(jsonCompany.getId());
                        jsonOrder.setCompanyName(jsonCompany.getName());
                        this.jsonOrder = jsonOrder;
                    }
                }
            }
            if (bundle.containsKey(AppParams.ORDER_DETAIL_KEY_ORDER)) {
                jsonOrder = bundle.getParcelable(AppParams.ORDER_DETAIL_KEY_ORDER);
                currentOrderStatus = OrderStatus.parseStatus(jsonOrder.getStatus());
            }
            canAccept = bundle.getBoolean(AppParams.ORDER_CAN_ACCEPT);
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
        if (jsonOrder.getId() == 0) {
//            setTextView(R.id.order_detail_number_tx, String.valueOf(jsonOrder.getId()));
            //隐藏订单号
            findViewById(R.id.order_detail_number_ly).setVisibility(View.GONE);
        } else {
            DecimalFormat format = new DecimalFormat("####00000000");
            setTextView(R.id.order_detail_number_tx, format.format(jsonOrder.getId()));
        }

//        setTextView(R.id.order_detail_enterprise_tx, "企业ID=" + jsonOrder.getEnterpriseId());
//        setTextView(R.id.order_detail_company_tx, "物流ID=" + jsonOrder.getCompanyId());
        setTextView(R.id.order_detail_enterprise_tx, jsonOrder.getEnterpriseName());
        setTextView(R.id.order_detail_company_tx, jsonOrder.getCompanyName());
        setTextView(R.id.order_detail_type_tx, jsonOrder.getGoodsType());
//        setTextView(R.id.order_detail_goods_name_tx, jsonOrder.getGoodsName());
        setTextView(R.id.order_detail_goods_weight_tx, jsonOrder.getGoodsWeight());
        setTextView(R.id.order_detail_volume_tx, jsonOrder.getGoodsVolume());
        setTextView(R.id.order_detail_remark_ed, jsonOrder.getRemark());
        if (jsonOrder.getDeliverDate() != null)
            setTextView(R.id.order_detail_start_date, Tools.toStringDate(jsonOrder.getDeliverDate()));
        if (jsonOrder.getDriverCount() > 0)
            setTextView(R.id.order_detail_driver_size_ed, String.valueOf(jsonOrder.getDriverCount()));
//        setTextView(R.id.order_detail_volume_tx,jsonOrder.getGoodsVolume());

        //线路初始化
        if (!Tools.isEmptyStrings(jsonOrder.getStartCity(), jsonOrder.getStopCity())) {
            initLineView();
        }

        //订单状态
        if (User.getInstance().getUserType() != Type.DriverType) {
            String statusStr = null;
//            String[] strings = getResources().getStringArray(R.array.order_status_items);
            Log.i(TAG, "json order status = " + jsonOrder.getOrderStatus().name());
            switch (jsonOrder.getOrderStatus()) {
                case Empty:
                    statusStr = getString(R.string.order_status_empty);
                    break;
                case TempOrder:
                    statusStr = getString(R.string.order_status_create);
                    break;
                case UncommittedOrder:
                    statusStr = getString(R.string.order_status_wait_company);
                    //特殊判断  企业直接下单 不需要物流公司接单
                    if (jsonOrder.getCompanyId() == 0)
                        statusStr = getString(R.string.order_status_wait_driver);
                    break;
                case AllocationOrder:
                    statusStr = getString(R.string.order_status_wait_driver);
                    ;//"物流已接单";

                    break;
                case TradeOrder:
                    statusStr = getString(R.string.order_status_transit);
                    ;// "司机已接单";
                    break;
                case LoadingOrder:
                case LoadingFinishOrder:
                    statusStr = getString(R.string.order_status_transit);// "运输中";
                    break;
                case FinishedOrder:
                    statusStr = getString(R.string.order_status_finished);//"订单已完成";
                    break;
                case DiscardOrder:
                    statusStr = getString(R.string.order_status_discard);// "订单已作废";
                    break;
            }
            if (statusStr != null)
                setTextView(R.id.order_detail_status_tx, statusStr);

        } else {
            //当前用户 为 司机
            if (currentMode == OrderDetailMode.UntreatedMode) {
                //抢单前 加密收发货人信息
                View lineLy = findViewById(R.id.order_detail_line_ly);
                if (lineLy != null) {
                    ViewUtils.setText(lineLy, R.id.line_item_consignor_name, Tools.encryptString(jsonOrder.getConsignorName()));
                    ViewUtils.setText(lineLy, R.id.line_item_consignor_phone, Tools.encryptString(jsonOrder.getConsignorPhone()));
//                    ViewUtils.setText(lineLy, R.id.line_item_consignor_address, jsonOrder.getStartCity());
                    ViewUtils.setText(lineLy, R.id.line_item_receiver_name, Tools.encryptString(jsonOrder.getReceiverName()));
                    ViewUtils.setText(lineLy, R.id.line_item_receiver_phone, Tools.encryptString(jsonOrder.getReceiverPhone()));
//                    ViewUtils.setText(lineLy, R.id.line_item_receiver_address, jsonOrder.getStopCity());
                }
            }
        }

        if (jsonOrder.getOrderStatus().ordinal() >= OrderStatus.FinishedOrder.ordinal()) {
            //判断是否为企业下单，如果是 则不能评价
            if (jsonOrder.getCompanyId() == 0) {
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
            }
        }
    }


    private void initView() {
//        EditText start_ed = (EditText) findViewById(R.id.order_detail_start_loc_tx);
//        EditText stop_ed = (EditText) findViewById(R.id.order_detail_stop_loc_tx);
        EditText remark_ed = (EditText) findViewById(R.id.order_detail_remark_ed);
        if (cannotEditStatus()) {
//            start_ed.setEnabled(false);
//            stop_ed.setEnabled(false);
            remark_ed.setEnabled(false);
        } else {
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateJsonOrder = true;
                }
            };
//            start_ed.addTextChangedListener(watcher);
//            stop_ed.addTextChangedListener(watcher);
            remark_ed.addTextChangedListener(watcher);
        }

        //已抢司机列表 默认为不显示
        findViewById(R.id.order_detail_select_driver_ly).setVisibility(View.GONE);

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
        findViewById(R.id.order_detail_select_driver_ly).setVisibility(View.GONE);
        findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
        //默认司机用户 不显示订单状态
        findViewById(R.id.order_detail_status_ly).setVisibility(View.GONE);
        findViewById(R.id.order_detail_status_line).setVisibility(View.GONE);
        findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
        findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
        switch (currentMode) {
            case CreateMode:

                break;
            case UntreatedMode://未提交订单

                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                if (canAccept) {
                    setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_driver_grab));
                } else {
                    findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                }
                break;
            case SubmittedMode:
                setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_driver_finish));
                break;
            case CompletedMode:
                setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_evaluate));
                break;
        }
    }

    private void refreshLoadingStatusView_Driver() {
        if (currentMode == OrderDetailMode.SubmittedMode && currentDriverLoadingStatus != null) {
            String[] strings = getResources().getStringArray(R.array.order_driver_loading_status_from_driver_items);
            switch (currentDriverLoadingStatus) {
                case Accept:
                    setTextView(R.id.order_detail_submit, strings[0]);
//                    自动触发一次位置上传
                    requestUploadLocation();
                    break;
                case Loading://等待确认装货 不可点击
                    setTextView(R.id.order_detail_submit, strings[1]);
                    findViewById(R.id.order_detail_submit).setEnabled(false);
                    break;
                case LoadingFinish:
                    setTextView(R.id.order_detail_submit, strings[2]);
                    break;
                case Finish:
                    setTextView(R.id.order_detail_submit, strings[3]);
                    break;
                default:
                    findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                    showToast("司机状态不可操作" + currentDriverLoadingStatus);
                    break;
            }
        }
    }


    private void initView_Company() {
        switch (currentMode) {
            case CreateMode:
                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_temp));
                break;
            case UntreatedMode://未提交订单
                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_uncommitted));
                if (currentOrderStatus == OrderStatus.AllocationOrder) {
                    setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_allocation));
                    setTextView(R.id.order_detail_select_driver_bt, "选择已接单司机");
                }
                break;
            case SubmittedMode:
                //修改为定位图标
                ImageButton driverBt = (ImageButton) findViewById(R.id.order_detail_location_driver_bt);
                driverBt.setImageResource(R.drawable.location_bt_selector);
//                setTextView(R.id.order_detail_submit, "订单已提交");
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                break;
            case CompletedMode:
//                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
//                setTextView(R.id.order_detail_submit, "订单已完成");
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                break;
        }
    }

    //初始化企业页面
    private void initView_Enterprise() {
        switch (currentMode) {
            case CreateMode:
                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
                setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_temp));
                //企业创建订单的时候不能选择司机
                findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                //如果是给物流公司下单 则不能选择车队司机
                if (jsonOrder != null && jsonOrder.getCompanyId() != 0) {
                    findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                    //给物流公司下单时 不能选择用车数量
                    findViewById(R.id.order_detail_driver_size_ed).setEnabled(false);
                }
                break;
            case UntreatedMode://未提交订单

//                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
//                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_call_phone_bt).setVisibility(View.GONE);
                if (jsonOrder.getCompanyId() == 0) {
                    setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_uncommitted));
                    if (currentOrderStatus == OrderStatus.AllocationOrder) {
//                    setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_allocation));
                        setTextView(R.id.order_detail_submit, getString(R.string.order_submit_order_allocation));

                    }
                } else {
                    findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                    findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                }
                break;
            case SubmittedMode:
                //修改为定位图标
                ImageButton driverBt = (ImageButton) findViewById(R.id.order_detail_location_driver_bt);
                driverBt.setImageResource(R.drawable.location_bt_selector);
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                break;
            case CompletedMode:
//                findViewById(R.id.order_detail_drivers_list_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_select_driver_bt).setVisibility(View.GONE);
                findViewById(R.id.order_detail_location_driver_bt).setVisibility(View.GONE);
//                setTextView(R.id.order_detail_submit, "订单已完成");
                findViewById(R.id.order_detail_submit).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppParams.LINES_REQUEST_CODE) {
            JsonLine jsonLine = null;
            if (data != null) {
                String json = (String) data.getCharSequenceExtra(AppParams.LINES_BUNDLE_JSON_LINE);
                Log.d(TAG, "JsonLine:" + json);
                jsonLine = JsonLine.ParseEntity(json, JsonLine.class);
            }
            if (jsonLine != null) {
                //保存使用的线路信息
                User.getInstance().setLastUseLine(jsonLine);
                parseJsonLine(jsonLine);
            }


        } else if (requestCode == AppParams.RESULT_CODE_ACCEPT_DRIVERS) {
            //物流点击了 确认装货 需要刷新orderHistory列表
            requestListOrderHistroy();

        } else if (requestCode == AppParams.RESULT_CODE_SELECT_DEVICES) {
            //车队选择返回
            if (data != null) {
                ArrayList<People> peoples = data.getParcelableArrayListExtra(AppParams.SELECTED_DRIVERS_LIST);
                if (peoples != null && !peoples.isEmpty()) {
                    selectedDrivers = peoples;
                    //将已选择的司机显示在页面上
                    StringBuffer stringBuffer = new StringBuffer();
                    int index = 0;
                    for (People people : peoples) {
                        index++;
                        stringBuffer.append(index + ".")
                                .append(people.getName())
                                .append("-")
                                .append(people.getPhoneNumber())
                                .append("\n");
                    }
                    //删除最后一个 /
                    if (stringBuffer.charAt(stringBuffer.length() - 1) == '\n')
                        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    findViewById(R.id.order_detail_select_driver_ly).setVisibility(View.VISIBLE);
                    setTextView(R.id.order_detail_select_drivers_tx, stringBuffer.toString());

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


    private void showChooseTelDialog() {
        //拨打电话 选择
        CallPhoneDialog callPhoneDialog = new CallPhoneDialog(this, jsonOrder.getConsignorPhone(), jsonOrder.getReceiverPhone());
        callPhoneDialog.setCanceledOnTouchOutside(true);
        callPhoneDialog.show();
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                progress.cancel();
//                if (v.getId() == R.id.dialog_call_me_btn) {
//
//                } else {
//
//                }
//            }
//        };
//
//        BaseDialog baseDialog = new BaseDialog(this);
//        baseDialog.setView(R.layout.dialog_call);
//        baseDialog.setCanceledOnTouchOutside(true);
//        baseDialog.setOnClickListener(R.id.dialog_call_me_btn, listener);
//        baseDialog.setOnClickListener(R.id.dialog_call_all_btn, listener);
//        baseDialog.show();
    }

    private void showChooseCallDialog() {
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

        BaseDialog baseDialog = new BaseDialog(this);
        baseDialog.setView(R.layout.dialog_call);
        baseDialog.setCanceledOnTouchOutside(false);
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
        if (currentOrderStatus != OrderStatus.Empty
                && currentOrderStatus != OrderStatus.TempOrder
                && currentOrderStatus != OrderStatus.UncommittedOrder)
            return true;
        return false;
    }

    public void onClick_InputDate(View view) {
        if (cannotEditStatus())
            return;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        final Date current_date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        //读取上次设定的日期
        if (jsonOrder.getDeliverDate() != null) {
            calendar.setTime(jsonOrder.getDeliverDate());
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Log.d(TAG, "您选择了：" + year + "年" + (monthOfYear) + "月" + dayOfMonth + "日");
//                Tools.getCurrentTime()
                GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                if (date.getTime().getTime() >= current_date.getTime()) {
                    setTextView(R.id.order_detail_start_date, Tools.toStringDate(date.getTime()));
                    jsonOrder.setDeliverDate(date.getTime());
                } else
                    showToast("日期不能倒退哦");
            }
        },
                year,
                monthOfYear,
                dayOfMonth
        )
                .show();
    }

    public void onClick_Lines(View v) {
        if (cannotEditStatus()) {
            if (User.getInstance().getUserType() == Type.DriverType) {
                if (currentMode == OrderDetailMode.SubmittedMode) {
                    //交易中 司机 可打电话
                    showChooseTelDialog();
                }
            }
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppParams.LINES_BUNDLE_FORM_ORDER, true);
        startActivityForResult(LinesListActivity.class, bundle, AppParams.LINES_REQUEST_CODE);
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
                .setInputLength(2)
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        if (string != null && string.length() < 3) {
                            updateJsonOrder = true;
                            setTextView(R.id.order_detail_driver_size_ed, string);
                            jsonOrder.setDriverCount(Integer.valueOf(string));
                        } else {
                            showToast("司机数量错误！");
                        }
                    }
                }).show();
    }

    /**
     * 体积
     *
     * @param view
     */
    public void onClick_InputVolume(View view) {
        if (cannotEditStatus())
            return;
        new InputDialog(this, true)
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        jsonOrder.setGoodsVolume(string);
                        setTextView(R.id.order_detail_volume_tx, string);
                    }
                }).show();
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
                .setInputLength(4)
                .setEditHintString("请输入货物重量")
                .setSuffixString("吨")
                .setCallback(new InputDialog.Callback() {
                    @Override
                    public void inputCallback(String string) {
                        updateJsonOrder = true;
                        jsonOrder.setGoodsWeight(string);
                        setTextView(R.id.order_detail_goods_weight_tx, string);
                    }
                }).show();

    }

    /**
     * 车队页面跳转
     *
     * @param needDriverNum
     * @param list          待选司机列表；如果是从车队选择填null
     */
    private void gotoSelectDriver(int needDriverNum, ArrayList<ResponseAllocationDriver> list) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppParams.RESULT_CODE_KEY, AppParams.RESULT_CODE_SELECT_DEVICES);
        bundle.putInt(AppParams.SELECT_DEVICES_SIZE_KEY, needDriverNum);
        if (list != null && !list.isEmpty())
            bundle.putStringArrayList(AppParams.SELECT_DRIVES_LIST_KEY, BaseEntity.ParseArrayToString(list));
        if (selectedDrivers != null && !selectedDrivers.isEmpty())
            bundle.putParcelableArrayList(AppParams.SELECTED_DRIVERS_LIST, selectedDrivers);
        startActivityForResult(FleetActivity.class, bundle, 0, AppParams.RESULT_CODE_SELECT_DEVICES);
    }


    //交易中 显示司机列表
    public void onClick_DriverList(View view) {

        if (acceptList == null || acceptList.isEmpty()) {
            showToast("司机列表为空");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(AppParams.RESULT_CODE_KEY, AppParams.RESULT_CODE_ACCEPT_DRIVERS);
        if (User.getInstance().getUserType() == Type.EnterpriseType)
            bundle.putBoolean(AppParams.SELECTED_DRIVERS_EDIT, true);
        else if (User.getInstance().getUserType() == Type.CompanyInformationType) {
            //物流公司只有在 没有企业的情况下才能操作订单
            bundle.putBoolean(AppParams.SELECTED_DRIVERS_EDIT, jsonOrder.getEnterpriseId() == 0);
        }
        bundle.putStringArrayList(AppParams.SELECT_DRIVES_LIST_KEY, BaseEntity.ParseArrayToString(acceptList));
        startActivityForResult(FleetActivity.class, bundle, AppParams.RESULT_CODE_ACCEPT_DRIVERS);
    }

    public void onClick_LocationDriverBtn(View view) {
        if (currentOrderStatus == OrderStatus.TradeOrder
                || currentOrderStatus == OrderStatus.LoadingOrder
                || currentOrderStatus == OrderStatus.LoadingFinishOrder) {
            gotoMap();
            return;
        }
    }

    //选择 司机 call车
    public void onClick_SelectDriverBtn(View view) {
//        if (cannotEditStatus())
//            return;
        if (currentOrderStatus == OrderStatus.TradeOrder
                || currentOrderStatus == OrderStatus.LoadingOrder
                || currentOrderStatus == OrderStatus.LoadingFinishOrder) {
//            gotoMap();
            return;
        } else if (currentOrderStatus == OrderStatus.AllocationOrder) {
            //to do 订单分配中 这里需要请求 抢单成功的司机列表
//            showToast("订单分配中 这里需要请求 抢单成功的司机列表");
            if (allocationList != null && !allocationList.isEmpty())
                gotoSelectDriver(jsonOrder.getDriverCount(), allocationList);
            else
                showToast("还没接单的司机！");
            return;
        } else {
            int needDriverNum = getNeedDriverNum();
            if (needDriverNum < 0) {
                showToast("请先填写车辆数");
                return;
            }
            gotoSelectDriver(needDriverNum, null);
        }
    }

    public void gotoMap() {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "运输司机位置");
        bundle.putString(AppParams.WEB_PAGE_URL, String.format(AppURL.LOCATION_MAP_ORDER.toString(), jsonOrder.getId()));
        startActivity(WebViewActivity.class, bundle);
    }

    /**
     * 联系客服
     *
     * @param view
     */
    public void onClick_CallPhone(View view) {

        new CallPhoneDialog(this)
                .setInfoMessage(String.format(getString(R.string.service_tel_dialog), getString(R.string.app_phone)))
                .setPhoneNum(getString(R.string.app_phone))
                .show();
    }

    public void onClick_Company(View view) {
        // 点击物流公司
        if (jsonOrder.getCompanyId() > 0) {
            Product product = new Product(jsonOrder.getCompanyId());
            product.setName(jsonOrder.getCompanyName());
            gotoDetail(product);
        }else{
            showToast("没有接单企业");
        }
    }

    //提交订单按钮
    public void onClick_SubmitOrder(View view) {
        User user = User.getInstance();
//        TextView editText = (TextView) findViewById(R.id.order_detail_open_ed);
        switch (currentMode) {
            case CreateMode://创建订单
                if (jsonOrder == null
                        || TextUtils.isEmpty(jsonOrder.getStartCity()) || TextUtils.isEmpty(jsonOrder.getStopCity())
                        || TextUtils.isEmpty(jsonOrder.getReceiverName()) || TextUtils.isEmpty(jsonOrder.getReceiverPhone())
                        || TextUtils.isEmpty(jsonOrder.getGoodsType())
                        || TextUtils.isEmpty(jsonOrder.getConsignorName()) || TextUtils.isEmpty(jsonOrder.getConsignorPhone())
                        || TextUtils.isEmpty(jsonOrder.getGoodsWeight())) {
                    showToast("订单信息不完整");
                    return;
                }
                //如果是企业向物流公司下单 则不检测用车数量字段


//                if (TextUtils.isEmpty(jsonOrder.getGoodsVolume()) && TextUtils.isEmpty(jsonOrder.getGoodsWeight())) {
//                    showToast("重量和体积至少填一项");
//                    return;
//                }

                //更新备注信息
                String remark = getStringFromTextView(R.id.order_detail_remark_ed);
                jsonOrder.setRemark(remark);
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
                } else /*if (user.getUserType() == Type.CompanyInformationType)*/ {
                    submitOrder_Company();
                } /*else {
                    showToast("无权操作订单");
                }*/
                break;
            case SubmittedMode://已提交订单
                if (user.getUserType() == Type.DriverType) {
                    submitOrder_DriversLoading();

                }
                break;
            case CompletedMode:// 已完成订单
                //跳转评价
                if (user.getUserType() == Type.DriverType && jsonOrder.getCompanyId() > 0) {
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

    private void submitOrder_DriversLoading() {
        if (currentDriverLoadingStatus != null)
            switch (currentDriverLoadingStatus) {
                case Accept:
                    new ConfirmDialog(this)
                            .setMessage("货物是否已经装车？")
                            .setConfirm("是")
                            .setCancel("否")
                            .setConfirmListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestStartLoading();
                                }
                            }).show();
                    break;
                case Loading:
                    break;
                case LoadingFinish:
                    //验证收货
                    Bundle bundle = new Bundle();
                    bundle.putString(AppParams.BUNDLE_VERIFICATION_ORDER_PHONE_KEY, jsonOrder.getReceiverPhone());
                    bundle.putInt(AppParams.BUNDLE_VERIFICATION_ORDER_ID_KEY, jsonOrder.getId());
                    startActivity(VerificationActivity.class, bundle);

                    finish();
                    break;
                case Finish:
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
            if (User.getInstance().getUserType() == Type.EnterpriseType) {
                //企业直接call车队车辆
                if (jsonOrder.getCompanyId() == 0)
                    requestCallMotorcade();
                else
                    finish();

            } else {
                if (currentOrderStatus == OrderStatus.AllocationOrder
                        && allocationList != null) {
                    showToast("请从已抢单司机列表中选择司机");
                    // 如果已经从 抢单司机列表中选择了，则直接走callDriver接口进行通知；callDriver成功后会自动调用allocation
                } else {
//                  没有选择司机，或者没有可选的接单列表 弹出CALL车选项
                    if (getMainLooper() == Looper.myLooper()) {
                        showChooseCallDialog();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showChooseCallDialog();
                            }
                        });
                    }
                }
            }
        } else if (allocationList != null && !allocationList.isEmpty()) {
            //选择了接单司机
//            requestAccept_Reject(true);
            ArrayList<JsonOrderHistory> list = new ArrayList<JsonOrderHistory>();
            for (People people : selectedDrivers) {
                if (people.getOrderHistoryId() != 0) {
                    JsonOrderHistory history = new JsonOrderHistory();
                    history.setId(people.getOrderHistoryId());
                    history.setRole(people.getRole());
                    history.setPhoneNumber(people.getPhoneNumber());
                    history.setName(people.getName());
                    list.add(history);
                } else
                    for (ResponseAllocationDriver orderHistory : allocationList) {
                        if (people.getId() == orderHistory.getRoleId()) {
                            list.add(orderHistory);
                            break;
                        }
                    }
            }
            requestAllocation(list);
        } else {
            //选择了 车队司机
            //已经选择了司机 直接进行call车
            requestCallDriver();
//                        requestAllocation();
//            requestAccept_Reject(true);
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
                        //to do 公共订单 抢单 用save_order_history
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

    private void requestStartLoading() {
        RequestOrderAccept_Reject params = new RequestOrderAccept_Reject();
        params.setOrderId(jsonOrder.getId());
//        params.setRole(driver.getRole());
//        params.setRoleId(driver.getRoleId());
        params.setRole(User.getInstance().getUserType().toRole());
        params.setRoleId(User.getInstance().getRoleId());
        HttpHelper.getInstance().post(AppURL.PostStartLoading, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("装货成功，等待发货方确认");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("装货失败");
            }
        });

    }


    //物流公司选择接受订单的司机并分配订单
    private void requestAllocation(List<JsonOrderHistory> list) {
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
//        params.setStatus(receiveStatus.ordinal());

        HttpHelper.getInstance().post(AppURL.PostAllocationDriverList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<ResponseAllocationDriver> list = ParseJson_Array(result, ResponseAllocationDriver.class);
                if (list == null || list.isEmpty())
                    return;

                if (User.getInstance().getUserType() == Type.DriverType) {
                    // 获取当前司机的运输状态
                    for (ResponseAllocationDriver json : list) {
                        if (User.getInstance().getRoleId() == json.getRoleId()) {
                            currentDriverLoadingStatus = json.getReceiveStatus();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshLoadingStatusView_Driver();
                                }
                            });
                            break;
                        }
                    }
                } else {
                    //只有物流公司 和 企业才能看到 这个列表
                    if (allocationList == null)
                        allocationList = new ArrayList<ResponseAllocationDriver>();
                    if (allocationList.size() > 0)
                        allocationList.clear();
                    if (acceptList == null)
                        acceptList = new ArrayList<ResponseAllocationDriver>();
                    if (acceptList.size() > 0)
                        acceptList.clear();

                    //筛选 接单成功的列表
//                    People people;
                    for (ResponseAllocationDriver json : list) {

                        if (json.getReceiveStatus() == OrderReceiveStatus.Receive) {
                            allocationList.add(json);
                        } else if (json.getStatus() >= OrderReceiveStatus.Accept.ordinal()) {
                            acceptList.add(json);
                        }
                    }

                    if (!allocationList.isEmpty()) {
                        //更新订单状态
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTextView(R.id.order_detail_status_tx, getString(R.string.order_status_confirm_driver));
                            }
                        });
                    }
                }
//
            }

            @Override
            public void onFailed(String str) {
                showToast("司机列表 获取失败" + str);
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
                finish();
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
                if (User.getInstance().getUserType() == Type.DriverType) {
                    finish();
                }
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
//                ArrayList<Integer> list = new ArrayList<Integer>();
//                for (JsonOrderHistory json : list_order) {
//                    list.add(json.getId());
//                }
                requestAllocation(list_order);
                finish();
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

        HttpHelper.getInstance().post(AppURL.PostCreatOrders, jsonOrder, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("下单成功" + msg);
                requestSendToCompany(result);
                Type userType = User.getInstance().getUserType();
                if (userType == Type.CompanyInformationType || userType == Type.EnterpriseType) {
                    //物流公司 创建订单后 重新获取订单信息 ，并询问是否call车
                    requestOrder(result, true);
                } else {
                    //其他用户类型直接关闭页面
                    finish();
                }
            }

            @Override
            public void onFailed(String str) {
                showToast("下单失败" + str);
            }
        });
    }

    //创建订单后 获取订单
    private void requestOrder(String orderId, final boolean isSubmit) {

        HttpHelper.getInstance().get(AppURL.GetOrder + orderId, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                JsonOrder tmp_json = ParseJson_Object(result, JsonOrder.class);
                jsonOrder = tmp_json;
                currentOrderStatus = OrderStatus.parseStatus(jsonOrder.getStatus());
                if (isSubmit) {
//                    Type userType = User.getInstance().getUserType();
//                    if (userType == Type.CompanyInformationType) {
                    submitOrder_Company_Call();
//                    } else if (userType == Type.EnterpriseType) {
//                        //企业直接call车队车辆
//                        requestCallMotorcade();
//
//                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView_JsonOrder();
                        }
                    });
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
        //企业下单没有物流公司
        if (jsonOrder.getCompanyId() == 0) {
            return;
        }
        User user = User.getInstance();
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("companyId", String.valueOf(jsonOrder.getCompanyId()));

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


    private void requestUploadLocation() {
        Log.i(TAG, "requestUploadLocation - first");
        JsonCarTrack params = new JsonCarTrack();
        User user = User.getInstance();
        params.setDriverId(user.getDriverID());
        if (user.getJsonCar() != null)
            params.setCarId(user.getJsonCar().getId());
//        params.setOrderId();
        if (user.getLocation() != null) {
            params.setLongitude(Double.valueOf(user.getLocation().getLongitude()));
            params.setLatitude(Double.valueOf(user.getLocation().getLatitude()));
        }
        params.setOrderId(jsonOrder.getId());
        HttpHelper.getInstance().post(AppURL.PostUploadLocation, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "requestUploadLocation - success:" + result);
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "requestUploadLocation - failed:" + str);
            }
        });
    }

}
