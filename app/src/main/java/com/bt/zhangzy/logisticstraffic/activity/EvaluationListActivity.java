package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.EvaluationListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.JsonComment;

import java.util.List;

/**
 * Created by ZhangZy on 2015/7/7.
 */
public class EvaluationListActivity extends BaseActivity {

    ListView list;
    EvaluationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_list);

        setPageName("评价列表");
        list = (ListView) findViewById(R.id.evaluation_list);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(AppParams.BUNDLE_EVALUATION_JSON_LIST)) {
                String json = bundle.getString(AppParams.BUNDLE_EVALUATION_JSON_LIST);
                List<JsonComment> commentList = BaseEntity.ParseArray(json, JsonComment.class);
                if (commentList != null && !commentList.isEmpty()) {
                    adapter = new EvaluationListAdapter(commentList);
                    this.list.setAdapter(adapter);
                }
            }
        }


//        list.setAdapter(new EvaluationListAdapter());

        //TO DO 接口 评价列表
    }
}
