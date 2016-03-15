package com.bt.zhangzy.tools;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * Created by ZhangZy on 2016-1-15.
 */
public class Json extends JSONObject {

    private static final String TAG = Json.class.getSimpleName();

    public Json() {
        super();
    }


    public Json(JSONTokener json) throws JSONException {
        super(json);
    }

    public Json(Map map) {
        super(map);
    }

    public static Json ToJson(String json) {
        try {
            return new Json(new JSONTokener(json));
        } catch (JSONException ex) {
            Log.w(TAG, ex);
        }
        return null;
    }

    public Json put(String name, Object value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return this;
    }

    public Json put(String name, long value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return this;
    }

    public Json put(String name, int value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return this;
    }

    public Json put(String name, double value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return this;
    }

    public Json put(String name, boolean value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return this;
    }

    public int getInt(String name) {
        try {
            return super.getInt(name);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return 0;
    }

    public long getLong(String name) {
        try {
            return super.getLong(name);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return 0;
    }

    public boolean getBoolean(String name) {
        try {
            return super.getBoolean(name);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return false;
    }

    public String getString(String name) {
        try {
            return super.getString(name);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return "";
    }

    public Json getJson(String name) {
        try {
//            return (Json) super.getJSONObject(name);
            return ToJson(getJSONObject(name).toString());
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, e);
        }
        return null;
    }
}
