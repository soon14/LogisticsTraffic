package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.LocationXmlParserHandler;
import com.bt.zhangzy.tools.PinyinComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class LocationView implements OnWheelChangedListener {
    public interface ChangingListener {
        void onChanged(String province, String city);
    }

    private static final String TAG = LocationView.class.getSimpleName();
    //    private JSONArray jsonCityList;
    private static String[] mProvinceArray;
    /**
     * key - 省 value - 市
     */
    private static Map<String, String[]> mCitiesMap = new HashMap<String, String[]>();
    private Context context;
    private WheelView mProvinceView;
    private WheelView mCityView;
    private String mProvinceCurrent;
    private String mCityCurrent;
    private PopupWindow popupWindow;
    private Dialog dialog;

    private ChangingListener listener;


    LocationView(Context context, PopupWindow view) {
        this.context = context;
        popupWindow = view;
        mProvinceView = (WheelView) view.getContentView().findViewById(R.id.location_wheel_province);
        mCityView = (WheelView) view.getContentView().findViewById(R.id.location_wheel_city);
        init();
    }

    LocationView(Context context,Dialog dialog){
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

        LocationView locationView = new LocationView(context, popupWindow);
        return locationView;
    }

    public static LocationView createDialog(Activity context){
        BaseDialog dialog = new BaseDialog(context);
        dialog.setContentView(R.layout.popup_location_wheel);

        LocationView locationView = new LocationView(context,dialog);
        return locationView;
    }

    public void show(View view) {
        if (popupWindow != null) {
            popupWindow.showAsDropDown(view);
        }

    }

    public void show(){
        if(dialog !=null){
            dialog.show();
        }
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public LocationView setListener(ChangingListener listener) {
        this.listener = listener;
        return this;
    }

    private void loadXmlData(Context context, String fileName) {

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

    }

    /**
     * 读取json数据
     * @param context
     */
    public static void loadData(final Context context) {
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
//                    Log.d(TAG, "json=" + jsonObject.toString());
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
    }

    private void init() {

        mProvinceView.setShadowColor(0xFFFFFFFF, 0xBBFFFFFF, 0x00FFFFFF);
        mCityView.setShadowColor(0xFFFFFFFF, 0xBBFFFFFF, 0x00FFFFFF);
        mProvinceView.addChangingListener(this);
        mCityView.addChangingListener(this);

        if (mProvinceArray == null) {
            loadXmlData(context, "province_data.xml");
        }
        mProvinceView.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceArray));
        mProvinceView.setCurrentItem(0);
        updateCities();
    }

    private void updateCities() {
        mProvinceCurrent = mProvinceArray[mProvinceView.getCurrentItem()];
        String[] cities = mCitiesMap.get(mProvinceCurrent);
        if (cities == null) {
            cities = new String[]{" "};
        }
        mCityView.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
        mCityView.setCurrentItem(0);
        mCityCurrent = cities[0];
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mProvinceView) {
            updateCities();
        } else if (wheel == mCityView) {
            mCityCurrent = mCitiesMap.get(mProvinceCurrent)[newValue];
        }
        Log.d(TAG, "mProvince=" + mProvinceCurrent + " mCity=" + mCityCurrent);
        if (listener != null) {
            listener.onChanged(mProvinceCurrent, mCityCurrent);
        }
    }
}
