package com.student.xxc.etime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.student.xxc.etime.R;

import java.util.List;

public class PictureViewAdapter extends PagerAdapter {

    Context context;
    List<String>  list_url;
    private  int mChildCount = 0;


    public PictureViewAdapter(Context context,List<String> list_url)
    {
        this.context =context;
        this.list_url =  list_url;
    }

    public void notifyDataSetChanged()
    {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    public int getItemPosition(Object object)
    {
        if(mChildCount>0)
        {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }



    public int getCount() {
        return list_url.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return  view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String url = list_url.get(position);
        PhotoView photoView = new PhotoView(context);
        Glide.with(context).load(url).placeholder(R.drawable.wait_picture)
                .error(R.drawable.invalid_picture_3).dontAnimate().into(photoView);
        container.addView(photoView);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return photoView;
    }

};





