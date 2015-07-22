package com.bt.zhangzy.logisticstraffic.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.adapter.HomeListAdapter;
import com.bt.zhangzy.logisticstraffic.adapter.HomeSpreadAdapter;

/**
 * Created by ZhangZy on 2015/7/2.
 */
public class HomeFragment extends BaseHomeFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private ViewPager spreadPager;
    private ListView listView;
    private View listHeadView;
    private ViewFlipper flipper;
    //    private GestureDetector detector;
    private View topView;

    public HomeFragment() {
        super(null);

    }


    @Override
    int getLayoutID() {
        return R.layout.fragment_home;
    }


    @Override
    void init() {
        super.init();

        topView = findViewById(R.id.home_top_ly);
        topView.getBackground().setAlpha(0);
    }

    @Override
    public void onStart() {
        super.onStart();

//        initSpreadViewPager();
//
//        initViewFlipper();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initListView();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (listView != null) {
            listView.setAdapter(null);
            listView = null;
        }
    }


    @Override
    public boolean onTouchEventFragment(MotionEvent event) {
//        if(detector != null)
//            return detector.onTouchEvent(event);
        return super.onTouchEventFragment(event);
//        return detector.onTouchEvent(event);
    }

    Handler spreadHandler;
    Handler spreadDelayHandler;

    /**
     * 推荐位 初始化
     */
    private void initSpreadViewPager(View view) {
        if (view == null)
            return;
        spreadPager = (ViewPager) view.findViewById(R.id.home_spread_viewpager);
        if (spreadPager == null) {
            Log.e(getTag(), "推荐位对象为null");
            return;
        }
        spreadPager.setAdapter(new HomeSpreadAdapter());
        spreadPager.setCurrentItem(1, false);
        spreadDelayHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
                int count = spreadPager.getAdapter().getCount();
                if (msg.what == 0) {
                    spreadPager.setCurrentItem(count - 2, false);
                } else if (msg.what == 1) {
                    spreadPager.setCurrentItem(1, false);
                }
            }
        };
        spreadPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //TODO 滑动过程不流畅
                /* 循环原理：   2 0 1 2 0   */
//                Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>onPageSelected=" + position);

                int count = spreadPager.getAdapter().getCount();
                if (position == 0) {
                    spreadDelayHandler.sendEmptyMessageDelayed(0, 200);

                } else if (position == count - 1) {
//                    spreadPager.setCurrentItem(1, false);
                    spreadDelayHandler.sendEmptyMessageDelayed(1, 200);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (spreadHandler != null) {
            Log.w(getTag(), "推荐位Handler 不为空");
            return;
        }
        spreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (spreadPager != null) {
                    int nextPage = spreadPager.getCurrentItem() + 1;
                    if (nextPage >= spreadPager.getAdapter().getCount()) {
//                        nextPage = 0;
                        Log.w(TAG, "取消跳转 nextPage=" + nextPage);
                        return;
                    }
                    spreadPager.setCurrentItem(nextPage);
                    spreadHandler.sendEmptyMessageDelayed(0, 3000);
                }
            }
        };
        spreadHandler.sendEmptyMessageDelayed(0, 3000);
    }

    public int getScrollY() {
        View c = listView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }


    /**
     * 数据列表初始化
     */
    private void initListView() {
        //防止重复创建
        if (listView != null) {
            Log.w(getTag(), "数据列表初始化时 不为空");
            listView.setAdapter(null);
            listView = null;
//            return;
        }

        listView = (ListView) findViewById(R.id.home_listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.d(TAG, firstVisibleItem+","+visibleItemCount+","+totalItemCount);
                if (listView == null)
                    return;

                View v = listView.getChildAt(0);
                if (v != null) {
                    topView.getBackground().setAlpha(Math.min(200, Math.abs(getScrollY())));
//                    Log.d(TAG, "top=" + getScrollY() + " -->" + topView.getBackground().getAlpha());
                }
            }
        });
        if (listHeadView == null) {
            listHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.home_list_header, null);
        }
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(listHeadView);
        }
//        initViewFlipper(listHeadView);
        initSpreadViewPager(listHeadView);

        HomeListAdapter adapter = new HomeListAdapter();
        listView.setAdapter(adapter);
        adapter.setOnClickItemListener(new HomeListAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(getTag(), "onItemClick>>>>>");
                if (v != null)
                    if (v.getId() == R.id.list_item_ly) {
                        Log.d(getTag(), "    >>>>>点击了item" + position);
                        getHomeActivity().gotoDetail();
                    } else if (v.getId() == R.id.list_item_phone) {
                        Log.d(getTag(), "    >>>>>点击了phone" + position);
                        getHomeActivity().showDialogCallPhone("12301253326");
                    }
            }
        });
        //        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                showDialogCallPhone("12301253326");
//            }
//        });
    }
}
