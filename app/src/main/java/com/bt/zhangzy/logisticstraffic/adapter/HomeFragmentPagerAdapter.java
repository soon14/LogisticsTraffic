package com.bt.zhangzy.logisticstraffic.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/7/3.
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;
    private FragmentManager fm;

    public HomeFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    /**
     * 刷新全部fragament
     *
     * @param fragments
     */
    public void setFragments(ArrayList fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    /**
     * 刷新某一个位置的Fragment
     *
     * @param position
     * @param fragment
     */
    public void updateFragment(int position, Fragment fragment) {
        if (fragments != null) {
            if (position < 0 || position >= fragments.size())
                return;

            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(position,null);
            ft.remove(fragments.get(position));
            ft.commit();
            fm.executePendingTransactions();
            fragments.set(position, fragment);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
//        return super.getItemPosition(object);
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        updateFragment(position, fragment);
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }
}
