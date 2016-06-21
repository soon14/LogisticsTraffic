package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.tools.Tools;

/**
 * Created by ZhangZy on 2016-6-21.
 */
public class SetPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        setPageName("修改密码");


    }

    public void onClick_Submit(View view) {
        //确认修改密码
        String old_password = getStringFromTextView(R.id.set_password_before);
        String new_password = getStringFromTextView(R.id.set_password_new);
        String again_password = getStringFromTextView(R.id.set_password_again);
        if (Tools.isEmptyStrings(old_password, new_password, again_password)) {
            showToast("输入为空");
            return;
        }
        if (!User.getInstance().getPassword().equals(Tools.MD5(old_password))) {
            showToast("原始密码输入错误");
            return;
        }
        if (!new_password.equals(again_password)) {
            showToast("输入密码不一致");
            return;
        }
        setPassword(new_password);

    }

    private void setPassword(final String password) {
        JsonUser jsonUser = User.getInstance().getJsonUser();
//        jsonUser.setPortraitUrl(url);
        jsonUser.setPassword(Tools.MD5(Tools.MD5(password)));

        HttpHelper.getInstance().put(AppURL.PutUserInfo, String.valueOf(jsonUser.getId()), jsonUser, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("密码修改失败");
            }

            @Override
            public void onSuccess(String msg, String result) {
                showToast("密码修改成功");
//                JsonUser user = ParseJson_Object(result, JsonUser.class);
                if (User.getInstance().isSave()) {
                    User.getInstance().setPassword(Tools.MD5(password));
                }
                finish();
            }
        });
    }
}
