package com.bt.zhangzy.logisticstraffic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class CollectListAdapter extends BaseAdapter {

    ArrayList<ViewHolder> listView = new ArrayList<ViewHolder>();

    private OnClickItemListener itemListener;
    private boolean isMyCollectType;

    public CollectListAdapter(boolean isMyCollectType) {
        this.isMyCollectType = isMyCollectType;
    }

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.itemListener = listener;
    }


    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return position < listView.size() ? listView.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            final int[] fakeImgId = {R.drawable.fake_1, R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collect_list_item, null);

            convertView.findViewById(R.id.collect_list_type_other).setVisibility(isMyCollectType ? View.GONE : View.VISIBLE);
            convertView.findViewById(R.id.collect_list_type_me).setVisibility(isMyCollectType ? View.VISIBLE : View.GONE);
            ViewHolder holder = new ViewHolder();
            holder.position = position;
            holder.img = (ImageView) convertView.findViewById(isMyCollectType ? R.id.collect_list_img_me : R.id.collect_list_img_other);
//            holder.button = (ImageButton) convertView.findViewById(R.id.home_list_item_phone);
//            holder.textView = (TextView) convertView.findViewById(R.id.home_list_item_tx);
//            holder.layout = convertView.findViewById(R.id.home_list_item_ly);
            listView.add(holder);

            holder.img.setImageResource(fakeImgId[position % fakeImgId.length]);

//            holder.button.setOnClickListener(holder.listener);
//            holder.layout.setOnClickListener(holder.listener);


        }
        return convertView;
    }

    class ViewHolder {
        int position;
        ImageView img;
        TextView textView;
        ImageButton button;
        View layout;
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
