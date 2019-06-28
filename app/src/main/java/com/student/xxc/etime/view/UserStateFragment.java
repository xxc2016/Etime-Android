package com.student.xxc.etime.view;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.student.xxc.etime.R;
import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.adapter.FollowViewPageAdapter;
import com.student.xxc.etime.impl.DealUserBean;
import com.student.xxc.etime.impl.HttpConnection;
import com.student.xxc.etime.impl.JsonManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class UserStateFragment extends Fragment {//原FollowListActivity

    private static final int REQUEST_CODE_SELECT_PIC = 120;
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<String> mDatas;
    private List<Fragment> fragments;
   // private FollowViewPageAdapter adapter;
    private FragmentPagerAdapter adapter;

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


//        initData();
        initFragments();
        //initData();

        mTablayout = (TabLayout)view.findViewById(R.id.tablayout);
        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        adapter = new FollowViewPageAdapter(getChildFragmentManager(),mDatas,fragments);
        mViewPager.setAdapter(adapter);


        updateAccount();

        FloatingActionButton fb = (FloatingActionButton)view.findViewById(R.id.setPost);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });

        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String pageTitle=getArguments().getString("key");
        Log.i(pageTitle, "userStateFragmemt onCreateView: _____________________");
        View view=null;
        view=createFragment(inflater,container);
        return view;
    }

    public void initData() {
        updateAccount();
        if(Account.getState()==Account.ACCOUNT_ONLINE) {
            UserBean userBean = new UserBean();
            userBean.setAccount(Account.getUserAccount());
            userBean.setSource("userState");
            downLoad_UserBean(userBean);
            Log.i("userState","click");
        }
    }

    private void initFragments() {
    if (mDatas==null)
        mDatas = new ArrayList<>();
    else
        mDatas.clear();
        mDatas.add("我的关注");
        mDatas.add("历史帖子");
        //mDatas.add("历史评论");

        if(fragments==null) {
            fragments = new ArrayList<>();
        }else
        {
            fragments.clear();
        }
        for (int i = 0; i < mDatas.size(); i++) {
            if(mDatas.get(i).equals("我的关注")) {
                FollowListFragment fragment = FollowListFragment.newInstance(mDatas.get(i));
                fragments.add(fragment);
            }
            if(mDatas.get(i).equals("历史帖子")) {
                HistoryPostFragment fragment = HistoryPostFragment.newInstance(mDatas.get(i));
                fragments.add(fragment);
            }
            if(mDatas.get(i).equals("历史评论"))
            {
                HistoryRemarkFragment fragment = HistoryRemarkFragment.newInstance(mDatas.get(i));
                fragments.add(fragment);
            }

        }

    }


    private void downLoad_UserBean(final UserBean userBean)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Callback callback=new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("POST",""+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("POST",response.protocol()+" "+
                                response.code()+" "+response.message());
                        Headers headers = response.headers();

                        String json = response.body().string();
                        Log.d("tag","onResponse"+json);

                        UserBean askUserBean = null;
                        try {
                            askUserBean = JsonManager.JsonToUserBean(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",UserBean.UNKNOWN_ERROR);
                            if (getActivity() != null && getActivity().getClass() == CommunityActivity.class) {
                                Message message = ((CommunityActivity) getActivity()).getMyhandler().obtainMessage();
                                message.setData(bundle);
                                message.sendToTarget();
                                Looper.loop();
                            }
                            return ;
                        }

                        if(askUserBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askUserBean.getResponseCode());
                            bundle.putString("json",JsonManager.UserBeanToJson(askUserBean));
                            if (getActivity() != null && getActivity().getClass() == CommunityActivity.class) {
                                Message message = ((CommunityActivity) getActivity()).getMyhandler().obtainMessage();
                                message.setData(bundle);
                                message.sendToTarget();
                                Looper.loop();
                            }
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_downLoadUserBean(userBean,callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void updateUserBean(UserBean userBean)
    {
         for(int i=0;i<fragments.size();i++)
         {
             ((DealUserBean)fragments.get(i)).updateUserBean(userBean);
         }
    }

    public void updatePost(PostBean postBean)
    {
        Log.i("posthistory","update1");
        if(postBean.getSource()!=null && postBean.getSource().equals("historypost"))
        {
            Log.i("posthistory","update2");
            ((HistoryPostFragment)fragments.get(1)).updatePost(postBean);
        }
    }

    public void updateAccount()//更新用户信息
    {
        updateUserImage();//更新用户头像
//        String userName = Account.getUserName();
//        TextView user_setting_name = (TextView) getActivity().findViewById(R.id.textView_user_setting_name);
//        user_setting_name.setText(userName);
    }

    private void updateUserImage()//刷新用户头像和背景
    {
        if(getView()==null)
        {
            return;
        }
        ImageView user_setting_background = (ImageView) getView().findViewById(R.id.imageView_user_setting_background);
        String imagePath = Account.getUserImagePath();
        ImageView user_setting_image = (ImageView) getView().findViewById(R.id.imageView_user_setting_image);

        RequestListener mRequestListener = new RequestListener() {//用于监听错误
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                Log.d("glide", "onException: " + e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e("glide", "model:" + model + "isFirstRource" + isFirstResource);
                return false;
            }
        };

        if (!getActivity().isDestroyed())//防止出现glide的一个bug
        {

            if (!Account.getUserImagePath().isEmpty())//网络地址有效
            {
                Log.i("glide", "network");
                Glide.with(this).load(Account.getUserImagePath())//背景
                        .dontAnimate()
                        .error(R.mipmap.ic_launcher)
                        .bitmapTransform(new BlurTransformation(getActivity(), 25, 3), new CenterCrop(getActivity()))
                        .into(user_setting_background);//使用glide包处理图片实现图片磨砂效果

                Glide.with(this)//头像
                        .load(Account.getUserImagePath())
                        .listener(mRequestListener)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .into(user_setting_image);
            } else {
                if ((new File(Account.getUserLocalImagePath()).exists()))//本地图片地址有效
                {
                    Log.i("glide", "local");
                    Glide.with(this).load(Account.getUserLocalImagePath())//背景
                            .dontAnimate()
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new BlurTransformation(getActivity(), 25, 3), new CenterCrop(getActivity()))
                            .into(user_setting_background);//使用glide包处理图片实现图片磨砂效果

                    Glide.with(this)//头像
                            .load(Account.getUserLocalImagePath())
                            .listener(mRequestListener)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new CropCircleTransformation(getActivity()))
                            .into(user_setting_image);
                } else //默认情况
                {
                    Log.i("glide", "default");
                    Glide.with(this).load(R.mipmap.ic_launcher)//背景
                            .dontAnimate()
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new BlurTransformation(getActivity(), 25, 3), new CenterCrop(getActivity()))
                            .into(user_setting_background);//使用glide包处理图片实现图片磨砂效果

                    Glide.with(this)//头像
                            .load(R.mipmap.ic_launcher)
                            .listener(mRequestListener)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(user_setting_image);
                }
            }
        }
    }

}
