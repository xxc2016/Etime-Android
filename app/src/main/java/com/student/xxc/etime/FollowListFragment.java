package com.student.xxc.etime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.FollowerAdapter;
import com.student.xxc.etime.helper.UrlHelper;
import com.student.xxc.etime.impl.DealUserBean;

import java.util.ArrayList;
import java.util.List;

public class FollowListFragment extends Fragment implements DealUserBean {

    List<User> userList = null;
    FollowerAdapter userAdapter = null;

    public static FollowListFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("key", data);
        FollowListFragment fragment = new FollowListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public View createFragment(LayoutInflater inflater, ViewGroup container)
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
        userList=new ArrayList<User>();
        userAdapter=new FollowerAdapter(getContext(),userList);
        listView.setAdapter(userAdapter);


        initData();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String pageTitle=getArguments().getString("key");
        Log.i(pageTitle, "followList onCreateView: _____________________");
        View view=null;

        view=createFragment(inflater,container);

        return view;
    }

    public void updateUserBean(UserBean userBean)
    {
        if(userList!=null && userBean.getFollowList()!=null)
        {
            userList.clear();
            for(int i=0;i<userBean.getFollowList().size();i++)
            {
                User  user = new User();
                user.setName(userBean.getFollowList().get(i).account);
                user.setNickName(userBean.getFollowList().get(i).nickName);
                user.setImagePath(UrlHelper.getUrl_base()+userBean.getFollowList().get(i).head);
                Log.i("followList",""+user.getImagePath());
                userList.add(user);
            }
            userAdapter.notifyDataSetChanged();
        }
    }

    public void initData()
    {
        ((UserStateFragment)(this.getParentFragment())).initData();
    }
}
