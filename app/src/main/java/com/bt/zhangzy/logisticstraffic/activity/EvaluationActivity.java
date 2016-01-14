package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.widget.RatingBar;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * Created by ZhangZy on 2015/6/24.
 */
public class EvaluationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_evaluation);

        RatingBar ratingBar = (RatingBar) findViewById(R.id.evaluation_ratingbar1);

        //监听星级改变并显示改变值
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
            }
        });

    }
}
