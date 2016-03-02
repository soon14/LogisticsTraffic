package com.bt.zhangzy.logisticstraffic.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.service.UpDataLocationService;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;
import com.bt.zhangzy.network.entity.ResponseUserInfo;
import com.bt.zhangzy.tools.Tools;

import java.util.Date;
import java.util.List;

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
        if (user.getUserType() == Type.EnterpriseType) {
            view.findViewById(R.id.user_services_item).setVisibility(View.GONE);
            view.findViewById(R.id.user_fleet_item).setVisibility(View.GONE);
        } else if (AppParams.DRIVER_APP) {
//            findViewById(R.id.user_services_item).setVisibility(View.GONE);
        }
        //更新用户信息
        if (user.getLogin()) {
            if (user.isSave() && !TextUtils.isEmpty(user.getPassword())) {
                //如果保存了密码 则自动登录
                request_Login(user.getPhoneNum(), user.getPassword());
            }else{
                //更新用户信息；
                User.getInstance().requestUserInfo();
            }

            //启动服务 上传坐标 仅限司机用户
            if (user.getUserType() == Type.DriverType) {
                Log.i(TAG, "启动位置上传服务");
                Intent intent = new Intent(getActivity(), UpDataLocationService.class);
                getActivity().startService(intent);
            }
        }

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

        if (User.getInstance().getLogin()) {
            Date registerDate = User.getInstance().getJsonUser().getRegisterDate();
            TextView regdate = (TextView) findViewById(R.id.user_reg_date_tx);
            if (registerDate != null) {
                regdate.setText(Tools.toStringDate(registerDate));
            } else {
                regdate.setVisibility(View.GONE);
            }
        }
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
