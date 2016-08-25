package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * 司机列表的 数据适配器
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListDriverAdapter extends BaseAdapter {

    List<JsonDriver> list;

    public CarListDriverAdapter(List<JsonDriver> list) {
        this.list = list;
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

        } else {
            holder = (Holder) convertView.getTag();
        }

        JsonDriver jsonDriver = list.get(position);
        ViewUtils.setText(holder.name, jsonDriver.getName());
        ViewUtils.setText(holder.phone, jsonDriver.getPhone());

        return convertView;
    }


    class Holder {
        TextView name,phone,number;
    }

}
