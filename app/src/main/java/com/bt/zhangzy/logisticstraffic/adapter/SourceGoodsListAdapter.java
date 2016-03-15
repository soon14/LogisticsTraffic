package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonOrder;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceGoodsListAdapter extends BaseAdapter {

    List<JsonOrder> list;

    public SourceGoodsListAdapter(List<JsonOrder> list) {
        this.list = list;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_goods_list_item, null);
        }
        Holder holder;
        if (convertView.getTag() == null) {
            holder = new Holder();
            holder.startCity = (TextView) convertView.findViewById(R.id.sg_item_start_tx);
            holder.stopCity = (TextView) convertView.findViewById(R.id.sg_item_stop_tx);
            holder.goodsType = (TextView) convertView.findViewById(R.id.sg_item_type_tx);
            holder.goodsWeight = (TextView) convertView.findViewById(R.id.sg_item_weight_tx);
            holder.creatTime = (TextView) convertView.findViewById(R.id.sg_item_date_tx);

        } else {
            holder = (Holder) convertView.getTag();
        }

        JsonOrder order = list.get(position);
        ViewUtils.setText(holder.startCity, order.getStartCity());
        ViewUtils.setText(holder.stopCity, order.getStopCity());
        ViewUtils.setText(holder.goodsType, order.getGoodsType());
        ViewUtils.setText(holder.goodsWeight, order.getGoodsWeight());
        ViewUtils.setText(holder.creatTime, Tools.toStringDate(order.getPublishDate()));


        return convertView;
    }

    class Holder {
        TextView startCity, stopCity;
        TextView goodsType, goodsWeight;
        TextView creatTime;
    }
}
