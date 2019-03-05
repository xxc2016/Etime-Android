package com.student.xxc.etime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.Post;
import com.student.xxc.etime.helper.CommunityAdapter;
import com.student.xxc.etime.helper.TimeCalculateHelper;
import com.student.xxc.etime.helper.UrlHelper;
import com.student.xxc.etime.impl.DealUserBean;
import com.student.xxc.etime.impl.HttpConnection;
import com.student.xxc.etime.impl.JsonManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class HistoryPostFragment extends Fragment implements DealUserBean {

    private CommunityAdapter adapter;
    private RecyclerView recyclerView;
    private List<Post> postList = new ArrayList<Post>();
    private LinearLayoutManager manager = new LinearLayoutManager(getActivity());

    public static HistoryPostFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("key", data);
        HistoryPostFragment fragment = new HistoryPostFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public View createFragment(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_history_post, container, false);
        recyclerView = view.findViewById(R.id.historyPostsView);

        initView();
        initData();

        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments()!=null) {
            String pageTitle = getArguments().getString("key");
            Log.i(pageTitle, "HistoryPost onCreateView: _____________________");
        }
        View view=null;
        view = createFragment(inflater,container);

        return view;
    }

    public void updateUserBean(UserBean userBean)
    {
        PostBean postBean  =new PostBean();
        List<Integer>  list = new LinkedList<Integer>();
        postBean.setRequestPostList(list);
        if(userBean.getPostList()!=null)
        {
            for(int i=0;i<userBean.getPostList().size();i++)
            {
                list.add(0,userBean.getPostList().get(i));//倒序申请
            }
           // list.addAll(userBean.getPostList());
        }
        //postBean.setRequestPostList(list);
        postBean.setSource("historypost");
         getPostBean_select(postBean);//申请历史帖子
    }


    private void initView() {
        if (adapter == null) {
            adapter = new CommunityAdapter(getActivity(), postList);
        } else {
            adapter.notifyDataSetChanged();
        }
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    public void initData() {

//        PostBean postBean = new PostBean();
//        postBean.setSource("historypost");//设置碎片序号
//        getPostBean_select(postBean);//发送请求下载所有帖子
////        String url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2756575517,833879878&fm=200&gp=0.jpg";
////        for(int i=0;i<5;i++){
////            postList.add(i,new Post(null,"test",url,"3分钟前",3500,200));
////        }

        ((UserStateFragment)(this.getParentFragment())).initData();
    }


    public void updatePost(PostBean postBean)//刷新帖子
    {
        Log.i("post","add");
        postList.clear();
        List<PostBean.Post> list = postBean.getPosts();
        if (list != null) {
            for (PostBean.Post post : list) {
                String timeGap = TimeCalculateHelper.getTimeGap(post.date, post.time);
                postList.add(new Post(UrlHelper.getUrl_base() + post.user.head, post.user.nickName, UrlHelper.getUrl_base() + post.pic,
                        timeGap, post.watch, post.remark, post.title, post.PostId, post.detailId));
            }
            adapter.notifyDataSetChanged();
        }
    }



    private void getPostBean_select(final PostBean postBean)//获得所有帖子
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("POST", "" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("POST", response.protocol() + " " +
                                response.code() + " " + response.message());
                        Headers headers = response.headers();

                        String json = response.body().string();
                        Log.d("tag", "onResponse" + json);

                        PostBean askPostBean = null;
                        try {
                            askPostBean = JsonManager.JsonToPostBean(json);
                        } catch (Exception e) {
                            Log.i("jsonError", e + "");
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", PostBean.UNKNOWN_ERROR);
                            if (getActivity() != null && getActivity().getClass() == CommunityActivity.class) {
                                Message message = ((CommunityActivity) getActivity()).getMyhandler().obtainMessage();
                                message.setData(bundle);
                                message.sendToTarget();
                                Looper.loop();
                            }
                            return;
                        }

                        if (askPostBean != null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askPostBean.getResponseCode());
                            bundle.putString("json", JsonManager.PostBeanToJson(askPostBean));
                            if (getActivity() != null && getActivity().getClass() == CommunityActivity.class) {
                                Message message = ((CommunityActivity) getActivity()).getMyhandler().obtainMessage();
                                message.setData(bundle);
                                message.sendToTarget();
                                Looper.loop();
                            }
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_downLoadPostByList(postBean, callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}
