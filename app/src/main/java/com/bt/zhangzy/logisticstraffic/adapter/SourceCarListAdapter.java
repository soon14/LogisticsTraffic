package com.bt.zhangzy.logisticstraffic.adapter;

import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceCarListAdapter extends BaseAdapter {
    private List<JsonCar> list;

    public SourceCarListAdapter(List<JsonCar> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JsonCar getItem(int position) {
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
            holder.recommendImg = (ImageView) convertView.findViewById(R.id.item_recommend_img);
            holder.nameTx = (TextView) convertView.findViewById(R.id.item_name_tx);
            holder.typeTx = (TextView) convertView.findViewById(R.id.item_type_tx);
            holder.lengthTx = (TextView) convertView.findViewById(R.id.item_length_tx);
            holder.weightTx = (TextView) convertView.findViewById(R.id.item_weight_tx);
            holder.locationTx = (TextView) convertView.findViewById(R.id.item_location_tx);

        } else {
            holder = (Holder) convertView.getTag();
        }
        JsonCar car = list.get(position);
        if (car != null) {
            ViewUtils.setTextView(holder.nameTx, car.getName()+"["+car.getNumber()+"]");
            ViewUtils.setTextView(holder.typeTx, car.getType());
            ViewUtils.setTextView(holder.lengthTx, car.getLength() + "米");
            ViewUtils.setTextView(holder.weightTx, car.getCapacity());
            ViewUtils.setTextView(holder.locationTx, car.getUsualResidence());
            ViewUtils.setImageUrl(holder.headImg,car.getFrontalPhotoUrl1());
        }

        return convertView;
    }

    class Holder {
        TextView nameTx, typeTx, lengthTx, weightTx, locationTx;
        ImageView headImg, recommendImg;
    }
}
