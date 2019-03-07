package com.student.xxc.etime.view;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import com.student.xxc.etime.R;
import com.student.xxc.etime.adapter.FollowViewPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowListActivity extends AppCompatActivity {
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<String> mDatas;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);

        initData();
        initFragments();
        mTablayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new FollowViewPageAdapter(getSupportFragmentManager(), mDatas, fragments));
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
