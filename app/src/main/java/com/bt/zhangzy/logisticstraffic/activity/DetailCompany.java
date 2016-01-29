package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.ImageHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.network.entity.JsonFavorite;
import com.bt.zhangzy.network.entity.JsonUser;

import java.util.Iterator;

/**
 * Created by ZhangZy on 2015/6/11.
 */
public class DetailCompany extends BaseActivity {

    private Product product;
    int favoritesId;//收藏id  标识是否被收藏；

    //testData
//        product = new Product(102);
//        product.setName("测试数据名称");
//        product.setPhoneNumber("10010");
//        product.setAddress("黄河大街以北重工路xxx号");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_cp);
        setPageName("门企详情");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppParams.BUNDLE_PRODUCT_KEY)) {
            product = (Product) bundle.get(AppParams.BUNDLE_PRODUCT_KEY);
            requestGetCompany(product.getID());
            View collectBtn = findViewById(R.id.detail_colletion);
            favoritesId = User.getInstance().checkFavoritesId(product.getID());
            collectBtn.setSelected(favoritesId > 0);
        } else {
            showToast("product = null");
            finish();
        }

        initView();
        setTelephonyListen();


    }

    private void setTelephonyListen() {
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

    //更新店铺信息
    private void requestGetCompany(int id) {
        // todo 店铺信息没有返回
        HttpHelper.getInstance().get(Url.GetCompany + id, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    private void initView() {
        if (AppParams.DEVICES_APP || User.getInstance().getUserType() == Type.InformationType) {
            findViewById(R.id.detail_gray_line).setVisibility(View.GONE);
            findViewById(R.id.detail_order_btn).setVisibility(View.GONE);
        }

        if (product != null) {
            setTextView(R.id.detail_name_tx, product.getName());
            setTextView(R.id.detail_cp_address_tx, product.getAddress());
        }

        ImageView headImg = (ImageView) findViewById(R.id.detail_user_head_img);
//        String url = "http://img1.3lian.com/img2011/w1/105/4/13.jpg";
//        ImageHelper.getInstance().load(url, headImg);
    }

    boolean openCall;

    public void onClick_CallPhone(View view) {
        if (AppParams.DEVICES_APP && !User.getInstance().isVIP()) {
            BaseDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PayActivity.class);
                }
            });
            return;
        }
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
        if (AppParams.DEVICES_APP && !User.getInstance().isVIP()) {
            BaseDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PayActivity.class);
                }
            });
            return;
        }
        //// TODO: 2016-1-29  考虑是否直接跳转到 OrderDetailActivity
        startActivity(OrderActivity.class);
    }

    public void onClick_CollectAdd(View view) {
        if (AppParams.DEVICES_APP && !User.getInstance().isVIP()) {
            BaseDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PayActivity.class);
                }
            });
            return;
        }
        if (findViewById(R.id.detail_colletion).isSelected()) {
            requestFavoritesDel();
        } else {
            //// TO DO: 2016-1-28  收藏接口
            requestFavorites();

        }
    }

    private void requestFavoritesDel() {
        HttpHelper.getInstance().del(Url.DelFavourite + favoritesId, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("取消收藏成功");
                Iterator<JsonFavorite> iterator = User.getInstance().getJsonFavorites().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getId() == favoritesId) {
                        iterator.remove();
                        break;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.detail_colletion).setSelected(false);
                    }
                });

            }

            @Override
            public void onFailed(String str) {
                showToast("取消收藏失败");
            }
        });
    }

    private void requestFavorites() {
        //传4个参数 fromRole,fromRoleId,toRole,toRoleId
        JsonUser jsonUser = User.getInstance().getJsonUser();
        int fromRole = jsonUser.getRole();
        int fromRoleId = jsonUser.getId();
        int toRole = 3;//默认只能显示信息部
        int toRoleId = product.getID();
        HttpHelper.getInstance().get(
                HttpHelper.toString(Url.GetFavourite,
                        new String[]{"fromRole=" + fromRole, "fromRoleId=" + fromRoleId, "toRole=" + toRole, "toRoleId=" + toRoleId}),
                new JsonCallback() {
                    @Override
                    public void onSuccess(String msg, String result) {
                        JsonFavorite favorite = ParseJson_Object(result, JsonFavorite.class);
                        favoritesId = favorite.getId();
                        User.getInstance().getJsonFavorites().add(favorite);
                        User.getInstance().addCollectionProduct(product);
                        showToast("收藏成功");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.detail_colletion).setSelected(true);
                            }
                        });
                    }

                    @Override
                    public void onFailed(String str) {
                        showToast("收藏失败");
                    }
                });
    }
}
