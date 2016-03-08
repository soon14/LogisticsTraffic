package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeListAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.ResponseCompany;
import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.tools.ViewUtils;
import com.zhangzy.baidusdk.BaiduSDK;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private HomeListAdapter adapter;
    private AutoCompleteTextView searchKeyWord;
    private ArrayAdapter<String> adapterHistory;//搜索记录
//    private String startCity, stopCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        setPageName("搜索店铺");
//        findViewById(R.id.search_keyword_btn).setSelected(true);
//        findViewById(R.id.search_type_line_ly).setVisibility(View.VISIBLE);
        setSearchType(true);
        if (listView == null)
            listView = (ListView) findViewById(R.id.search_listView);
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


        if (isKeyWord) {
            //关键词搜索模式
            lineBtn.setSelected(false);
            keywordBtn.setSelected(true);
            lineTypeLy.setVisibility(View.INVISIBLE);
            keywordTypeLy.setVisibility(View.VISIBLE);

            if (searchKeyWord == null) {
                searchKeyWord = (AutoCompleteTextView) keywordTypeLy.findViewById(R.id.search_keyword_ed);
                //android.R.layout.simple_dropdown_item_1line
                adapterHistory = new ArrayAdapter<String>(this, R.layout.autocomplete_adapter_item, User.getInstance().getSearchKeyWordList());
                searchKeyWord.setAdapter(adapterHistory);
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
                    TextView ed = (TextView) lineTypeLy.findViewById(R.id.search_start_ed);
                    ed.setText(cityName);
                }
            }
        }
    }

    //搜索接口
    private void requestSearch(HashMap<String, String> params) {

        HttpHelper.getInstance().get(AppURL.GetSearch, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<ResponseCompany> list = ParseJson_Array(result, ResponseCompany.class);
                if (list == null || list.isEmpty()) {
                    showToast("搜索内容为空");
                    return;
                }
                showToast("搜索成功");
                ArrayList<Product> p_list = new ArrayList<Product>();
                Product p;
                for (ResponseCompany json : list) {
                    p = new Product(json.getId());
                    p.setUserId(json.getUserId());
                    p.setName(json.getName());
                    p.setPhotoUrl(json.getPhotoUrl());
                    //设置 认证用户或者 付费用户
                    p.setIsVip(json.getStatus() != -1);
//                    product.setLevel(company.getStar());
                    p.setTimes(json.getViewCount());
                    p.setCallTimes(json.getCallCount());
                    p.setPhotoUrl(json.getPhotoUrl());
                    p.setCompany(json);
                    p_list.add(p);
                }
                adapter = new HomeListAdapter(p_list);
                refreshListView();
            }

            @Override
            public void onFailed(String str) {
                showToast("搜索失败");
            }
        });
    }

    private void refreshListView() {
        if (adapter == null)
            return;

        adapter.setOnClickItemListener(new HomeListAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "onItemClick>>>>>");
                if (v != null)
//                    Product product = adapter.getItem(position);
                    if (v.getId() == R.id.list_item_ly) {
                        Log.d(TAG, "    >>>>>点击了item" + position);
                        gotoDetail(adapter.getItem(position));
                    } else if (v.getId() == R.id.list_item_phone) {
                        Log.d(TAG, "    >>>>>点击了phone" + position);
                        showDialogCallPhone("12301253326");
                    }
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }


    //点击了搜索按钮 // TODO: 2016-1-28 搜索接口 线路搜索
    public void onClick_Search(View view) {
        if (shopListLy != null)
            shopListLy.setVisibility(View.GONE);
        ContextTools.HideKeyboard(searchKeyWord);
        if (isKeyWordType) {
            searchKeyWord.clearFocus();
            String keyWord = searchKeyWord.getText().toString();
            User.getInstance().addSearchKeyWord(keyWord);
            adapterHistory.add(keyWord);

            /*按照公司属性名和字段值查询公司列表，字段包括：id，userId，name，address，area，oftenSendType，scaleOfOperation*/
            HashMap<String, String> params = new HashMap<>();
            params.put("fieldName", "name");
            params.put("value", keyWord);
            requestSearch(params);
        } else {

            String start_city = getStringFromTextView(R.id.search_start_ed);
            String stop_city = getStringFromTextView(R.id.search_end_ed);
            if (TextUtils.isEmpty(start_city) || TextUtils.isEmpty(stop_city)) {
                showToast("城市名称为空");
                return;
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("fieldName", "oftenRoute");
            params.put("value", start_city + " AND " + stop_city);
            requestSearch(params);
        }
//
//
//        HomeListAdapter adapter = new HomeListAdapter(new ArrayList<Product>(10));
//        listView.setAdapter(adapter);
//        listView.requestFocus();

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

    /**
     * 地址选择
     *
     * @param view
     */
    public void onClick_ChangeLocation(View view) {
        if (view == null || !(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;
        String string = ViewUtils.getStringFromTextView(textView);
        Location location;
        if (TextUtils.isEmpty(string)) {
            location = User.getInstance().getLocation();
        } else {
            location = new Location(null, string);
        }
        LocationView.createDialog(this)
                .setCurrentLocation(location)
                .setListener(new LocationView.ChangingListener() {
                                 @Override
                                 public void onChanged(Location loc) {
                                 }

                                 public void onCancel(Location loc){
                                     if (TextUtils.isEmpty(loc.getCityName()))
                                         return;

                                     String params = /*province + "·" +*/ loc.getCityName();
                                     textView.setText(params);
                                 }
                             }

                ).show();

    }

}
