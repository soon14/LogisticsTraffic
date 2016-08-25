package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * 车辆列表的 数据适配器
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListAdapter extends BaseAdapter {

    List<JsonCar> list;

    public CarListAdapter(List<JsonCar> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JsonCar getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_item, null);
            convertView.setTag(holder);
            holder.carNumTx = (TextView) convertView.findViewById(R.id.item_car_num_tx);

        } else {
            holder = (Holder) convertView.getTag();
        }

        JsonCar jsonCar = list.get(position);
        ViewUtils.setText(holder.carNumTx, jsonCar.getNumber());
        return convertView;
    }


    class Holder {
        TextView carNumTx;
    }

}