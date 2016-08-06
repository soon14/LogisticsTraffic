package com.bt.zhangzy.logisticstraffic.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.tools.ContextTools;
import com.bt.zhangzy.tools.ViewUtils;

/**
 * 广告轮播组件
 * Created by ZhangZy on 2015/7/13.
 */
public class AdViewFlipper extends ViewFlipper implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private GestureDetector detector;
    CallbackAd callback;

    public AdViewFlipper(Context context) {
        super(context);
    }

    public AdViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLongClickable(true);
        detector = new GestureDetector(context, this);
        setOnTouchListener(this);
    }

    /*
    ViewFlow嵌套在ViewPager事件冲突的解决
    链接地址 http://blog.csdn.net/lp8800/article/details/9978219

    解决方法的核心就是ViewGroup里的两个个方法。
onInterceptTouchEvent()；
requestDisallowInterceptTouchEvent()；
以下是android官网的对这两个个方法的一个说明：http://developer.android.com/training/gestures/viewgroup.html
说明里面其中有一段这么说的
Note that ViewGroup also provides arequestDisallowInterceptTouchEvent() method. TheViewGroup calls this method when a child does not want the parent and its ancestors to intercept touch events withonInterceptTouchEvent().
意思是说要注意ViewGroup也提供了一个requestDisallowInterceptTouchEvent()方法。当一个子控件不希望父控件去用onInterceptTouchEvent()插入触控事件处理，ViewGroup就可以调用这个方法。
ViewFlow作为ViewPager的一个子控件，何时让父控件处理何时不让父控件处理呢？
我们只要让手指在ViewFlow上的时候不让ViewPager处理事件就可以了。
    * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > 120) {//向左滑，右边显示
            setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
            setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
            showNext();
        }
        if (e1.getX() - e2.getX() < -120) {//向右滑，左边显示
            setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
            setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right));
            showPrevious();
        }
        return true;
    }

    //setOnTouchListener(this);  OnTouchListener
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    /**
     * 信息部 展示门头照片用
     *
     * @param img_url
     */
    public void addView(String img_url) {
        addView(img_url, "", "",  ContextTools.dip2px(getContext(), 200));
    }

    /**
     * 顶部广告位用
     *
     * @param img_url
     * @param click_url
     * @param label
     */
    public void addView(String img_url, String click_url, String label) {
        addView(img_url, click_url, label, ContextTools.dip2px(getContext(), 160));
    }

    public void addView(String img_url, String click_url, String label, int paramsHeight) {


        ImageView imageView = new ImageView(getContext());
        imageView.setId(imageView.hashCode());
        imageView.setTag(R.id.ad_view_tag_label, label);
        imageView.setTag(R.id.ad_view_tag_url, click_url);
        imageView.setTag(label);
//        Log.d("addView --", "tag object=" + imageView.getId());
//        Log.d("addView --", "tag=" + imageView.getTag(R.id.ad_view_tag_label) + "," + imageView.getTag(R.id.ad_view_tag_url));
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, paramsHeight);

        imageView.setLayoutParams(params);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setImageResource(R.drawable.def_cp_ad);

        addView(imageView);
        ViewUtils.setImageUrl(imageView, img_url);
//        imageView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v != null && v.getTag() != null) {
//                    if (v.getTag() instanceof CallbackAd) {
//                        CallbackAd call = (CallbackAd) v.getTag();
//                        call.onClick((String) v.getTag(R.id.ad_view_tag_label), (String) v.getTag(R.id.ad_view_tag_url));
//                    }
//                }
//            }
//        });
    }

    public void setCallback(CallbackAd callback) {
        if (callback == null)
            return;

        this.callback = callback;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    ViewFlipper flipper = (ViewFlipper) v;
                    View view = flipper.getCurrentView();
                    Log.d("on click ", "tag object=" + view.getId());
                    Log.d("on click ", "tag=" + view.getTag(R.id.ad_view_tag_label) + "," + view.getTag(R.id.ad_view_tag_url));
                    AdViewFlipper.this.callback.onClick((String) view.getTag(R.id.ad_view_tag_label), (String) view.getTag(R.id.ad_view_tag_url));
                }
            }
        });
    }

    public interface CallbackAd {
        void onClick(String label, String url);
    }

}
