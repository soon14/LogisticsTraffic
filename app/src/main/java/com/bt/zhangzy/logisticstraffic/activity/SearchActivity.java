package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2015/6/16.
 */
public class SearchActivity extends BaseActivity {


    boolean isKeyWordType; // true = keyword ; false = line
    private View lineBtn;
    private View keywordBtn;
    private View lineTypeLy;
    private View keywordTypeLy;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        setPageName("搜索店铺");
//        findViewById(R.id.search_keyword_btn).setSelected(true);
//        findViewById(R.id.search_type_line_ly).setVisibility(View.VISIBLE);
        setSearchType(false);
    }

    public void onClick_ChangeType(View view) {
        if (view != null) {
//            view.setSelected(true);
            if (view.getId() == R.id.search_keyword_btn) {
                setSearchType(true);
            } else if (view.getId() == R.id.search_line_btn) {
                setSearchType(false);
            }
        }
    }

    public void setSearchType(boolean isKeyWord) {
        this.isKeyWordType = isKeyWord;
        if (lineBtn == null)
            lineBtn = findViewById(R.id.search_line_btn);
        if (keywordBtn == null)
            keywordBtn = findViewById(R.id.search_keyword_btn);
        if (lineTypeLy == null)
            lineTypeLy = findViewById(R.id.search_type_line_ly);
        if (keywordTypeLy == null)
            keywordTypeLy = findViewById(R.id.search_type_keyword_ly);

        if (isKeyWord) {
            //关键词搜索模式
            lineBtn.setSelected(false);
            keywordBtn.setSelected(true);
            lineTypeLy.setVisibility(View.INVISIBLE);
            keywordTypeLy.setVisibility(View.VISIBLE);
        } else {
            //线路模式
            lineBtn.setSelected(true);
            keywordBtn.setSelected(false);
            lineTypeLy.setVisibility(View.VISIBLE);
            keywordTypeLy.setVisibility(View.INVISIBLE);

            String cityName = User.getInstance().getLocation().getCityName();
            if (!TextUtils.isEmpty(cityName)) {
                EditText ed = (EditText) lineTypeLy.findViewById(R.id.search_start_ed);
                ed.setText(cityName);
            }
        }
    }

    //点击了搜索按钮
    public void onClick_Search(View view) {
        if (isKeyWordType) {

        } else {

        }

        if (listView == null)
            listView = (ListView) findViewById(R.id.search_listView);
        HomeListAdapter adapter = new HomeListAdapter();
        listView.setAdapter(adapter);

    }

    public void onClick_Shop(View view) {
        startActivity(DetailCompany.class);
    }

    public void onClick_SWOP(View view) {
        //交换始发地按钮
        if (lineTypeLy != null) {
            EditText startEd = (EditText) lineTypeLy.findViewById(R.id.search_start_ed);
            EditText endEd = (EditText) lineTypeLy.findViewById(R.id.search_end_ed);
            Editable editable = startEd.getText();
            startEd.setText(endEd.getText());
            endEd.setText(editable);
        }
    }
}
