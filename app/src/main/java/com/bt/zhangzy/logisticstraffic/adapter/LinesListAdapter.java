package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.JsonLine;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * Created by ZhangZy on 2016-5-23.
 */
public class LinesListAdapter extends BaseAdapter implements View.OnClickListener {

    List<JsonLine> lineList;
    OnClickItemEdit listener;

    public LinesListAdapter(List<JsonLine> linesList) {
        this.lineList = linesList;
    }

    public void setListener(OnClickItemEdit listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return lineList == null ? 0 : lineList.size();
    }

    @Override
    public JsonLine getItem(int position) {
        return lineList == null ? null : lineList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_item, null);
            holder = new Holder();
            convertView.setTag(holder);
            holder.receiverName = (TextView) convertView.findViewById(R.id.line_item_receiver_name);
            holder.receiverPhone = (TextView) convertView.findViewById(R.id.line_item_receiver_phone);
            holder.receiverAddress = (TextView) convertView.findViewById(R.id.line_item_receiver_address);
            holder.consignorName = (TextView) convertView.findViewById(R.id.line_item_consignor_name);
            holder.consignorPhone = (TextView) convertView.findViewById(R.id.line_item_consignor_phone);
            holder.consignorAddress = (TextView) convertView.findViewById(R.id.line_item_consignor_address);
            holder.editBt = convertView.findViewById(R.id.line_item_edit_bt);
        } else {
            holder = (Holder) convertView.getTag();
        }
        JsonLine jsonLine = lineList.get(position);
        if (jsonLine != null) {
            ViewUtils.setText(holder.consignorName, jsonLine.getConsignorName());
            ViewUtils.setText(holder.consignorPhone, jsonLine.getConsignorPhone());
            ViewUtils.setText(holder.consignorAddress, jsonLine.getConsignorCity() + '-' + jsonLine.getConsignorAddress());
            ViewUtils.setText(holder.receiverName, jsonLine.getReceiverName());
            ViewUtils.setText(holder.receiverPhone, jsonLine.getReceiverPhone());
            ViewUtils.setText(holder.receiverAddress, jsonLine.getReceiverCity() + '-' + jsonLine.getReceiverAddress());

            holder.editBt.setTag(position);
            holder.editBt.setOnClickListener(this);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int position = (int) v.getTag();
            if (position >= 0 && position < lineList.size()) {
                if (listener != null)
                    listener.onClick(position, lineList.get(position));
            }
        }
    }

    public interface OnClickItemEdit {
        void onClick(int position, JsonLine jsonLine);
    }

    class Holder {
        TextView consignorName, consignorPhone, consignorAddress;
        TextView receiverName, receiverPhone, receiverAddress;
        View editBt;
    }
}
