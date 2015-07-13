package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/5.
 */
public class WelcomeActivity extends BaseActivity {

    private ViewPager viewPager;
    private ViewFlipper viewFlipper;
    private Animation fromDownIn;
    private Animation fromUpOut;
    private float startY;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

//        initViewPager();
        initViewFlipper();

        setNextButtonVisible(View.INVISIBLE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null && viewPager.getAdapter() != null) {
            if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                setNextButtonVisible(View.VISIBLE);
            } else {
                setNextButtonVisible(View.INVISIBLE);
            }
        }
    }

    private void setNextButtonVisible(int invisible) {
       /* View tmp_v = findViewById(R.id.welcome_next_bt);
        if (tmp_v != null) {
            tmp_v.setVisibility(invisible);
        }*/
    }

    public void nextActivityBtn(View view) {
//        startActivity(new Intent(this, LocationActivity.class));
        startActivity(LocationActivity.class);
        finish();
    }

    private void initViewFlipper() {
        viewFlipper = (ViewFlipper) findViewById(R.id.welcome_flipper);
        // 从下面进入，从上面退出 动画XML
        fromDownIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        fromUpOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
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

                if (e1.getY() - e2.getY() > 100) { // 向上滑动

                    viewFlipper.setInAnimation(fromDownIn);
                    viewFlipper.setOutAnimation(fromUpOut);
                    if (viewFlipper.getDisplayedChild() < viewFlipper.getChildCount() - 1) {
                        viewFlipper.showNext();
                    } else {
//                    nextActivityBtn(null);
                    }
//                viewFlipper.showPrevious();
                } else if (e1.getY() - e2.getY() < -100) {// 向下滑动
//                viewFlipper.showPrevious();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (detector != null)
            return detector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) { // 手指按下时
            startY = event.getY();// 获取手指按下时的坐标位置
        } else if (event.getAction() == MotionEvent.ACTION_UP) { // 当手指松开时，执行这里
            float endY = event.getY(); // 获取手指松开时的坐标位置

            // 1.判断手指是向上滑动还是向下滑动
            // 2.手机屏幕的开始位置是： 最左上角是坐标位置开始 X坐标和Y坐标都是0；
            // 3.当开始坐标（手机按下） 大于 结束坐标（松开手指） 是向上滑动
            // 4.当开始坐标（手指按下）小于结束坐标（松开手指） 是向下滑动 ；

            if (startY > endY) { // 向上滑动

                viewFlipper.setInAnimation(fromDownIn);
                viewFlipper.setOutAnimation(fromUpOut);
                if (viewFlipper.getDisplayedChild() < viewFlipper.getChildCount() - 1) {
                    viewFlipper.showNext();
                } else {
//                    nextActivityBtn(null);
                }
//                viewFlipper.showPrevious();
            } else if (startY < endY) {// 向下滑动
//                viewFlipper.setInAnimation(fromUpIn);
//                viewFlipper.setOutAnimation(fromDownOut);
//                viewFlipper.showNext();
//                viewFlipper.showPrevious();
            }
            int viewFlipperCurrentDisplayViewPosition = viewFlipper.getDisplayedChild();  //获取当前显示View的位置 potsition
            System.out.println("ViewFlipper Current View : " + viewFlipperCurrentDisplayViewPosition);


//            setCurrentPoint(viewFlipperCurrentDisplayViewPosition);
        }

        return super.onTouchEvent(event);
    }

    private void initViewPager() {
//        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);

        int[] ids = {R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3};
        viewPager.setAdapter(new mPagerAdapter(ids));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (viewPager == null || viewPager.getAdapter() == null)
                    return;

                if (position == viewPager.getAdapter().getCount() - 1) {
                    setNextButtonVisible(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    class mPagerAdapter extends android.support.v4.view.PagerAdapter {

        private final int[] ids;
        private ArrayList<View> views = new ArrayList<View>();

        public mPagerAdapter(int[] ids) {
            this.ids = ids;
        }

        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (container.getChildAt(position) == null) {
                FrameLayout layout = new FrameLayout(container.getContext());
                ImageView view = new ImageView(getBaseContext());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setImageResource(ids[position]);
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                layout.addView(view);
                if (position == getCount() - 1) {
                    RelativeLayout rly = new RelativeLayout(container.getContext());
                    rly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    Button btn = new Button(getBaseContext(), null, R.style.home_button_style);
                    btn.setText("立即体验");
                    btn.setTextSize(24);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    params.bottomMargin = 160;
                    rly.addView(btn, params);

                    layout.addView(rly);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nextActivityBtn(v);
                        }
                    });
                }
                container.addView(layout);
                views.add(position, layout);
                return layout;
            } else
                return container.getChildAt(position);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            if (!views.isEmpty()) {
                views.remove(object);
            }
        }
    }


}
