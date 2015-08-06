package com.bt.zhangzy.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 工具类
 * Created by ZhangZy on 2015/7/22.
 */
public final class Tools {

    /**
     *
     * @param format 格式
     * @return 返回当前时间的字符串表达式
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    /**
     *
     * @return 根据默认的格式返回当前时间
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }
}
