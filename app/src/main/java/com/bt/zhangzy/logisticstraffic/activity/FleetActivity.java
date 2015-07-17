package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.FleetListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.People;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetActivity extends BaseActivity {

    ListView listView;
    boolean isSelectDevices = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fleet);

        setPageName("我的车队");
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(Constant.RESULT_CODE_KEY)) {
                if (bundle.getInt(Constant.RESULT_CODE_KEY) == Constant.RESULT_CODE_SELECT_DEVICES) {
                    isSelectDevices = true;
                }
            }
        }


        listView = (ListView) findViewById(R.id.fleet_list);
        FleetListAdapter adapter = new FleetListAdapter(isSelectDevices);
        adapter.addPeople(User.getInstance().getDriverList());
        listView.setAdapter(adapter);
        adapter.setDelBtnListener(new FleetListAdapter.DelBtnListener() {
            @Override
            public void onClick(int id) {
                onclick_DelDriver(id);
            }
        });

        if (isSelectDevices) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    People people = (People) listView.getItemAtPosition(position);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constant.RESULT_CODE_KEY, people);
//                    intent.putExtra(Constant.RESULT_CODE_KEY,people);
                    intent.putExtras(bundle);
                    setResult(Constant.RESULT_CODE_SELECT_DEVICES, intent);
                    finish();
                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User.getInstance().setDriverList(((FleetListAdapter) listView.getAdapter()).getList());
    }

    public void onclick_DelDriver(final int id) {
        BaseDialog.showConfirmDialog(this, "确认删除车队司机", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FleetListAdapter adp = (FleetListAdapter) listView.getAdapter();
                adp.removePeople(id);
            }
        });
    }

    public void onclick_AddDriver(View view) {
        BaseDialog dialog = new BaseDialog(this);
        dialog.setView(R.layout.dialog_add_driver);
        dialog.setOnClickListener(R.id.add_driver_dl_cancel_bt, null).setOnClickListenerForDialog(R.id.add_driver_dl_confirm_bt, new BaseDialog.DialogClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
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


        }).show();
    }
}
