package com.bt.zhangzy.logisticstraffic.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;

/**
 * Created by ZhangZy on 2015/6/8.
 */
public class HomeSpreadAdapter extends PagerAdapter {

    private static final String TAG = HomeSpreadAdapter.class.getSimpleName();
    final int[] ids = {R.layout.home_spread_item, R.layout.home_spread_item_2, R.layout.home_spread_item_3};

    List<Product> list;
    OnClick clickListener;

    public HomeSpreadAdapter(List<Product> list) {
        this.list = list;
    }

    /**
     * 点击事件注册
     *
     * @param click
     */
    public void setItemClick(OnClick click) {
        this.clickListener = click;
    }

    @Override
    public int getCount() {
        return ids.length * 9;
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
        view = LayoutInflater.from(container.getContext()).inflate(ids[index], container, false);
        switch (index) {
            case 0:
                initView(view,
                        new MEntry(R.id.spread_item_1_img, list.get(0)),
                        new MEntry(R.id.spread_item_2_img, list.get(1)),
                        new MEntry(R.id.spread_item_3_img, list.get(2)),
                        new MEntry(R.id.spread_item_4_img, list.get(3))
                );
                break;
            case 1:
                initView(view,
                        new MEntry(R.id.spread_item_5_img, list.get(4)),
                        new MEntry(R.id.spread_item_6_img, list.get(5))
                );
                break;
            case 2:
                initView(view,
                        new MEntry(R.id.spread_item_7_img, list.get(6)),
                        new MEntry(R.id.spread_item_8_img, list.get(7)),
                        new MEntry(R.id.spread_item_9_img, list.get(8))
                );
                break;
        }
        container.addView(view);
        return view;
    }


    private void initView(View view, MEntry... maps) {
        if (view == null || maps == null)
            return;
        ImageButton imageView;
        for (MEntry entry : maps) {
            imageView = (ImageButton) view.findViewById(entry.id);
            imageView.setClickable(true);
            imageView.setOnClickListener(entry);
            ViewUtils.setImageUrl(imageView, entry.url);
        }
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

    class MEntry implements View.OnClickListener {
        int id;
        String url;
        Product product;

        public MEntry(int id, Product product) {
            this.id = id;
            this.product = product;
            url = product.getIconImgUrl();
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onClick(product);
        }
    }

    public interface OnClick {
        void onClick(Product product);
    }
}
