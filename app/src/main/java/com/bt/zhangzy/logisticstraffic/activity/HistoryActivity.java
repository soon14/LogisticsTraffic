package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/15.
 */
public class HistoryActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        setPageName("历史浏览");
        listView = (ListView) findViewById(R.id.history_list);
        ArrayList<Product> historyList = User.getInstance().getHistoryList();
        if (historyList != null && !historyList.isEmpty()) {
            final HomeListAdapter adapter = new HomeListAdapter(historyList);
            listView.setAdapter(adapter);
            adapter.setOnClickItemListener(new HomeListAdapter.OnClickItemListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.d(TAG, "onItemClick>>>>>");
                    if (v != null)
                        if (v.getId() == R.id.list_item_ly) {
//                            Log.d(TAG, "    >>>>>点击了item" + position);
                            gotoDetail(adapter.getItem(position));
                        } else if (v.getId() == R.id.list_item_phone) {
//                            Log.d(TAG, "    >>>>>点击了phone" + position);
                            Product product = adapter.getItem(position);
                            showDialogCallPhone(product.getPhoneNumber(),product.getID());
                        }
                }
            });
        }
    }
}
