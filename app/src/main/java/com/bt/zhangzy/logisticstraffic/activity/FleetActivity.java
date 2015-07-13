package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.AlertDialogApp;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fleet);

        setPageName("我的车队");

        listView = (ListView) findViewById(R.id.fleet_list);
        FleetListAdapter adapter = new FleetListAdapter();
        adapter.addPeople(User.getInstance().getDriverList());
        listView.setAdapter(adapter);
        adapter.setDelBtnListener(new FleetListAdapter.DelBtnListener() {
            @Override
            public void onClick(int id) {
                onclick_DelDriver(id);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User.getInstance().setDriverList(((FleetListAdapter) listView.getAdapter()).getList());
    }

    public void onclick_DelDriver(final int id) {
        AlertDialogApp.showConfirm(this, "确认删除车队司机", new AlertDialogApp.DialogClickListener() {
            @Override
            public void onClick(AlertDialog dialog, View view) {
                FleetListAdapter adp = (FleetListAdapter) listView.getAdapter();
                adp.removePeople(id);
                dialog.cancel();
            }
        });
    }

    public void onclick_AddDriver(View view) {
        AlertDialogApp dialog = new AlertDialogApp(this, R.layout.dialog_add_driver);
        dialog.create().show();
        dialog.addClickListener(R.id.add_driver_dl_cancel_bt, null).addClickListener(R.id.add_driver_dl_confirm_bt, new AlertDialogApp.DialogClickListener() {
            @Override
            public void onClick(AlertDialog dialog, View view) {
                EditText name = (EditText) dialog.findViewById(R.id.add_driver_dl_name_ed);
                EditText phone = (EditText) dialog.findViewById(R.id.add_driver_dl_phone_ed);
                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(phone.getText())) {
                    Toast.makeText(context, "请检查填写内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                People p = new People();
                p.setName(name.getText().toString());
                p.setPhoneNumber(phone.getText().toString());
                FleetListAdapter adp = (FleetListAdapter) listView.getAdapter();
                adp.addPeople(p);
                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }
}
