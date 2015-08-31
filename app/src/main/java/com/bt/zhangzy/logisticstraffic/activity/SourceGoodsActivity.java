package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.SourceGoodsListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;

/**
 * Created by ZhangZy on 2015/8/26.
 */
public class SourceGoodsActivity extends BaseActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_source_goods);
        setPageName("货源信息");
        listView = (ListView) findViewById(R.id.source_list);
        listView.setAdapter(new SourceGoodsListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoDetail();
            }
        });
    }

    private void gotoDetail() {
        if (User.getInstance().getLogin()) {
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.ORDER_DETAIL_KEY_TYPE, ordinal);
            startActivity(OrderDetailActivity.class);
        } else {
            BaseDialog.showConfirmDialog(this, "您还没有登录，是否登录？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(LoginActivity.class);
                }
            });
        }
    }
}
