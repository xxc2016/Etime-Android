package com.student.xxc.etime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.student.xxc.etime.view.MainActivity;
import com.student.xxc.etime.R;
import com.student.xxc.etime.view.SetTraceActivity;
import com.student.xxc.etime.entity.Trace;
import com.student.xxc.etime.impl.ItemTouchHelperAdapter;
import com.student.xxc.etime.impl.TraceManager;

import java.util.Collections;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> implements ItemTouchHelperAdapter{

    private Context context;
    private List<Trace>traces;
    private int downX;
    private int upX;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView time, event;//时间、事件
        public TextView tvDot;//图标
        public LinearLayout del;//删除按钮
        public LinearLayout activity;//活动部分
        public LinearLayout finish;//完成按钮
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=(TextView)itemView.findViewById(R.id.tvAcceptTime);
            event=(TextView)itemView.findViewById(R.id.tvAcceptStation);
            tvDot=(TextView)itemView.findViewById(R.id.tvDot);
            del = (LinearLayout)itemView.findViewById(R.id.del);
            activity=(LinearLayout)itemView.findViewById(R.id.activity);
            finish=(LinearLayout)itemView.findViewById(R.id.finish);
        }
        public float getActionWidth(){
            return del.getWidth();
        }
    }

    public TimeLineAdapter(Context text,List<Trace> races) {
        traces=races;
        context=text;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.time_line,viewGroup,false);
        return new ViewHolder((view));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
//        if (getItemViewType(position) == TYPE_TOP) {
//            // 第一行头的竖线不显示
//            viewHolder.tvTopLine.setVisibility(View.INVISIBLE);
//            // 字体颜色加深
//
//        } else if (getItemViewType(position) == TYPE_NORMAL) {
//            viewHolder.tvTopLine.setVisibility(View.VISIBLE);
//            viewHolder.time.setTextColor(0xff999999);
//            viewHolder.event.setTextColor(0xff999999);
//            viewHolder.tvDot.setBackgroundResource(R.drawable.ic_menu_send);
//        }
        viewHolder.time.setTextColor(context.getResources().getColor(R.color.colorTime));
        viewHolder.event.setTextColor(context.getResources().getColor(R.color.colorText));
        viewHolder.tvDot.setBackgroundResource(R.drawable.ic_menu_send );
        if(traces.get(position).getFinish()){
            CardView cardView=viewHolder.itemView.findViewById(R.id.card);
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorFinish));
        }

        viewHolder.time.setText(traces.get(position).getTime());
        viewHolder.event.setText(traces.get(position).getEvent());
        viewHolder.itemView.findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,viewHolder.getAdapterPosition()+"",Toast.LENGTH_SHORT).show();//切入Activity
                Intent intent =new Intent();
                int position = viewHolder.getAdapterPosition();//位置
                intent.putExtra("traceId",traces.get(position).getTraceId());
                intent.putExtra("time",traces.get(position).getTime());
                intent.putExtra("event",traces.get(position).getEvent());
                intent.putExtra("date",traces.get(position).getDate());
                intent.putExtra("hasESTime",traces.get(position).isHasESTime());
                intent.putExtra("hasLETime",traces.get(position).isHasLETime());
                intent.putExtra("isfinish",traces.get(position).getFinish());
                intent.putExtra("ESTime",traces.get(position).getESTime());
                intent.putExtra("LETime",traces.get(position).getLETime());
                intent.putExtra("siteId",traces.get(position).getSiteId());
                intent.putExtra("siteText",traces.get(position).getSiteText());
                intent.putExtra("predict",traces.get(viewHolder.getAdapterPosition()).getPredict());

//                intent.putExtra("trace",traces.get(position));//序列化尝试  预留接口
               // intent.putExtra("siteId",traces.get(viewHolder.getAdapterPosition()).getFix());

                intent.setClass(context, SetTraceActivity.class);
                startActivityForResult((MainActivity)context,intent,2,null);
            }
        });
        viewHolder.itemView.findViewById(R.id.card).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {//长按事件
//                mItemTouchHelper.startDrag(viewHolder);
//                DragItemTouchHelper.getHelper().startDrag(viewHolder);
                return true;
            }
        });
        viewHolder.itemView.findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeData(viewHolder.getAdapterPosition());
                MyItemTouchHelperCallback.setIfdel(true);
            }
        });
        viewHolder.itemView.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishData(viewHolder.getAdapterPosition());
                MyItemTouchHelperCallback.setIfdel(true);
            }
        });
    }
    @Override
    public int getItemCount() {
        return traces.size();
    }

    private void finishData(int position) {
        TraceManager.finishTrace(traces.get(position));
        traces.remove(position);
        notifyItemRemoved(position);
    }

    public void addData(Trace one, int position)
    {
          traces.add(position, one);
          TraceManager.addTrace(one); //输入数据库
          notifyItemInserted(position);
     }

    public void removeData(int position) {
        TraceManager.deleteTrace(traces.get(position));
        traces.remove(position);
        notifyItemRemoved(position);

    }
    public void MoveToPosition(LinearLayoutManager manager, int n) {
        int m=manager.findFirstVisibleItemPosition();
        if(n<=m) {
            manager.scrollToPositionWithOffset(n, 0);
//            manager.setStackFromEnd(true);
        }
    }

    @Override
    public void onItemMove(int fromPos, int toPos) {
        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(traces, i, i + 1);
                String tempTime = traces.get(i).getTime();
                traces.get(i).setTime(traces.get(i + 1).getTime());
                traces.get(i + 1).setTime(tempTime);
                TraceManager.updateTrace(traces.get(i));
                TraceManager.updateTrace(traces.get(i + 1)); //交换跟新数据库内容
            }
        }
        else if (fromPos > toPos) {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(traces, i, i - 1);
                String tempTime = traces.get(i).getTime();
                traces.get(i).setTime(traces.get(i - 1).getTime());
                traces.get(i - 1).setTime(tempTime);
                TraceManager.updateTrace(traces.get(i));
                TraceManager.updateTrace(traces.get(i - 1)); //交换跟新数据库内容
            }
        }
        notifyItemMoved(fromPos, toPos);
    }

    @Override
    public void onItemClearView() {
        notifyDataSetChanged();
    }
}
