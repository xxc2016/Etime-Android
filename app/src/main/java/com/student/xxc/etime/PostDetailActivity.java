package com.student.xxc.etime;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.student.xxc.etime.entity.Post;
import com.student.xxc.etime.entity.PostDetail;
import com.student.xxc.etime.entity.Remark;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.FilePathHelper;
import com.student.xxc.etime.helper.GlideCirlceTransHelper;
import com.student.xxc.etime.helper.PermissionHelper;
import com.student.xxc.etime.helper.RemarkAdapter;
import com.student.xxc.etime.helper.UrlHelper;
import com.student.xxc.etime.impl.HttpConnection;
import com.student.xxc.etime.impl.JsonManager;
import com.student.xxc.etime.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private List<Uri> remarkPicPathList  =new ArrayList<Uri>();//图片路径
    private static final int SELECT_REMARK_PIC = 555;//选择图片
    private AlertDialog  waitDialog = null;//等待对话框



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
                mActivity.get().askUpdateFollowListButton();//请求更新按钮
                mActivity.get().updateRemark(postDetailBean);//更新评论信息

            }

            if(response ==UserBean.USER_DOWN_LOAD_RESPONSE_SUCCESSED)
            {
                String json = bundle.getString("json");
                UserBean userBean = JsonManager.JsonToUserBean(json);
                mActivity.get().updateFollowListButton(userBean);
            }

            if(response == UserBean.USER_UP_FOLLOWLIST_RESPONSE_SUCCESSED)
            {
                mActivity.get().askUpdateFollowListButton();
            }

            if(response == UserBean.USER_DELETE_FOLLOWLIST_RESPONSE_SUCCESSED)
            {
                mActivity.get().askUpdateFollowListButton();
            }

            if(response == RemarkBean.REMARK_UP_STORE_RESPONSE_SUCCESSED)
            {
                mActivity.get().stopWaitDialog();
                mActivity.get().updatePostDetailBean();//更新界面
            }

            if(response== RemarkBean.REMARK_UP_STORE_RESPONSE_FAILED)
            {
                mActivity.get().stopWaitDialog();
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
            case UserBean.USER_DOWN_LOAD_RESPONSE_SUCCESSED:
                Toast.makeText(this,"用户信息下载成功",Toast.LENGTH_SHORT).show();
                break;
            case UserBean.USER_DOWN_LOAD_RESPONSE_FAILED:
                Toast.makeText(this,"用户信息下载失败",Toast.LENGTH_SHORT).show();
                break;
            case RemarkBean.REMARK_UP_STORE_RESPONSE_SUCCESSED:
                Toast.makeText(this,"发表评论成功",Toast.LENGTH_SHORT).show();
                break;
            case RemarkBean.REMARK_UP_STORE_RESPONSE_FAILED:
                Toast.makeText(this,"发表失败失败",Toast.LENGTH_SHORT).show();
                break;
            case RemarkBean.UNKNOWN_ERROR:
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
       // initRemarkData();

        initView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.setRemark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAdd(view);
            }
        });//添加评论
        ImageButton button=(ImageButton)findViewById(R.id.set_remark_setPic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {//分发点击事件
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            CardView v = findViewById(R.id.setRemarkCardView);
            if(isShouldHide(v,ev)){//点击添加评论cardview以外位置控件消失
                v.setVisibility(View.GONE);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHide(View v, MotionEvent event) {
        if (v != null && (v instanceof CardView)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void actionAdd(View view) {//添加评论
        CardView cardView=findViewById(R.id.setRemarkCardView);
        if(cardView.getVisibility()==View.GONE){
            cardView.setVisibility(View.VISIBLE);
        }else{
            TextView textView=(TextView)cardView.findViewById(R.id.setRemarkText);
            if(textView.getText().toString().equals("")) {
                Snackbar.make(view, "评论不可为空", Snackbar.LENGTH_SHORT).show();
                return;
            }

            RemarkBean remarkBean = new RemarkBean();
            String userAccount = Account.getUserAccount();
            RemarkBean.User user = new RemarkBean.User();
            user.account = userAccount;
            remarkBean.setUser(user);//设置用户
            String content = textView.getText().toString();
            remarkBean.setContent(content);//获得评论内容

            remarkBean.setDetailId(postDetail.getDetailId());//设置detailId

            SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
            Date tempDate = Calendar.getInstance().getTime();
            String date = df_date.format(tempDate);
            SimpleDateFormat df_time = new SimpleDateFormat("HH:mm");
            String time = df_time.format(tempDate);
            remarkBean.setDate(date);
            remarkBean.setTime(time);//设置时间日期

            LinkedList<File>  files =  new LinkedList<File>();
            for(int i=0;i<remarkPicPathList.size();i++)
            {
                //if(isDelete(i,content)) continue;
                Uri imageUri  =remarkPicPathList.get(i);
                String imagePath = FilePathHelper.getFilePathByUri(PostDetailActivity.this,imageUri);
                try {
                    File file = new File(imagePath);
                    files.add(file);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            sendRemarkBean(remarkBean,files);
            showWaitDialog();

            textView.setText("");
            cardView.setVisibility(View.GONE);
            remarkPicPathList.clear();//清空评论图片
        }
    }

    public void getImage() {
        PermissionHelper.checkPermission(PostDetailActivity.this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_REMARK_PIC);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_REMARK_PIC) {
            if (data != null) {
                ContentResolver cr = this.getContentResolver();
                Uri selectedImage = data.getData();

                Bitmap originalBitmap = null;
                try {
                    originalBitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImage));
                    Bitmap bitmap = ImageUtil.resizeImage(originalBitmap,200f,480f);//屏幕比例缩放，质量压缩
                    remarkPicPathList.add(selectedImage);
                    insertPic(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void insertPic(Bitmap bitmap){//imagespan图文混合
        EditText et_detail=(EditText)findViewById(R.id.setRemarkText);
        ImageSpan imageSpan = new ImageSpan(PostDetailActivity.this, bitmap);
        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "[pic:"+(remarkPicPathList.size()-1)+"]";
        SpannableString spannableString = new SpannableString(tempUrl);
        //用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //将选择的图片追加到EditText中光标所在位置
        int index = et_detail.getSelectionStart(); //获取光标所在位置
        Editable edit_text = et_detail.getEditableText();
        if(index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        }
        else
        {
            edit_text.insert(index, spannableString);
        }
        // System.out.println("插入的图片：" + spannableString.toString());
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
        if(postDetail.getUser().getNickName()==null)//缺省信息处理2.18
        {
            nickName.setText("用户");
        }else {
            nickName.setText(postDetail.getUser().getNickName());//用户名
        }

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
        int sub=-1;
        for(int i=0;i<bitmaps.size();i++) {
            //存在删除图片后，[pic:]可能不从0开始,也可能中间少数
            sub+=1;
            String tmpSub = "[pic:" + sub + "]";
            while(!content.contains(tmpSub)){
                tmpSub = "[pic:" + (++sub) + "]";
            }
            final int finalI = sub;
            Glide.with(this).load(bitmaps.get(i)).asBitmap().into(new SimpleTarget<Bitmap>(){
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    resource= ImageUtil.resizeImage(resource,800f,480f);
                    ImageSpan imageSpan = new ImageSpan(PostDetailActivity.this, resource);
                    //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                    String tempUrl = "[pic:" + finalI + "]";
                    //用ImageSpan对象替换你指定的字符串
                    int start=spannableString.toString().indexOf(tempUrl);
                    spannableString.setSpan(imageSpan, start, start+tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                    Log.i("PDA", "onResourceReady: "+spannableString.toString());

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


    private void askUpdateFollowListButton()//通过请求用户列表来刷新关注按钮
    {
        if(Account.getState() ==Account.ACCOUNT_ONLINE) {
            UserBean userBean = new UserBean();
            userBean.setAccount(Account.getUserAccount());
            getFollowList(userBean);
        }
    }

    private  void updateFollowListButton(UserBean userBean)
    {
        if(userBean==null || userBean.getFollowList()==null || postDetail==null)
        {
            Log.i("update","return");
            return ;
        }

        List<String>  followList = userBean.getFollowList();
        boolean isFollow = false;
        for(int i=0;i<followList.size();i++)
        {
            Log.i("update",""+followList.get(i)+"    :"+postDetail.getUser().getName());
            if(followList.get(i).equals(postDetail.getUser().getName()))
            {
                isFollow = true;
                break;
            }
        }

        Button button_follow = this.findViewById(R.id.follow);

        if(isFollow==true)
        {
            button_follow.setText("已关注");
            button_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(postDetail !=null)
                    {
                        UserBean userBean = new UserBean();
                        userBean.setAccount(Account.getUserAccount());
                        LinkedList<String> addList =  new LinkedList<String>();
                        addList.add(postDetail.getUser().getName());
                        userBean.setFollowList(addList);
                        delete_UserBean_FollowList(userBean);
                    }
                }
            });
        }else
        {
            button_follow.setText("+关注");
            button_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        }


    }


    private void  getFollowList(final UserBean userBean)
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
                            bundle.putString("json",JsonManager.UserBeanToJson(askUserBean));
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






    private  void sendRemarkBean(final RemarkBean remarkBean,final List<File>  files)//发送评论
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

                        RemarkBean askRemarkBean = null;
                        try {
                            askRemarkBean = JsonManager.JsonToRemarkBean(json);
                        } catch (Exception e) {
                            Log.i("jsonError", e + "");
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", RemarkBean.UNKNOWN_ERROR);
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return;
                        }

                        if (askRemarkBean != null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askRemarkBean.getResponseCode());
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };
                HttpConnection.sendOkHttpRequest_sendRemarkBean(remarkBean, callback,files);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public void showWaitDialog()
    {
        View view_wait = LayoutInflater.from(PostDetailActivity.this).inflate(R.layout.dialog_wait,null,false);
        waitDialog = new AlertDialog.Builder(PostDetailActivity.this).setView(view_wait).create();
        waitDialog.setTitle("帖子发送中");
        waitDialog.show();
    }

    public void stopWaitDialog()
    {
        if(waitDialog!=null) {
            waitDialog.dismiss();
        }
    }

    private boolean isDelete(int i,String content) {
        String tmp="[pic:"+i+"]";
        return !content.contains(tmp);
    }


    private  void updateRemark(PostDetailBean postDetailBean)//更新评论信息
    {
        remarkList.clear();
        List<PostDetailBean.Remark> remarkList_1  =postDetailBean.getRemarkList();
        if(remarkList_1==null)  return;

        for(int i=0;i<remarkList_1.size();i++)
        {
            PostDetailBean.Remark r = remarkList_1.get(i);
            User user = new User();
            user.setName(r.user.account);
            user.setNickName(r.user.nickName);
            user.setImagePath(UrlHelper.getUrl_base()+r.user.head);//路径补全

            List<String>  bitmapPath_2 = r.bitmapPath;
            if(bitmapPath_2!=null)
            {
                for(int j=0;j<bitmapPath_2.size();j++)
                {
                    bitmapPath_2.set(j, UrlHelper.getUrl_base()+bitmapPath_2.get(j));//路径补全
                }
            }

            Remark remark = new Remark(user,r.content,bitmapPath_2,r.date,r.time,r.remarkId,r.detailId);
            remarkList.add(remark);
        }
        adapter.notifyDataSetChanged();
    }

    private void updatePostDetailBean()//发表评论之后刷新帖子
    {
        if(postDetail==null)
        {
            return;
        }

        initPostDetailData(postDetail.getDetailId());
    }
}
