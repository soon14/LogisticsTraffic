package com.bt.zhangzy.logisticstraffic.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.tools.ViewUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class HomeListAdapter extends BaseAdapter {

    ArrayList<ViewHolder> listView = new ArrayList<ViewHolder>();

    private OnClickItemListener itemListener;
    private List<Product> list;

    public HomeListAdapter(List<Product> list) {
        this.list = list;
    }

    public void addList(List<Product> list) {
        list.addAll(list);
    }

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.itemListener = listener;
    }


    @Override
    public int getCount() {
        return list == null || list.isEmpty() ? 0 : list.size();
    }

    @Override
    public Product getItem(int position) {
        return position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            final int[] fakeImgId = {R.drawable.fake_1, R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, null);
            holder.position = position;
            holder.img = (ImageView) convertView.findViewById(R.id.list_item_img);
            holder.button = (ImageButton) convertView.findViewById(R.id.list_item_phone);
            holder.textView = (TextView) convertView.findViewById(R.id.list_item_name_tx);
            holder.levelBar = (RatingBar) convertView.findViewById(R.id.list_item_lv_rating);
            holder.vipImg = (ImageView) convertView.findViewById(R.id.list_item_vip_img);
            holder.layout = convertView.findViewById(R.id.list_item_ly);
            holder.call_times_tx = (TextView) convertView.findViewById(R.id.list_item_times_count_tx);
            holder.times_tx = (TextView) convertView.findViewById(R.id.list_item_times_tx);
            listView.add(holder);

            holder.img.setImageResource(fakeImgId[position % fakeImgId.length]);

            holder.button.setOnClickListener(holder.listener);
            holder.layout.setOnClickListener(holder.listener);

        }
        Product product = getItem(position);
        if (product != null) {
            ViewUtils.setTextView(holder.textView, product.getName());
            if (holder.levelBar != null) {
                if (product.getLevel() <= 0) {
                    holder.levelBar.setVisibility(View.GONE);
                } else {
                    holder.levelBar.setRating(product.getLevel());
                }
            }
            if (holder.vipImg != null) {
                holder.vipImg.setVisibility(product.isVip() ? View.VISIBLE : View.INVISIBLE);
            }
            if (!TextUtils.isEmpty(product.getCallTimes())) {
                holder.call_times_tx.setText(product.getCallTimes());
            }
            if (!TextUtils.isEmpty(product.getTimes())) {
                holder.times_tx.setText(product.getTimes());
            }
        }
        return convertView;
    }


    class ViewHolder {
        int position;
        ImageView img;
        TextView textView;
        TextView call_times_tx, times_tx;
        ImageButton button;
        View layout;
        RatingBar levelBar;
        ImageView vipImg;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.onItemClick(v, position);
                }
            }
        };
    }

    public interface OnClickItemListener {
        public void onItemClick(View v, int position);
    }
}
