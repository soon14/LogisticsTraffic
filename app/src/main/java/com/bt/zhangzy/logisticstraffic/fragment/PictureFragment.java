package com.bt.zhangzy.logisticstraffic.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.view.ZoomImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by ZhangZy on 2016-3-9.
 */
public class PictureFragment extends Fragment {
    private View contentView;

    private ZoomImageView zoomImageView;
    //    private Bitmap bitmap;
    String url;

    public PictureFragment() {
        super();
    }

//    public PictureFragment(String url) {
//        super();
////        this.bitmap = bitmap;
//        this.url = url;
//    }


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_picture, null, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_picture, null, false);
        }
        zoomImageView = (ZoomImageView) contentView.findViewById(R.id.picture_img);
        return contentView;
//            return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        if (zoomImageView == null)
            return;

        if (TextUtils.isEmpty(url)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fake_detail_1);
            zoomImageView.setImageBitmap(bitmap);
//                zoomImageView.setImageResource(R.drawable.fake_detail_1);
        } else {
//                ViewUtils.setImageUrl(zoomImageView, list.get(0));
//            String imageUrl = list.get(0);
//                ImageView imageView = zoomImageView;
//                ImageLoader.getInstance().displayImage(imageUrl, imageView, ImageHelper.getInstance().options);
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
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
    }
}
