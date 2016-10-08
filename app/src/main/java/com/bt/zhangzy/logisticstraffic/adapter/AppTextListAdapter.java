package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.People;

import java.util.List;

/**
 * Created by ZhangZy on 2016-8-8.
 */
public class AppTextListAdapter extends BaseAdapter{

    private List<People> list = null;

    public AppTextListAdapter(List<People> array) {
        list = array;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public People getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_text_list_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.app_text_list_item_tx);
            convertView.setTag(holder);
        } else if (convertView.getTag() != null) {
            holder = (Holder) convertView.getTag();
        }
        if (holder != null) {
            String tx = list.get(position).getName();
            holder.name.setText(tx);
        }


        return convertView;
    }

    public List<People> getList() {
        return list;
    }

    class Holder {
        final int id;
        TextView name;

        Holder(int id) {
            this.id = id;
        }
    }

}
