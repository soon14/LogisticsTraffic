package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeListAdapter;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.zhangzy.baidusdk.BaiduSDK;

import java.util.ArrayList;

/**
 * TODO 1、搜索记录弹窗；2、点击搜索按钮后隐藏推荐店铺
 * Created by ZhangZy on 2015/6/16.
 */
public class SearchActivity extends BaseActivity {


    boolean isKeyWordType; // true = keyword ; false = line
    private View lineBtn;
    private View keywordBtn;
    private View lineTypeLy;
    private View keywordTypeLy;
    private View shopListLy;
    private ListView listView;
    private AutoCompleteTextView searchKeyWord;

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
        if (shopListLy == null)
            shopListLy = findViewById(R.id.search_shop_ly);
        shopListLy.setVisibility(View.VISIBLE);
        if (searchKeyWord == null)
            searchKeyWord = (AutoCompleteTextView) keywordTypeLy.findViewById(R.id.search_keyword_ed);

        if (isKeyWord) {
            //关键词搜索模式
            lineBtn.setSelected(false);
            keywordBtn.setSelected(true);
            lineTypeLy.setVisibility(View.INVISIBLE);
            keywordTypeLy.setVisibility(View.VISIBLE);

           /* if (searchKeyWord.getAdapter() == null)*/
            {
                //android.R.layout.simple_dropdown_item_1line
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.autocomplete_adapter_item, User.getInstance().getSearchKeyWordList());
                searchKeyWord.setAdapter(adapter);
                searchKeyWord.setDropDownBackgroundResource(R.color.white);
                searchKeyWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        AutoCompleteTextView view = (AutoCompleteTextView) v;
                        if (hasFocus) {
                            view.showDropDown();
                        }
                    }
                });
                searchKeyWord.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        AutoCompleteTextView view = (AutoCompleteTextView) v;
                        view.showDropDown();
                        return false;
                    }
                });
            }

            //语音输入回调
            BaiduSDK.getInstance().setVoiceListener(new BaiduSDK.VoiceListener() {
                @Override
                public void callbackVoice(String string) {
                    if (searchKeyWord != null) {
                        searchKeyWord.setText(string);
                        onClick_Search(searchKeyWord);
                    }
                }
            });

        } else {
            //线路模式
            lineBtn.setSelected(true);
            keywordBtn.setSelected(false);
            lineTypeLy.setVisibility(View.VISIBLE);
            keywordTypeLy.setVisibility(View.INVISIBLE);

            Location location = User.getInstance().getLocation();
            if (location != null) {
                String cityName = location.getCityName();
                if (!TextUtils.isEmpty(cityName)) {
                    EditText ed = (EditText) lineTypeLy.findViewById(R.id.search_start_ed);
                    ed.setText(cityName);
                }
            }
        }
    }

    //搜索接口
    private void requestSearch(String searchStr) {
        //// TODO: 2016-1-28  关键词搜索
        HttpHelper.getInstance().get(HttpHelper.toString(AppURL.GetSearch, new String[]{"name=" + searchStr}), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("搜索成功");

            }

            @Override
            public void onFailed(String str) {
            showToast("搜索失败");
            }
        });
    }

    //点击了搜索按钮 // TODO: 2016-1-28 搜索接口
    public void onClick_Search(View view) {
        if (shopListLy != null)
            shopListLy.setVisibility(View.GONE);
        ContextTools.HideKeyboard(searchKeyWord);
        if (isKeyWordType) {
            searchKeyWord.clearFocus();
            String keyWord = searchKeyWord.getText().toString();
            User.getInstance().addSearchKeyWord(keyWord);
            requestSearch(keyWord);
        } else {

        }

        if (listView == null)
            listView = (ListView) findViewById(R.id.search_listView);
        HomeListAdapter adapter = new HomeListAdapter(new ArrayList<Product>(10));
        listView.setAdapter(adapter);
        listView.requestFocus();

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

    /**
     * 语音搜索接入
     *
     * @param view
     */
    public void onClick_SearchVoice(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaiduSDK.getInstance().showVoiceDialog(context);

            }
        });
    }
}
