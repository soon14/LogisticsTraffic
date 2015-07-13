package com.bt.zhangzy.logisticstraffic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.HomeActivity;

/**
 * Created by ZhangZy on 2015/7/2.
 */
public abstract class BaseHomeFragment extends Fragment {

    private String pageName;

    public BaseHomeFragment(String pageName) {
        this.pageName = pageName;
    }

    abstract int getLayoutID();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getLayoutID(), container, false);
        if (pageName != null) {
            TextView pageTopName = (TextView) view.findViewById(R.id.page_top_name);
            pageTopName.setText(pageName);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    void init() {

    }


    View findViewById(int id) {
        return getView().findViewById(id);
    }

    HomeActivity getHomeActivity() {
        return (HomeActivity) getActivity();
    }


    void startActivity(Class<?> cls) {
        getHomeActivity().startActivity(cls);
    }


    public boolean onTouchEventFragment(MotionEvent event){

        return false;
    }
}
