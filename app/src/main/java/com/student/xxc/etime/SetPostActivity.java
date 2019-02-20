package com.student.xxc.etime;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.PostDetailBean;
import com.student.xxc.etime.bean.RemarkBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.helper.FilePathHelper;
import com.student.xxc.etime.helper.PermissionHelper;
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
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class SetPostActivity extends AppCompatActivity {

    private static final int SELECT_POST_PIC = 544;//选择图片
    private List<Uri> picPathList  =new ArrayList<Uri>();//图片路径

    private MyHandler myhandler = new MyHandler(this);
    private AlertDialog  waitDialog = null;//等待对话框


    private static class MyHandler extends Handler {
        private final WeakReference<SetPostActivity> mActivity;

        public MyHandler(SetPostActivity activity) {
            mActivity = new WeakReference<SetPostActivity>(activity);
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
            if(response  == PostDetailBean.POST_DETAIL_UP_STORE_RESPONSE_SUCCESSED)
            {
                mActivity.get().goToCommunityActivity();//发帖成功返回之前页面
                mActivity.get().stopWaitDialog();
            }

        }
    }


    private void updateToast(int response)//提示
    {
        switch (response) {
            case PostDetailBean.POST_DETAIL_UP_STORE_RESPONSE_SUCCESSED:
                Toast.makeText(this,"详细帖子上传成功",Toast.LENGTH_SHORT).show();
                break;
            case PostDetailBean.POST_DETAIL_UP_STORE_RESPONSE_FAILED:
                Toast.makeText(this,"详细帖子上传失败",Toast.LENGTH_SHORT).show();
                break;
            case PostDetailBean.UNKNOWN_ERROR:
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                break;

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_set_post);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        ImageButton button=(ImageButton)findViewById(R.id.set_post_setPic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImageView pic= (ImageView)findViewById(R.id.set_post_upPic);
//                pic.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_lock));
                getImage();
            }
        });

        final EditText et_detail=(EditText)findViewById(R.id.set_post_editPost);
        Button upPic=(Button)findViewById(R.id.set_post_send);
        upPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//点击发送帖子
                Log.i("content",et_detail.getText().toString());
                if(Account.getState()!=Account.ACCOUNT_ONLINE)
                {
                    Toast.makeText(SetPostActivity.this,"请登陆",Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
                Date tempDate = Calendar.getInstance().getTime();
                String date_test = df_date.format(tempDate);
                SimpleDateFormat df_time = new SimpleDateFormat("HH:mm");
                String time_test = df_time.format(tempDate);

                PostDetailBean postDetailBean = new PostDetailBean();
                postDetailBean.setDate(date_test);
                postDetailBean.setTime(time_test);//获得日期时间

                EditText set_post_title = (EditText) SetPostActivity.this.findViewById(R.id.set_post_title);
                String title = set_post_title.getText().toString();
                postDetailBean.setTitle(title);
                EditText set_post_editPost = (EditText)SetPostActivity.this.findViewById(R.id.set_post_editPost);
                String content = set_post_editPost.getText().toString();
                postDetailBean.setContent(content);//获得内容和标题

                String userAccount = Account.getUserAccount();
                PostDetailBean.Remark.User user = new PostDetailBean.Remark.User();
                user.account = userAccount;
                postDetailBean.setUser(user);//获得用户账号

                LinkedList<File>  files =  new LinkedList<File>();
                for(int i=0;i<picPathList.size();i++)
                {
                    if(isDelete(i,content)) continue;
                    Uri imageUri  =picPathList.get(i);
                    String imagePath = FilePathHelper.getFilePathByUri(SetPostActivity.this,imageUri);
                    try {
                        File file = new File(imagePath);
                        files.add(file);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    //Log.i("imagePath",imagePath);
                }

                sendPostDetailBean(postDetailBean,files);
                showWaitDialog();
            }
        });

    }

    private boolean isDelete(int i,String content) {
        String tmp="[pic:"+i+"]";
        return !content.contains(tmp);
    }

    public void insertPic(Bitmap bitmap){//imagespan图文混合
        EditText et_detail=(EditText)findViewById(R.id.set_post_editPost);
        ImageSpan imageSpan = new ImageSpan(SetPostActivity.this, bitmap);
        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "[pic:"+(picPathList.size()-1)+"]";
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

    public void getImage() {
        PermissionHelper.checkPermission(SetPostActivity.this);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_POST_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_POST_PIC) {
            if (data != null) {
                ContentResolver cr = this.getContentResolver();
                Uri selectedImage = data.getData();

                Bitmap originalBitmap = null;
                try {
                    originalBitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImage));
                    Bitmap bitmap = ImageUtil.resizeImage(originalBitmap,800f,480f);//屏幕比例缩放，质量压缩
//                    bitmapList.add(bitmap);
                    picPathList.add(selectedImage);
                    insertPic(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private  void sendPostDetailBean(final PostDetailBean postDetailBean,final List<File> files)//发送帖子详细类
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

                        PostDetailBean askPostDetailBean = null;
                        try {
                            askPostDetailBean = JsonManager.JsonToPostDetailBean(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",PostDetailBean.UNKNOWN_ERROR);
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        if(askPostDetailBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askPostDetailBean.getResponseCode());
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };
                HttpConnection.sendOkHttpRequest_sendPostDetail(postDetailBean,callback,files);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }


    private void  goToCommunityActivity()
    {
        Intent intent = new Intent();
        intent.setClass(SetPostActivity.this,CommunityActivity.class);
        startActivity(intent);
        finish();
    }

    public void showWaitDialog()
    {
        View view_wait = LayoutInflater.from(SetPostActivity.this).inflate(R.layout.dialog_wait,null,false);
        waitDialog = new AlertDialog.Builder(SetPostActivity.this).setView(view_wait).create();
        waitDialog.setTitle("帖子发送中");
        waitDialog.show();
    }

    public void stopWaitDialog()
    {
        if(waitDialog!=null) {
            waitDialog.dismiss();
        }
    }


}
