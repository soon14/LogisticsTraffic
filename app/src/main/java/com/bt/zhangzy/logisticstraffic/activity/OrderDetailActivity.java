package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderDetailActivity extends BaseActivity {

    public static final byte MODE_CP = 0x2;
    public static final byte MODE_DP = 0X4;
    public static final byte MODE_DEVICES = 0x8;
    public static final byte MODE_FINISH = 0x16;
    static byte currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("type")){
            int type = bundle.getInt("type");
            if(type == 1){
                currentMode = MODE_FINISH;
            }
        }else {
            if(User.getInstance().getUserType() == Type.DriverType){
                currentMode = MODE_DEVICES;
                Toast.makeText(this, "司机模式", Toast.LENGTH_LONG).show();
            }else if(User.getInstance().getUserType() == Type.EnterpriseType){
                currentMode = MODE_CP;
                Toast.makeText(this, "企业模式", Toast.LENGTH_LONG).show();
            }else if(User.getInstance().getUserType() == Type.InformationType){
                currentMode = MODE_DP;
                Toast.makeText(this, "信息部模式", Toast.LENGTH_LONG).show();
            }

        }

        initView();

    }

    final int[] edit_ids = {/*R.id.order_detail_open_ed,R.id.order_detail_finish_ed,*/R.id.order_detail_phone, R.id.order_detail_kg_ed, R.id.order_detail_check_phone_ed, R.id.order_detail_start_ed,
            R.id.order_detail_end_ed, R.id.order_detail_type_ed, R.id.order_detail_name_ed, R.id.order_detail_size_ed};

    private void initView() {
        TextView textView;
        EditText editText;
        Button button;
        switch (currentMode) {
            case MODE_CP:
                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                button = (Button) findViewById(R.id.order_detail_yes);
                button.setText("下单");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                for (int id : edit_ids) {
                    if (id == R.id.order_detail_start_ed || id == R.id.order_detail_end_ed) {

                    } else {
                        editText = (EditText) findViewById(id);
                        editText.setEnabled(false);
                        editText.setBackgroundColor(getResources().getColor(R.color.mask_black));
                    }
                }
                break;
            case MODE_DP:
                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                button = (Button) findViewById(R.id.order_detail_yes);
                button.setText("提交订单");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChooseCallDialog();
                    }
                });
                break;
            case MODE_DEVICES:
                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                findViewById(R.id.order_detail_yes).setVisibility(View.GONE);
                for (int id : edit_ids) {
                    editText = (EditText) findViewById(id);
                    editText.setEnabled(false);
                    editText.setBackgroundColor(getResources().getColor(R.color.mask_black));
                }
                break;
            case MODE_FINISH:
                findViewById(R.id.order_detail_no).setVisibility(View.GONE);
                textView = (TextView) findViewById(R.id.order_detail_yes);
                textView.setText("订单成功");

                break;
        }
    }

    private void showChooseCallDialog() {
//        new AlertDialog.Builder(this).setNegativeButton("呼叫车队",null)
//                .setPositiveButton("Call车",null).show();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_call,null);
        final AlertDialog dialog =  new AlertDialog.Builder(this).setView(view).create();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(v.getId() == R.id.dialog_call_me_btn){
                    gotoFleet();
                }else{
                    finish();
                }
            }
        };
        view.findViewById(R.id.dialog_call_me_btn).setOnClickListener(listener);
        view.findViewById(R.id.dialog_call_all_btn).setOnClickListener(listener);
        dialog.show();

    }

    private void gotoFleet() {
        startActivity(FleetActivity.class);
    }


}
