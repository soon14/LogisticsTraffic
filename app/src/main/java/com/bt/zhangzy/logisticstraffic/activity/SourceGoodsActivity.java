package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.SourceGoodsListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;

import java.util.HashMap;

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
        initListView();
        lastSelectBtn = findViewById(R.id.source_goods_type1);
        lastSelectBtn.setSelected(true);
        requestOrderList();
    }

    private void requestOrderList(){
        //// TODO: 2016-1-30  可抢订单列表获取失败
        //http://182.92.77.31:8080/orders/list?orderType=1&status=3
        HashMap<String ,String> params = new HashMap<>();
        params.put("orderType","0");
        params.put("status","3");
        HttpHelper.getInstance().get(AppURL.GetOrderList,params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {

            }
        });

    }
    private void initListView() {
        listView = (ListView) findViewById(R.id.source_list);
        listView.setAdapter(new SourceGoodsListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoDetail();
            }
        });
    }


    private View lastSelectBtn;//记录上一次选中的按钮

    public void onClick_ChangeSourceType(View view) {
        if (view == null)
            return;
        if (lastSelectBtn != null && lastSelectBtn.getId() == view.getId())
            return;
        view.setSelected(true);
        if (lastSelectBtn != null) {
            lastSelectBtn.setSelected(false);
        }
        lastSelectBtn = view;
        listView.setAdapter(new SourceGoodsListAdapter());
    }

    private void gotoDetail() {
        if (User.getInstance().getLogin()) {
            if(AppParams.DEVICES_APP && !User.getInstance().isVIP()){
                ConfirmDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(PayActivity.class);
                    }
                });
                return;
            }
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.ORDER_DETAIL_KEY_TYPE, ordinal);
            startActivity(OrderDetailActivity.class);
        } else {
            ConfirmDialog.showConfirmDialog(this, "您还没有登录，是否登录？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(LoginActivity.class);
                }
            });
        }
    }
}
