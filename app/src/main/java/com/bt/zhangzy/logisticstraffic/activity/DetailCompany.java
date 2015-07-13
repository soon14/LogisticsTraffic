package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.zhangzy.baidusdk.BaiduMapActivity;

/**
 * Created by ZhangZy on 2015/6/11.
 */
public class DetailCompany extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_cp);

        //TODO 设置电话监听
        TelephonyManager mTelephonyMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyMgr.listen(new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        System.out.println("挂断 -CALL_STATE_IDLE");
                        if (openCall) {
                            openCall = false;
// restart app
//                            Intent i = getBaseContext().getPackageManager()
//                                    .getLaunchIntentForPackage(
//                                            getBaseContext().getPackageName());
////                            i.putExtra("target", TARGET);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(i);
                            onRestart();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        System.out.println("接听");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        System.out.println("响铃:来电号码" + incomingNumber);
                        //输出来电号码
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    boolean openCall;

    public void onClick_CallPhone(View view) {
        openCall = true;
//        ContextTools.callPhone(this, "10010");
        getApp().callPhone( "10010");
    }


    public void onClick_gotoMap(View view) {
        startActivity(BaiduMapActivity.class);
    }


    public void onClick_Evaluation(View view) {
        startActivity(EvaluationListActivity.class);
    }

    public void onClick_OpenPicture(View view) {
//        ImageView img = (ImageView) view;
//        Drawable drawable = getResources().getDrawable(getResources().getDrawable(R.drawable.fake_detail_1));
//        Uri parse = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.fake_detail_1);
//        Uri parse = Uri.parse("file:///android_asset/fake_detail_1.png");
//        Uri parse = Uri.parse("file:///android_asset/1.jpg");
//        ContextTools.OpenPicture(this, parse);
        startActivity(PictureActivity.class);
    }

    public void onClick_Order(View view) {
        startActivity(OrderActivity.class);
    }
}
