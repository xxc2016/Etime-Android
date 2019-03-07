package com.student.xxc.etime.adapter;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.student.xxc.etime.helper.WItemTouchHelperPlus;
import com.student.xxc.etime.impl.ItemTouchHelperAdapter;

public class MyItemTouchHelperCallback extends WItemTouchHelperPlus.Callback {

    private final ItemTouchHelperAdapter alphaAdapter;
    private static boolean ifmoved=false;
    private static boolean ifdel=false;
    private boolean isEnableSwipe=true;//允许滑动
    private boolean isEnableDrag=true;//允许拖动

    public boolean isEnableSwipe() {
        return isEnableSwipe;
    }

    public void setEnableSwipe(boolean enableSwipe) {
        isEnableSwipe = enableSwipe;
    }

    public boolean isEnableDrag() {
        return isEnableDrag;
    }

    public void setEnableDrag(boolean enableDrag) {
        isEnableDrag = enableDrag;
    }

    public MyItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        alphaAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        //是否可拖拽
        return isEnableDrag();
    }

    @Override
    public int getSlideViewWidth() {
        return 0;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//设置滑动时间方向
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public String getItemSlideType() {
        return null;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolderto) {
        int fromPos = viewHolder.getAdapterPosition();
        int toPos = viewHolderto.getAdapterPosition();

        ifmoved = true;
        alphaAdapter.onItemMove(fromPos, toPos);


            //对函数进行了改造   会导致动画闪烁
        Log.i("pos", "-----------------------" + "form" + fromPos + "  toPos" + toPos);

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {//左右滑动

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //仅对侧滑状态下的效果做出改变

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (viewHolder instanceof TimeLineAdapter.ViewHolder) {
                TimeLineAdapter.ViewHolder holder = (TimeLineAdapter.ViewHolder) viewHolder;
                float actionWidth = holder.getActionWidth();
                if (dX < -actionWidth) {
                    dX = -actionWidth;
                }
                if (dX > actionWidth) {
                    dX = actionWidth;
                }
                holder.finish.setTranslationX(dX);
                holder.activity.setTranslationX(dX);
                holder.del.setTranslationX(dX);

            }
            return;
        }

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {//选中时特效
        super.onSelectedChanged(viewHolder, actionState);

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//松手时特效
//                Log.i("pos",pos+"");
//                Log.i("now",viewHolder.getAdapterPosition()+"");
        if (ifmoved) {
            alphaAdapter.onItemClearView();
            ifmoved = false;
        }
        if (ifdel) {
            TimeLineAdapter.ViewHolder holder = (TimeLineAdapter.ViewHolder) viewHolder;
            holder.del.setTranslationX(0);
            holder.activity.setTranslationX(0);
            holder.finish.setTranslationX(0);
            ifdel = false;
        }
        super.clearView(recyclerView, viewHolder);
    }

    public static boolean isIfdel() {
        return ifdel;
    }

    public static void setIfdel(boolean ifdel) {
        MyItemTouchHelperCallback.ifdel = ifdel;
    }
}


