package com.bt.zhangzy.logisticstraffic.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.OrderReceiveStatus;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceCarListAdapter extends BaseAdapter {
    private List<JsonCar> list;
    private ArrayList<Integer> selectedList = new ArrayList<>();
    int needSize;
    boolean isShowLoadingStatus;
    BtnClickListener callback;
    View.OnClickListener statusBtListener;

    public SourceCarListAdapter(List<JsonCar> list) {
        this.list = list;
    }

    public void initSelsect(int size) {
        selectedList = new ArrayList<>(size);
        needSize = size;
    }

    public void initLoadinStatus(BtnClickListener listener) {
        isShowLoadingStatus = true;
        callback = listener;
        statusBtListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null && v.getTag() != null) {
                    if (callback != null)
                        callback.onClick((int) v.getTag());
                }
            }
        };
    }

    public boolean isSelect(int position) {
        Iterator<Integer> it = selectedList.iterator();
        while (it.hasNext()) {
            if (position == it.next()) {
                return true;
            }
        }
        return false;
    }

    //
    public void selectPosition(int position, boolean selected) {

        if (selected) {
            //防止重复添加
            if (isSelect(position))
                return;

            if (selectedList.size() == needSize) {
                //如果已经数量已经足够 则不能继续添加
                return;
            }
            selectedList.add(position);
        } else {
            selectedList.remove(Integer.valueOf(position));
        }

        notifyDataSetChanged();
        return;
    }

    public ArrayList<Integer> getSelectedList() {

        return selectedList;
    }


    public int getSelectedListSize() {
        return selectedList.size();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JsonCar getItem(int position) {
        if (list == null || list.isEmpty())
            return null;
        position = Math.max(0, Math.min(position, list.size() - 1));
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_list_item, null);
        }
        Holder holder;
        if (convertView.getTag() == null) {
            holder = new Holder();
            convertView.setTag(holder);
            holder.headImg = (ImageView) convertView.findViewById(R.id.item_img);
//            holder.recommendImg = (ImageView) convertView.findViewById(R.id.item_recommend_img);
            holder.nameTx = (TextView) convertView.findViewById(R.id.item_name_tx);
            holder.typeTx = (TextView) convertView.findViewById(R.id.item_type_tx);
            holder.lengthTx = (TextView) convertView.findViewById(R.id.item_length_tx);
            holder.weightTx = (TextView) convertView.findViewById(R.id.item_weight_tx);
            holder.locationTx = (TextView) convertView.findViewById(R.id.item_location_tx);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.item_it_select_btn);
            holder.loadingStatusBt = (Button) convertView.findViewById(R.id.item_loading_status_tx);

        } else {
            holder = (Holder) convertView.getTag();
        }
        JsonCar car = list.get(position);
        if (car != null) {
            ViewUtils.setText(holder.nameTx, car.getName() + "[" + car.getNumber() + "]");
            ViewUtils.setText(holder.typeTx, car.getType());
            if (!TextUtils.isEmpty(car.getLength()))
                ViewUtils.setText(holder.lengthTx, car.getLength() + "米");
            ViewUtils.setText(holder.weightTx, car.getCapacity());
            ViewUtils.setText(holder.locationTx, car.getUsualResidence());
            ViewUtils.setImageUrl(holder.headImg, car.getFrontalPhotoUrl1());
        }

        if (needSize > 0) {
            holder.loadingStatusBt.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setSelected(false);
            for (int s : selectedList) {
                if (position == s) {
                    holder.checkBox.setSelected(true);
                    break;
                }
            }
        } else if (isShowLoadingStatus) {
            holder.loadingStatusBt.setVisibility(View.VISIBLE);

            if (car.getReceiveStatus().ordinal() >= OrderReceiveStatus.Accept.ordinal()) {
                // 取货中 确认取货  运输中  货已送达
                String[] strings = parent.getResources().getStringArray(R.array.order_driver_loading_status_items);
                holder.loadingStatusBt.setText(strings[car.getReceiveStatus().ordinal() - OrderReceiveStatus.Accept.ordinal()]);
                switch (car.getReceiveStatus()) {
                    case Accept:
                    case LoadingFinish:
                    case Finish:
                        holder.loadingStatusBt.setEnabled(false);
                        break;
                    case Loading:
                        holder.loadingStatusBt.setEnabled(true);
                        holder.loadingStatusBt.setTag(position);
                        holder.loadingStatusBt.setOnClickListener(statusBtListener);
                        break;
                }
            }
        } else {
            holder.loadingStatusBt.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    class Holder {
        TextView nameTx, typeTx, lengthTx, weightTx, locationTx;
        ImageView headImg, recommendImg;
        ImageView checkBox;
        Button loadingStatusBt;
    }

    public interface BtnClickListener {
        public void onClick(int position);
    }
}
