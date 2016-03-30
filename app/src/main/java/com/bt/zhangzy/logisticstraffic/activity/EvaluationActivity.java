package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.JsonComment;
import com.bt.zhangzy.network.entity.JsonOrder;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class EvaluationActivity extends BaseActivity {

    JsonOrder jsonOrder;
    JsonComment jsonComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_evaluation);
        setPageName("评价订单");
        jsonComment = new JsonComment();
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(AppParams.BUNDLE_EVALUATION_ORDER)) {
                String json = bundle.getString(AppParams.BUNDLE_EVALUATION_ORDER);
                jsonOrder = BaseEntity.ParseEntity(json, JsonOrder.class);
                jsonComment.setCommentedRoleId(jsonOrder.getCompanyId());
            }
        }

        findViewById(R.id.orderlist_item_blankout).setVisibility(View.GONE);
        if (jsonOrder != null) {
            setTextView(R.id.orderlist_item_num_tx, String.valueOf(jsonOrder.getId()));
            setTextView(R.id.orderlist_item_enterprise_tx, "enterprise=" + String.valueOf(jsonOrder.getEnterpriseId()));
            setTextView(R.id.orderlist_item_company_tx, "company=" + String.valueOf(jsonOrder.getCompanyId()));
            setTextView(R.id.orderlist_item_driver_tx, jsonOrder.getReceiverName());
            setTextView(R.id.orderlist_item_driver_phone_tx, jsonOrder.getReceiverPhone());
            setTextView(R.id.orderlist_item_start_end_tx, jsonOrder.getStartCitySimple() + " - " + jsonOrder.getStopCitySimple());

        }
        RatingBar ratingBar = (RatingBar) findViewById(R.id.evaluation_ratingbar1);

        //监听星级改变并显示改变值
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                ratingBar.setRating(rating);
                jsonComment.setRate(rating);
            }
        });

        jsonComment.setRate(ratingBar.getRating());
    }

    //提交按钮
    public void onClick_SubmitEvaluation(View view) {
        String content = getStringFromTextView(R.id.evaluation_content_ed);
        if (TextUtils.isEmpty(content)) {
            showToast("评价内容为空");
            return;
        }
        jsonComment.setContent(content);
        requestComment();
    }


    private void requestComment() {
//        jsonComment = new JsonComment();
        jsonComment.setRole(User.getInstance().getUserType().toRole());
        jsonComment.setRoleId(User.getInstance().getRoleId());
        jsonComment.setCommentedRole(Type.CompanyInformationType.toRole());
//        jsonComment.setCommentedRoleId(roleId);
        HttpHelper.getInstance().post(AppURL.PostComment, jsonComment, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("评价成功");
                finish();
            }

            @Override
            public void onFailed(String str) {
                showToast("评价失败" + str);

            }
        });
    }


}
