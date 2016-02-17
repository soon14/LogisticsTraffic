package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.data.OrderStatus;
import com.bt.zhangzy.network.entity.JsonOrder;
import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderListAdapter extends BaseAdapter {

    List<JsonOrder> list;

    public OrderListAdapter(List<JsonOrder> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JsonOrder getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, null);

//            convertView.findViewById(R.id.orderlist_item_blankout).setVisibility(isBlankOut ? View.VISIBLE : View.INVISIBLE);
//            if(isBlankOut){
//                convertView.findViewById(R.id.orderlist_item_ly).setBackgroundColor(parent.getResources().getColor(R.color.editpage_bg_color));
//            }
            holder = new Holder();
            convertView.setTag(holder);
            holder.blankOut = convertView.findViewById(R.id.orderlist_item_blankout);
            holder.ly = convertView.findViewById(R.id.orderlist_item_ly);
            holder.orderNum = (TextView) convertView.findViewById(R.id.orderlist_item_num_tx);
            holder.enterpriseTx = (TextView) convertView.findViewById(R.id.orderlist_item_enterprise_tx);
            holder.companyTx = (TextView) convertView.findViewById(R.id.orderlist_item_company_tx);
            holder.start_end_Tx = (TextView) convertView.findViewById(R.id.orderlist_item_start_end_tx);
            holder.driverTx = (TextView) convertView.findViewById(R.id.orderlist_item_driver_tx);
            holder.driverPhoneTx = (TextView) convertView.findViewById(R.id.orderlist_item_driver_phone_tx);

        } else {
            holder = (Holder) convertView.getTag();
        }
        JsonOrder order = list.get(position);
        ViewUtils.setTextView(holder.orderNum, String.valueOf(order.getId()));
        ViewUtils.setTextView(holder.enterpriseTx, String.valueOf(order.getEnterpriseId()));
        ViewUtils.setTextView(holder.companyTx, String.valueOf(order.getCompanyId()));
        ViewUtils.setTextView(holder.start_end_Tx, order.getStartCity() + "-" + order.getStopCity());
        ViewUtils.setTextView(holder.driverTx, order.getReceiverName());
        ViewUtils.setTextView(holder.driverPhoneTx, order.getReceiverPhone());
        if (OrderStatus.parseStatus(order.getStatus()) == OrderStatus.DiscardOrder) {
            holder.blankOut.setVisibility(View.VISIBLE);
//            holder.ly.setBackgroundColor(parent.getResources().getColor(R.color.editpage_bg_color));
            holder.ly.setBackgroundResource(R.color.editpage_bg_color);
        } else {
            holder.blankOut.setVisibility(View.INVISIBLE);
//            holder.ly.setBackgroundColor(parent.getResources().getColor(R.color.white));
            holder.ly.setBackgroundResource(R.color.white);

        }


        return convertView;
    }

    class Holder {
        TextView orderNum;
        TextView enterpriseTx;
        TextView companyTx;
        TextView start_end_Tx;
        TextView driverTx;
        TextView driverPhoneTx;
        View blankOut;
        View ly;
    }
}
