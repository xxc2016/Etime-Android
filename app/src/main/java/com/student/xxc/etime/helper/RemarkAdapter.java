package com.student.xxc.etime.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.student.xxc.etime.R;
import com.student.xxc.etime.entity.Remark;

import java.util.List;

public class RemarkAdapter extends RecyclerView.Adapter<RemarkAdapter.ViewHolder>{
    Context context;
    List<Remark> remarkList;

    public RemarkAdapter(Context context, List<Remark> remarks) {
        this.context = context;
        this.remarkList = remarks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_remarklist,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.username.setText(remarkList.get(i).getUser().getNickName());
        insertPic(viewHolder.content,remarkList.get(i).getContent(),remarkList.get(i).getBitmapList());
//        Glide.with(context).load(R.mipmap.personal).into(viewHolder.head);
//        Glide.with(context).load(remarkList.get(i).getPic()).into(viewHolder.pic);

    }

    @Override
    public int getItemCount() {
        return remarkList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView head;
        TextView username;
        TextView content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            head=(ImageView)itemView.findViewById(R.id.userLogo);
            username=(TextView)itemView.findViewById(R.id.textView5);
            content=(TextView)itemView.findViewById(R.id.remarkContent);
        }
    }

    private void insertPic(final TextView textView, final String content, final List<String> bitmaps){//imagespan图文混合
        final SpannableString spannableString = new SpannableString(content);
        int sub=-1;
        for(int i=0;i<bitmaps.size();i++) {
            //存在删除图片后，[pic:]可能不从0开始,也可能中间少数
            sub+=1;
            String tmpSub = "[pic:" + sub + "]";
            while(!content.contains(tmpSub)){
                tmpSub = "[pic:" + (++sub) + "]";
            }
            final int finalI = sub;
            Glide.with(context).load(bitmaps.get(i)).asBitmap().into(new SimpleTarget<Bitmap>(){
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    ImageSpan imageSpan = new ImageSpan(context, resource);
                    //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                    String tempUrl = "[pic:" + finalI + "]";
                    //用ImageSpan对象替换你指定的字符串
                    int start=spannableString.toString().indexOf(tempUrl);
                    spannableString.setSpan(imageSpan, start, start+tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                    Log.i("RA", "onResourceReady: "+spannableString.toString());

                }
            });
        }
    }
}
