package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.data.People;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class FleetListAdapter extends BaseAdapter {

    private ArrayList<People> list = new ArrayList<People>();
    private DelBtnListener delBtnListener;
    boolean hide_delBtn;

    public FleetListAdapter(boolean isSelect) {
        this.hide_delBtn = isSelect;
    }


    public ArrayList<People> getList() {
        return list;
    }

    public void addPeople(ArrayList<People> array) {
        list.addAll(array);
    }

    public void setPeoples(ArrayList<People> array) {
        list.clear();
        list.addAll(array);
    }

    public void addPeople(People people) {
        list.add(people);
    }

    public void removePeople(People people) {
        if (list == null || list.isEmpty())
            return;
        if (list.contains(people)) {
            list.remove(people);
        }
    }

    public void removePeople(int position) {
        if (list.isEmpty() || position >= list.size())
            return;
        list.remove(position);
    }

    public void setDelBtnListener(DelBtnListener delBtnListener) {
        this.delBtnListener = delBtnListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public People getItem(int position) {
        return list == null || list.isEmpty() || position < 0 || position >= list.size() ? null : list.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fleet_list_item, null);

            holder.name = (TextView) convertView.findViewById(R.id.fleet_it_name_tx);
            holder.phone = (TextView) convertView.findViewById(R.id.fleet_it_phone_tx);
            holder.del = (ImageButton) convertView.findViewById(R.id.fleet_it_del_btn);
            if (hide_delBtn) {
                holder.del.setVisibility(View.GONE);
            } else {
                holder.del.setOnClickListener(holder);
            }
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
                People item = getItem(id);
                if (item != null)
                    delBtnListener.onClick(id, item);
            }
        }
    }

    public interface DelBtnListener {
        void onClick(int position, People people);
    }
}
