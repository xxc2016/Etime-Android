package com.student.xxc.etime.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class CommunityViewPageAdapter extends FragmentStatePagerAdapter{

    private List<String> mDatas;
    private List<Fragment> mFragments;

    public CommunityViewPageAdapter(FragmentManager fm, List<String> mDatas, List<Fragment> fragments) {
        super(fm);
        this.mDatas = mDatas;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size()==mFragments.size()?mFragments.size():0;
    }

    /**
     * 重写此方法，返回TabLayout的内容
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mDatas.get(position);
    }
}
