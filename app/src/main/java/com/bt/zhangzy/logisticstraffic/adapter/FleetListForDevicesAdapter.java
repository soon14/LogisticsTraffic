package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonMotorcades;

import java.util.List;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetListForDevicesAdapter extends BaseAdapter {

    private List<JsonMotorcades> list = null;

    public FleetListForDevicesAdapter(List<JsonMotorcades> array) {
        list = array;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JsonMotorcades getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        if (convertView == null) {
            holder = new Holder(position);
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fleet_list_devices_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.fleet_it_name_tx);
            holder.phone = (TextView) convertView.findViewById(R.id.fleet_it_phone_tx);
            convertView.setTag(holder);
        } else if (convertView.getTag() != null) {
            holder = (Holder) convertView.getTag();
        }
        if (holder != null) {
            JsonMotorcades people = list.get(position);
            holder.name.setText(people.getName());
//                holder.phone.setText(people.getPhoneNumber());
        }


        return convertView;
    }

    class Holder {
        final int id;
        TextView name;
        TextView phone;

        Holder(int id) {
            this.id = id;
        }
    }

}
