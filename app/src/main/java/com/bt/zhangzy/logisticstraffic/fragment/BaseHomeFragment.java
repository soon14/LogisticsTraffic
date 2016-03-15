package com.bt.zhangzy.logisticstraffic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.activity.HomeActivity;

/**
 * Created by ZhangZy on 2015/7/2.
 */
public abstract class BaseHomeFragment extends Fragment {

    private String pageName;
    private View contentView;

    public BaseHomeFragment(String pageName) {
        this.pageName = pageName;
    }

    abstract int getLayoutID();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = getActivity().getLayoutInflater().inflate(getLayoutID(), null, false);
        if (pageName != null) {
            TextView pageTopName = (TextView) contentView.findViewById(R.id.page_top_name);
            if (pageTopName != null)
                pageTopName.setText(pageName);
        }
        if (contentView.findViewById(R.id.page_top_ly) != null) {
            contentView.findViewById(R.id.page_top_ly).setBackgroundColor(getResources().getColor(R.color.main_bg_color));
        }
        init(contentView);
    }

    /* 使用FragmentPagerAdapter 时，onCreateView 会被在切换时调用，不适合在这个方法中创建view ,只适用于更新view数据*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView != null)
            return contentView;
        else
            return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        init();
    }

    void init(View view) {

    }


    View findViewById(int id) {
        return getView().findViewById(id);
    }

    HomeActivity getHomeActivity() {
        return (HomeActivity) getActivity();
    }


    public boolean onTouchEventFragment(MotionEvent event) {

        return false;
    }
}
