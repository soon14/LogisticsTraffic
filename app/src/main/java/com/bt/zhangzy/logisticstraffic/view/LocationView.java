package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.LocationListAdapter;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonLocationCity;
import com.bt.zhangzy.network.entity.JsonLocationProvince;
import com.bt.zhangzy.network.entity.ResponseOpenCity;
import com.zhangzy.baidusdk.BaiduSDK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * 地址选择封装类
 * Created by ZhangZy on 2015/8/10.
 */
public class LocationView implements OnWheelChangedListener, BaiduSDK.LocationListener {
    public LocationView() {
        requestCityList();
        requestOpenCityList();
    }

    public interface ChangingListener {
        void onChanged(String province, String city);
    }

    private static final String TAG = LocationView.class.getSimpleName();

    //共用数据，设计为单例模式;
    static LocationView instance = new LocationView();
    ;

    public static LocationView getInstance() {
        return instance;
    }

    //    private JSONArray jsonCityList;
//    private String[] mProvinceArray;
//    /**
//     * key - 省 value - 市
//     */
//    private Map<String, String[]> mCitiesMap = new HashMap<String, String[]>();
    private Context context;
    private WheelView mProvinceView;
    private WheelView mCityView;
    private String mProvinceCurrent;
    private String mCityCurrent;
    private PopupWindow popupWindow;
    private Dialog dialog;

    private ChangingListener listener;
    private Location currentLocation;


    public void init(Context context, PopupWindow view) {
        this.context = context;
        popupWindow = view;
        mProvinceView = (WheelView) view.getContentView().findViewById(R.id.location_wheel_province);
        mCityView = (WheelView) view.getContentView().findViewById(R.id.location_wheel_city);
        init();
    }

    public void init(Context context, Dialog dialog) {
        this.context = context;
        this.dialog = dialog;
        mProvinceView = (WheelView) dialog.findViewById(R.id.location_wheel_province);
        mCityView = (WheelView) dialog.findViewById(R.id.location_wheel_city);
        init();
    }

