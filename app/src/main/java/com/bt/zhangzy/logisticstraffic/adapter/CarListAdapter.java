package com.bt.zhangzy.logisticstraffic.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Car;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆列表的 数据适配器
 * Created by ZhangZy on 2016-8-24.
 */
public class CarListAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = CarListAdapter.class.getSimpleName();
    List<Car> list;
    boolean isCheckMode;
    ArrayList<Integer> checkList = new ArrayList<>();
    boolean isLook;

    public CarListAdapter(List<Car> list) {
        this.list = list;
    }



    public void setLook(boolean look) {
        isLook = look;
    }

    public void setCheckMode(boolean checkMode) {
        isCheckMode = checkMode;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Car getItem(int position) {
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

            holder = new Holder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_item, null);
            convertView.setTag(holder);

            holder.carNumTx = (TextView) convertView.findViewById(R.id.item_car_num_tx);
            holder.carTypeTx = (TextView) convertView.findViewById(R.id.item_car_type_tx);
            holder.carLengthTx = (TextView) convertView.findViewById(R.id.item_car_length_tx);
            holder.carWeightTx = (TextView) convertView.findViewById(R.id.item_car_weight_tx);
            holder.carStatusTx = (TextView) convertView.findViewById(R.id.item_car_status_tx);
            holder.payStatusTx = (TextView) convertView.findViewById(R.id.item_car_pay_status_tx);
            if (isLook) {
                holder.driverLy = convertView.findViewById(R.id.item_car_driver_ly);
            } else {
                holder.driverNameTx = (TextView) convertView.findViewById(R.id.item_car_driver_name_tx);
                holder.driverPhoneTx = (TextView) convertView.findViewById(R.id.item_car_driver_phone_tx);
            }
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.item_check_bt);


        } else {
            holder = (Holder) convertView.getTag();
        }


        Car car = list.get(position);
        ViewUtils.setText(holder.carNumTx, car.getNumber());
        ViewUtils.setText(holder.carTypeTx, car.getType());
        ViewUtils.setText(holder.carLengthTx, car.getLength());
        ViewUtils.setText(holder.carWeightTx, car.getCapacity());
        ViewUtils.setText(holder.carStatusTx, car.getRunStatusName());
        ViewUtils.setText(holder.payStatusTx, car.getPayStatusName());
        if (isLook) {
            holder.driverLy.setVisibility(View.GONE);
        } else {
            ViewUtils.setText(holder.driverNameTx, car.getName());
            ViewUtils.setText(holder.driverPhoneTx, car.getPhoneNumber());
        }


        if (isCheckMode) {
            holder.checkBox.setTag(position);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(this);
            if (checkList.contains(position)) {
                holder.checkBox.setChecked(true);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == null)
            return;

        int position = (int) buttonView.getTag();
        setCheck(isChecked, position);
        Log.d(TAG, buttonView.getTag() + "  =========>>check: " + isChecked + " list==>" + checkList);

    }

    private void setCheck(boolean isChecked, int position) {
        if (isChecked) {
            if (!checkList.contains(position)) {
                checkList.add(position);
            }
        } else {
            if (checkList.contains(position)) {
                checkList.remove(checkList.indexOf(position));
            }
        }
    }

    /**
     * 返回选中的 车辆列表
     *
     * @return
     */
    public ArrayList<Car> getCheckList() {
        ArrayList<Car> checkCarList = new ArrayList<>();
        if (checkList.isEmpty()) {
            return null;
        } else {
            for (int k : checkList) {
                checkCarList.add(getItem(k));
            }
        }

        return checkCarList;
    }

    public void allCheck() {
        for (int k = 0; k < getCount(); k++) {
            setCheck(true, k);
        }
        notifyDataSetChanged();
    }


    class Holder {
        TextView carNumTx, carTypeTx, carLengthTx, carWeightTx, carStatusTx, payStatusTx;
        TextView driverNameTx, driverPhoneTx;
        CheckBox checkBox;
        View driverLy;

    }

}
