package com.student.xxc.etime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.FollowerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowListFragment extends Fragment {
    public static FollowListFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("key", data);
        FollowListFragment fragment = new FollowListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public View createAttentionFragment(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_attention, container, false);
        ListView listView=(ListView)view.findViewById(R.id.Attention_listview);
        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent();
                String account=String.valueOf(i);
                intent.putExtra("account_data", account);
                intent.setClass(getContext(), UserHomePageActivity.class);
                startActivity(intent);
            }

                                        });
        List<User>userList=new ArrayList<User>();
        User u1=new User("cxcxcxcx");
        User u2=new User("aaaaaaaa");
        userList.add(u1);
        userList.add(u2);
        FollowerAdapter userAdapter=new FollowerAdapter(getContext(),userList);
        listView.setAdapter(userAdapter);
        return view;
    }
    public View createFansFragment(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_attention, container, false);
        ListView listView=(ListView)view.findViewById(R.id.Attention_listview);
        List<User>userList=new ArrayList<User>();
        User u1=new User("111111");//用户初始化
        User u2=new User("2222222");
        userList.add(u1);//添加用户
        userList.add(u2);
        FollowerAdapter followerAdapter=new FollowerAdapter(getContext(),userList);
        listView.setAdapter(followerAdapter);
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String pageTitle=getArguments().getString("key");
        Log.i(pageTitle, "onCreateView: _____________________");
        View view=null;
        if(pageTitle=="关注")
        {
            view=createAttentionFragment(inflater,container);
        }
        else if(pageTitle=="粉丝")
        {
            view=createFansFragment(inflater,container);
        }
        else {
             view = inflater.inflate(R.layout.fragment_attention, container, false);
        }
        return view;
    }
}
