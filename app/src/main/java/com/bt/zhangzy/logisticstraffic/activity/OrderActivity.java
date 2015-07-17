package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.zhangzy.baidusdk.BaiduMapActivity;

/**
 * Created by ZhangZy on 2015/7/8.
 */
public class OrderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!User.getInstance().getLogin()) {
            //用户未登陆时自动跳转到登陆页面
            startActivity(LoginActivity.class);
            finish();
            return;
        }

        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            setContentView(R.layout.order_place);
            setPageName("订单详情");
        } else if (User.getInstance().getUserType() == Type.InformationType) {
            setContentView(R.layout.order_submit);
            setPageName("货物详情");
        } else {


        }


    }


    public void onClick_gotoMap(View view) {
        startActivity(BaiduMapActivity.class);
    }

    public void onClick_OrderPlace(View view) {
        finish();
    }

    public void onClick_OrderSubmit(View view) {
        finish();
    }

    public void onClick_ChangeType(View view) {
        final TextView textView = (TextView) view;
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.order_dialog_type);
        dialog.setOnClickListener(R.id.order_dialog_type_ly, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textView.setText("建材");
            }
        });
        dialog.show();
    }

    public void onClick_ChangeTruckType(View view) {
        final TextView textView = (TextView) view;
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.order_dialog_truck_type);
        dialog.setOnClickListener(R.id.order_dialog_truck_type_ly,new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textView.setText("平板车");
            }
        });
        dialog.show();
    }

    public void onClick_ChangeTruckLength(View view) {
        final TextView textView = (TextView) view;
//        final Dialog dialog = new Dialog(this);
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.order_dialog_truck_length);
        dialog.setOnClickListener(R.id.order_dialog_truck_length_ly, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("5米");
            }
        });
        dialog.show();
    }


    //
    public void onClick_ChangeLocation(View view) {
        final TextView textView = (TextView) view;
        BaseDialog baseDialog = new BaseDialog(this);
        baseDialog.setView(R.layout.order_dialog_location);
        baseDialog.setOnClickListener(R.id.order_dialog_location_ly, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(System.currentTimeMillis() % 2 == 1 ? "呼和浩特" : "北京");
            }
        }).show();

    }


    public void onClick_ChangeWeight(View view) {
        final TextView textView = (TextView) view;
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.order_dialog_weight).setOnClickListener(R.id.order_dialog_weight_btn,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("20");
            }
        });
        dialog.show();
    }

}
