package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.adapter.LinesListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.entity.JsonLine;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2016-5-23.
 */
public class LinesListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    LinesListAdapter adapter;
    //    boolean isEditMode;//编辑模式
    int selectEditIndex = -1;
    boolean isFromOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_list);


        if (getIntent().getExtras() != null) {
            isFromOrder = getIntent().getBooleanExtra(AppParams.LINES_BUNDLE_FORM_ORDER, false);
        }
        setPageName(isFromOrder ? "选择地址" : "地址管理");
        listView = (ListView) findViewById(R.id.line_list_view);
        listView.setOnItemClickListener(this);
        initListView();
    }

    private void initListView() {
        ArrayList<JsonLine> linesList = User.getInstance().getLinesList();
        /*if (linesList == null || linesList.isEmpty()) {
            showToast("常发线路为空");
            listView.setAdapter(new LinesListAdapter(new ArrayList<JsonLine>()));
        } else*/
        {
            adapter = new LinesListAdapter(linesList);
            listView.setAdapter(adapter);
            adapter.setListener(new LinesListAdapter.OnClickItemEdit() {
                @Override
                public void onClick(int position, JsonLine jsonLine) {
                    //编辑按钮
                    if (jsonLine != null) {
                        selectEditIndex = position;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(AppParams.LINES_BUNDLE_FORM_ORDER, isFromOrder);
                        bundle.putCharSequence(AppParams.LINES_BUNDLE_JSON_LINE, jsonLine.toString());
                        startActivityForResult(LinesDetailActivity.class, bundle, AppParams.LINES_REQUEST_CODE);
                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppParams.LINES_REQUEST_CODE) {
            JsonLine jsonLine = null;
            if (data != null) {
                String json = (String) data.getCharSequenceExtra(AppParams.LINES_BUNDLE_JSON_LINE);
                Log.d(TAG, "JsonLine:" + json);
                jsonLine = JsonLine.ParseEntity(json, JsonLine.class);
            }
            if (resultCode == AppParams.LINES_RESULT_CODE_NEW) {
                //新建线路返回
                if (isFromOrder) {
                    setResult(AppParams.LINES_REQUEST_CODE, data);
                    finish();
                    return;
                }
            } else if (resultCode == AppParams.LINES_RESULT_CODE_EDIT) {
                // 编辑线路返回
                if (jsonLine != null && selectEditIndex > -1) {
                    User.getInstance().getLinesList().set(selectEditIndex, jsonLine);
                    if (isFromOrder) {
                        setResult(AppParams.LINES_REQUEST_CODE, data);
                        finish();
                        return;
                    }
                }

            } else if (resultCode == AppParams.LINES_RESULT_CODE_REMOVE) {
                //删除线路返回
                if (jsonLine != null && selectEditIndex > -1) {
                    User.getInstance().getLinesList().remove(selectEditIndex);
                }
            }
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    public void onClick_AddLine(View view) {
        //添加线路
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppParams.LINES_BUNDLE_FORM_ORDER, isFromOrder);
        startActivityForResult(LinesDetailActivity.class, bundle, AppParams.LINES_REQUEST_CODE);
    }

    public void onClick_Submit(View view) {
//        isEditMode = !isEditMode;
//        setTextView(R.id.line_list_submit, isEditMode ? "完成" : "编辑");
//        if (isEditMode) {
//            setPageName("编辑地址");
//            listView.removeFooterView(footerView);
//        } else {
//            setPageName("选择地址");
//            listView.addFooterView(footerView);
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        selectEditIndex = position;
        if (isFromOrder) {
            if (parent.getAdapter() != null) {
                JsonLine jsonLine = (JsonLine) parent.getAdapter().getItem(position);
                Intent data = new Intent();
                data.putExtra(AppParams.LINES_BUNDLE_JSON_LINE, jsonLine.toString());
                setResult(AppParams.LINES_RESULT_CODE_NEW, data);
                finish();
            }
        }
    }
}
