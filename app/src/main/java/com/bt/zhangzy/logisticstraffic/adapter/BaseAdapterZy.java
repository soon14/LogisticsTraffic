package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by ZhangZy on 2016-3-15.
 */
public abstract class BaseAdapterZy<T> extends BaseAdapter {
    List<T> list;

    public BaseAdapterZy(List<T> list) {
        this.list = list;
    }

    public void setData(List<T> list) {
        if (list == null || list.isEmpty())
            return;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public T getItem(int position) {
        return list == null ? null : position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
