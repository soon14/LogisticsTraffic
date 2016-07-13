package com.bt.zhangzy.logisticstraffic;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.tools.Tools;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Log.d("test", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.print("=====================");
        ContextTools.getLocalIpAddress();
//        WeiXinPay.getInstanse().sign(null);

        Log.d("test", "phone = " + Tools.IsPhoneNum("14701233325"));
        Log.d("test", "phone = " + Tools.IsPhoneNum("12001233325"));
        Log.d("test", "phone = " + Tools.IsPhoneNum("17501233325"));

    }
}