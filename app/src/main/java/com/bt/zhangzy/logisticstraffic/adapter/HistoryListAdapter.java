package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;

import java.util.List;

/**
 * Created by ZhangZy on 2015/6/25.
 */
public class HistoryListAdapter extends BaseAdapter {

    List<Product> list;
    public HistoryListAdapter() {
        list = User.getInstance().getHistoryList();
    }

    @Override
    public int getCount() {
        return list==null? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list==null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
//            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item ,  null);
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item ,  null);

        }
        return convertView;
    }
}
