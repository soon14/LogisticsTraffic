package com.bt.zhangzy.logisticstraffic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * Created by ZhangZy on 2015/7/13.
 */
public class AdViewFlipper extends ViewFlipper implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private GestureDetector detector;

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
}
