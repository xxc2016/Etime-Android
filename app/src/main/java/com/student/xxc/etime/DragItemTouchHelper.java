package com.student.xxc.etime;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ViewGroup;

import com.student.xxc.etime.entity.Trace;
import com.student.xxc.etime.impl.TraceManager;

import java.util.Collections;
import java.util.List;
/*
长按拖拽点击事件
 */
public class DragItemTouchHelper {

    private static ItemTouchHelper helper;
    private static boolean ifmoved=false;

    public static void setItemTouchHelper(final RecyclerView.Adapter alphaAdapter, final List<Trace> traceList) {
        helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//设置滑动时间方向
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolderto) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = viewHolderto.getAdapterPosition();
                ifmoved=true;
                if(fromPos<toPos){
                    for(int i=fromPos;i<toPos;i++){
                        Collections.swap(traceList,i,i+1);
                        String tempTime = traceList.get(i).getTime();
                        traceList.get(i).setTime(traceList.get(i+1).getTime());
                        traceList.get(i+1).setTime(tempTime);
                        TraceManager.updateTrace(traceList.get(i));
                        TraceManager.updateTrace(traceList.get(i+1)); //交换跟新数据库内容
                    }
                }
                if(fromPos>toPos){
                    for(int i=fromPos;i>toPos;i--){
                        Collections.swap(traceList,i,i-1);
                        String tempTime = traceList.get(i).getTime();
                        traceList.get(i).setTime(traceList.get(i-1).getTime());
                        traceList.get(i-1).setTime(tempTime);
                        TraceManager.updateTrace(traceList.get(i));
                        TraceManager.updateTrace(traceList.get(i-1)); //交换跟新数据库内容
                    }
                }

                alphaAdapter.notifyItemMoved(fromPos, toPos);     //对函数进行了改造   会导致动画闪烁
                Log.i("pos","-----------------------"+"form"+fromPos+"  toPos"+toPos);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {//左右滑动

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //仅对侧滑状态下的效果做出改变
                if (actionState ==ItemTouchHelper.ACTION_STATE_SWIPE){
                    //如果dX小于等于删除方块的宽度，那么我们把该方块滑出来
                        viewHolder.itemView.scrollTo(-(int) dX,0);
                }else {
                    //拖拽状态下不做改变，需要调用父类的方法
                    super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
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
                if(ifmoved) {
                    alphaAdapter.notifyDataSetChanged();
                    ifmoved = false;
                }
                super.clearView(recyclerView, viewHolder);
            }
        });
    }

    public static ItemTouchHelper getHelper() {
        return helper;
    }


    /**
     * 获取删除方块的宽度
     */
    public static int getSlideLimitation(RecyclerView.ViewHolder viewHolder){
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }

}
