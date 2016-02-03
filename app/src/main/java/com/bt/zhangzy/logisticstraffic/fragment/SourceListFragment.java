package com.bt.zhangzy.logisticstraffic.fragment;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.activity.SourceDetailActivity;
import com.bt.zhangzy.logisticstraffic.adapter.SourceListAdapter;

/**
 * 车源信息
 * Created by ZhangZy on 2015/7/24.
 */
public class SourceListFragment extends BaseHomeFragment {

    private PopupWindow popupWindow;

    enum SearchType {
        Empty, All, Precision
    }

    SearchType currentSearchType = SearchType.Empty;


    @Override
    int getLayoutID() {
        return R.layout.activity_source;
    }

    public SourceListFragment() {
        super("空车信息");
    }

    ListView listView;

    @Override
    void init(View view) {
//        super.init();
        listView = (ListView) view.findViewById(R.id.source_list);
        listView.setAdapter(new SourceListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(SourceDetailActivity.class);
            }
        });

        currentSearchType = SearchType.Precision;
//        onClick_SourceListFM(findViewById(R.id.source_search_all_btn));
    }

//    View lastSearchTypeView;

    /**
     * 统一处理此fragment类
     *
     * @param view
     */
    public void onClick_SourceListFM(View view) {
        if (view == null)
            return;
//        if (view.getId() == R.id.source_search_all_btn) {
//            currentSearchType = SearchType.All;
//        } else if (view.getId() == R.id.source_search_precision_btn) {
//            currentSearchType = SearchType.Precision;
//        } else
//            return;
//        if (lastSearchTypeView != null) {
//            if (lastSearchTypeView == view)
//                return;
//            lastSearchTypeView.setSelected(false);
//        }
//        lastSearchTypeView = view;
//        view.setSelected(true);
//        findViewById(R.id.source_precision_ly).setVisibility(currentSearchType == SearchType.Precision ? View.VISIBLE : View.GONE);
        switch (view.getId()) {
            case R.id.source_truck_type_bt:
                createPopupWindow(view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null)
                            popupWindow.dismiss();
                        Button btn = (Button) findViewById(R.id.source_truck_type_bt);
                        Button button = (Button) v;
                        if (button.getText().equals("不限")) {
                            btn.setText("");
                        } else {
                            btn.setText(button.getText());
                        }
                    }
                }, "不限", "平板车", "高低板车", "厢式车", "低栏车", "中拦车", "高栏车", "保温车", "冷藏车");
                break;
            case R.id.source_truck_length_bt:
                createPopupWindow(view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null)
                            popupWindow.dismiss();
                        Button btn = (Button) findViewById(R.id.source_truck_length_bt);
                        Button button = (Button) v;
                        if (button.getText().equals("不限")) {
                            btn.setText("");
                        } else {
                            btn.setText(button.getText());
                        }
                    }
                }, "不限", "4.2米", "5.2米", "5.8米", "6.8米", "7.2米", "8米");

                break;
            case R.id.source_truck_weight_bt:
                createPopupWindow(view, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null)
                            popupWindow.dismiss();
                        Button btn = (Button) findViewById(R.id.source_truck_weight_bt);
                        Button button = (Button) v;
                        if (button.getText().equals("不限")) {
                            btn.setText("");
                        } else {
                            btn.setText(button.getText());
                        }
                    }
                }, "不限", "1吨", "2吨", "3吨", "4吨", "5吨", "6吨", "8吨");
                break;
            case R.id.source_location_bt:
                showLocationPopup(view);
                break;
        }
//        createPopupWindow(view, listener);
    }


    /**
     * 创建浮窗
     *
     * @param view
     * @param listener
     */
    private void createPopupWindow(View view, View.OnClickListener listener, String... params) {
        if (params == null)
            return;
        View tmp_view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_choose_items, null);
        popupWindow = new PopupWindow(tmp_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        final int[] ids = {R.id.popup_choose_item_1, R.id.popup_choose_item_2, R.id.popup_choose_item_3, R.id.popup_choose_item_4, R.id.popup_choose_item_5, R.id.popup_choose_item_6,
                R.id.popup_choose_item_7, R.id.popup_choose_item_8, R.id.popup_choose_item_9, R.id.popup_choose_item_10
                , R.id.popup_choose_item_11, R.id.popup_choose_item_12, R.id.popup_choose_item_13, R.id.popup_choose_item_14, R.id.popup_choose_item_15, R.id.popup_choose_item_16};

        Button viewById = null;
        for (int k = 0; k < ids.length; k++) {
            viewById = (Button) tmp_view.findViewById(ids[k]);
            if (k < params.length) {
                viewById.setVisibility(View.VISIBLE);
                viewById.setText(params[k]);
                viewById.setOnClickListener(listener);
            } else {
                viewById.setVisibility(View.GONE);
            }
        }

        popupWindow.showAsDropDown(view);

    }

    private void showLocationPopup(View view) {
        if (view == null)
            return;
        View tmp_view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_location, null);
        popupWindow = new PopupWindow(tmp_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null)
                    popupWindow.dismiss();
                Button btn = (Button) findViewById(R.id.source_location_bt);
                Button tx = (Button) v;
                if (tx.getText().equals("不限")) {
                    btn.setText("");
                } else {
                    btn.setText(tx.getText());
                }
            }
        };
        tmp_view.findViewById(R.id.location_province_3).setSelected(true);
        tmp_view.findViewById(R.id.location_city_1).setOnClickListener(listener);
        tmp_view.findViewById(R.id.location_city_2).setOnClickListener(listener);
        tmp_view.findViewById(R.id.location_city_3).setOnClickListener(listener);


        popupWindow.showAsDropDown(view);
    }
}
