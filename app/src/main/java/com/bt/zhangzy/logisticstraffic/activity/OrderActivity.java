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
            //信息部 不能 给别的信息部下单
            showToast("信息部 不能 给别的信息部下单");
            finish();
//            setContentView(R.layout.order_submit);
//            setPageName("货物详情");
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
                    textView.setText(tx.getText());
                }
            }
        }, getResources().getStringArray(R.array.order_change_type_items));

    }

    /**
     * 车型 选择事件
     *
     * @param view
     */
    public void onClick_ChangeTruckType(View view) {
        final TextView textView = (TextView) view;
        BaseDialog.showChooseItemsDialog(this, getString(R.string.order_change_truck_type_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v instanceof TextView) {
                    TextView tx = (TextView) v;
                    textView.setText(tx.getText());
                }
            }
        }, getResources().getStringArray(R.array.order_change_truck_type_items));
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
                    textView.setText(tx.getText());
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
        final TextView textView = (TextView) view;
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.order_dialog_weight).setOnClickListener(R.id.order_dialog_weight_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("20");
            }
        });
        dialog.show();
    }

}
