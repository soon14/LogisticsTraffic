package com.bt.zhangzy.logisticstraffic.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * 司机列表的 数据适配器
 * 如果是选择模式，只能单选
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListDriverAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {


    private static final String TAG = CarListDriverAdapter.class.getSimpleName();
    List<JsonDriver> list;
    boolean isSelectModel;//选择模式
    int selectPosition = -1;
    CompoundButton selectView;//选中的view 重置状态用

    public CarListDriverAdapter(List<JsonDriver> list, boolean isSelectModel) {
        this.list = list;
        this.isSelectModel = isSelectModel;
        if (isSelectModel) {

        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JsonDriver getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {

            holder = new Holder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_driver_item, null);
            convertView.setTag(holder);
            holder.name = (TextView) convertView.findViewById(R.id.item_name_tx);
            holder.phone = (TextView) convertView.findViewById(R.id.item_phone_tx);
            holder.number = (TextView) convertView.findViewById(R.id.item_user_num_tx);
            holder.check = (CheckBox) convertView.findViewById(R.id.item_check_bt);
            holder.check.setVisibility(isSelectModel ? View.VISIBLE : View.GONE);

        } else {
            holder = (Holder) convertView.getTag();
        }

        if (isSelectModel) {
            holder.check.setTag(position);
            holder.check.setOnCheckedChangeListener(this);
//            holder.check.setOnClickListener(this);
        }
        JsonDriver jsonDriver = list.get(position);
        ViewUtils.setText(holder.name, jsonDriver.getName());
        ViewUtils.setText(holder.phone, jsonDriver.getPhone());

        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView != null)
            Log.d(TAG, buttonView.getTag() + "  =========>>check: " + isChecked);

        int position = (int) buttonView.getTag();
        if (isChecked) {
            // 单选模式 重置以前的选项
            if (selectView != null) {
                selectView.setChecked(false); // 会再次调用onCheckedChanged 接口
            }
            selectView = buttonView;
            selectPosition = position;
        } else {
            selectPosition = -1;
        }

    }

    /**
     * 返回选中的对象
     *
     * @return
     */
    public JsonDriver getSelectDriver() {
        if (selectPosition > -1) {
            return list.get(selectPosition);
        }
        return null;
    }


    class Holder {
        TextView name, phone, number;
        CheckBox check;

    }

}
