package com.bt.zhangzy.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * Universal Image Loader 工具类的封装
 * Created by ZhangZy on 2016-1-14.
 */
public class ImageHelper {

    static ImageHelper instance = new ImageHelper();

    private ImageHelper() {
    }

    public static ImageHelper getInstance() {
        return instance;
    }

    /*
    * 参考网址：http://blog.csdn.net/xiaanming/article/details/26810303
    * http://blog.csdn.net/vipzjyno1/article/details/23206387
    * */

    //显示图片的配置
    final DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.ic_stub) // resource or drawablee
//            .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//            .showImageOnFail(R.drawable.ic_error) // resource or drawable
            .cacheInMemory(true)
//            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public void init(Context ctx) {
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(ctx.getApplicationContext());
        //创建默认的ImageLoader配置参数
        /*ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs() //打印log信息
                .build();
        */

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

    }

    /**
     * 设置Activity中ImageView的图片地址；
     *
     * @param act
     * @param imgId
     * @param url
     */
    public void load(Activity act, int imgId, String url) {
        if (act != null) {
            View view = act.findViewById(imgId);
            if (view instanceof ImageView) {
                load(url, (ImageView) view);
            }
        }
    }

    /**
     * 加载网络图片 在Ui线程中
     *
     * @param url
     * @param imageView
     */
    public void loadImgOnUiThread(Activity act, final String url, final ImageView imageView) {
        if (TextUtils.isEmpty(url) || imageView == null)
            return;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageHelper.getInstance().load(url, imageView);
            }
        });

    }


    /**
     * 加载网络图片
     *
     * @param imageUrl
     * @param imageView
     */
    public void load(String imageUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
    }

    /**
     * 加载本地资源
     *
     * @param imagePath
     * @param imageView
     */
    public void loadFile(String imagePath, ImageView imageView) {

//        String imagePath = "/mnt/sdcard/image.png";
        String imageUrl = ImageDownloader.Scheme.FILE.wrap(imagePath);
        load(imageUrl, imageView);
    }

    /**
     * 加载assets资源
     *
     * @param path
     * @param imageView
     */
    public void loadAssets(String path, ImageView imageView) {
        //图片来源于assets   例：image.png
        String assetsUrl = ImageDownloader.Scheme.ASSETS.wrap(path);
        load(assetsUrl, imageView);

    }

    /**
     * 加载drawable资源
     *
     * @param path
     * @param imageView
     */
    public void loadDrawable(String path, ImageView imageView) {
        //图片来源于  例：R.drawable.image
        String drawableUrl = ImageDownloader.Scheme.DRAWABLE.wrap(path);
        load(drawableUrl, imageView);
    }

}
