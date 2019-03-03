package com.student.xxc.etime.helper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class NoScrollViewPager extends ViewPager {//控制是否允许滑动的ViewPager
    private boolean isScroll = false;

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(isScroll)
        {
            return super.onInterceptTouchEvent(ev);
        }
        else
        {
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent ev)
    {
        if(isScroll)
        {
            return super.onTouchEvent(ev);
        }
        else
        {
            return true;
        }
    }

}
