package com.student.xxc.etime.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.student.xxc.etime.R;

public class shareView extends RelativeLayout {

    public TextView time, event;//时间、事件
    public TextView tvDot;//图标
    public LinearLayout del;//删除按钮
    public LinearLayout activity;//活动部分
    public LinearLayout finish;//完成按钮

    public shareView(Context context)
    {
        this(context,null);
    }

    public shareView(Context context,AttributeSet attrs)
    {
        this(context,attrs,0);
    }

    public shareView(Context context,AttributeSet attributeSet,int defStyleAttr)
    {
        super(context,attributeSet,defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.time_line,this);
        time=(TextView)findViewById(R.id.tvAcceptTime);
        event=(TextView)findViewById(R.id.tvAcceptStation);
        tvDot=(TextView)findViewById(R.id.tvDot);
        del = (LinearLayout)findViewById(R.id.del);
        activity=(LinearLayout)findViewById(R.id.activity);
        finish=(LinearLayout)findViewById(R.id.finish);

        time.setText("88:88");
        time.setTextColor(context.getResources().getColor(R.color.colorTime));
        event.setTextColor(context.getResources().getColor(R.color.colorText));
        tvDot.setBackgroundResource(R.drawable.ic_menu_send );
    }


}
