package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonTender;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * Created by ZhangZy on 2016-3-15.
 */
public class TenderListAdapter extends BaseAdapter {

    List<JsonTender> list;

    public TenderListAdapter(List<JsonTender> list) {
//        super(list);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public JsonTender getItem(int position) {
        return list == null ? null : position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HolderView holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tender_item, null);
            holder = new HolderView();
            convertView.setTag(holder);
            holder.name = (TextView) convertView.findViewById(R.id.item_name_tx);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        JsonTender json = getItem(position);
        ViewUtils.setText(holder.name, json.getName());
        return convertView;
    }

    class HolderView {
        TextView name;
    }
}
