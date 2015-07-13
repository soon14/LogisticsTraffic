package com.bt.zhangzy.logisticstraffic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class LocationListAdapter extends BaseAdapter {

    ArrayList<ViewHolder> listView = new ArrayList<ViewHolder>();
    private ItemOnClickCallback itemOnClickCallback;

    public LocationListAdapter(){

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return position<listView.size() ? listView.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item,null);
            ViewHolder holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.location_list_item_title);
            holder.title.setOnClickListener(holder.listener);
            holder.ly = (LinearLayout) convertView.findViewById(R.id.location_list_item_ly);
            holder.addItem("01",parent.getContext());
            holder.addItem("02",parent.getContext());
            holder.addItem("03",parent.getContext());
            holder.addItem("05",parent.getContext());
            listView.add(holder);
        }

        return convertView;
    }

    public void setItemOnClickCallback(ItemOnClickCallback itemOnClickCallback) {
        this.itemOnClickCallback = itemOnClickCallback;
    }

    class ViewHolder{
        TextView title;
        LinearLayout ly;
        ArrayList<String> list = new ArrayList<String>();

        void addItem(String string, Context context){
            if(ly == null)
                return;
            list.add(string);
//            TextView tx = new TextView(context,null,R.style.location_list_smail);
            FrameLayout ly_tx = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.location_list_item_smail,null);
            TextView tx = (TextView) ly_tx.findViewById(R.id.location_list_item_tx);
            tx.setText(string);
            int index = list.indexOf(string);
            tx.setId(index);
//            tx.setTag(index);
            tx.setOnClickListener(listener);
            //,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            ly.addView(ly_tx);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == title) {
                    if (ly != null) {
                        ly.setVisibility(ly.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        notifyDataSetChanged();
                    }
                }else if(v!=null && v.getId() < list.size()){
                    if(v.getId()> -1){
                        if(itemOnClickCallback != null){
                            itemOnClickCallback.onClickItem(list.get(v.getId()));
                        }
                    }
                }
            }
        };
    }

    public interface ItemOnClickCallback{
        public void onClickItem(String string);

    }
}
