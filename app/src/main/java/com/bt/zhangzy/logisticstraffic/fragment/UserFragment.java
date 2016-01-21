package com.bt.zhangzy.logisticstraffic.fragment;

import android.view.View;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;

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
        if(User.getInstance().getUserType() == Type.EnterpriseType){
            findViewById(R.id.user_services_item).setVisibility(View.GONE);
            findViewById(R.id.user_fleet_item).setVisibility(View.GONE);
        }else if(AppParams.DEVICES_APP){
//            findViewById(R.id.user_services_item).setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView(){
        TextView nickTx = (TextView) findViewById(R.id.user_nick_tx);
        nickTx.setText(User.getInstance().getNickName());
        TextView name = (TextView)findViewById(R.id.user_name_tx);
        name.setText(User.getInstance().getUserName());
    }
}
