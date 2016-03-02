package com.bt.zhangzy.logisticstraffic.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.view.ZoomImageView;

/**
 * 图片展示页 有缩放功能
 * Created by ZhangZy on 2015/7/7.
 */
public class PictureActivity extends BaseActivity {


    private ZoomImageView zoomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture);
        zoomImageView = (ZoomImageView) findViewById(R.id.picture_img);
//        String imagePath = getIntent().getStringExtra("image_path");
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        /* Intent intent = new Intent(getContext(), ImageDetailsActivity.class);
                intent.putExtra("image_path", getImagePath(mImageUrl));
                getContext().startActivity(intent);  */

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fake_detail_1);
        zoomImageView.setImageBitmap(bitmap);


    }
}
