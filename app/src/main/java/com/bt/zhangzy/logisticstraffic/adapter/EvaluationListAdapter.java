package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.entity.ResponseComment;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class EvaluationListAdapter extends BaseAdapter {
    List<ResponseComment> list;

    public EvaluationListAdapter(List<ResponseComment> commentList) {
        list = commentList;
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

//    final int[] ids = {R.drawable.fake_user_1, R.drawable.fake_user_2, R.drawable.fake_user_3, R.drawable.fake_user_4, R.drawable.fake_user_5, R.drawable.fake_user_6};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluate_list_item, null);
            holder = new Holder();
            convertView.setTag(holder);
            holder.headImg = (ImageView) convertView.findViewById(R.id.evaluate_list_user_img);
            holder.vipImg = (ImageView) convertView.findViewById(R.id.item_vip_img);
            holder.nameTx = (TextView) convertView.findViewById(R.id.item_name_tx);
            holder.dateTx = (TextView) convertView.findViewById(R.id.item_date_tx);
            holder.contentTx = (TextView) convertView.findViewById(R.id.item_content_tx);
            holder.lvBar = (RatingBar) convertView.findViewById(R.id.item_star_bar);

//            ImageView img = (ImageView) convertView.findViewById(R.id.evaluate_list_user_img);
//            img.setImageResource(ids[position % ids.length]);
        } else {
            holder = (Holder) convertView.getTag();
        }

        ResponseComment json = list.get(position);
        ViewUtils.setText(holder.nameTx, json.getRoleObject().getName());
        ViewUtils.setText(holder.contentTx, json.getComment().getContent());
        ViewUtils.setText(holder.dateTx, Tools.toStringDate(json.getComment().getDate()));
        holder.lvBar.setRating((float) json.getComment().getRate());
        //// TO DO: 2016-3-2  评价内容缺少 名字、头像图片、是否vip 等参数
        ViewUtils.setImageUrl(holder.headImg, json.getRoleObject().getPersonPhotoUrl());

        return convertView;
    }

    class Holder {
        ImageView headImg, vipImg;
        TextView nameTx, contentTx, dateTx;
        RatingBar lvBar;
    }
}
