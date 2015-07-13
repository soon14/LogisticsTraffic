package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class OrderListAdapter extends BaseAdapter {

    private boolean isBlankOut;

    public OrderListAdapter(boolean isBlankOut){
        this.isBlankOut = isBlankOut;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, null);

            convertView.findViewById(R.id.orderlist_item_blankout).setVisibility(isBlankOut ? View.VISIBLE : View.INVISIBLE);
            if(isBlankOut){
                convertView.findViewById(R.id.orderlist_item_ly).setBackgroundColor(parent.getResources().getColor(R.color.editpage_bg_color));
            }
        }

        return convertView;
    }
}
