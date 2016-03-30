package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.CollectListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.TenderListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonTender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZhangZy on 2016-3-14.
 */
public class TenderListActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    ListView listView;
    TenderListAdapter adapter;
    CollectListAdapter homeListAdapter;
    boolean isTenderList;//标记是否显示标书列表
    String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tender_list);
        listView = (ListView) findViewById(R.id.tender_list);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(AppParams.BUNDLE_TENDER_COMPANY_JSON)) {
                JsonEnterprise json = BaseEntity.ParseEntity(bundle.getString(AppParams.BUNDLE_TENDER_COMPANY_JSON), JsonEnterprise.class);
                if (json != null) {
                    companyId = String.valueOf(json.getId());
                }
            }
        }
        if (companyId == null) {
            setPageName("企业标书");
            requestListEnterprise();
            findViewById(R.id.tender_add_bt).setVisibility(View.GONE);
        } else {
            setPageName("我的标书");
            requestList(companyId);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (companyId == null) {
            setPageName("企业标书");
            requestListEnterprise();
        } else {
            setPageName("我的标书");
            requestList(companyId);
        }
    }

    public void onClick_Add(View view) {
        Bundle bundle = new Bundle();
        JsonTender jsonTender = new JsonTender();
        jsonTender.setEnterpriseId(Integer.valueOf(companyId));
        bundle.putString(AppParams.BUNDLE_TENDER_JSON, jsonTender.toString());
        bundle.putBoolean(AppParams.BUNDLE_TENDER_EDIT_BOOLEAN, true);
        startActivity(TenderActivity.class, bundle);
    }

    @Override
    public void onClick_Back(View view) {
        if (companyId == null && isTenderList) {
            requestListEnterprise();
        } else {
            super.onClick_Back(view);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (companyId == null && isTenderList) {
                requestListEnterprise();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //长按删除
        if (parent.getAdapter() == adapter) {
            JsonTender jsonTender = adapter.getItem(position);
            if (jsonTender != null)
                requestDelete(jsonTender.getId());
            return true;
        } else
            return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Item点击
        if (parent.getAdapter() == adapter) {
            JsonTender jsonTender = adapter.getItem(position);
            if (jsonTender != null) {
                Bundle bundle = new Bundle();
                bundle.putString(AppParams.BUNDLE_TENDER_JSON, jsonTender.toString());
                startActivity(TenderActivity.class, bundle);
            }
        } else if (parent.getAdapter() == homeListAdapter) {
            ////
            Product product = homeListAdapter.getItem(position);
            if (product != null) {
                int value = product.getID();
                requestList(String.valueOf(value));
            }
        }
    }

    private void requestListEnterprise() {
        isTenderList = false;
        showProgress("加载中···");
        HttpHelper.getInstance().get(AppURL.GetTenderEnterprise, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonCompany> list = ParseJson_Array(result, JsonCompany.class);
                if (list == null || list.isEmpty()) {
                    showToast("列表为空");
                    cancelProgress();
                    return;
                }
                ArrayList<Product> arrayList = new ArrayList<Product>();
                for (JsonCompany json : list) {
                    Product p = Product.ParseJson(json);
                    if (p != null)
                        arrayList.add(p);
                }

                homeListAdapter = new CollectListAdapter(true, arrayList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgress();
                        listView.setAdapter(homeListAdapter);
                    }
                });

            }

            @Override
            public void onFailed(String str) {
                cancelProgress();
                showToast("列表请求失败" + str);
            }
        });
    }


    private void requestList(String value) {
        isTenderList = true;
        showProgress("列表加载中···");
        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseId", value);
        HttpHelper.getInstance().get(AppURL.GetTendersList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonTender> list = ParseJson_Array(result, JsonTender.class);
                if (list == null || list.isEmpty()) {
                    cancelProgress();
                    showToast("数据返回为空");
                    return;
                }
                adapter = new TenderListAdapter(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgress();
                        listView.setAdapter(adapter);
                    }
                });

            }

            @Override
            public void onFailed(String str) {
                cancelProgress();
                showToast("列表请求失败" + str);
            }
        });
    }

    private void requestDelete(int id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(id));
        HttpHelper.getInstance().get(AppURL.GetTendersDelete, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {
                showToast("删除失败:" + str);
            }
        });
    }
}
