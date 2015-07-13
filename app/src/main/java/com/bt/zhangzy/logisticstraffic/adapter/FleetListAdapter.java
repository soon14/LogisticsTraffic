package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.data.People;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetListAdapter extends BaseAdapter {

    private ArrayList<People> list = new ArrayList<People>();
    private DelBtnListener delBtnListener;

    public ArrayList<People> getList() {
        return list;
    }

    public void addPeople(ArrayList<People> array) {
        list.addAll(array);
        notifyDataSetChanged();
    }

    public void addPeople(People people) {
        list.add(people);
        notifyDataSetChanged();
    }

    public void removePeople(int position) {
        if (list.isEmpty() || position >= list.size())
            return;
        list.remove(position);
        notifyDataSetChanged();
    }

    public void setDelBtnListener(DelBtnListener delBtnListener) {
        this.delBtnListener = delBtnListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fleet_list_item, null);
            holder = new Holder(position);
            holder.name = (TextView) convertView.findViewById(R.id.fleet_it_name_tx);
            holder.phone = (TextView) convertView.findViewById(R.id.fleet_it_phone_tx);
            holder.del = (ImageButton) convertView.findViewById(R.id.fleet_it_del_btn);
            holder.del.setOnClickListener(holder);
            convertView.setTag(holder);
        } else if (convertView.getTag() != null) {
            holder = (Holder) convertView.getTag();
        }
        if (holder != null) {
            People people = list.get(position);
            holder.name.setText(people.getName());
            holder.phone.setText(people.getPhoneNumber());

        }


        return convertView;
    }

    class Holder implements View.OnClickListener {
        final int id;
        TextView name;
        TextView phone;
        ImageButton del;

        Holder(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
//            removePeople(id);
            if (delBtnListener != null) {
                delBtnListener.onClick(id);
            }
        }
    }

    public interface DelBtnListener {
         void onClick(int id);
    }
}
