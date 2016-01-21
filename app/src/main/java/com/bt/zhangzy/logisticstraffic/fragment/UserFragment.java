package com.bt.zhangzy.logisticstraffic.fragment;

import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.Url;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonEnterprise;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class UserFragment extends BaseHomeFragment {

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
        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            requestEnterpriseInfo();
        }
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

    private void requestEnterpriseInfo() {

        HttpHelper.getInstance().get(Url.GetEnterprisesInfo + User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                JsonEnterprise json = ParseJson_Object(result, JsonEnterprise.class);
                User user = User.getInstance();
                user.setUserName(json.getName());
                user.setAddress(json.getAddress());

                refreshView();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    private void requestCompaniesInfo() {
        HttpHelper.getInstance().get(Url.GetCompaniesInfo + User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                JsonCompany json = ParseJson_Object(result, JsonCompany.class);
                User user = User.getInstance();
                user.setUserName(json.getName());
                user.setAddress(json.getAddress());

                refreshView();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }
}
