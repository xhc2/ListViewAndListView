package com.example.pc.listviewandlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by pc on 2016/5/26.
 * 解决listview，和expandlistview同屏的问题
 */
public class MyListView extends LinearLayout {
    private Scroller scroller;
    private int mTouchSlop;         //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
    private int mMinimumVelocity;   //允许执行一个fling手势动作的最小速度值
    private int mMaximumVelocity;   //允许执行一个fling手势动作的最大速度值
    private ListView listView;
    private ExpandableListView expandableListView;

    public MyListView(Context context) {
        this(context, null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity(); //允许执行一个fling手势动作的最小速度值
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity(); //允许执行一个fling手势动作的最大速度值
        mTouchSlop = configuration.getScaledTouchSlop();   //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
        this.setOrientation(VERTICAL);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e("xhc", "加载完毕---");
        Log.e("xhc","count "+getChildCount());
        listView = (ListView) getChildAt(1);
        expandableListView = (ExpandableListView) getChildAt(3);
    }


    private int totalHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int view2Height = 0;
        int count = getChildCount();
        totalHeight = 0;
        for (int i = 0; i < count; ++i) {
            View view = getChildAt(i);

            measureChildWithMargins(view, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
            if (i == 2) {
                view2Height = view.getMeasuredHeight();
            }
            if (view instanceof ExpandableListView) {
                ExpandableListView listView = (ExpandableListView) view;
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, heightSize - view2Height);
                listView.setLayoutParams(params);
            }
        }

        for (int i = 0; i < 2; ++i) {
            View view = getChildAt(i);
            totalHeight += view.getHeight();
        }


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean verticalScrollFlag = false;   //是否允许垂直滚动
    private float mDownX;  //第一次按下的x坐标
    private float mDownY;  //第一次按下的y坐标
    private float mLastY;  //最后一次移动的Y坐标

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();                   //当前手指相对于当前view的X坐标
        float currentY = ev.getY();
        float shiftX = Math.abs(currentX - mDownX);   //当前触摸位置与第一次按下位置的X偏移量
        float shiftY = Math.abs(currentY - mDownY);   //当前触摸位置与第一次按下位置的Y偏移量
        float deltaY;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = currentX;
                mDownY = currentY;
                mLastY = currentY;
                scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                deltaY = mLastY - currentY; //连续两次进入move的偏移量
                mLastY = currentY;
                if (shiftX > mTouchSlop && shiftX > shiftY) {
                    //水平滑动
                    verticalScrollFlag = false;
                } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                    //垂直滑动
                    verticalScrollFlag = true;
                }

                if (verticalScrollFlag) {
                    //如果是向下滑，则deltaY小于0，对于scrollBy来说
                    //正值为向上和向左滑，负值为向下和向右滑，这里要注意
                    Log.e("xhc","什么情况"+ listViewIsBottom());
                    if(deltaY > 0 && listViewIsBottom()){
                        //向上滑
                        scrollBy(0, (int) (deltaY + 0.5));
                        invalidate();
                    }
                    else{
                        //向下滑

                    }

                }
                listViewIsBottom();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 判断listview是否滑动到底部了
     * @return
     */
    private boolean listViewIsBottom(){
        int lastVisiblePosition = listView.getLastVisiblePosition();
        Log.e("xhc", " lastVisiblePosition "+lastVisiblePosition+" count "+ listView.getCount());
        if(lastVisiblePosition >= listView.getCount() - 1){
            Log.e("xhc","滑动到了底部");
            return true;
        }
        return false;
    }

    /**
     * 判断expandlistview是否滑动到了顶部
     * @return
     */
    private boolean expandListViewIsTop(){

        

        return false;
    }

    @Override
    public void scrollTo(int x, int y) {
//        Log.e("xhc","scrollTo x "+x +" y "+y);
        super.scrollTo(x, y);
    }

    @Override
    public void scrollBy(int x, int y) {
        int sY = getScrollY();
        int toY = sY + y;
        if (toY <= 0) {
            //预防向下滑动超出边界
            toY = 0;

        } else if (toY >= totalHeight) {
            toY = totalHeight;
        }
        y = toY - sY;
        Log.e("xhc", "scrollBy x " + x + " y " + y + " sy " + sY + " totalHeight " + totalHeight);
        super.scrollBy(x, y);
    }
}




























