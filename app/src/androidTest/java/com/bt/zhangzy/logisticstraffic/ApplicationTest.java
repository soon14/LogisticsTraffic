package com.bt.zhangzy.logisticstraffic;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.bt.zhangzy.pay.WeiXinPay;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Log.d("ccccccc", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        WeiXinPay.getInstanse().sign(null);
    }
}