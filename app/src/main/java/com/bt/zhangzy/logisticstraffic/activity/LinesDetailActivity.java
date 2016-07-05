package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.entity.JsonLine;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-5-23.
 */
public class LinesDetailActivity extends BaseActivity {
    JsonLine jsonLine;
    boolean isEditMode;
    private boolean isFromOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_detail);

        setPageName("编辑地址");
        if (getIntent().getExtras() != null) {
            isFromOrder = getIntent().getBooleanExtra(AppParams.LINES_BUNDLE_FORM_ORDER, false);
            String json = (String) getIntent().getCharSequenceExtra(AppParams.LINES_BUNDLE_JSON_LINE);
            if (!TextUtils.isEmpty(json)) {
                isEditMode = true;
                jsonLine = JsonLine.ParseEntity(json, JsonLine.class);
            } else {
                isEditMode = false;
                jsonLine = new JsonLine();
                jsonLine.setCreateDate(new Date());
                jsonLine.setUserId((int) User.getInstance().getId());
                String consignorCity = User.getInstance().getLocation().toText();
                jsonLine.setConsignorCity(consignorCity);
                findViewById(R.id.line_detail_remove_bt).setVisibility(View.GONE);
            }
        }
        setTextView(R.id.line_list_submit, isFromOrder ? "保存并使用" : "保存");
        setTextView(R.id.line_detail_consignor_city, jsonLine.getConsignorCity());
        if (isEditMode) {
            setTextView(R.id.line_detail_name, jsonLine.getLineName());
            setTextView(R.id.line_detail_consignor_name, jsonLine.getConsignorName());
            setTextView(R.id.line_detail_consignor_phone, jsonLine.getConsignorPhone());
            setTextView(R.id.line_detail_consignor_address, jsonLine.getConsignorAddress());
            setTextView(R.id.line_detail_receiver_name, jsonLine.getReceiverName());
            setTextView(R.id.line_detail_receiver_phone, jsonLine.getReceiverPhone());
            setTextView(R.id.line_detail_receiver_city, jsonLine.getReceiverCity());
            setTextView(R.id.line_detail_receiver_address, jsonLine.getReceiverAddress());
        }
    }

    /**
     * 地址选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(View view) {

        if (view == null || !(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;
        String string = ViewUtils.getStringFromTextView(textView);
        Location location;
        if (TextUtils.isEmpty(string)) {
            location = new Location(User.getInstance().getLocation());
        } else {
            location = Location.Parse(string);
        }
        LocationView.createDialogForOrder(this)
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
                        if (textView.getId() == R.id.line_detail_consignor_city) {
//                            consignorCity = params;
                            jsonLine.setConsignorCity(params);
                        } else if (textView.getId() == R.id.line_detail_receiver_city) {
//                            receiverCity = params;
                            jsonLine.setReceiverCity(params);
                        }
                    }
                }).show();
    }

    //更新出发地和目的地的详细地址
    private void updateLocationInfo() {
//        String startCity = "", stopCity = "";
//        if (!TextUtils.isEmpty(jsonOrder.getConsignorCity())) {
//            String[] split = jsonOrder.getConsignorCity().split(",");
//            startCity = split[0];
//        }
//        if (!TextUtils.isEmpty(jsonOrder.getStopCity())) {
//            String[] split = jsonOrder.getStopCity().split(",");
//            stopCity = split[0];
//        }
//        String start_str = getStringFromTextView(R.id.line_detail_consignor_address);
//        String stop_str = getStringFromTextView(R.id.line_detail_receiver_address);
//        start_str = start_str == null ? consignorCity : consignorCity + "," + start_str;
//        stop_str = stop_str == null ? receiverCity : receiverCity + "," + stop_str;
//        jsonLine.setConsignorCity(start_str);
//        jsonLine.setStopCity(stop_str);
    }

    public void onClick_Submit(View view) {
        if (Tools.isEmptyStrings(jsonLine.getConsignorCity(), jsonLine.getReceiverCity())) {
            showToast("请选择省市");
            return;
        }
        String lineName = getStringFromTextView(R.id.line_detail_name);
        String consignorName = getStringFromTextView(R.id.line_detail_consignor_name);
        String consignorPhone = getStringFromTextView(R.id.line_detail_consignor_phone);
        String receiverName = getStringFromTextView(R.id.line_detail_receiver_name);
        String receiverPhone = getStringFromTextView(R.id.line_detail_receiver_phone);
        String consignorAddress = getStringFromTextView(R.id.line_detail_consignor_address);
        String receiverAddress = getStringFromTextView(R.id.line_detail_receiver_address);

        if (Tools.isEmptyStrings(lineName,consignorName, consignorPhone, receiverName, receiverPhone, consignorAddress, receiverAddress)) {
            showToast("信息不全");
            return;
        }
        if (!Tools.IsPhoneNum(consignorPhone) || !Tools.IsPhoneNum(receiverPhone)) {
            showToast("电话格式错误");
            return;
        }
//        updateLocationInfo();
        jsonLine.setLineName(lineName);
        jsonLine.setConsignorName(consignorName);
        jsonLine.setConsignorPhone(consignorPhone);
        jsonLine.setReceiverName(receiverName);
        jsonLine.setReceiverPhone(receiverPhone);
        jsonLine.setConsignorAddress(consignorAddress);
        jsonLine.setReceiverAddress(receiverAddress);
        if (isEditMode) {
            Intent data = new Intent();
            data.putExtra(AppParams.LINES_BUNDLE_JSON_LINE, jsonLine.toString());
            setResult(AppParams.LINES_RESULT_CODE_EDIT, data);
            finish();
        } else {
            if (isFromOrder) {
                new ConfirmDialog(this)
                        .setMessage("是否将此线路添加到常用线路？")
                        .setConfirm("是")
                        .setCancel("否")
                        .setListener(new ConfirmDialog.ConfirmDialogListener() {
                            @Override
                            public void onClick(boolean isConfirm) {
                                if (isConfirm) {
//                                    User.getInstance().addLines(jsonLine);
                                    User.getInstance().requestFreightLineAdd(jsonLine);
                                }
                                Intent data = new Intent();
                                data.putExtra(AppParams.LINES_BUNDLE_JSON_LINE, jsonLine.toString());
                                setResult(AppParams.LINES_RESULT_CODE_NEW, data);
                                finish();
                            }
                        })
                        .show();
            } else {
//                User.getInstance().addLines(jsonLine);
                User.getInstance().requestFreightLineAdd(jsonLine);
                Intent data = new Intent();
                data.putExtra(AppParams.LINES_BUNDLE_JSON_LINE, jsonLine.toString());
                setResult(AppParams.LINES_RESULT_CODE_NEW, data);
                finish();
            }
        }

    }

    public void onClick_RemoveLine(View view) {
        new ConfirmDialog(this)
                .setMessage("是否删除此线路")
                .setConfirm("是")
                .setCancel("否")
                .setListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void onClick(boolean isConfirm) {
                        if (isConfirm) {
                            Intent data = new Intent();
                            data.putExtra(AppParams.LINES_BUNDLE_JSON_LINE, jsonLine.toString());
                            setResult(AppParams.LINES_RESULT_CODE_REMOVE, data);
                            finish();
                        }
                    }
                })
                .show();
    }
}
