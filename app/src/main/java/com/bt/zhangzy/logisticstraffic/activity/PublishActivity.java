package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.LicenceKeyboardPopupWindow;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.RequestAddCar;

/**
 * 发布车源信息
 * Created by ZhangZy on 2015/7/23.
 */
public class PublishActivity extends BaseActivity {

    EditText licenceEd;
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_publish, null);
        setContentView(contentView);
        setPageName("发布车辆");
        licenceEd = (EditText) findViewById(R.id.publish_licence_ed);

        initView();
    }

    private void initView(){
        User user = User.getInstance();
        JsonDriver driver = user.getJsonTypeEntity();

    }

    public void onClick_Reset(View view) {
        //重置按钮
        final int[] ids = {R.id.publish_address_ed, R.id.publish_length_ed, R.id.publish_licence_ed, R.id.publish_status_ed, R.id.publish_type_ed, R.id.publish_weight_ed};
        View tmpV;
        for (int id : ids) {
            tmpV = findViewById(id);
            if (tmpV != null && tmpV instanceof EditText) {
                ((EditText) tmpV).setText("");
            }
        }
    }

    public void onClick_ShowLicence(View view) {
        //虚拟键盘
        LicenceKeyboardPopupWindow.create(this).setListener(new LicenceKeyboardPopupWindow.ConfirmListener() {
            @Override
            public void confirm(String string) {
                licenceEd.setText(string);

            }
        }).showAsDropDown(view, 0, -view.getHeight());

    }

    public void onClick_Publish(View view) {
        if (TextUtils.isEmpty(licenceEd.getText())) {
            showToast("请输入车牌号");
            return;
        }
        requestCarJson.setNumber(licenceEd.getText().toString().trim());
        requestPublishCar();

    }

    JsonCar requestCarJson = new JsonCar();

    private void requestPublishCar() {
        RequestAddCar requestJson = new RequestAddCar();
        JsonDriver jsonDriver = User.getInstance().getJsonTypeEntity();
        requestJson.setDriver(jsonDriver);
        requestJson.setCar(requestCarJson);
        requestCarJson.setDriverId(jsonDriver.getId());

        HttpHelper.getInstance().post(AppURL.PostDriversAddCar, requestJson, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("发布成功");
//                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("发布失败");
            }
        });
    }
}
