package com.student.xxc.etime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.student.xxc.etime.entity.Trace;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private Context context;
    private List<Trace>traces;
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView time, event;//时间、事件
        private TextView tvDot;//图标
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=(TextView)itemView.findViewById(R.id.tvAcceptTime);
            event=(TextView)itemView.findViewById(R.id.tvAcceptStation);
            tvDot=(TextView)itemView.findViewById(R.id.tvDot);
        }
    }

    TimeLineAdapter(Context text,List<Trace> races) {
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
        viewHolder.time.setTextColor(0xff555555);
        viewHolder.event.setTextColor(0xff555555);
        viewHolder.tvDot.setBackgroundResource(R.drawable.ic_menu_send);

        viewHolder.time.setText(traces.get(position).getTime());
        viewHolder.event.setText(traces.get(position).getEvent());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//点击事件
                Toast.makeText(context,viewHolder.getAdapterPosition()+"",Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {//长按事件
                DragItemTouchHelper.getHelper().startDrag(viewHolder);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return traces.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if(position==0){
//            return TYPE_TOP;
//        }
//        return TYPE_NORMAL;
//    }

    public void addData(Trace one, int position)
    {
          traces.add(position, one);
          notifyItemInserted(position);
     }

}
