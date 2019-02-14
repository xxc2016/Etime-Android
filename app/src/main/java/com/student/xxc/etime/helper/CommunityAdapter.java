package com.student.xxc.etime.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.student.xxc.etime.PostDetailActivity;
import com.student.xxc.etime.R;
import com.student.xxc.etime.entity.Post;

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.username.setText(posts.get(i).getUsername());
        viewHolder.postTime.setText(posts.get(i).getPostTime());
        viewHolder.watch.setText(posts.get(i).getWatch()+"");
        viewHolder.remark.setText(posts.get(i).getRemark()+"");
//        Glide.with(context).load(R.mipmap.personal).into(viewHolder.head);
        Glide.with(context).load(posts.get(i).getPic()).into(viewHolder.pic);
        viewHolder.itemView.findViewById(R.id.communityCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(context, PostDetailActivity.class);
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            head=(ImageView)itemView.findViewById(R.id.userLogo);
            username=(TextView)itemView.findViewById(R.id.textView4);
            pic=(ImageView)itemView.findViewById(R.id.picture);
            postTime=(TextView)itemView.findViewById(R.id.postTime);
            watch=(TextView)itemView.findViewById(R.id.watch);
            remark=(TextView)itemView.findViewById(R.id.remark);
        }
    }
}