    public static LocationView creatPopupWindow(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_location_wheel, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));

        getInstance().init(context, popupWindow);
        return getInstance();
    }

    public static LocationView createDialog(Activity context) {
        BaseDialog dialog = new BaseDialog(context);
        dialog.setContentView(R.layout.popup_location_wheel);

        getInstance().init(context, dialog);
        return getInstance();
    }

    public void show(View view) {
        if (popupWindow != null) {
            popupWindow.showAsDropDown(view);
        }

    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public LocationView setListener(ChangingListener listener) {
        this.listener = listener;
        return this;
    }

    List<JsonLocationProvince> cityList;//城市列表
    ArrayList<ArrayList<Location>> cityListOpen;//城市列表


    private void requestCityList() {
        HttpHelper.getInstance().get(AppURL.GetCityList, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<JsonLocationProvince> list = ParseJson_Array(result, JsonLocationProvince.class);
                //// TO DO: 2016-1-27
                if (list != null && !list.isEmpty()) {
                    cityList = list;
                }
            }

            @Override
            public void onFailed(String str) {
                Log.w(TAG, "城市列表请求失败:" + str);
            }
        });
    }

    private void requestOpenCityList() {
        HttpHelper.getInstance().get(AppURL.GetOpenCityList, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                List<ResponseOpenCity> list = ParseJson_Array(result, ResponseOpenCity.class);
                //// TO DO: 2016-1-27
                if (list != null && !list.isEmpty()) {
//                    cityListOpen = list;

                    Collections.sort(list, new Comparator<ResponseOpenCity>() {
                        @Override
                        public int compare(ResponseOpenCity lhs, ResponseOpenCity rhs) {
                            return lhs.getFirstLetter() - rhs.getFirstLetter();
                        }
                    });
                    ArrayList<ArrayList<Location>> locationList = new ArrayList<ArrayList<Location>>(list.size());
//                    JsonLocationProvince jsonLocationProvince;
                    ArrayList<Location> tmp_list;
                    String province;
                    Location location;
                    for (ResponseOpenCity jsonLocationProvince : list) {
                        province = jsonLocationProvince.getProvince();
                        tmp_list = new ArrayList<Location>();
                        locationList.add(tmp_list);
                        for (String city : jsonLocationProvince.getCity()) {
                            tmp_list.add(new Location(province, city));
                        }
                    }
                    cityListOpen = locationList;
                }
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }


    private void init() {
        if (cityList == null || cityList.isEmpty()) {
            requestCityList();
            return;
        }
        mProvinceView.setShadowColor(0xFFFFFFFF, 0xBBFFFFFF, 0x00FFFFFF);
        mCityView.setShadowColor(0xFFFFFFFF, 0xBBFFFFFF, 0x00FFFFFF);
        mProvinceView.addChangingListener(this);
        mCityView.addChangingListener(this);

        JsonLocationProvince[] array = new JsonLocationProvince[cityList.size()];
        cityList.toArray(array);
        mProvinceView.setViewAdapter(new ArrayWheelAdapter<JsonLocationProvince>(context, array));
        mProvinceView.setCurrentItem(0);
        updateCities();
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
        if (location != null) {
            if (!TextUtils.isEmpty(location.getProvinceName())) {

                JsonLocationProvince province;
                for (int k = 0; k < cityList.size(); k++) {
                    province = cityList.get(k);
                    if (location.getProvinceName().startsWith(province.getProvince())) {
                        mProvinceView.setCurrentItem(k);
                        updateCities();
                        for (int j = 0; j < province.getCity().size(); j++) {
                            if (location.getCityName().startsWith(province.getCity().get(j).getCity())) {
                                mCityView.setCurrentItem(j);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void updateCities() {
        JsonLocationProvince jsonLocationProvince = cityList.get(mProvinceView.getCurrentItem());
        mProvinceCurrent = jsonLocationProvince.getProvince();
//        mProvinceCurrent = mProvinceArray[mProvinceView.getCurrentItem()];
        List<JsonLocationCity> city = jsonLocationProvince.getCity();
        JsonLocationCity[] cities = city.toArray(new JsonLocationCity[city.size()]);
//        String[] cities = mCitiesMap.get(mProvinceCurrent);
//        if (cities == null) {
//            cities = new String[]{" "};
//        }
        mCityView.setViewAdapter(new ArrayWheelAdapter<JsonLocationCity>(context, cities));
        mCityView.setCurrentItem(0);
        mCityCurrent = cities[0].toString();
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mProvinceView) {
            updateCities();
        } else if (wheel == mCityView) {
            mCityCurrent = cityList.get(mProvinceView.getCurrentItem()).getCity().get(newValue).getCity();
//            mCityCurrent = mCitiesMap.get(mProvinceCurrent)[newValue];
        }
        Log.d(TAG, "mProvince=" + mProvinceCurrent + " mCity=" + mCityCurrent);
        if (listener != null) {
            listener.onChanged(mProvinceCurrent, mCityCurrent);
        }
    }


    /**
     * ======= 侧边栏城市列表 ==============================
     */
//    private PopupWindow popupWindowSide;
    private ListView listView;
    private TextView locationNetworkTx;
    private LocationCallback locationCallback;
    private Location location;//用于缓存已经定位的信息
    private Window shadowWindow;

    /**
     * TODO 2016年1月28日 城市选择的三级列表 效率低，待优化；
     * 地址选择对话框
     */
    private PopupWindow creatPopupWindow(Activity act) {
        //        Context act = this.getBaseContext();
        View tmp_view = LayoutInflater.from(act).inflate(R.layout.location_popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(tmp_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置颜色值 修正奇葩错误
        tmp_view.findViewById(R.id.location_pop_ly).setBackgroundColor(act.getResources().getColor(R.color.main_bg_color));
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.AnimationFadeRight);
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(act.getResources().getColor(R.color.mask_black)));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());//// TO DO: 2016-1-27  点击外部消失，会相应到下面的时间
        popupWindow.setFocusable(true);
        Button backBtn = (Button) tmp_view.findViewById(R.id.popupwindow_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        shadowWindow = act.getWindow();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = shadowWindow.getAttributes();
                lp.alpha = 1f;
                shadowWindow.setAttributes(lp);
            }
        });

        //用于显示网络定位的结果
        locationNetworkTx = (TextView) tmp_view.findViewById(R.id.location_network_tx);
        if (location != null) {
            locationNetworkTx.setText(location.getCityName());
        }

        listView = (ListView) tmp_view.findViewById(R.id.location_city_list);

        LocationListAdapter adapter = new LocationListAdapter(cityListOpen);
        listView.setAdapter(adapter);
        adapter.setItemOnClickCallback(new LocationListAdapter.ItemOnClickCallback() {
            @Override
            public void onClickItem(Location loc) {
                Log.d(TAG, "点击了：" + loc.toString());
//                Toast.makeText(getBaseContext(),"点击了："+
                if (locationCallback != null) {
                    location = loc;
                    if (locationNetworkTx != null)
                        locationNetworkTx.setText(location.getCityName());
                    locationCallback.chooseLocation(loc);
                }
            }
        });

        return popupWindow;

    }

    public void showLoacaitonList(Activity act, View view) {
        context = act;
        PopupWindow popupWindow = creatPopupWindow(act);
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            shadowWindow = act.getWindow();
            popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, 0);
            WindowManager.LayoutParams lp = shadowWindow.getAttributes();
            lp.alpha = 0.3f;
            shadowWindow.setAttributes(lp);
        }
    }

    /**
     * 共用一个请求接口
     */
    public void requestLocation(Context context, LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
        BaiduSDK.getInstance().setLocationListener(this);
        BaiduSDK.getInstance().requestLocationServer(context);
    }

    @Override
    public void callbackCityName(String province, String cityname, String latitude, String langitude) {
        location = new Location();
        location.setProvinceName(province);
        location.setCityName(cityname);
        location.setLatitude(latitude);
        location.setLangitude(langitude);
        Log.i(TAG, "定位信息：" + location);
        //更新用户的定位信息
        User.getInstance().setLocation(location);
        if (locationNetworkTx != null) {
            locationNetworkTx.setText(cityname);
        }
        if (locationCallback != null) {
            locationCallback.networkLocation(location);
        }
    }

    public interface LocationCallback {
        /**
         * 网络定位 回调
         */
        public void networkLocation(Location location);

        /**
         * 用户选择位置 回调
         */
        public void chooseLocation(Location location);
    }
    /*======================================*/

}
