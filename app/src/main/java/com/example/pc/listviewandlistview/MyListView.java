package com.example.pc.listviewandlistview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
        int count = getChildCount();
        Log.e("xhc","加载完毕----"+count );
        for(int i = 0 ;i < count ; ++ i){
            View view = getChildAt(i);
            if(view instanceof  ExpandableListView){
                Log.e("xhc","---zheil ---expandlistview");
                expandableListView = (ExpandableListView)view;

            }
            else if(view instanceof  ListView){
                Log.e("xhc","--这里---listview");
                listView = (ListView) view;
            }
        }




    }


    private int headHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int view2Height = 0;
        int count = getChildCount();
        headHeight = 0;
        for (int i = 0; i < count; ++i) {
            View view = getChildAt(i);

            measureChildWithMargins(view, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
            if (i == count - 2) {
                view2Height = view.getMeasuredHeight();
            }
            if (view instanceof ExpandableListView) {
                ExpandableListView listView = (ExpandableListView) view;
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, heightSize /*- view2Height*/);
                listView.setLayoutParams(params);
            }
        }

        for (int i = 0; i < count - 1  ; ++i) {
            View view = getChildAt(i);
            headHeight += view.getMeasuredHeight();
        }
        Log.e("xhc","child count "+getChildCount());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int direction = -1;
    private int UP = 0 ;
    private int DOWN = 2 ;
    private boolean verticalScrollFlag = false;   //是否允许垂直滚动
    private float mDownX;  //第一次按下的x坐标
    private float mDownY;  //第一次按下的y坐标
    private float mLastY;  //最后一次移动的Y坐标
    private VelocityTracker mVelocityTracker;
    private int mLastScrollerY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();                   //当前手指相对于当前view的X坐标
        float currentY = ev.getY();
        float shiftX = Math.abs(currentX - mDownX);   //当前触摸位置与第一次按下位置的X偏移量
        float shiftY = Math.abs(currentY - mDownY);   //当前触摸位置与第一次按下位置的Y偏移量
        float deltaY;
        obtainVelocityTracker(ev);
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
                    if (deltaY > 0 && listViewIsBottom()) {
                        //向上滑
                        direction = UP;
                        scrollBy(0, (int) (deltaY + 0.5));
                        invalidate();
                    } else if (deltaY <= 0 && expandListViewIsTop()) {
                        //向下滑
                        direction = DOWN;
                        scrollBy(0, (int) (deltaY + 0.5));
                        invalidate();
                    }

                }
                Log.e("xhc","scroll Y "+getScrollY());
                break;
            case MotionEvent.ACTION_UP:
                if(verticalScrollFlag){
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity); //1000表示单位，每1000毫秒允许滑过的最大距离是mMaximumVelocity
                    float yVelocity = mVelocityTracker.getYVelocity();  //获取当前的滑动速度
                    direction = yVelocity > 0 ? DOWN : UP;  //下滑速度大于0，上滑速度小于0
                    Log.e("xhc", "速度-> " + yVelocity);
                    scroller.fling(0, getScrollY(), 0, -(int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);

                    invalidate();  //更新界面，该行代码会导致computeScroll中的代码执行


//                    if ((shiftX > mTouchSlop || shiftY > mTouchSlop)) {
//                            int action = ev.getAction();
//                            ev.setAction(MotionEvent.ACTION_CANCEL);
//                            boolean dd = super.dispatchTouchEvent(ev);
//                            ev.setAction(action);
//                            return dd;
//                    }
                }
                mLastScrollerY = getScrollY();


                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    public void fling(int velocityY, int distance, int duration){
        scroller.fling(0, getScrollY(), 0, velocityY, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    @Override
    public void computeScroll() {

        //滑动的动画还没结束
        if(scroller.computeScrollOffset()){
            final int currY = scroller.getCurrY();
//            Log.e("xhc","curry "+currY + direction);
            if(direction == UP){
                //向上滑动
                if(!listViewIsBottom()){return ;}
                else if(!headGone()){
                    //头部还没有滑动完
                    Log.e("xhc"," computeScroll mLastScrollerY "+mLastScrollerY +" curry "+currY);
                    int deltaY = (currY - mLastScrollerY);
                    deltaY /= 1.5;
                    Log.e("xhc","--deltaY---"+deltaY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);  //将外层布局滚动到指定位置
                    invalidate();        //移动完后刷新界面
                }
                else{
                    //头部已经滑动完了,然后就可以滑动expandlistview了

                    scroller.abortAnimation();
                    int distance = scroller.getFinalY() - currY; //剩余的距离
                    int duration = calcDuration(scroller.getDuration(), scroller.timePassed()); //除去布局滚动的距离后，剩余的距离
                    expandableListView.smoothScrollBy(distance, duration);


                }

            }
            else if(direction == DOWN){
                //向下滑动
                if(!expandListViewIsTop()){
                    //如果expandlistview自己还可以滑动，就滑动自己
                    int distance = scroller.getFinalY() - currY; //剩余的距离
                    int duration = calcDuration(scroller.getDuration(), scroller.timePassed()); //除去布局滚动的距离后，剩余的距离
                    expandableListView.smoothScrollBy(distance, duration);
                }
                else{
                    //头部在完全在屏幕外 , 或者部分在屏幕外
                    int deltaY = (currY - mLastScrollerY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);
                    invalidate();

                }

            }
            mLastScrollerY = currY;
        }

    }



    //速度
    private int getScrollerVelocity(int distance, int duration) {
        if (scroller == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) scroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }
    private int calcDuration(int duration, int timepass) {
        return duration - timepass;
    }
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {

            mVelocityTracker.recycle();
            mVelocityTracker = null;

        }
    }

    //判断这个控件是否滑动到了顶部
    public boolean isTop(){
        return getScrollY() <= 0;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }


    /**
     * 判断listview是否滑动到底部了
     *
     * @return
     */
    private boolean listViewIsBottom() {
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastVisiblePosition >= listView.getCount() - 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断expandlistview是否滑动到了顶部
     *
     * @return
     */
    private boolean expandListViewIsTop() {

        if (expandableListView.getFirstVisiblePosition() <= 0) {
            return true;
        }

        return false;
    }

    private int currY ;

    //头部是否已经隐藏了
    private boolean headGone(){
        return currY >= headHeight;
    }



    @Override
    public void scrollTo(int x, int y) {
        if(!listView.isShown() || !expandableListView.isShown()){
            return ;
        }


        if(y <= 0){
            y = 0 ;
        }
        else if(y >= headHeight){
            y = headHeight;
        }
        currY = y;

        super.scrollTo(x, y);
    }



    @Override
    public void scrollBy(int x, int y) {


        int sY = getScrollY();
        int toY = sY + y;
        Log.e("xhc"," head height "+headHeight+" to Y "+toY);

        if (toY <= 0) {
            //预防向下滑动超出边界
            toY = 0;

        } else if (toY >= headHeight) {

            toY = headHeight;
        }
        y = toY - sY;
        super.scrollBy(x, y);
    }
}




























