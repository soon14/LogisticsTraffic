package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.JsonTender;
import com.bt.zhangzy.tools.ViewUtils;

/**
 * 标书信息展示  和 创建
 * Created by ZhangZy on 2016-3-15.
 */
public class TenderActivity extends BaseActivity {

    JsonTender jsonTender;
    boolean editMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender);
        setPageName("标书详情");
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(AppParams.BUNDLE_TENDER_JSON)) {
                jsonTender = BaseEntity.ParseEntity(bundle.getString(AppParams.BUNDLE_TENDER_JSON), JsonTender.class);
                findViewById(R.id.tender_name_ed).setEnabled(false);
                findViewById(R.id.tender_photo_img).setClickable(false);
                setTextView(R.id.tender_name_ed, jsonTender.getName());
                setImageUrl(R.id.tender_photo_img, jsonTender.getPictureBigUrl());
            }
            if (bundle.containsKey(AppParams.BUNDLE_TENDER_EDIT_BOOLEAN)) {
                editMode = bundle.getBoolean(AppParams.BUNDLE_TENDER_EDIT_BOOLEAN);
            }
        }
        if (editMode) {
            setPageName("添加新标书");
//            jsonTender = new JsonTender();
            findViewById(R.id.tender_name_ed).setEnabled(true);
            findViewById(R.id.tender_photo_img).setClickable(true);
        } else {
            findViewById(R.id.tender_submit_bt).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_Photo(View view) {
        if (!editMode)
            return;
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView imageView, String url) {
                ViewUtils.setImageUrl(imageView,url);
                jsonTender.setPictureBigUrl(url);
                jsonTender.setPictureSmallUrl(url);
            }
        });
    }

    public void onClick_Submit(View view) {
        String name = getStringFromTextView(R.id.tender_name_ed);
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(jsonTender.getPictureBigUrl())) {
            showToast("信息不全");
            return;
        }
        jsonTender.setName(name);
        requestAddTender();
    }

    private void requestAddTender() {
        HttpHelper.getInstance().post(AppURL.PostTendersCreate, jsonTender, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("标书创建成功");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("标书创建失败" + str);
            }
        });
    }

}
