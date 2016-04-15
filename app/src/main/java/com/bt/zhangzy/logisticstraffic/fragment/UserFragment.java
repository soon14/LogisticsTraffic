package com.bt.zhangzy.logisticstraffic.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.service.UpDataLocationService;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonMember;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.Date;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class UserFragment extends BaseHomeFragment {

    private static final String TAG = UserFragment.class.getSimpleName();

    public UserFragment() {
        super("我的");
    }

    @Override
    int getLayoutID() {
        return R.layout.fragment_user;
    }

    @Override
    void init(View view) {
//        super.init();
        User user = User.getInstance();
        if (AppParams.DRIVER_APP || user.getUserType() == Type.DriverType) {
//            findViewById(R.id.user_services_item).setVisibility(View.GONE);
            ViewUtils.setText(view, R.id.user_fleet_bt, "我加入的车队");
            view.findViewById(R.id.user_tender_item).setVisibility(View.GONE);
            view.findViewById(R.id.user_shop_setting_bt).setVisibility(View.GONE);


        } else if (user.getUserType() == Type.EnterpriseType) {
            view.findViewById(R.id.user_services_item).setVisibility(View.GONE);
//            view.findViewById(R.id.user_fleet_item).setVisibility(View.GONE);
            view.findViewById(R.id.user_shop_setting_bt).setVisibility(View.GONE);
            view.findViewById(R.id.user_tender_item).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.user_tender_item).setVisibility(View.GONE);
        }
        //更新用户信息
        if (user.isLogin()) {
            if (user.isSave() && !TextUtils.isEmpty(user.getPassword())) {
                //如果保存了密码 则自动登录
                request_Login(user.getPhoneNum(), user.getPassword());
            } else {
                //更新用户信息；
                User.getInstance().requestUserInfo();
            }
//            User.getInstance().requestPayStatus();

            //启动服务 上传坐标 仅限司机用户
            if (user.getUserType() == Type.DriverType) {
                Log.i(TAG, "启动位置上传服务");
                Intent intent = new Intent(getActivity(), UpDataLocationService.class);
                getActivity().startService(intent);
            } else {
                getHomeActivity().getApp().stopLocationServer();
            }
        }

        if (user.getUserType() != Type.EmptyType)
            if (AppParams.DRIVER_APP ^ user.getUserType() == Type.DriverType) {
//                getHomeActivity().showToast("登录用户与客户端类型不统一");
                ConfirmDialog.showConfirmDialog(getActivity(), "登录用户与客户端类型不统一,请重新启动APP", null);
            }

    }

    @Override
    public void onStart() {
        super.onStart();
        initView();


    }

    // 发起登录请求
    private void request_Login(String username, String password) {

        JsonUser user = new JsonUser();
//        user.setName(username);
        user.setPhoneNumber(username);
        user.setPassword(password);

        HttpHelper.getInstance().post(AppURL.Login, user, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                getHomeActivity().showToast("自动登录失败：" + str);
            }

            @Override
            public void onSuccess(String msg, String jsonstr) {
                if (TextUtils.isEmpty(jsonstr)) {
                    getHomeActivity().showToast("自动登录失败：" + msg);
                    return;
                }
                ResponseLogin json = ParseJson_Object(jsonstr, ResponseLogin.class);
                User.getInstance().setLoginResponse(json);
//                JsonUser jsonUser = ParseJson_Object(jsonstr, JsonUser.class);
                if (AppParams.DRIVER_APP) {
                    if (User.getInstance().getUserType() != Type.DriverType) {
                        getHomeActivity().showToast("不是司机用户");
                        return;
                    }
                }
                getHomeActivity().showToast("自动登录成功");

            }

        });

    }


    private void initView() {
        TextView nickTx = (TextView) findViewById(R.id.user_name_tx);
        nickTx.setText(User.getInstance().getUserName());
        TextView name = (TextView) findViewById(R.id.user_phone_tx);
        name.setText(User.getInstance().getPhoneNum());


        if (User.getInstance().isLogin()) {
            JsonUser jsonUser = User.getInstance().getJsonUser();
            ViewUtils.setImageUrl((ImageView) findViewById(R.id.user_head_img), jsonUser.getPortraitUrl());


            Date registerDate = jsonUser.getRegisterDate();
            TextView regdate = (TextView) findViewById(R.id.user_reg_date_tx);
//            if (AppParams.DEBUG) {
            ViewUtils.setText(regdate, String.format(getString(R.string.app_version_tx), getHomeActivity().getApp().getVersionName()));
//            } else {
//                if (registerDate != null) {
//                    regdate.setText(Tools.toStringDate(registerDate, "yyyy-MM-dd"));
//                } else {
//                    regdate.setVisibility(View.GONE);
//                }
//            }

            //跟新用户审核状态
            User.getInstance().requestUserInfo();
            //更新用户的支付状态
            if (User.getInstance().getUserType() == Type.DriverType) {
                //刷新用户的会员有效期 显示
                refreshPayStatus();
                User.getInstance().requestPayStatus(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                refreshPayStatus();
                            }
                        });
                        return false;
                    }
                });
            } else {
                findViewById(R.id.user_expire_date_tx).setVisibility(View.GONE);
            }

        }
    }

    private void refreshPayStatus() {
        TextView expireTx = (TextView) findViewById(R.id.user_expire_date_tx);
        JsonMember payJson = User.getInstance().getPayJson();
        if (payJson != null) {
            if (payJson.getExpireTime() != null) {
                ViewUtils.setText(expireTx, String.format(getString(R.string.user_expire_str), Tools.toStringDate(payJson.getExpireTime())));
                return;
            }
        }
        expireTx.setVisibility(View.GONE);
    }

    /*刷新 页面上的信息 在UI线程中*/
    private void refreshView() {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
    }

}
