package com.bt.zhangzy.logisticstraffic.adapter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
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
//            final int[] fakeImgId = {R.drawable.fake_1, R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, null);
            holder.position = position;
            holder.img = (ImageView) convertView.findViewById(R.id.item_img);
            holder.button = (ImageButton) convertView.findViewById(R.id.list_item_phone);
            holder.textView = (TextView) convertView.findViewById(R.id.list_item_name_tx);
            holder.levelBar = (RatingBar) convertView.findViewById(R.id.list_item_lv_rating);
            holder.vipImg = (ImageView) convertView.findViewById(R.id.list_item_vip_img);
            holder.layout = convertView.findViewById(R.id.list_item_ly);
            holder.call_times_tx = (TextView) convertView.findViewById(R.id.list_item_call_times_tx);
            holder.times_tx = (TextView) convertView.findViewById(R.id.list_item_times_count_tx);
            holder.dir_tx = (TextView) convertView.findViewById(R.id.list_item_dir_tx);

            listView.add(holder);

//            holder.img.setImageResource(fakeImgId[position % fakeImgId.length]);

            holder.button.setOnClickListener(holder.listener);
            holder.layout.setOnClickListener(holder.listener);

        }
        Product product = getItem(position);
        if (product != null) {
            Resources resources = convertView.getResources();
            ViewUtils.setImageUrl(holder.img, product.getPhotoUrl());
            ViewUtils.setTextView(holder.textView, product.getName());
            String callTimes = product.getCallTimes();
            if (!TextUtils.isEmpty(callTimes)) {
                ViewUtils.setTextView(holder.call_times_tx, String.format(resources.getString(R.string.item_call_times_template), callTimes));
            }
            String times = product.getTimes();
            if (!TextUtils.isEmpty(times))
                ViewUtils.setTextView(holder.times_tx, String.format(resources.getString(R.string.item_view_times_template), times));
            ViewUtils.setTextView(holder.dir_tx, product.getDescribe());


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

        }
        return convertView;
    }


    class ViewHolder {
        int position;
        ImageView img;
        TextView textView;
        TextView call_times_tx, times_tx;
        TextView dir_tx;
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
