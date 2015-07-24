package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.view.BaseDialog;
import com.bt.zhangzy.logisticstraffic.view.LicenceKeyboardPopupWindow;

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
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_publish,null);
        setContentView(contentView);
        setPageName("发布车辆");
        licenceEd = (EditText) findViewById(R.id.publish_licence_ed);
    }

    public void onClick_Reset(View view) {
        //重置按钮
    }

    public void onClick_ShowLicence(View view) {
        //虚拟键盘
        LicenceKeyboardPopupWindow.create(this).setListener(new LicenceKeyboardPopupWindow.ConfirmListener() {
            @Override
            public void confirm(String string) {
                licenceEd.setText(string);
            }
        }).showAsDropDown(view,0,-view.getHeight());

    }

    public void onClick_Publish(View view) {
        showToast("发布成功");
        finish();
    }
}
