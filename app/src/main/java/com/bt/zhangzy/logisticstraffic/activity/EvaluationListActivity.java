package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.EvaluationListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * Created by ZhangZy on 2015/7/7.
 */
public class EvaluationListActivity extends BaseActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_list);

        setPageName("评价列表");
        list = (ListView) findViewById(R.id.evaluation_list);

        list.setAdapter(new EvaluationListAdapter());

        //TODO 接口 评价列表
    }
}
