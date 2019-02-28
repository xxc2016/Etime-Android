package com.student.xxc.etime.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.student.xxc.etime.PictureViewActivity;
import com.student.xxc.etime.PostDetailActivity;
import com.student.xxc.etime.R;
import com.student.xxc.etime.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder>{
    Context context;
    List<Post> posts;

    public CommunityAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.community_post,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,final int i) {
        if(viewHolder==null)
        {
            return;
        }
        if(posts.get(i).getUsername()==null) {
           viewHolder.username.setText("用户");  //新增缺省信息处理2.18
        }else {
            viewHolder.username.setText(posts.get(i).getUsername());
        }
        viewHolder.postTime.setText(posts.get(i).getPostTime());
        viewHolder.watch.setText(posts.get(i).getWatch()+"");
        viewHolder.remark.setText(posts.get(i).getRemark()+"");
        viewHolder.title.setText(posts.get(i).getTitle()+"");

        Object tag = viewHolder.head.getTag(R.id.useLogo);
        if(tag!=null && (int)tag!= i)//防止组件复用
        {
            Glide.clear(viewHolder.head);
            Glide.clear(viewHolder.pic);
            Log.i("glide","clear");
        }

//        Glide.with(context).load(posts.get(i).getHead()).bitmapTransform(new CropCircleTransformation(context))
//                .placeholder(R.mipmap.personal).into(viewHolder.head);
//        Glide.with(context).load(posts.get(i).getHead()).asBitmap()
//                .placeholder(R.mipmap.personal).diskCacheStrategy(DiskCacheStrategy.ALL)
//
//                .into(new BitmapImageViewTarget(viewHolder.head){
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(),resource);
//                        circularBitmapDrawable.setCircular(true);
//                        viewHolder.head.setImageDrawable(circularBitmapDrawable);
//                    }
//                });


        Glide.with(context).load(posts.get(i).getHead()).transform(new GlideCirlceTransHelper(context)).placeholder(R.mipmap.personal)  //添加占位图2.18
                .into(viewHolder.head);

//        Glide.with(context).load(posts.get(i).getHead()).skipMemoryCache(true).dontAnimate()
//                .bitmapTransform(new CropCircleTransformation(context)).placeholder(R.mipmap.personal).into(viewHolder.head);

        viewHolder.head.setTag(R.id.useLogo,i);
        Glide.with(context).load(posts.get(i).getPic()).centerCrop().into(viewHolder.pic);
        viewHolder.itemView.findViewById(R.id.communityCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                int postDetailId = posts.get(i).getPostDetailId();
                Log.i("postDetailId",""+postDetailId);
                intent.putExtra("postDetailId",postDetailId);
                intent.setClass(context, PostDetailActivity.class);
                context.startActivity(intent);
            }
        });

        viewHolder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击查看原图 2.26
                Intent intent=new Intent();
                String url = posts.get(i).getPic();
                Bundle bundle = new Bundle();
                ArrayList<String> pic = new ArrayList<String>();
                pic.add(url);
                bundle.putStringArrayList("pic",pic);
                intent.putExtras(bundle);
                intent.setClass(context, PictureViewActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView head;
        TextView username;
        ImageView pic;
        TextView postTime;
        TextView watch;
        TextView remark;
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            head=(ImageView)itemView.findViewById(R.id.userLogo);
            username=(TextView)itemView.findViewById(R.id.textView4);
            pic=(ImageView)itemView.findViewById(R.id.picture);
            postTime=(TextView)itemView.findViewById(R.id.postTime);
            watch=(TextView)itemView.findViewById(R.id.watch);
            remark=(TextView)itemView.findViewById(R.id.remark);
            title =(TextView)itemView.findViewById(R.id.textView_post_title);
        }
    }
}
