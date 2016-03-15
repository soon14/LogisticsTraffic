package com.bt.zhangzy.logisticstraffic.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class CollectListAdapter extends BaseAdapter {

    private static final String TAG = CollectListAdapter.class.getSimpleName();
    //    ArrayList<ViewHolder> listView = new ArrayList<ViewHolder>();
    ArrayList<Product> list;

    private boolean isMyCollectType;

    public CollectListAdapter(boolean isMyCollectType, ArrayList<Product> array) {
        this.isMyCollectType = isMyCollectType;
        setData(array);
    }

    public void setData(ArrayList<Product> array) {
        try {

            if (array != null && !array.isEmpty()) {
                list = new ArrayList<Product>(array);
//                notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Log.e(TAG, "setData()", ex);
        }
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Product getItem(int position) {
        return list == null ? null : position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;
            if (convertView == null) {
//            final int[] fakeImgId = {R.drawable.fake_1, R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_list_item, null);

                convertView.findViewById(R.id.collect_list_type_other).setVisibility(isMyCollectType ? View.GONE : View.VISIBLE);
                convertView.findViewById(R.id.collect_list_type_me).setVisibility(isMyCollectType ? View.VISIBLE : View.GONE);
                holder = new ViewHolder();
                holder.position = position;
                holder.img = (ImageView) convertView.findViewById(isMyCollectType ? R.id.collect_list_img_me : R.id.collect_list_img_other);
                holder.name = (TextView) convertView.findViewById(R.id.list_item_name_tx);
                holder.dir = (TextView) convertView.findViewById(R.id.list_item_dir_tx);
                holder.lv = (RatingBar) convertView.findViewById(R.id.list_item_lv_rating);
                holder.times = (TextView) convertView.findViewById(R.id.list_item_times_tx);
                holder.vipImg = (ImageView) convertView.findViewById(R.id.list_item_vip_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Product item = list.get(position);
            if (TextUtils.isEmpty(item.getIconImgUrl())) {
                //设置默认图
                holder.img.setImageResource(R.drawable.fake_1);
            } else
                ViewUtils.setImageUrl(holder.img, item.getIconImgUrl());
            ViewUtils.setText(holder.name, item.getName());
            ViewUtils.setText(holder.dir, item.getDescribe());
            String times = holder.times.getText().toString();
            ViewUtils.setText(holder.times, times.replace("#n", item.getTimes() == null ? "0" : item.getTimes()));
//            holder.lv.setRating(item.getLevel());
            if (holder.lv != null) {
                if (item.getLevel() <= 0) {
                    holder.lv.setVisibility(View.GONE);
                } else {
                    holder.lv.setRating(item.getLevel());
                }
            }
            if (holder.vipImg != null) {
                holder.vipImg.setVisibility(item.isVip() ? View.VISIBLE : View.INVISIBLE);
            }
            return convertView;
        } catch (Exception ex) {
            Log.e(TAG, "getView()", ex);
        }
        return null;
    }

    class ViewHolder {
        int position;
        ImageView img;
        TextView name;
        TextView dir;
        TextView times;
        RatingBar lv;
        View layout;
        ImageView vipImg;
    }

}
