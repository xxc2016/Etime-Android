package com.student.xxc.etime.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.student.xxc.etime.R;
import com.student.xxc.etime.entity.User;

import java.util.List;

public class FollowerAdapter extends BaseAdapter {
    private List<User> userList;
    private LayoutInflater layoutInflater;
    private Context context;

    public FollowerAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    //获得数据数量
    @Override
    public int getCount() {
        return userList.size();
    }

    //获得某一位置的数据
    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    //获得唯一标识
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.userdetail, null);
        User user=getItem(position);
        ImageView head = (ImageView) convertView.findViewById(R.id.User_head);
        TextView nickName = (TextView) convertView.findViewById(R.id.User_nickName);
//        head.setImageResource(user.gethead());
        nickName.setText(user.getNickName());
        return convertView;
    }
}

