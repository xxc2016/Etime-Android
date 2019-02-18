package com.student.xxc.etime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.PostDetailBean;
import com.student.xxc.etime.bean.RemarkBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.entity.PostDetail;
import com.student.xxc.etime.entity.Remark;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.GlideCirlceTransHelper;
import com.student.xxc.etime.helper.RemarkAdapter;
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

public class PostDetailActivity extends AppCompatActivity {
    private RemarkAdapter adapter;
    private RecyclerView recyclerView;
    private List<Remark> remarkList=new ArrayList<Remark>();
    private LinearLayoutManager manager=new LinearLayoutManager(this);
    private MyHandler myhandler = new MyHandler(this);
    private PostDetail postDetail = null;//用于存储相关信息



    private static class MyHandler extends Handler {
        private final WeakReference<PostDetailActivity> mActivity;

        public MyHandler(PostDetailActivity activity) {
            mActivity = new WeakReference<PostDetailActivity>(activity);
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

            if(response == PostDetailBean.POST_DETAIL_DOWN_LOAD_RESPONSE_SUCCESSED)
            {
                String json  =bundle.getString("json");
                PostDetailBean postDetailBean = JsonManager.JsonToPostDetailBean(json);
                mActivity.get().updatePostDetail(postDetailBean);//更新详细帖子信息
                mActivity.get().updatePostDetailView();//更新详细帖子界面

            }


        }
    }

    private void updateToast(int response)//提示
    {
        switch (response) {
            case PostDetailBean.POST_DETAIL_DOWN_LOAD_RESPONSE_SUCCESSED:
                Toast.makeText(this,"详细帖子下载成功",Toast.LENGTH_SHORT).show();
                break;
            case PostDetailBean.POST_DETAIL_DOWN_LOAD_RESPONSE_FAILED:
                Toast.makeText(this,"详细帖子下载失败",Toast.LENGTH_SHORT).show();
                break;
            case RemarkBean.REMARK_DOWN_LOAD_RESPONSE_SUCCESSED:
                Toast.makeText(this,"评论下载成功",Toast.LENGTH_SHORT).show();
                break;
            case RemarkBean.REMARK_DOWN_LOAD_RESPONSE_FAILED:
                Toast.makeText(this,"评论下载失败",Toast.LENGTH_SHORT).show();
                break;
            case UserBean.USER_UP_FOLLOWLIST_RESPONSE_SUCCESSED:
                Toast.makeText(this,"添加关注成功",Toast.LENGTH_SHORT).show();
                break;
            case UserBean.USER_UP_FOLLOWLIST_RESPONSE_FAILED:
                Toast.makeText(this,"添加关注失败",Toast.LENGTH_SHORT).show();
                break;
            case UserBean.USER_DELETE_FOLLOWLIST_RESPONSE_SUCCESSED:
                Toast.makeText(this,"取消关注成功",Toast.LENGTH_SHORT).show();
                break;
            case UserBean.USER_DELETE_FOLLOWLIST_RESPONSE_FAILED:
                Toast.makeText(this,"取消关注失败",Toast.LENGTH_SHORT).show();
                break;
            case PostDetailBean.UNKNOWN_ERROR:
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                break;
            case UserBean.UNKNOWN_ERROR:
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                break;
        }
    }
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

        Intent intent = getIntent();
        int postDetailId = intent.getIntExtra("postDetailId",-1);

        Log.i("postDetailId",""+postDetailId);


        initPostDetailData(postDetailId);
        initRemarkData();

