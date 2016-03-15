package com.bt.zhangzy.logisticstraffic.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bt.zhangzy.logisticstraffic.d.R;

/**
 * 悬浮窗功能实现  通过静态方法createView创建
 * <p/>
 * Created by ZhangZy on 2015/6/10.
 */
public class FloatView extends ImageView {
    private static final String TAG = FloatView.class.getSimpleName();
    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private float mStartX;
    private float mStartY;
    private OnClickListener mClickListener;
    private WindowManager windowManager;//= (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    // 此windowManagerParams变量为获取的全局变量，用以保存悬浮窗口的属性
    private static WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams();
    //屏幕大小
    private int screenWidth;
    private int screenHeight;
    //    private static WindowManager.LayoutParams windowManagerParams;
    private long lastTouchTime;
    private Handler updateWindow = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            if (System.currentTimeMillis() - lastTouchTime > 3000)
                alpha = 150;
                getDrawable().setAlpha(alpha);
        }
    };

    public FloatView(Context context) {
        super(context);
        updateWindow.sendEmptyMessageDelayed(0, 3000);
    }

    public WindowManager.LayoutParams getWindowManagerParams() {
        return windowManagerParams;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
        getScreenParam();
    }

    public static FloatView CreateView(Context context, OnClickListener clickListener) {
        if (context == null)
            return null;
        FloatView floatView = new FloatView(context);
        floatView.setOnClickListener(clickListener);
        floatView.setImageResource(R.drawable.float_view_left_icon);
        // 设置LayoutParams(全局变量）相关参数
//        windowManagerParams = ((LogisticsTrafficApplication) getApplication()).getWindowParams();
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    /*
    * 注意，flag的值可以为：
	* LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
	* LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦
	* LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
	*/
        // 调整悬浮窗口至左上角，便于调整坐标
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        windowManagerParams.x = 0;
        windowManagerParams.y = 0;
        // 设置悬浮窗口长宽数据
        windowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return floatView;
    }

    long action_down_time;
    int alpha  = 255;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastTouchTime = System.currentTimeMillis();
        if (alpha == 150) {
            alpha = 255;
            getDrawable().setAlpha(alpha);
        }
        //获取到状态栏的高度
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        //        System.out.println("statusBarHeight:" + statusBarHeight);
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - statusBarHeight; // statusBarHeight是系统状态栏的高度
        //        Log.i("tag", "currX" + x + "====currY" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                action_down_time = System.currentTimeMillis();
                // 获取相对View的坐标，即以此View左上角为原点
                mTouchX = event.getX();
                mTouchY = event.getY();
                mStartX = x;
                mStartY = y;
                //                Log.i("tag", "startX" + mTouchX + "====startY" + mTouchY);
                break;
            case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                updateViewPosition();
                mTouchX = mTouchY = 0;
                if ((x - mStartX) < 5 && (y - mStartY) < 5 && Math.abs(System.currentTimeMillis() - action_down_time) < 500) {
                    if (mClickListener != null) {
                        mClickListener.onClick(this);
                    }
                }
                //停靠逻辑
                locViewPosition();
                updateWindow.sendEmptyMessageDelayed(0, 3000);
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }


    private void updateViewPosition() {
        if (resID == R.drawable.float_view_left_icon || resID == R.drawable.float_view_l_icon) {
            setImageResource(R.drawable.float_view_l_icon);
        } else {
            setImageResource(R.drawable.float_view_icon);
        }
// 更新浮动窗口位置参数
        windowManagerParams.x = (int) (x - mTouchX);
        windowManagerParams.y = (int) (y - mTouchY);
        if (windowManager != null)
            windowManager.updateViewLayout(this, windowManagerParams); // 刷新显示
    }

    private void locViewPosition() {
        Log.d(TAG, "停靠逻辑");
        if (screenWidth <= 0 || screenHeight <= 0)
            return;
        int locSize = 200;
        int half_x = windowManagerParams.x + getWidth() / 2;
        int half_y = windowManagerParams.y + getHeight() / 2;
        /*if (half_y > screenHeight - locSize) {
            //bottom
            windowManagerParams.y = screenHeight;
            setImageResource(R.drawable.float_view_bottom_icon);
        } else*/
        if (half_x < screenWidth / 2) {
            //left
            windowManagerParams.x = 0;
            setImageResource(R.drawable.float_view_left_icon);
        } else if (half_x > screenWidth / 2) {
            //right
            windowManagerParams.x = screenWidth;
            setImageResource(R.drawable.float_view_right_icon);
        }
        if (half_y > screenHeight - getHeight() * 2) {
            windowManagerParams.y = screenHeight - getHeight() * 2;
        }
        if (windowManager != null)
            windowManager.updateViewLayout(this, windowManagerParams); // 刷新显示
    }

    //获取屏幕大小
    public void getScreenParam() {
        DisplayMetrics display = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(display);
//        String string = "\n		分辨率为：" + display.widthPixels + " × " + display.heightPixels;
        screenWidth = display.widthPixels;
        screenHeight = display.heightPixels;

    }

    private int resID;

    @Override
    public void setImageResource(int resId) {
        if (resId == resID)
            return;

        resID = resId;
        super.setImageResource(resId);
    }
}
