package com.student.xxc.etime;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.student.xxc.etime.entity.Remark;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.RemarkAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    private RemarkAdapter adapter;
    private RecyclerView recyclerView;
    private List<Remark> remarkList=new ArrayList<Remark>();
    private LinearLayoutManager manager=new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        recyclerView=this.findViewById(R.id.postDetailView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        initPostDetailData(0);
        initRemarkData();
        initView();
    }

    private void initPostDetailData(int postId) {//帖子详情数据加载，intent传过来的id
        TextView nickName=(TextView)findViewById(R.id.textView5);
        TextView title=(TextView)findViewById(R.id.titleDetail);
        ImageButton head=(ImageButton)findViewById(R.id.userLogo2);
        Button follow=(Button)findViewById(R.id.follow);
        follow.setOnClickListener(new View.OnClickListener() {//关注
            @Override
            public void onClick(View view) {

            }
        });
        head.setOnClickListener(new View.OnClickListener() {//访问个人主页
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void initView() {
        if(adapter==null) {
            adapter = new RemarkAdapter(this, remarkList);
        }
        else{
            adapter.notifyDataSetChanged();
        }
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }
    public void initRemarkData(){//评论数据加载
        String url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2756575517,833879878&fm=200&gp=0.jpg";
        List<String> t=new ArrayList<String>();
        t.add(url);
        t.add(url);
        for(int i=0;i<5;i++){
            remarkList.add(new Remark(new User("test"),"test[pic:0]test[pic:1]test",t));
        }
    }
}
