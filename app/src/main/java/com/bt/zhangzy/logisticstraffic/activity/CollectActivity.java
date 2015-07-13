package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.CollectListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.HomeListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.view.FloatView;

/**
 * Created by ZhangZy on 2015/7/7.
 */
public class CollectActivity extends BaseActivity {

    private static final String TAG = CollectActivity.class.getSimpleName();
    private final boolean MY_COLLECT = true;
    private final boolean OTHER_COLLECT = false;

    private boolean pageState;
    private View myCollectType;
    private View otherCollectType;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collect);
        setPageName("收藏夹");
        setPageState(MY_COLLECT);
    }



    public void setPageState(boolean pageState) {
        this.pageState = pageState;
        if (myCollectType == null)
            myCollectType = findViewById(R.id.collect_me_type);
        if (otherCollectType == null)
            otherCollectType = findViewById(R.id.collect_other_type);
        if (listView == null)
            listView = (ListView) findViewById(R.id.collect_list);
        listView.setAdapter(null);

        if (pageState == MY_COLLECT) {
            myCollectType.setSelected(true);
            otherCollectType.setSelected(false);
            listView.setAdapter(new CollectListAdapter(true));
        } else if (pageState == OTHER_COLLECT) {
            myCollectType.setSelected(false);
            otherCollectType.setSelected(true);
            listView.setAdapter(new CollectListAdapter(false));
        }
    }


    public void onClick_ChangeType(View view) {
        if (view != null) {
            setPageState(view.getId() == R.id.collect_me_type);
        }
    }
}
