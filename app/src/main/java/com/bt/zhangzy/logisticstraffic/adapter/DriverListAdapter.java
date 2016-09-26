package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Driver;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * 司机列表适配器
 * 场景：车队页面，订单中选择抢单司机页面
 * Created by ZhangZy on 2016-9-13.
 */
public class DriverListAdapter extends BaseAdapter {
    static final String TAG = DriverListAdapter.class.getSimpleName();

    //数据源
    List<Driver> list;
    boolean isChooseMode;//标记是否为选择模式；

    public DriverListAdapter(List<Driver> list, boolean isChooseMode) {
        this.list = list;
        this.isChooseMode = isChooseMode;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Driver getItem(int position) {
        return list == null || position >= list.size() ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list_item, null);
            convertView.setTag(holder);
            holder.nameTx = (TextView) convertView.findViewById(R.id.item_name_tx);
            holder.phoneTx = (TextView) convertView.findViewById(R.id.item_phone_tx);
            holder.countTx = (TextView) convertView.findViewById(R.id.item_driver_car_total_tx);
            holder.numTx = (TextView) convertView.findViewById(R.id.item_driver_car_select_total_tx);

        } else {
            holder = (Holder) convertView.getTag();
        }

        Driver driver = list.get(position);
        if (driver != null) {
            ViewUtils.setText(holder.nameTx, driver.getName());
            ViewUtils.setText(holder.phoneTx, driver.getPhone());
            ViewUtils.setText(holder.countTx, parent.getResources().getString(R.string.driver_own_car_num, driver.getOwnCarCount()));
            if (isChooseMode) {
                ViewUtils.setText(holder.numTx, parent.getResources().getString(R.string.driver_select_car_num, driver.getSelectCarNum()));
            } else {
                holder.numTx.setVisibility(View.GONE);
            }

        }


        return convertView;
    }

    private class Holder {
        TextView nameTx, phoneTx;
        TextView countTx, numTx;
    }
}
