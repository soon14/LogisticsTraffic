package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.adapter.CollectListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.ResponseFavorites;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ZhangZy on 2015/7/7.
 */
public class CollectActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = CollectActivity.class.getSimpleName();

    private ListView listView;
    CollectListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collect);
        setPageName("收藏夹");
        init();
        //// TO DO: 2016-1-28  收藏夹列表
        requestFavouriteList();
    }

    private void requestFavouriteList() {

        HashMap<String, String> params = new HashMap<>();
        params.put("roleId", String.valueOf(User.getInstance().getRoleId()));
        params.put("role", String.valueOf(User.getInstance().getUserType().toRole()));
        HttpHelper.getInstance().get(AppURL.GetFavouriteList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                //// TODO: 2016-3-10  收藏对象初始化 还有登录的返回
                ResponseFavorites response = ParseJson_Object(result, ResponseFavorites.class);
                if (response == null)
                    return;

                //更新收藏信息
                if (response.getFavorites() != null && !response.getFavorites().isEmpty()) {
                    User.getInstance().setJsonFavorites(response);
                }
                if (response.getCompanies() != null && !response.getCompanies().isEmpty()) {
                    ArrayList<Product> list = new ArrayList<Product>();
                    Product p;
                    for (JsonCompany company : response.getCompanies()) {
                        p = Product.ParseJson(company);
                        if (p != null) {
                            list.add(p);
                        }
                    }

                    adapter.setData(list);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }

            @Override
            public void onFailed(String str) {
                showToast("收藏列表获取失败");
            }
        });
    }


    public void init() {
        if (listView == null)
            listView = (ListView) findViewById(R.id.collect_list);

        listView.setOnItemClickListener(this);

        adapter = new CollectListAdapter(true, null);
        listView.setAdapter(adapter);
        adapter.setData(User.getInstance().getCollectionList());
    }


//    public void onClick_ChangeType(View view) {
//        if (view != null) {
//            setPageState(view.getId() == R.id.collect_me_type);
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gotoDetail(adapter.getItem(position));
    }
}
