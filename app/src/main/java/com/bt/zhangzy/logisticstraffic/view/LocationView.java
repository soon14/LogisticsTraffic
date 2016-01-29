package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.bt.zhangzy.logisticstraffic.app.LocationXmlParserHandler;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.network.entity.JsonLocationCity;
import com.bt.zhangzy.network.entity.JsonLocationProvince;
import com.zhangzy.baidusdk.BaiduSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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

    private void requestCityList() {
        HttpHelper.getInstance().get(Url.GetCityList, new JsonCallback() {
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

    /*private void loadXmlData(Context context, String fileName) {

        HashMap<String, ArrayList<String>> array;
        InputStream input = null;
        try {
            input = context.getAssets().open(fileName);

            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            LocationXmlParserHandler handler = new LocationXmlParserHandler();
            parser.parse(input, handler);
            input.close();

            HashMap<String, ArrayList<String>> provinceMap = handler.getProvinceMap();
            Set<String> strings = provinceMap.keySet();
            mProvinceArray = new String[strings.size()];
            strings.toArray(mProvinceArray);
            //排序
            Comparator cmp = Collator.getInstance(Locale.CHINESE);
//            Collections.sort((List) strings,cmp);
            Arrays.sort(mProvinceArray, cmp);

            for (String str : strings) {
                ArrayList<String> tmp_array = provinceMap.get(str);
                String[] cities = new String[tmp_array.size()];
                tmp_array.toArray(cities);
                mCitiesMap.put(str, cities);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

//    /**
//     * 读取json数据
//     *
//     * @param context
//     */
  /*  public static void loadData(final Context context) {
        //读取城市列表
        new AsyncTask<String, Integer, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... params) {
                try {
                    InputStream is = context.getResources().getAssets().open(params[0]);
                    int size = is.available();
                    // Read the entire asset into a local byte buffer.
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    // Convert the buffer into a string.
                    String text = new String(buffer, "UTF-8");
                    JSONArray jsonObject = new JSONArray(text.trim());
//                    Log.d(TAG, "json=" + jsonObject.toJsonString());
                    if (jsonObject == null)
                        return null;
                    mProvinceArray = new String[jsonObject.length()];
                    JSONObject json;
                    JSONArray jsonArray;
                    String[] tmpCitys;
                    for (int k = 0; k < jsonObject.length(); k++) {
                        json = jsonObject.getJSONObject(k);
                        mProvinceArray[k] = json.getString("province");
                        jsonArray = json.getJSONArray("list");
                        tmpCitys = new String[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            tmpCitys[j] = jsonArray.getString(j);
                        }
                        mCitiesMap.put(mProvinceArray[k], tmpCitys);
                    }
                    return jsonObject;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                super.onPostExecute(jsonArray);
                if (jsonArray != null) {
//                    jsonCityList = jsonArray;
//                    init();
                }
            }
        }.execute("cityList.json");
    }*/

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

        LocationListAdapter adapter = new LocationListAdapter(cityList);
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
    public void callbackCityName(String cityname, String latitude, String langitude) {
        location = new Location();
        location.setCityName(cityname);
        location.setLatitude(latitude);
        location.setLangitude(langitude);
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
