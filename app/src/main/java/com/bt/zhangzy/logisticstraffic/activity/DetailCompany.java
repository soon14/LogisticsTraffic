package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.app.Constant;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.zhangzy.baidusdk.BaiduMapActivity;

/**
 * Created by ZhangZy on 2015/6/11.
 */
public class DetailCompany extends BaseActivity {

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //testData
        product = new Product(102);
        product.setName("测试数据名称");
        product.setPhoneNumber("10010");
        product.setAddress("黄河大街以北重工路xxx号");


        setContentView(R.layout.activity_detail_cp);
        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if(bundle.containsKey(Constant.BUNDLE_PRODUCT_KEY)){
                product = (Product) bundle.get(Constant.BUNDLE_PRODUCT_KEY);
            }
        }

        setTextView(R.id.detail_name_tx, product.getName());
        setTextView(R.id.detail_cp_address_tx,product.getAddress());

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
        getApp().callPhone(product.getPhoneNumber());
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
        if (!User.getInstance().getLogin()) {
            //用户未登陆时自动跳转到登陆页面
            startActivity(LoginActivity.class);
            return;
        }
        startActivity(OrderActivity.class);
    }

    public void onClick_CollectAdd(View view) {
        User.getInstance().addCollectionProduct(product);
        showToast("收藏成功");
    }
}
