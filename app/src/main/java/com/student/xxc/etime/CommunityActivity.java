package com.student.xxc.etime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.PostDetailBean;
import com.student.xxc.etime.entity.Post;
import com.student.xxc.etime.helper.CommunityAdapter;
import com.student.xxc.etime.helper.TimeCalculateHelper;
import com.student.xxc.etime.helper.UrlHelper;
import com.student.xxc.etime.impl.HttpConnection;
import com.student.xxc.etime.impl.JsonManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class CommunityActivity extends AppCompatActivity {

    private CommunityAdapter adapter;
    private RecyclerView recyclerView;
    private List<Post> postList=new ArrayList<Post>();
    private LinearLayoutManager manager=new LinearLayoutManager(this);
    private MyHandler myhandler = new MyHandler(this);
    private SparseArray<Fragment> mFragmentSparseArray;

    private static class MyHandler extends Handler {
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

            if(response == PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_SUCCESSED)
            {
                String json = bundle.getString("json");
                PostBean postBean =  JsonManager.JsonToPostBean(json);
                mActivity.get().updatePost(postBean);
            }
        }
    }


    private void updateToast(int response)//提示
    {
        switch (response) {
            case PostBean.POST_COMMUNITY_GET_LIST_RESPONSE_SUCCESSED:
                Toast.makeText(this,"预览帖子获得序列号成功",Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_COMMUNITY_GET_LIST_RESPONSE_FAILED:
                Toast.makeText(this,"预览帖子获得序列号失败",Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_SUCCESSED:
                Toast.makeText(this,"预览帖子全部下载成功",Toast.LENGTH_SHORT).show();
                break;
            case PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_FAILED:
                Toast.makeText(this,"预览帖子全部下载失败",Toast.LENGTH_SHORT).show();
                break;
            case  PostBean.POST_DOWN_LOAD_LIST_RESPONSE_SUCCESSED:
                Toast.makeText(this,"预览帖子按照给定序号下载成功",Toast.LENGTH_SHORT).show();
                break;
            case  PostBean.POST_DOWN_LOAD_LIST_RESPONSE_FAILED:
                Toast.makeText(this,"预览帖子按照给定序号下载失败",Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        recyclerView=this.findViewById(R.id.postsView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        int currentNightMode=getIntent().getIntExtra("mode", Configuration.UI_MODE_NIGHT_NO);
//        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
//                AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.setPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAdd();
            }
        });

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);//底部导航栏初始化
        ViewPager mVp = (ViewPager) findViewById(R.id.fragment_vp);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        
        initData();
        initView();

    }

    private void initView() {
        if(adapter==null) {
            adapter = new CommunityAdapter(this, postList);
        }
        else{
            adapter.notifyDataSetChanged();
        }
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    public void initData(){

        getPostBean_ALL(new PostBean());//发送请求下载所有帖子
//        String url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2756575517,833879878&fm=200&gp=0.jpg";
//        for(int i=0;i<5;i++){
//            postList.add(i,new Post(null,"test",url,"3分钟前",3500,200));
//        }


    }
    public void actionAdd(){
        Intent intent=new Intent();
        intent.setClass(this, SetPostActivity.class);
        this.startActivity(intent);
    }

    public void updatePost(PostBean postBean)//刷新帖子
    {
        postList.clear();
        List<PostBean.Post> list = postBean.getPosts();
        if(list!=null){
            for(PostBean.Post post:list)
            {
                String timeGap = TimeCalculateHelper.getTimeGap(post.date,post.time);
                postList.add(new Post(UrlHelper.getUrl_base()+post.user.head,post.user.nickName, UrlHelper.getUrl_base()+post.pic,
                        timeGap,post.watch,post.remark,post.title,post.PostId,post.detailId));
            }
            adapter.notifyDataSetChanged();
        }
    }


    private  void   getPostBean_ALL(final PostBean postBean)//获得所有帖子
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

                        PostBean askPostBean = null;
                        try {
                            askPostBean = JsonManager.JsonToPostBean(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",PostBean.UNKNOWN_ERROR);
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        if(askPostBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askPostBean.getResponseCode());
                            bundle.putString("json",JsonManager.PostBeanToJson(askPostBean));
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_downLoadPostALL(postBean,callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
    //底部导航栏
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_me:

                    return true;
            }
            return false;
        }
    };


}
