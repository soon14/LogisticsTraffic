package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;

import static com.bt.zhangzy.logisticstraffic.data.OrderDetailMode.EnterpriseMode;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderDetailActivity extends BaseActivity {


    OrderDetailMode currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);
        setPageName("订单详情");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppParams.ORDER_DETAIL_KEY_TYPE)) {
            //根据传入的参数判断显示模式
            int type = bundle.getInt(AppParams.ORDER_DETAIL_KEY_TYPE);
            OrderDetailMode[] values = OrderDetailMode.values();
            currentMode = values[Math.max(0, Math.min(values.length - 1, type))];
            if (currentMode == OrderDetailMode.UntreatedMode) {
                setCurrentModeForUser();
            }
        } else {
            setCurrentModeForUser();
        }

        initView();

    }

    /**
     * 根据当前用户的登陆状态判断显示模式
     */
    private void setCurrentModeForUser() {
        //根据当前用户的登陆状态判断显示模式
        if (User.getInstance().getUserType() == Type.DriverType) {
            currentMode = OrderDetailMode.DriverMode;
            Toast.makeText(this, "司机模式", Toast.LENGTH_LONG).show();
        } else if (User.getInstance().getUserType() == Type.EnterpriseType) {
            currentMode = EnterpriseMode;
            Toast.makeText(this, "企业模式", Toast.LENGTH_LONG).show();
        } else if (User.getInstance().getUserType() == Type.InformationType) {
            currentMode = OrderDetailMode.InformationMode;
            Toast.makeText(this, "信息部模式", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppParams.RESULT_CODE_SELECT_DEVICES) {
            //车队选择返回
            if (data != null) {
                People people = data.getParcelableExtra(AppParams.RESULT_CODE_KEY);
                if (people != null) {
                    Log.d(TAG, ">>>>>>>>>>>>>>>>" + people.getPhoneNumber() + "," + people.getName());
//                Toast.makeText(this,people.getPhoneNumber(),Toast.LENGTH_SHORT).show();
                    EditText edit = (EditText) findViewById(R.id.order_detail_phone);
                    edit.setText(people.getPhoneNumber());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    final int[] edit_ids = {R.id.order_detail_phone, R.id.order_detail_kg_bt, R.id.order_detail_check_phone_ed, R.id.order_detail_start_bt,
            R.id.order_detail_end_bt, R.id.order_detail_type_bt, R.id.order_detail_name_bt,R.id.order_detail_truck_type_bt};

    private void initView() {
        TextView textView;
        EditText editText;
        Button button;
        switch (currentMode) {
            case EnterpriseMode://企业模式
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                button = (Button) findViewById(R.id.order_detail_yes);
                button.setText("下单");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClick_Back(v);
                    }
                });
                for (int id : edit_ids) {
                    if (id == R.id.order_detail_start_bt || id == R.id.order_detail_end_bt) {

                    } else {
                        View viewById = findViewById(id);
                        if (viewById instanceof EditText) {
                            editText = (EditText) viewById;
                            editText.setEnabled(false);
                            editText.setBackgroundColor(getResources().getColor(R.color.mask_black));
                        } else {
                            viewById.setClickable(false);
                        }

                    }
                }
                break;
            case InformationMode://信息部模式
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                button = (Button) findViewById(R.id.order_detail_yes);
                button.setText("提交订单");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edit = (EditText) findViewById(R.id.order_detail_phone);
                        if (edit.getText().length() == 0) {
                            showChooseCallDialog();
                        } else {
                            finish();
                        }
                    }
                });
                //更改按钮功能  定位改成车队列表选择；
                button = (Button) findViewById(R.id.order_detail_phone_btn);
                button.setText("");
                button.setBackgroundResource(R.drawable.add_devices);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        startActivity(FleetActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(AppParams.RESULT_CODE_KEY, AppParams.RESULT_CODE_SELECT_DEVICES);
                        startActivityForResult(FleetActivity.class, bundle, AppParams.RESULT_CODE_SELECT_DEVICES);
                    }
                });
                break;
            case DriverMode://司机
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                findViewById(R.id.order_detail_phone_ly).setVisibility(View.GONE);
                textView = (TextView) findViewById(R.id.order_detail_yes);
                textView.setText("抢单");
                for (int id : edit_ids) {
                    View viewById = findViewById(id);
                    if (viewById instanceof EditText) {
                        editText = (EditText) viewById;
                        editText.setEnabled(false);
                        editText.setBackgroundColor(getResources().getColor(R.color.mask_black));
                    } else {
                        viewById.setClickable(false);
                    }
                }
                break;
            case CompletedMode:
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                findViewById(R.id.order_detail_phone_btn).setVisibility(View.GONE);
                textView = (TextView) findViewById(R.id.order_detail_yes);
                textView.setText("订单已完成");
                break;
            case SubmittedMode:
//                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                textView = (TextView) findViewById(R.id.order_detail_yes);
                textView.setText("订单已提交");
                break;
        }
    }

    private void showChooseCallDialog() {
//        new AlertDialog.Builder(this).setNegativeButton("呼叫车队",null)
//                .setPositiveButton("Call车",null).show();

//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_call, null);
//        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.cancel();
                if (v.getId() == R.id.dialog_call_me_btn) {
                    gotoFleet();
                } else {
                    finish();
                }
            }
        };
//        view.findViewById(R.id.dialog_call_me_btn).setOnClickListener(listener);
//        view.findViewById(R.id.dialog_call_all_btn).setOnClickListener(listener);
//        dialog.show();

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
