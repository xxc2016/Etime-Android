package com.student.xxc.etime.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.student.xxc.etime.PictureViewActivity;
import com.student.xxc.etime.PostDetailActivity;
import com.student.xxc.etime.R;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.entity.PostDetail;
import com.student.xxc.etime.entity.Remark;
import com.student.xxc.etime.util.ImageUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        viewHolder.content.setText(remarkList.get(i).getContent());

        Glide.with(context).load(remarkList.get(i).getUser().getImagePath()).transform(new GlideCirlceTransHelper(context)).placeholder(R.mipmap.personal)  //添加占位图2.18
                    .into(viewHolder.head);

        updateFollowButton(viewHolder.follow,i);

    }

    @Override
    public int getItemCount() {
        return remarkList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView head;
        TextView username;
        TextView content;
        Button  follow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            head=(ImageView)itemView.findViewById(R.id.userLogo2);//应该是2
            username=(TextView)itemView.findViewById(R.id.textView5);
            content=(TextView)itemView.findViewById(R.id.remarkContent);
            follow = (Button)itemView.findViewById(R.id.button_remark_follow);
        }
    }


    private void insertPic(final TextView textView, final String content, final List<String> bitmaps){//imagespan图文混合
        final SpannableString spannableString = new SpannableString(content);
        int sub=-1;
        int count = -1;
        for(int i=0;i<bitmaps.size();i++) {
            //存在删除图片后，[pic:]可能不从0开始,也可能中间少数
            sub+=1;
            String tmpSub = "[pic:" + sub + "]";
            while(!content.contains(tmpSub)){
                tmpSub = "[pic:" + (++sub) + "]";
            }
            count++;
            final int position = count;
            final int finalI = sub;
            Glide.with(context).load(bitmaps.get(i)).asBitmap().skipMemoryCache(true).dontAnimate().into(new SimpleTarget<Bitmap>(){
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Log.e("RA2",finalI+"");
                    resource= ImageUtil.resizeImage(resource,800f,480f);//已经压缩大小过了
                    ImageSpan imageSpan = new ImageSpan(context, resource);
                    //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                    String tempUrl = "[pic:" + finalI + "]";
                    //用ImageSpan对象替换你指定的字符串
                    int start=spannableString.toString().indexOf(tempUrl);
                    spannableString.setSpan(imageSpan, start, start+tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            Log.i("span","clicked");

                                ArrayList<String> imagePath  = new ArrayList<String>();
                                imagePath.addAll(bitmaps);
                                Intent intent = new Intent();
                                Bundle bundle =new Bundle();
                                bundle.putStringArrayList("pic",imagePath);
                                bundle.putInt("position",position);
                                intent.putExtras(bundle);
                                intent.setClass(context,PictureViewActivity.class);
                                context.startActivity(intent);

                        }
                    };

                    spannableString.setSpan(clickableSpan,start,start+tempUrl.length(),spannableString.SPAN_EXCLUSIVE_EXCLUSIVE);


                    textView.setText(spannableString);
                    Log.i("RA", "onResourceReady: "+spannableString.toString());
                    textView.setMovementMethod(LinkMovementMethod.getInstance());

                }
            });
        }
    }

    private  void updateFollowButton(Button follow,final int i)
    {
        if(remarkList.get(i).isFollowState())//跟随
        {
            follow.setText("已关注");
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(Account.getUserAccount());
                    LinkedList<String>  followList = new LinkedList<String>();
                    followList.add(remarkList.get(i).getUser().getName());
                    userBean.setFollowList(followList);
                    if(context.getClass()== PostDetailActivity.class)
                    {
                        ((PostDetailActivity)context).delete_UserBean_FollowList(userBean);
                    }
                }
            });
        }else
        {
            follow.setText("+关注");
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(Account.getUserAccount());
                    LinkedList<String>  followList = new LinkedList<String>();
                    followList.add(remarkList.get(i).getUser().getName());
                    userBean.setFollowList(followList);
                    if(context.getClass()== PostDetailActivity.class)
                    {
                        ((PostDetailActivity)context).send_UserBean_FollowList(userBean);
                    }
                }
            });
        }
    }
}
