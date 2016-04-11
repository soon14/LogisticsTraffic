package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeFragmentPagerAdapter;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.fragment.PictureFragment;

import java.util.ArrayList;

/**
 * 废弃 2016年4月8日
 * 图片展示页 有缩放功能
 * Created by ZhangZy on 2015/7/7.
 */
@Deprecated
public class PictureActivity extends BaseActivity {


    private ViewPager contentViewPager;
    private ArrayList<String> list;
//    private ZoomImageView zoomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.fragment_picture);
        setContentView(R.layout.activity_zoom_picture);
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(AppParams.BUNDLE_PICTURE_ARRAY)) {
//                imgUlr = bundle.getString(AppParams.BUNDLE_PICTURE_URL);
                list = bundle.getStringArrayList(AppParams.BUNDLE_PICTURE_ARRAY);
            }
        }
        contentViewPager = (ViewPager) findViewById(R.id.picture_content_pager);
        if (list != null) {
            ArrayList<Fragment> fragments = new ArrayList<Fragment>();
            for (String string : list) {
                PictureFragment fragment = new PictureFragment();
                fragment.setUrl(string);
                fragments.add(fragment);
            }
            contentViewPager.setAdapter(new HomeFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        }
        /*zoomImageView = (ZoomImageView) findViewById(R.id.picture_img);
        if (list != null) {
            if (TextUtils.isEmpty(list.get(0))) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fake_detail_1);
                zoomImageView.setImageBitmap(bitmap);
//                zoomImageView.setImageResource(R.drawable.fake_detail_1);
            } else {
//                ViewUtils.setImageUrl(zoomImageView, list.get(0));
                String imageUrl = list.get(0);
//                ImageView imageView = zoomImageView;
//                ImageLoader.getInstance().displayImage(imageUrl, imageView, ImageHelper.getInstance().options);
                ImageLoader.getInstance().loadImage(imageUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        zoomImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }*/
//        String imagePath = getIntent().getStringExtra("image_path");
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        /* Intent intent = new Intent(getContext(), ImageDetailsActivity.class);
                intent.putExtra("image_path", getImagePath(mImageUrl));
                getContext().startActivity(intent);  */


    }
}
