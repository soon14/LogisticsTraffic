package com.bt.zhangzy.logisticstraffic.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bt.zhangzy.logisticstraffic.R;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/8.
 */
public class HomeSpreadAdapter extends PagerAdapter {

    private static final String TAG = HomeSpreadAdapter.class.getSimpleName();
    final int[] ids = {R.id.spread_3_ly, R.id.spread_1_ly, R.id.spread_2_ly, R.id.spread_3_ly, R.id.spread_1_ly};

    public HomeSpreadAdapter() {

    }

    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        int index = position % ids.length;
        View view;
        //实例化项目布局 并填入数据
        view = LayoutInflater.from(container.getContext()).inflate(R.layout.home_spread_item, container, false);
        for (int id : ids) {
            view.findViewById(id).setVisibility(View.GONE);
        }
        view.findViewById(ids[index]).setVisibility(View.VISIBLE);
//        Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>addview=" + index + " ," + position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        int index = position % ids.length;
//        Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>destroyItem =" + index + " ," + position);
        View view = (View) object;
        if (container.indexOfChild(view) > 0) {
            container.removeView(view);
//            Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>destroyItem = true");
        }
//        if (container.getChildAt(position) != null) {
//            container.removeViewAt(position);
//        }
    }
}
