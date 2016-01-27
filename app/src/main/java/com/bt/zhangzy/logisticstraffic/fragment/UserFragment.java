package com.bt.zhangzy.logisticstraffic.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonEnterprise;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseUserInfo;

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
    void init() {
        super.init();
        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            findViewById(R.id.user_services_item).setVisibility(View.GONE);
            findViewById(R.id.user_fleet_item).setVisibility(View.GONE);
        } else if (AppParams.DEVICES_APP) {
//            findViewById(R.id.user_services_item).setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initView();

        if (User.getInstance().getLogin()) {
            requestUserInfo();
        }
        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            requestEnterpriseInfo();
        } else if (User.getInstance().getUserType() == Type.InformationType) {
            requestCompaniesInfo();
        } else if (User.getInstance().getUserType() == Type.DriverType) {
            requestDriverInfo();
        }
    }

    private void requestUserInfo() {
        //如果登陆成功  更新用户的基本信息；
        HttpHelper.getInstance().get(Url.GetUserInfo + User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                if (TextUtils.isEmpty(result)) {
                    Log.i(TAG, "用户信息更新失败：" + msg);
                    return;
                }
                Log.i(TAG, "用户信息更新成功");
                JsonUser jsonUser = ParseJson_Object(result, JsonUser.class);
                User user = User.getInstance();
//                    user.setLogin(true);
//                    showToast(JSON.toJSONString(jsonUser));
                user.setId(jsonUser.getId());
                user.setUserName(jsonUser.getName());
                user.setPhoneNum(jsonUser.getPhoneNumber());
                user.setNickName(jsonUser.getNickname());
                user.setJsonUser(jsonUser);
                switch (jsonUser.getRole()) {
                    case 1:
                        user.setUserType(Type.DriverType);
                        break;
                    case 2:
                        user.setUserType(Type.EnterpriseType);
                        break;
                    case 3:
                        user.setUserType(Type.InformationType);
                        break;
                }

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    private void initView() {
        TextView nickTx = (TextView) findViewById(R.id.user_nick_tx);
        nickTx.setText(User.getInstance().getNickName());
        TextView name = (TextView) findViewById(R.id.user_name_tx);
        name.setText(User.getInstance().getUserName());
    }

    /*刷新 页面上的信息 在UI线程中*/
    private void refreshView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
    }

    //更新司机信息
    private void requestDriverInfo() {
        HttpHelper.getInstance().get(Url.GetDriverInfo + User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "司机信息更新成功：" + msg);
                JsonDriver json = ParseJson_Object(result, JsonDriver.class);
                User user = User.getInstance();
                user.setDriverID(json.getId());
//                user.setUserName(json.getName());
//                user.setAddress(json.getAddress());
                user.setJsonTypeEntity(json);

                refreshView();
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "司机信息更新失败：" + str);
            }
        });
    }

    //更新企业信息
    private void requestEnterpriseInfo() {

        HttpHelper.getInstance().get(Url.GetEnterprisesInfo + User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                ResponseUserInfo json = ParseJson_Object(result, ResponseUserInfo.class);
                User user = User.getInstance();
                user.setEnterpriseID(json.getEnterprise().getId());
                user.setUserName(json.getEnterprise().getName());
                user.setAddress(json.getEnterprise().getAddress());
                user.setJsonTypeEntity(json.getEnterprise());

                refreshView();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    //更新物流公司信息
    private void requestCompaniesInfo() {
        HttpHelper.getInstance().get(Url.GetCompaniesInfo + User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                ResponseUserInfo json = ParseJson_Object(result, ResponseUserInfo.class);
                User user = User.getInstance();

                user.setCompanyID(json.getCompany().getId());
                user.setUserName(json.getCompany().getName());
                user.setAddress(json.getCompany().getAddress());
                user.setJsonTypeEntity(json.getCompany());

                List<JsonMotorcades> motorcades = json.getMotorcades();
                user.setMotorcades(motorcades);

                refreshView();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }
}
