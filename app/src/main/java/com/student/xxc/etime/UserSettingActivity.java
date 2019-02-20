package com.student.xxc.etime;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.student.xxc.etime.bean.TraceBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.FilePathHelper;
import com.student.xxc.etime.helper.PermissionHelper;
import com.student.xxc.etime.helper.UrlHelper;
import com.student.xxc.etime.impl.HttpConnection;
import com.student.xxc.etime.impl.JsonManager;
import com.student.xxc.etime.impl.TraceManager;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class UserSettingActivity extends AppCompatActivity {//用于设置账号信息

    private static final int REQUEST_CODE_SELECT_PIC = 120;
    private   MyHandler myhandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_setting);

        setInitialStyle();

        initUI();

    }


    private static class MyHandler extends Handler
    {
        private final WeakReference<UserSettingActivity> mActivity;

        public MyHandler(UserSettingActivity activity)
        {
            mActivity = new WeakReference<UserSettingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {//处理消息
            super.handleMessage(msg);
            if(mActivity.get()==null)
            {
                return ;
            }
            Bundle bundle  =msg.getData();
            int  response = bundle.getInt("response");
            mActivity.get().updateToast(response);

            if(response == User.LOGIN_RESPONSE_SUCCESSED)
            {
                String account = bundle.getString("account");
                Account.setState(Account.ACCOUNT_ONLINE);
                Account.setUserAccount(account);
                mActivity.get().downLoadUserImage();
                mActivity.get().updateAccount();
            }
            if(response == User.IMAGE_STORE_RESPONSE_SUCCESSED)
            {
                String imagePath = bundle.getString("imagePath");
                Account.setUserImagePath(imagePath);
                mActivity.get().updateAccount();
            }
            if(response == User.IMAGE_STORE_EXTRA_RESPONSE_SUCCESSED)
            {
                String userName = bundle.getString("userName");
                Account.setUserName(userName);
                mActivity.get().updateAccount();
            }
            if(response == User.IMAGE_DOWNLOAD_RESPONSE_SUCCESSED)
            {
                String userName = bundle.getString("userName");
                String imagePath = bundle.getString("imagePath");
                Account.setUserName(userName);
                Account.setUserImagePath(imagePath);
                mActivity.get().updateAccount();
            }
            if (response == TraceBean.UP_STORE_RESPONSE_SUCCESSED)
            {
                String json = bundle.getString("json");
                Log.i("json_response",json);
            }
            if(response == TraceBean.DOWN_LOAD_REUQEST_SUCCESSED)
            {
                String json = bundle.getString("json");
                Log.i("json_response",json);
                mActivity.get().updateTraces(json);
            }
        }
    }



    private  void updateToast(int response)//提示
    {
        switch (response)
        {
            case User.LOGIN_RESPONSE_SUCCESSED:
                Toast.makeText(this,"登陆成功",Toast.LENGTH_SHORT).show();
                break;
            case User.LOGIN_RESPONSE_FAILED:
                Toast.makeText(this,"登陆失败",Toast.LENGTH_SHORT).show();
                break;
            case User.REGISTER_RESPONSE_SUCCESSED:
                Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
                break;
            case User.REGISTER_RESPONSE_FAILED:
                Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_STORE_RESPONSE_FAILED:
                Toast.makeText(this,"头像上传失败",Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_STORE_RESPONSE_SUCCESSED:
                Toast.makeText(this,"头像上传成功",Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_DOWNLOAD_RESPONSE_FAILED:
                Toast.makeText(this,"下载失败",Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_DOWNLOAD_RESPONSE_SUCCESSED:
                Toast.makeText(this,"下载成功",Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_STORE_EXTRA_RESPONSE_FAILED:
                Toast.makeText(this,"信息上传失败",Toast.LENGTH_SHORT).show();
                break;
            case User.IMAGE_STORE_EXTRA_RESPONSE_SUCCESSED:
                Toast.makeText(this,"信息上传成功",Toast.LENGTH_SHORT).show();
                break;
            case  User.UNKNOWN_ERROR:
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                break;
            case  TraceBean.DOWN_LOAD_REUQEST_SUCCESSED:
                Toast.makeText(this,"日程下载成功",Toast.LENGTH_SHORT).show();
                break;
            case  TraceBean.DOWN_LOAD_REQUEST_FAILED:
                Toast.makeText(this,"日程下载失败",Toast.LENGTH_SHORT).show();
                break;
            case   TraceBean.UP_STORE_RESPONSE_FAILED:
                Toast.makeText(this,"日程上传失败",Toast.LENGTH_SHORT).show();
                break;
            case   TraceBean.UP_STORE_RESPONSE_SUCCESSED:
                Toast.makeText(this,"日程上传成功",Toast.LENGTH_SHORT).show();
                break;
            case   TraceBean.UNKNOWN_ERROR:
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public boolean isConnectingToInternet()//判断网络状态
    {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();//需要添加权限
        if(mNetworkInfo!=null)
        {
            return mNetworkInfo.isConnected();
        }
        else
        {
            return false;
        }
    }


    void updateButton(boolean isOnline)//更新按钮选项
    {
        Button button_user_setting_login = (Button)this.findViewById(R.id.button_user_setting_login);
        Button button_user_setting_exit = (Button)this.findViewById(R.id.button_user_setting_exit);
        if(!isOnline)
        {
            button_user_setting_login.setVisibility(View.VISIBLE);
            button_user_setting_exit.setVisibility(View.INVISIBLE);
        }
        else
        {
            button_user_setting_login.setVisibility(View.INVISIBLE);
            button_user_setting_exit.setVisibility(View.VISIBLE);
        }

    }

   void updateAccount()//更新用户信息
   {
       updateUserImage();//更新用户头像
       String userName = Account.getUserName();
       String account=Account.getUserAccount();
       int account_state = Account.getState();

       TextView user_setting_name =(TextView)this.findViewById(R.id.textView_user_setting_name);
       user_setting_name.setText(userName);

       TextView user_setting_account = (TextView)this.findViewById(R.id.textView_user_setting_account);
       if(account.isEmpty() || account_state==Account.ACCOUNT_OFFLINE)
       {
           if(account.isEmpty()) {
               user_setting_account.setText("未登录");
           }
           else
           {
               user_setting_account.setText(account+" - 未登录");
           }

           updateButton(false);

       }
       else
       {
           user_setting_account.setText(account+" - 登陆");

           updateButton(true);

       }

   }

    void initUI()//初始化界面
    {
        updateUserImage();//更新头像
        updateAccount();//更新文字和按钮
        final TextView user_setting_name =(TextView)this.findViewById(R.id.textView_user_setting_name);

        user_setting_name.setOnClickListener(new View.OnClickListener() {//同样点击修改称呼
            @Override
            public void onClick(View v) {
                //SelectIconHelper.showInputDialog(user_setting_name,UserSettingActivity.this);  没有使用工具类而是自己显示对话框  否则需要回调
                //upStoreUserImageExtra();//更新用户信息（除头像）  避免回调问题 1.29
                UserSettingActivity.this.showInputDialog(user_setting_name);
            }
        });


        Button button_user_setting_exit= (Button) this.findViewById(R.id.button_user_setting_exit);//用户退出
        button_user_setting_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Account.getState()==Account.ACCOUNT_ONLINE) {
                    Account.setState(Account.ACCOUNT_OFFLINE);
                    Account.setUserAccount("");//改正退出时设置的部分逻辑
                    Account.setUserImagePath("");
                    Toast.makeText(UserSettingActivity.this, "登出成功", Toast.LENGTH_SHORT).show();
                    UserSettingActivity.this.updateAccount();
                }

            }
        });


        Button button_user_setting_upStore_trace  = (Button)this.findViewById(R.id.button_user_setting_upStore_trace);//上传日程
        button_user_setting_upStore_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      upStoreTraces();
            }
        });


        Button button_user_setting_downLoad_trace = (Button)this.findViewById(R.id.button_user_setting_downLoad_trace);//下载日程
        button_user_setting_downLoad_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    downLoadTrace();
            }
        });



        Button button_user_setting_login = (Button) this.findViewById(R.id.button_user_setting_login);
        button_user_setting_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 View view = LayoutInflater.from(UserSettingActivity.this).inflate(R.layout.dialog_login,null,false);
                 final AlertDialog dialog_login = new AlertDialog.Builder(UserSettingActivity.this).setView(view).create();
                 dialog_login.setTitle("用户登陆");

                 Button button_login_cancel = (Button)view.findViewById(R.id.button_login_cancel);
                 button_login_cancel.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         dialog_login.dismiss();
                     }
                 });

                 TextView textView_login_register = (TextView)view.findViewById(R.id.textView_login_register);//点击显示注册界面
                 textView_login_register.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         View view_register = LayoutInflater.from(UserSettingActivity.this).inflate(R.layout.dialog_register,null,false);
                         final AlertDialog dialog_register = new AlertDialog.Builder(UserSettingActivity.this).setView(view_register).create();
                         dialog_register.setTitle("用户注册");

                         Button button_register_cancel = (Button)view_register.findViewById(R.id.button_register_cancel);
                         button_register_cancel.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 dialog_register.dismiss();
                             }
                         });

                         Button button_register_confirm = (Button)view_register.findViewById(R.id.button_register_confirm);
                         button_register_confirm.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {

                                 dialog_login.dismiss();
                                 EditText edit_name = (EditText)dialog_register.findViewById(R.id.editText_register_account);
                                 EditText edit_password = (EditText)dialog_register.findViewById(R.id.editText_register_password);
                                 final String name = edit_name.getText().toString();
                                 final String password  = edit_password.getText().toString();

                                 Log.i("UserSettingActivity","name:"+name+"  password:"+password);

                                 if(name.isEmpty() || password.isEmpty())
                                 {
                                     Toast.makeText(UserSettingActivity.this,"用户名或密码不能为空！",Toast.LENGTH_SHORT).show();
                                     return ;
                                 }

                                 if(!isConnectingToInternet())
                                 {
                                     Toast.makeText(UserSettingActivity.this,"请检查网络状态！",Toast.LENGTH_LONG).show();
                                     return ;
                                 }

                                 Runnable runnable = new Runnable() {
                                     @Override
                                     public void run() {

                                         Callback callback =new Callback()
                                         {
                                             @Override
                                             public void onFailure(Call call, IOException e) {
                                                 Log.d("POSTERROR",""+e);
                                             }

                                             @Override
                                             public void onResponse(Call call, Response response) throws IOException{
                                                 Log.d("POST",response.protocol()+" "+
                                                         response.code()+" "+response.message());
                                                 Headers headers = response.headers();
                                                 for (int i=0;i<headers.size();i++) {
                                                     Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                                                 }
                                                 String json = response.body().string();
                                                 Log.d("tag","onResponse"+json);
                                                 User askUser = null;
                                                 try {
                                                     askUser = JsonManager.JsonToUser(json);
                                                 }catch (Exception e)
                                                 {
                                                     Log.i("jsonError",e+"");
                                                     Looper.prepare();
                                                     Bundle  bundle = new Bundle();
                                                     bundle.putInt("response",User.UNKNOWN_ERROR);
                                                     Message  message = myhandler.obtainMessage();
                                                     message.setData(bundle);
                                                     message.sendToTarget();
                                                     Looper.loop();
                                                     return ;
                                                 }

                                                 Looper.prepare();
                                                 Bundle  bundle = new Bundle();
                                                 bundle.putInt("response",askUser.getResponseCode());
                                                 Message  message = myhandler.obtainMessage();
                                                 message.setData(bundle);
                                                 //myhandler.sendMessage(message);
                                                 message.sendToTarget();
                                                 Log.i("thread","-----------------------------send message");
                                                 Looper.loop();
                                             }
                                         };
                                         HttpConnection.sendOkHttpRequest_register(name,password,callback);
                                     }
                                 };
                                 Log.i("UserSettingActivity","--------------------------new thread");
                                 Thread thread = new Thread(runnable);
                                 thread.start();

                                 dialog_register.dismiss();

                             }
                         });

                         PermissionHelper.checkNetWorkPermission(UserSettingActivity.this);
                         dialog_register.show();
                     }

                     });

                 Button button_login_confirm = (Button)view.findViewById(R.id.button_login_confirm);
                 button_login_confirm.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         EditText edit_name = (EditText)dialog_login.findViewById(R.id.editText_login_account);
                         EditText edit_password = (EditText)dialog_login.findViewById(R.id.editText_login_password);
                         final String name = edit_name.getText().toString();
                         final String password  = edit_password.getText().toString();

                         if(name.isEmpty() || password.isEmpty())
                         {
                             Toast.makeText(UserSettingActivity.this,"用户名或密码不能为空！",Toast.LENGTH_SHORT).show();
                             return ;
                         }

                         if(!isConnectingToInternet())
                         {
                             Toast.makeText(UserSettingActivity .this,"请检查网络状态！",Toast.LENGTH_LONG).show();
                             return ;
                         }

                         Runnable runnable = new Runnable() {
                             @Override
                             public void run() {

                                 Callback callback =new Callback()
                                 {
                                     @Override
                                     public void onFailure(Call call, IOException e) {
                                         Log.d("POSTERROR",""+e);
                                     }

                                     @Override
                                     public void onResponse(Call call, Response response) throws IOException{
                                         Log.d("POST",response.protocol()+" "+
                                                 response.code()+" "+response.message());
                                         Headers headers = response.headers();
                                         for (int i=0;i<headers.size();i++) {
                                             Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                                         }
                                         String json = response.body().string();
                                         Log.d("tag","onResponse"+json);

                                         User askUser = null;
                                         try {
                                             askUser = JsonManager.JsonToUser(json);
                                         }catch (Exception e)
                                         {
                                             Log.i("jsonError",e+"");
                                             Looper.prepare();
                                             Bundle  bundle = new Bundle();
                                             bundle.putInt("response",User.UNKNOWN_ERROR);
                                             Message  message = myhandler.obtainMessage();
                                             message.setData(bundle);
                                             message.sendToTarget();
                                             Looper.loop();
                                             return ;
                                         }

                                         Looper.prepare();
                                         Bundle  bundle = new Bundle();
                                         bundle.putInt("response",askUser.getResponseCode());
                                         bundle.putString("account",askUser.getName());//获得用户账号
                                         Message  message = myhandler.obtainMessage();
                                         message.setData(bundle);
                                         //myhandler.sendMessage(message);
                                         message.sendToTarget();
                                         Log.i("thread","-----------------------------send message");
                                         Looper.loop();
                                     }
                                 };
                                 HttpConnection.sendOkHttpRequest_login(name,password,callback);
                             }
                         };
                         Log.i("UserSettingActivity","--------------------------new thread");
                         Thread thread = new Thread(runnable);
                         thread.start();

                         dialog_login.dismiss();
                     }
                 });

                 PermissionHelper.checkNetWorkPermission(UserSettingActivity.this);
                 dialog_login.show();
            }

        });

    }

    void updateUserImage()//刷新用户头像和背景
    {
        ImageView user_setting_background = (ImageView)this.findViewById(R.id.imageView_user_setting_background);
        String imagePath = Account.getUserImagePath();
        ImageView user_setting_image = (ImageView)this.findViewById(R.id.imageView_user_setting_image);

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

        if(!this.isDestroyed())//防止出现glide的一个bug
        {

            if (!Account.getUserImagePath().isEmpty())//网络地址有效
            {
                Log.i("glide", "network");
                Glide.with(this).load(Account.getUserImagePath())//背景
                        .dontAnimate()
                        .error(R.mipmap.ic_launcher)
                        .bitmapTransform(new BlurTransformation(this, 25, 3), new CenterCrop(this))
                        .into(user_setting_background);//使用glide包处理图片实现图片磨砂效果

                Glide.with(this)//头像
                        .load(Account.getUserImagePath())
                        .listener(mRequestListener)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(user_setting_image);
            } else {
                if ((new File(Account.getUserLocalImagePath()).exists()))//本地图片地址有效
                {
                    Log.i("glide", "local");
                    Glide.with(this).load(Account.getUserLocalImagePath())//背景
                            .dontAnimate()
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new BlurTransformation(this, 25, 3), new CenterCrop(this))
                            .into(user_setting_background);//使用glide包处理图片实现图片磨砂效果

                    Glide.with(this)//头像
                            .load(Account.getUserLocalImagePath())
                            .listener(mRequestListener)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .into(user_setting_image);
                } else //默认情况
                {
                    Log.i("glide", "default");
                    Glide.with(this).load(R.mipmap.ic_launcher)//背景
                            .dontAnimate()
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new BlurTransformation(this, 25, 3), new CenterCrop(this))
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


        user_setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.checkPermission(UserSettingActivity.this);
                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
            }
        });

    }


    void setInitialStyle()//初始化风格
    {
        //        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        int currentNightMode=getIntent().getIntExtra("mode", Configuration.UI_MODE_NIGHT_NO);


        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);

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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode==REQUEST_CODE_SELECT_PIC)//修改头像
        {
            // 获取选择的图片
            if(data!=null) {
                Uri selectedImage = data.getData();
                // 获取到图片的路径
                String selectedImagePath = null;
                if (selectedImage != null) {
                    selectedImagePath = FilePathHelper.getFilePathByUri(UserSettingActivity.this,selectedImage);
                }

                //  Account.setUserImagePath(selectedImagePath);
                Account.setUserLocalImagePath(selectedImagePath);
                updateUserImage(); //本地选择图片的弥补
                upStoreUserImage(selectedImagePath);

                Log.i("imagepath","--------------------------------"+selectedImagePath);
                //updateUserImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void upStoreUserImage(String userImagePath)//网络上传用户头像
    {
        if(Account.getUserAccount().isEmpty())//账户不存在时 2.1
        {
            return ;
        }
        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            Toast.makeText(UserSettingActivity.this,"请登陆！",Toast.LENGTH_LONG).show();
            return ;
        }

        final String userAccount =  Account.getUserAccount();
        final File userImage = new File(userImagePath);

        if(!isConnectingToInternet())
        {
            Toast.makeText(UserSettingActivity.this,"请检查网络状态！",Toast.LENGTH_LONG).show();
            return ;
        }

        if(!userImage.exists()) //文件不存在返回
        {
            return ;
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
                        for (int i=0;i<headers.size();i++) {
                            Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                        }

                        String json = response.body().string();
                        Log.d("tag","onResponse"+json);

                        User askUser = null;
                        try {
                            askUser = JsonManager.JsonToUser(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",User.UNKNOWN_ERROR);
                            Message  message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        String url = "";
                        if(askUser.getImagePath() != null) { //路径非空添加前缀
                            url = UrlHelper.getUrl_base() + askUser.getImagePath();
                        }
                        Log.i("tag", url);

                        Looper.prepare();
                        Bundle  bundle = new Bundle();
                        bundle.putInt("response",askUser.getResponseCode());
                        bundle.putString("imagePath",url);
                        Message  message = myhandler.obtainMessage();
                        message.setData(bundle);
                        message.sendToTarget();
                        Log.i("thread","-----------------------------send message");
                        Looper.loop();

                    }
                };

                HttpConnection.sendOkHttpRequest_sendUserImage(userAccount,callback,userImage);
            }
        };

        Log.i("UserSettingActivity","--------------------------new thread");
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void upStoreUserImageExtra()//网络上传用户信息（除头像）
    {
        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            Toast.makeText(UserSettingActivity.this,"请登陆！",Toast.LENGTH_LONG).show();
            return ;
        }

        final String userAccount =  Account.getUserAccount();
        final String userName = Account.getUserName();

        if(!isConnectingToInternet())
        {
            Toast.makeText(UserSettingActivity.this,"请检查网络状态！",Toast.LENGTH_LONG).show();
            return ;
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
                        for (int i=0;i<headers.size();i++) {
                            Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                        }

                        String json = response.body().string();
                        Log.d("tag","onResponse"+json);

                        User askUser = null;
                        try {
                            askUser = JsonManager.JsonToUser(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",User.UNKNOWN_ERROR);
                            Message  message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }


                        Looper.prepare();
                        Bundle  bundle = new Bundle();
                        bundle.putInt("response",askUser.getResponseCode());
                        bundle.putString("userName",askUser.getNickName());
                        Message  message = myhandler.obtainMessage();
                        message.setData(bundle);
                        message.sendToTarget();
                        Log.i("thread","-----------------------------send message");
                        Looper.loop();

                    }
                };

                HttpConnection.sendOkHttpRequest_sendUserExtraImage(userAccount,userName,callback);
            }
        };

        Log.i("UserSettingActivity","--------------------------new thread");
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void downLoadUserImage()//用户下载用户信息
    {
        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            return;
        }

        if(!isConnectingToInternet())
        {
            Toast.makeText(UserSettingActivity.this,"请检查网络状态！",Toast.LENGTH_LONG).show();
            return ;
        }

        final String userAccount = Account.getUserAccount();

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
                        for (int i=0;i<headers.size();i++) {
                            Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                        }

                        String json = response.body().string();
                        Log.d("tag","onResponse"+json);

                        User askUser = null;
                        try {
                            askUser = JsonManager.JsonToUser(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",User.UNKNOWN_ERROR);
                            Message  message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        String url = "";//对于回应结果的预处理
                        if(askUser.getImagePath() != null) { //路径非空添加前缀
                            url = UrlHelper.getUrl_base() + askUser.getImagePath();
                        }
                        Log.i("tag", url);

                        Looper.prepare();
                        Bundle  bundle = new Bundle();
                        bundle.putInt("response",askUser.getResponseCode());
                        bundle.putString("imagePath",url);
                        bundle.putString("userName",askUser.getNickName());
                        Message  message = myhandler.obtainMessage();
                        message.setData(bundle);
                        message.sendToTarget();
                        Log.i("thread","-----------------------------send message");
                        Looper.loop();
                    }
                };

                HttpConnection.sendOkHttpRequest_image_downLoad(userAccount,callback);
            }
        };

        Log.i("UserSettingActivity","--------------------------new thread");
        Thread thread = new Thread(runnable);
        thread.start();

    }


    public  void showInputDialog(final TextView username) { //输入对话框   没有使用工具类  否则需要写回调接口 1.29
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("输入您的昵称").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user_name=editText.getText().toString();
                if(user_name.equals(""))
                    return;
                username.setText(user_name);
                Account.setUserName(user_name);
                UserSettingActivity.this.upStoreUserImageExtra();
            }
        }).show();
    }

    private  void  upStoreTraces()
    {
        final TraceBean traceBean = TraceManager.getTraces();

        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            Toast.makeText(UserSettingActivity.this,"请登陆后使用",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isConnectingToInternet())
        {
            Toast.makeText(UserSettingActivity.this,"请检查网络状态！",Toast.LENGTH_LONG).show();
            return ;
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
                        for (int i=0;i<headers.size();i++) {
                            Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                        }

                        String json = response.body().string();
                        Log.d("tag","onResponse"+json);

                        TraceBean traceBean = new TraceBean();
                        try {
                            traceBean = JsonManager.JsonToTraceBean(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",TraceBean.UNKNOWN_ERROR);
                            Message  message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        Looper.prepare();
                        Bundle  bundle = new Bundle();
                        bundle.putInt("response", traceBean.getResponseCode());
                        bundle.putString("json",json); //绕过序列化的取巧方法
                        Message  message = myhandler.obtainMessage();
                        message.setData(bundle);
                        message.sendToTarget();
                        Log.i("thread","-----------------------------send message");
                        Looper.loop();

                    }
                };
                HttpConnection.sendOkHttpRequest_sendTraces(traceBean,callback);
            }
        };

        Log.i("UserSettingActivity","--------------------------new thread");
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void downLoadTrace()
    {
         final TraceBean traceBean = new TraceBean();
        if(Account.getState()!=Account.ACCOUNT_ONLINE)
        {
            Toast.makeText(UserSettingActivity.this,"请登陆后使用",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isConnectingToInternet())
        {
            Toast.makeText(UserSettingActivity.this,"请检查网络状态！",Toast.LENGTH_LONG).show();
            return ;
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
                        for (int i=0;i<headers.size();i++) {
                            Log.d("HEAD", headers.name(i) + ":" + headers.value(i));
                        }

                        String json = response.body().string();
                        Log.d("tag","onResponse"+json);

                        TraceBean traceBean = new TraceBean();
                        try {
                            traceBean = JsonManager.JsonToTraceBean(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",TraceBean.UNKNOWN_ERROR);
                            Message  message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        Looper.prepare();
                        Bundle  bundle = new Bundle();
                        bundle.putInt("response", traceBean.getResponseCode());
                        bundle.putString("json",json); //绕过序列化的取巧方法
                        Message  message = myhandler.obtainMessage();
                        message.setData(bundle);
                        message.sendToTarget();
                        Log.i("thread","-----------------------------send message");
                        Looper.loop();

                    }
                };
                HttpConnection.sendOkHttpRequest_downLoadTraces(Account.getUserAccount(),traceBean,callback);
            }
        };

        Log.i("UserSettingActivity","--------------------------new thread");
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private void updateTraces(String json)//调用修改数据库
    {
        TraceBean traceBean = JsonManager.JsonToTraceBean(json);
        TraceManager.resetTraces(traceBean);
    }
}
