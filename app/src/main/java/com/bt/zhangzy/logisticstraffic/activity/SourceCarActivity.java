package com.bt.zhangzy.logisticstraffic.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.data.UserStatus;
import com.bt.zhangzy.logisticstraffic.fragment.SourceCarListFragment;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 车源信息页
 * to do 将车源信息改为弹出形式  服务页面也得改
 * Created by ZhangZy on 2015/7/23.
 */
public class SourceCarActivity extends BaseActivity {

    int motorcadeId = -1;
    private PopupWindow popupWindow;
    Button typeBt, lengthBt, weightBt, locationBt;
    SourceCarListFragment publicFragment;
    SourceCarListFragment motorcadeFragment;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);
        setPageName("车源信息");

        typeBt = (Button) findViewById(R.id.source_car_type_bt);
        lengthBt = (Button) findViewById(R.id.source_car_length_bt);
        weightBt = (Button) findViewById(R.id.source_car_weight_bt);
        locationBt = (Button) findViewById(R.id.source_car_location_bt);


        try {
            Log.w(TAG, "Motorcades = " + User.getInstance().getMotorcades());
            List<JsonMotorcades> motorcades = User.getInstance().getMotorcades();
            if (motorcades != null) {
                if (!motorcades.isEmpty()) {
                    motorcadeId = motorcades.get(0).getId();
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "JsonMotorcades 错误", e);
            e.printStackTrace();
        }
        initView();
//        listView = (ListView) findViewById(R.id.source_list);
////        listView.setAdapter(new SourceCarListAdapter());
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                JsonCar car = adapter.getItem(position);
//                Bundle bundle = new Bundle();
//                bundle.putString(AppParams.SOURCE_PAGE_CAR_KEY, car.toString());
////                bundle.putSerializable(AppParams.SOURCE_PAGE_CAR_KEY,car);
//                startActivity(SourceCarDetailActivity.class, bundle);
//            }
//        });

        lastSelectBtn = findViewById(R.id.source_car_public);
        lastSelectBtn.setSelected(true);
//        requestPublicCarList();
        requestSearch(-1);
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.source_list_viewpager);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        publicFragment = new SourceCarListFragment();
        motorcadeFragment = new SourceCarListFragment();
        fragments.add(publicFragment);
        fragments.add(motorcadeFragment);
        SourceCarListFragment.OnItemClickListener listener = new SourceCarListFragment.OnItemClickListener() {

            @Override
            public void OnItemClick(SourceCarListFragment fragment, JsonCar car) {
                // 车源点击
                if (User.getInstance().getUserStatus() == UserStatus.UN_CHECKED) {
                    showToast("用户资料不完善，无法使用此功能");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(AppParams.SOURCE_PAGE_CAR_KEY, car.toString());
                bundle.putBoolean(AppParams.SOURCE_PAGE_FROM_PUBLIC, fragment == publicFragment);
//                bundle.putSerializable(AppParams.SOURCE_PAGE_CAR_KEY,car);
                startActivity(SourceCarDetailActivity.class, bundle);
            }
        };
        publicFragment.setListener(listener);
        motorcadeFragment.setListener(listener);
        FragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        onClick_ChangeSourceType(findViewById(R.id.source_car_public));
                        break;
                    case 1:
                        onClick_ChangeSourceType(findViewById(R.id.source_car_motorcade));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        int currentPage = -1;
        if (view.getId() == R.id.source_car_public) {
            currentPage = 0;
//            requestPublicCarList();
            requestSearch(-1);
        } else {
            currentPage = 1;
            requestMotorcadeList();
            requestSearch(motorcadeId);
        }
        if (viewPager.getCurrentItem() != currentPage) {
            viewPager.setCurrentItem(currentPage);
        }
    }

    private int getMotorcadeId() {
        return viewPager.getCurrentItem() == 0 ? -1 : motorcadeId;
    }

    /**
     * 统一处理此fragment类
     *
     * @param view
     */
    public void onClick_SourceListFM(View view) {
        if (view == null)
            return;
        switch (view.getId()) {
            case R.id.source_car_type_bt:
                createPopupWindow(view, 3, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null)
                            popupWindow.dismiss();
                        Button button = (Button) v;
                        if (button.getText().equals("不限")) {
                            typeBt.setText("");
                        } else {
                            typeBt.setText(button.getText());
                        }
                        requestSearch(getMotorcadeId());
                    }
                }, getResources().getStringArray(R.array.order_change_truck_type_items));
                break;
            case R.id.source_car_length_bt:
                createPopupWindow(view, 4, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null)
                            popupWindow.dismiss();
                        Button button = (Button) v;
                        if (button.getText().equals("不限")) {
                            lengthBt.setText("");
                        } else {
                            lengthBt.setText(button.getText());
                        }
                        requestSearch(getMotorcadeId());
                    }
                }, getResources().getStringArray(R.array.order_change_truck_length_items));

                break;
            case R.id.source_car_weight_bt:
                createPopupWindow(view, 4, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow != null)
                            popupWindow.dismiss();
                        Button button = (Button) v;
                        if (button.getText().equals("不限")) {
                            weightBt.setText("");
                        } else {
                            weightBt.setText(button.getText());
                        }
                        requestSearch(getMotorcadeId());
                    }
                }, getResources().getStringArray(R.array.order_change_truck_weight_items));
                break;
            case R.id.source_car_location_bt:
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
    private void createPopupWindow(View view, int column_count, View.OnClickListener listener, String... params) {
        if (params == null)
            return;
        ArrayList<String> items = new ArrayList<>();
        items.add("不限");
        items.addAll(Arrays.asList(params));
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View tmp_view = layoutInflater.inflate(R.layout.popup_choose_items, null);

        GridLayout itemsLy = (GridLayout) tmp_view.findViewById(R.id.popup_choose_item_ly);
        itemsLy.setColumnCount(column_count);
        GridLayout.LayoutParams item_params;
        Button item_bt;
        for (String item_name : items) {
            item_bt = (Button) layoutInflater.inflate(R.layout.choose_item_button, null);
            item_bt.setText(item_name);
            item_bt.setPadding(10, 10, 10, 10);
            item_params = new GridLayout.LayoutParams();
            item_params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            item_params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            item_params.setGravity(Gravity.CENTER);
            item_params.setMargins(10, 0, 10, 0);
            itemsLy.addView(item_bt, item_params);
            item_bt.setOnClickListener(listener);
        }
        popupWindow = new PopupWindow(tmp_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        popupWindow.showAsDropDown(view);

    }


    private void showLocationPopup(View view) {
        if (view == null) {
            return;
        }
        String string = ViewUtils.getStringFromTextView(locationBt);
        Location location;
        if (TextUtils.isEmpty(string)) {
            location = User.getInstance().getLocation();
        } else {
            String[] strings = Tools.splitAddress(string, "·");
            location = new Location(strings[0], strings[1]);
        }
        LocationView locationView = LocationView.creatPopupWindow(this);
        locationView.setCurrentLocation(location);
        locationView.setListener(new LocationView.ChangingListener() {
            @Override
            public void onChanged(Location loc) {
            }

            public void onCancel(Location location) {
//                if (TextUtils.isEmpty(location.getCityName()))
//                    return;
//                if (location.getCityName().equals("不填")) {
//                    locationBt.setText("");
//                } else {
                locationBt.setText(location.toText());
//                }
                requestSearch(getMotorcadeId());
            }
        });
        locationView.show(view);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stop, R.anim.slide_out_bottom);
    }


    private void requestPublicCarList() {

        HttpHelper.getInstance().get(AppURL.GetPublicCarList, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonCar> list = ParseJson_Array(result, JsonCar.class);
                publicFragment.setAdapter(list);
            }

            @Override
            public void onFailed(String str) {
                showToast("列表获取失败" + str);
            }
        });
    }

    /**
     * 取TextView的内容
     *
     * @param btn
     * @param suffix 需要去除的后缀名
     * @return
     */
    private String getString(TextView btn, String suffix) {
        if (btn == null || TextUtils.isEmpty(btn.getText()))
            return null;
        String str = btn.getText().toString();
//        if (!TextUtils.isEmpty(suffix) && str.endsWith(suffix))
//            str = str.substring(0, str.lastIndexOf(suffix));
        return str;
    }


    private void requestSearch(final int m_id) {
//        RequestSourceCarSearch params = new RequestSourceCarSearch();
//        params.setType(getString(typeBt, null));
//        params.setLength(getString(lengthBt, "米"));
//        params.setCapacity(getString(weightBt, "吨"));
//        params.setUsualResidence(getString(locationBt, null));
        HashMap<String, String> params = new HashMap<>();
        if (m_id >= 0) {
            params.put("motorcadeId", String.valueOf(m_id));
        }
        String string = getString(typeBt, null);
        if (string != null)
            params.put("type", string);
        string = getString(lengthBt, "米");
        if (string != null)
            params.put("length", string);
        string = getString(weightBt, "吨");
        if (string != null)
            params.put("capacity", string);
        string = getString(locationBt, null);
        if (string != null)
            params.put("usualResidence", string);
        HttpHelper.getInstance().get(AppURL.GetCarSearch, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonCar> list = ParseJson_Array(result, JsonCar.class);
                if (list == null || list.isEmpty()) {
                    showToast("搜索结果为空");
                    list = null;
                }
                if (m_id >= 0) {
                    motorcadeFragment.setAdapter(list);
                } else {
                    publicFragment.setAdapter(list);
                }
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    private void requestMotorcadeList() {
        HttpHelper.getInstance().get(AppURL.GetMotorcadeCarList + "?motorcadeId=" + motorcadeId, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonCar> list = ParseJson_Array(result, JsonCar.class);
//                adapter = new SourceCarListAdapter(list);
//                setListAdapter();
                motorcadeFragment.setAdapter(list);
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }
}
