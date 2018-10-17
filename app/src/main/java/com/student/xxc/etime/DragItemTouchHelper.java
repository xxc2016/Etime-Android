package com.student.xxc.etime;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.student.xxc.etime.entity.Trace;

import java.util.Collections;
import java.util.List;
/*
长按拖拽点击事件
 */
public class DragItemTouchHelper {

    private static ItemTouchHelper helper;

    public static void setItemTouchHelper(final RecyclerView.Adapter alphaAdapter, final List<Trace> traceList) {
        helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//设置滑动时间方向
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolderto) {//拖拽过程中
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = viewHolderto.getAdapterPosition();
                if(fromPos<toPos){
                    for(int i=fromPos;i<toPos;i++){
                        Collections.swap(traceList,i,i+1);
                    }
                }
                if(fromPos>toPos){
                    for(int i=fromPos;i>toPos;i--){
                        Collections.swap(traceList,i,i-1);
                    }
                }
                alphaAdapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {//拖拽后

            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {//选中时特效
//                if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE)
//                    viewHolder.itemView.findViewById(R.id.card).setBackgroundColor(Color.GRAY);
                super.onSelectedChanged(viewHolder, actionState);

            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//松手时特效
//                viewHolder.itemView.findViewById(R.id.card);
                super.clearView(recyclerView, viewHolder);
            }
        });
    }

    public static ItemTouchHelper getHelper() {
        return helper;
    }
}
