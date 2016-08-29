package params.devices.zhangzy.bugly;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by ZhangZy on 2016-8-6.
 */
public class BuglySDK {

    /*AppID：
6d69bb7789
AppKey：
eafa11cb-f9c6-405d-84be-5758fde8662e
*/
    public static final String APP_ID = "6d69bb7789";
    public static final String APP_KEY = "eafa11cb-f9c6-405d-84be-5758fde8662e";

    static BuglySDK instance = new BuglySDK();

    private BuglySDK() {
    }

    public static BuglySDK getInstance() {
        return instance;
    }

    /**
     * 初始化
     *
     * @param appContext
     */
    public void init(Context appContext, boolean isDebug) {
        //屏蔽测试是的bug反馈
        if (isDebug)
            return;

        CrashReport.initCrashReport(appContext, APP_ID, isDebug);
    }

    /**
     * 测试
     */
    public void test() {
        CrashReport.testJavaCrash();
    }
}
