package com.student.xxc.etime.helper;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.student.xxc.etime.FollowListFragment;
import com.student.xxc.etime.HistoryPostFragment;

import java.util.List;

public class FollowViewPageAdapter extends FragmentPagerAdapter {
    private List<String> mDatas;
    private List<Fragment> mFragments;

    public FollowViewPageAdapter(FragmentManager fm, List<String> mDatas, List<Fragment> fragments) {
        super(fm);
        this.mDatas = mDatas;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
//        Fragment fragment = null;
//        if(mDatas.get(position).equals("我的关注"))
//        {
//            fragment  =FollowListFragment.newInstance(mDatas.get(position));
//        }
//        else
//        {
//            if(mDatas.get(position).equals("历史帖子"))
//            {
//                fragment  = HistoryPostFragment.newInstance(mDatas.get(position));
//            }
//        }
//        mFragments.set(position,fragment);
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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
//    }


    @Override
    public long getItemId(int position) {
        return mDatas.get(position).hashCode();
    }

}
