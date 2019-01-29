package com.student.xxc.etime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.student.xxc.etime.entity.Post;
import com.student.xxc.etime.helper.CommunityAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private CommunityAdapter adapter;
    private RecyclerView recyclerView;
    private List<Post> postList=new ArrayList<Post>();
    private LinearLayoutManager manager=new LinearLayoutManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        recyclerView=this.findViewById(R.id.postsView);

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
        String url = "http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg";
        for(int i=0;i<5;i++){
            postList.add(i,new Post(null,"test",url,"3分钟前",3500,200));
        }
    }

}
