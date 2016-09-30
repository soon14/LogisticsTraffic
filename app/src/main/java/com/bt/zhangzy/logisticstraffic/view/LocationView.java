package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.bt.zhangzy.logisticstraffic.adapter.LocationListAdapter;
import com.bt.zhangzy.logisticstraffic.d.R;
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
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * 地址选择封装类
 * Created by ZhangZy on 2015/8/10.
 */
public class LocationView implements OnWheelChangedListener, BaiduSDK.LocationListener {
    private static final String TAG = LocationView.class.getSimpleName();
    final String NullString = "不填";

    public interface ChangingListener {
        void onChanged(Location location);

        //隐藏时调用
        void onCancel(Location location);
    }

    public LocationView() {
        requestCityList();
        requestOpenCityList();
    }

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
    private ArrayWheelAdapter mProvinceAdapter;//adapter缓存
    private ArrayWheelAdapter[] mCityAdapterList;
    //    private String mProvinceCurrent;
//    private String mCityCurrent;
    private PopupWindow popupWindow;
    private Dialog dialog;
    private PopupWindow rightPopupWindow;//右侧的城市选择列表

    private ChangingListener listener;
    private Location currentLocation;


    public void init(Context context, PopupWindow view) {
        this.context = context;
        popupWindow = view;
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });
        mProvinceView = (WheelView) view.getContentView().findViewById(R.id.location_wheel_province);
        mCityView = (WheelView) view.getContentView().findViewById(R.id.location_wheel_city);
        init();
    }

    public void init(Context context, Dialog dialog) {
        this.context = context;
        this.dialog = dialog;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dismiss();
            }
        });
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

    public static LocationView createDialogForOrder(Activity context) {
        BaseDialog dialog = new BaseDialog(context);
        dialog.setContentView(R.layout.order_location_wheel);

        getInstance().init(context, dialog);
        dialog.setOnClickListener(R.id.order_location_confirm_bt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().dialog.cancel();
            }
        });
        return getInstance();
    }

    public LocationView setTitle(String title) {
        if (dialog != null) {
            dialog.setTitle(title);
        }
        return this;
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
        if (listener != null) {
            if (currentLocation == null) {
                currentLocation = new Location("", "");
            }
            listener.onCancel(currentLocation);
        }
        if (mProvinceView != null)
            mProvinceView.removeChangingListener(this);
        if (mCityView != null)
            mCityView.removeChangingListener(this);
        mProvinceView = null;
        mCityView = null;
        listener = null;
        currentLocation = null;
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
//                    cityList = list;
                    cityList = new ArrayList<JsonLocationProvince>();
                    //添加一组空数据
                    JsonLocationProvince json = new JsonLocationProvince();
                    json.setProvince(NullString);
                    ArrayList<JsonLocationCity> tmp_c = new ArrayList<>();
                    JsonLocationCity city = new JsonLocationCity();
                    city.setCity(NullString);
                    tmp_c.add(city);
                    json.setCity(tmp_c);
                    cityList.add(json);
                    //添加服务器返回数据
                    cityList.addAll(list);
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
        mProvinceView.setShadowColor(0xFFFFFFFF, 0x66FFFFFF, 0x00FFFFFF);
        mCityView.setShadowColor(0xFFFFFFFF, 0x66FFFFFF, 0x00FFFFFF);
        mProvinceView.addChangingListener(this);
        mCityView.addChangingListener(this);
        if (mProvinceAdapter == null) {
            JsonLocationProvince[] array = new JsonLocationProvince[cityList.size()];
            cityList.toArray(array);
            mProvinceAdapter = new ArrayWheelAdapter<JsonLocationProvince>(context, array);
            mProvinceAdapter.setItemResource(R.layout.location_text_item);
            mCityAdapterList = new ArrayWheelAdapter[cityList.size()];
        }
        mProvinceView.setViewAdapter(mProvinceAdapter);
        mProvinceView.setCurrentItem(0);
        updateCities();
        mCityView.setCurrentItem(0);
    }

    private void updateCities() {
        int current_province_index = mProvinceView.getCurrentItem();
        ArrayWheelAdapter<JsonLocationCity> city_adapter;
        if (mCityAdapterList[current_province_index] == null) {
            JsonLocationProvince jsonLocationProvince = cityList.get(current_province_index);
            List<JsonLocationCity> city = jsonLocationProvince.getCity();
            JsonLocationCity[] cities;
            if (city == null) {
                JsonLocationCity c = new JsonLocationCity();
                c.setCity(NullString);
                cities = new JsonLocationCity[]{c};
            } else {
                cities = city.toArray(new JsonLocationCity[city.size()]);
            }
            city_adapter = new ArrayWheelAdapter<>(context, cities);
            city_adapter.setItemResource(R.layout.location_text_item);
            mCityAdapterList[current_province_index] = city_adapter;
//            mCityAdapterList.add(current_province_index, city_adapter);
        } else {
            city_adapter = mCityAdapterList[current_province_index];
        }
        mCityView.setViewAdapter(city_adapter);
    }

    public LocationView setCurrentLocation(Location location) {

        if (location == null)
            return this;
        if (cityList == null || cityList.isEmpty()) {
            requestCityList();
            return this;
        }

        if (!TextUtils.isEmpty(location.getProvinceName())) {
            JsonLocationProvince province;
            for (int k = 0; k < cityList.size(); k++) {
                province = cityList.get(k);
                if (location.getProvinceName().startsWith(province.getProvince())) {
                    mProvinceView.setCurrentItem(k);
                    for (int j = 0; j < province.getCity().size(); j++) {
                        if (location.getCityName().startsWith(province.getCity().get(j).getCity())) {
                            updateCities();
                            mCityView.setCurrentItem(j);
                            this.currentLocation = location;
                            break;
                        }
                    }
                    break;
                }
            }
        } else if (!TextUtils.isEmpty(location.getCityName())) {
            //provinceName == null
            JsonLocationProvince province;
            for (int k = 0; k < cityList.size(); k++) {
                province = cityList.get(k);
                for (int j = 0; j < province.getCity().size(); j++) {
                    if (location.getCityName().startsWith(province.getCity().get(j).getCity())) {
                        mProvinceView.setCurrentItem(k);
                        updateCities();
                        mCityView.setCurrentItem(j);
                        this.currentLocation = location;
                        return this;
                    }
                }
            }
        }

        return this;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        String mProvinceCurrent = null;
        String mCityCurrent = null;
        JsonLocationProvince province = cityList.get(mProvinceView.getCurrentItem());
        if (wheel == mProvinceView) {
            updateCities();
            mCityView.setCurrentItem(0);
            mProvinceCurrent = province.getProvince();
            mCityCurrent = province.getCity().get(0).getCity();

        } else if (wheel == mCityView) {
//            JsonLocationProvince province = cityList.get(mProvinceView.getCurrentItem());
            mProvinceCurrent = province.getProvince();
            mCityCurrent = province.getCity().get(newValue).getCity();
        }
        if (mProvinceCurrent == NullString) {
            mProvinceCurrent = "";
        }
        if (mCityCurrent == NullString) {
            mCityCurrent = "";
        }
        if (currentLocation == null) {
            currentLocation = new Location(mProvinceCurrent, mCityCurrent);
        } else {
            currentLocation.setProvinceName(mProvinceCurrent);
            currentLocation.setCityName(mCityCurrent);
        }
        Log.d(TAG, "currentLocation=" + currentLocation);
        if (listener != null) {
            listener.onChanged(currentLocation);
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
            if (TextUtils.isEmpty(location.getCityName()))
                locationNetworkTx.setText("定位失败");
            else
                locationNetworkTx.setText(location.getCityName());
        }

        listView = (ListView) tmp_view.findViewById(R.id.location_city_list);

        setListViewAdapter();

        return popupWindow;

    }

    /**
     * 设置开通城市列表的数据
     */
    private void setListViewAdapter() {
        LocationListAdapter adapter = new LocationListAdapter(cityListOpen);
        listView.setAdapter(adapter);
        adapter.setItemOnClickCallback(new LocationListAdapter.ItemOnClickCallback() {
            @Override
            public void onClickItem(Location loc) {
                Log.d(TAG, "点击了：" + loc.toString());
//                Toast.makeText(getBaseContext(),"点击了："+
                if (locationCallback != null) {
                    location = loc;
                    //更新用户的定位信息
                    User.getInstance().setLocation(location);
//                    if (locationNetworkTx != null)
//                        locationNetworkTx.setText(location.getCityName());
                    locationCallback.chooseLocation(loc);

                    if (rightPopupWindow != null)
                        rightPopupWindow.dismiss();
                }
            }
        });
    }

    public void showLoacaitonList(Activity act, View view) {
        context = act;

        if (rightPopupWindow == null) {
            PopupWindow popupWindow = creatPopupWindow(act);
            rightPopupWindow = popupWindow;
        } else {
            //检测数据
            if (cityListOpen == null || cityListOpen.isEmpty()) {
                requestOpenCityList();
            }
            //刷新数据
            if (listView != null) {
                setListViewAdapter();
            }
        }
        if (rightPopupWindow.isShowing()) {
            rightPopupWindow.dismiss();
        } else {
            shadowWindow = act.getWindow();
            rightPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, 0);
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
        Location location = new Location();
        location.setProvinceName(province);
        location.setCityName(cityname);
        location.setLatitude(latitude);
        location.setLongitude(langitude);
        Log.i(TAG, "定位信息：" + location);

        this.location = findLocation(location);
        this.location.setLatitude(latitude);
        this.location.setLongitude(langitude);
        Log.i(TAG, "转化 定位信息：" + this.location);

        //更新用户的定位信息
        User.getInstance().setLocation(this.location);
        if (locationNetworkTx != null) {
            if (TextUtils.isEmpty(cityname))
                locationNetworkTx.setText("定位失败");
            else
                locationNetworkTx.setText(cityname);
        }
        if (locationCallback != null) {
            locationCallback.networkLocation(location);
        }
    }

    /**
     * 将sdk定位的城市信息 转换为 服务器的城市信息
     *
     * @param location
     * @return
     */
    private Location findLocation(Location location) {
        if (!TextUtils.isEmpty(location.getProvinceName())) {
            JsonLocationProvince province;
            for (int k = 0; k < cityList.size(); k++) {
                province = cityList.get(k);
                if (location.getProvinceName().startsWith(province.getProvince())) {
                    for (int j = 0; j < province.getCity().size(); j++) {
                        if (location.getCityName().startsWith(province.getCity().get(j).getCity())) {
                            return new Location(province.getProvince(), province.getCity().get(j).getCity());
                        }
                    }
                    break;
                }
            }
        } else if (!TextUtils.isEmpty(location.getCityName())) {
            //provinceName == null
            JsonLocationProvince province;
            for (int k = 0; k < cityList.size(); k++) {
                province = cityList.get(k);
                for (int j = 0; j < province.getCity().size(); j++) {
                    if (location.getCityName().startsWith(province.getCity().get(j).getCity())) {
                        return new Location(province.getProvince(), province.getCity().get(j).getCity());
                    }
                }
            }
        }
        return null;
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