        initView();
    }

    private void initPostDetailData(final int postDetailId) {//帖子详情数据加载，intent传过来的id  帖子详细类id
        TextView nickName=(TextView)findViewById(R.id.textView5);
        TextView title=(TextView)findViewById(R.id.titleDetail);
        ImageButton head=(ImageButton)findViewById(R.id.userLogo2);
        Button follow=(Button)findViewById(R.id.follow);
        follow.setOnClickListener(new View.OnClickListener() {//关注
            @Override
            public void onClick(View view) {
                if(postDetail !=null)
                {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(Account.getUserAccount());
                    LinkedList<String> addList =  new LinkedList<String>();
                    addList.add(postDetail.getUser().getName());
                    userBean.setFollowList(addList);
                    send_UserBean_FollowList(userBean);
                }
            }
        });
        head.setOnClickListener(new View.OnClickListener() {//访问个人主页
            @Override
            public void onClick(View view) {

            }
        });

        if(postDetailId!=-1) {
            PostDetailBean postDetailBean =new PostDetailBean();
            postDetailBean.setDetailId(postDetailId);
            getPostDetail(postDetailBean);
        }

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


    public  void updatePostDetail(PostDetailBean postDetailBean)//加载详细帖子内容
    {
        String title  = postDetailBean.getTitle();
        String head =UrlHelper.getUrl_base()+postDetailBean.user.head;
        String nickName = postDetailBean.user.nickName;
        String userId = postDetailBean.user.account;
        String content = postDetailBean.getContent();
        String date = postDetailBean.getDate();
        String time = postDetailBean.getTime();
        int postDetailId = postDetailBean.getDetailId();
        int postId = postDetailBean.getPostId();

        List<String> bitmapList = postDetailBean.getBitmapPath();

        for(int i=0;i<bitmapList.size();i++)
        {
            String path  = UrlHelper.getUrl_base()+bitmapList.get(i);//路径补全
            bitmapList.set(i,path);
        }

        User user = new User();
        user.setName(userId);
        user.setImagePath(head);
        user.setNickName(nickName);
        postDetail = new PostDetail(user,bitmapList,content,title,date,time,postDetailId,postId);
    }

    public  void updatePostDetailView()//更新帖子界面
    {
        if(postDetail==null)
        {
            return ;
        }
        TextView nickName=(TextView)findViewById(R.id.textView5);
        nickName.setText(postDetail.getUser().getNickName());//用户名

        TextView title=(TextView)findViewById(R.id.titleDetail);
        title.setText(postDetail.getTitle());//标题


        ImageButton head=(ImageButton)findViewById(R.id.userLogo2);
        Glide.with(this).load(postDetail.getUser().getImagePath())
                .placeholder(R.mipmap.personal).transform(new GlideCirlceTransHelper(this)).into(head);

        TextView  postContent = (TextView)this.findViewById(R.id.postContent);
        insertPic(postContent,postDetail.getContent(),postDetail.getBitmapList());
    }


    private void getPostDetail(final PostDetailBean postDetailBean) {//下载指定id 的帖子详细类
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

                        PostDetailBean askPostDetailBean = null;
                        try {
                            askPostDetailBean = JsonManager.JsonToPostDetailBean(json);
                        } catch (Exception e) {
                            Log.i("jsonError", e + "");
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", PostDetailBean.UNKNOWN_ERROR);
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return;
                        }

                        if (askPostDetailBean != null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askPostDetailBean.getResponseCode());
                            bundle.putString("json",JsonManager.PostDetailBeanToJson(askPostDetailBean));
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };
                HttpConnection.sendOkHttpRequest_downLoadPostDetail(postDetailBean, callback);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void insertPic(final TextView textView, final String content, final List<String>bitmaps){//imagespan图文混合
        final SpannableString spannableString = new SpannableString(content);
        for(int i=0;i<bitmaps.size();i++) {
            final int finalI = i;
            Glide.with(this).load(bitmaps.get(i)).asBitmap().into(new SimpleTarget<Bitmap>(){
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    ImageSpan imageSpan = new ImageSpan(PostDetailActivity.this, resource);
                    //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                    String tempUrl = "[pic:" + finalI + "]";
                    //用ImageSpan对象替换你指定的字符串
                    int start=spannableString.toString().indexOf(tempUrl);
                    spannableString.setSpan(imageSpan, start, start+tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                    Log.i("hh", "onResourceReady: "+spannableString.toString());

                }
            });
        }
    }



    public void send_UserBean_FollowList(final UserBean userBean)
    {//添加followList中的好友

        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            Toast.makeText(this,"请先登陆！",Toast.LENGTH_SHORT).show();
            return;
        }

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
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        if(askUserBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askUserBean.getResponseCode());
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_sendUserBean_FollowList(userBean,callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void delete_UserBean_FollowList(final UserBean userBean)
    {//删除followList中的好友

        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            Toast.makeText(this,"请先登陆！",Toast.LENGTH_SHORT).show();
            return;
        }

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
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        if(askUserBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askUserBean.getResponseCode());
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_deleteUserBean_FollowList(userBean,callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
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
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        if(askUserBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askUserBean.getResponseCode());
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_downLoadUserBean(userBean,callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }






}
