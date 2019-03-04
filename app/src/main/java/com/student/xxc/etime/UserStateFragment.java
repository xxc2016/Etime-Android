package com.student.xxc.etime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.FollowViewPageAdapter;
import com.student.xxc.etime.helper.FollowerAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserStateFragment extends Fragment {//原FollowListActivity

    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<String> mDatas;
    private List<Fragment> fragments;

    public static UserStateFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("key", data);
        UserStateFragment fragment = new UserStateFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public View createFragment(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_userstate, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("我的主页");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        initData();
        initFragments();
        mTablayout = (TabLayout)view.findViewById(R.id.tablayout);
        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new FollowViewPageAdapter(getChildFragmentManager(), mDatas, fragments));

        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String pageTitle=getArguments().getString("key");
        Log.i(pageTitle, "onCreateView: _____________________");
        View view=null;
        view=createFragment(inflater,container);

        return view;
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add("关注");
        mDatas.add("粉丝");
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            FollowListFragment fragment = FollowListFragment.newInstance(mDatas.get(i));
            fragments.add(fragment);
        }

    }
}
