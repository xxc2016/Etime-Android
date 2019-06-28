package com.student.xxc.etime.view;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.student.xxc.etime.R;
import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.adapter.CommunityViewPageAdapter;
import com.student.xxc.etime.helper.NoScrollViewPager;
import com.student.xxc.etime.impl.JsonManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private MyHandler myhandler = new MyHandler(this);
    private List<String> mDatas;
    private List<Fragment> fragments;
    private CommunityViewPageAdapter viewPageAdapter;
    private NoScrollViewPager mViewPage;

    public MyHandler getMyhandler() {
        return myhandler;
    }

    public static class MyHandler extends Handler {
        private final WeakReference<CommunityActivity> mActivity;

        public MyHandler(CommunityActivity activity) {
            mActivity = new WeakReference<CommunityActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {//处理消息
            super.handleMessage(msg);
            if (mActivity.get() == null) {
                return;
            }
            Bundle bundle = msg.getData();
            int response = bundle.getInt("response");
            mActivity.get().updateToast(response);

            if (response == PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_SUCCESSED) {
                String json = bundle.getString("json");
                PostBean postBean = JsonManager.JsonToPostBean(json);
                if (postBean.getSource() != null && postBean.getSource().equals("community")) {
                    ((CommunityFragment) mActivity.get().getSupportFragmentManager().getFragments().get(0)).updatePost(postBean);
                }
                //((CommunityFragment) mActivity.get().getSupportFragmentManager().getFragments().get(0)).updatePost(postBean);
            }

            if (response == PostBean.POST_DOWN_LOAD_LIST_RESPONSE_SUCCESSED) {
                String json = bundle.getString("json");
                PostBean postBean = JsonManager.JsonToPostBean(json);
                if (postBean.getSource() != null && postBean.getSource().equals("historypost")) {
                    Log.i("postHistory", "update0");
                    ((UserStateFragment) (mActivity.get().getSupportFragmentManager().getFragments().get(1))).updatePost(postBean);
                }
            }

            if (response == UserBean.USER_DOWN_LOAD_RESPONSE_SUCCESSED) {
                String json = bundle.getString("json");
                UserBean userBean = JsonManager.JsonToUserBean(json);
                if (userBean.getSource() != null && userBean.getSource().equals("userState")) {
                    ((UserStateFragment) mActivity.get().getSupportFragmentManager().getFragments().get(1)).updateUserBean(userBean);
                }
            }

//            if (response == User.IMAGE_DOWNLOAD_RESPONSE_SUCCESSED) {
//                String userName = bundle.getString("userName");
//                String imagePath = bundle.getString("imagePath");
//                Account.setUserName(userName);
//                Account.setUserImagePath(imagePath);
//                ((UserStateFragment)mActivity.get().getSupportFragmentManager().getFragments().get(1)).updateAccount();
//            }
        }
    }


    private void updateToast(int response)//提示
    {
        switch (response) {
            case PostBean.POST_COMMUNITY_GET_LIST_RESPONSE_SUCCESSED:
                Toast.makeText(this, "预览帖子获得序列号成功", Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_COMMUNITY_GET_LIST_RESPONSE_FAILED:
                Toast.makeText(this, "预览帖子获得序列号失败", Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_SUCCESSED:
                Toast.makeText(this, "预览帖子全部下载成功", Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_FAILED:
                Toast.makeText(this, "预览帖子全部下载失败", Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_DOWN_LOAD_LIST_RESPONSE_SUCCESSED:
                Toast.makeText(this, "预览帖子按照给定序号下载成功", Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_DOWN_LOAD_LIST_RESPONSE_FAILED:
                Toast.makeText(this, "预览帖子按照给定序号下载失败", Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_DOWNLOAD_RESPONSE_FAILED:
                Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_DOWNLOAD_RESPONSE_SUCCESSED:
                Toast.makeText(this, "下载成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night", getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);//底部导航栏初始化

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        initFragment();

        mViewPage = (NoScrollViewPager) findViewById(R.id.fragment_vp);
        viewPageAdapter = new CommunityViewPageAdapter(getSupportFragmentManager(), mDatas, fragments);
        mViewPage.setAdapter(viewPageAdapter);
        mViewPage.setScroll(false);//禁止滑动

    }

    private void initFragment() {
        mDatas = new ArrayList<>();
        mDatas.add("community");
        mDatas.add("userState");

        fragments = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).equals("community")) {
                CommunityFragment fragment = CommunityFragment.newInstance(mDatas.get(i));
                fragments.add(fragment);
            } else {
                if (mDatas.get(i).equals("userState")) {
                    UserStateFragment fragment = UserStateFragment.newInstance(mDatas.get(i));
                    fragments.add(fragment);
                }
            }
        }
    }


    //底部导航栏
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (mViewPage == null) {
                return false;
            }
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    mViewPage.setCurrentItem(0);
                    return true;
                case R.id.navigation_me:
                    mViewPage.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

}