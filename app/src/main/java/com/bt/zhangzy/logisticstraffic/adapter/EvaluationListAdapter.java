package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class EvaluationListAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    final int[] ids = {R.drawable.fake_user_1, R.drawable.fake_user_2, R.drawable.fake_user_3, R.drawable.fake_user_4, R.drawable.fake_user_5, R.drawable.fake_user_6};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluate_list_item, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.evaluate_list_user_img);
            img.setImageResource(ids[position % ids.length]);
        }

        return convertView;
    }
}
