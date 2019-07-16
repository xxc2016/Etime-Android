package com.student.xxc.etime.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.student.xxc.etime.R;
import com.student.xxc.etime.bean.SiteBean;
import com.student.xxc.etime.bean.TraceBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.entity.Trace;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.BitmapTranslateHelper;
import com.student.xxc.etime.helper.GlideCirlceTransHelper;
import com.student.xxc.etime.adapter.MyItemTouchHelperCallback;
import com.student.xxc.etime.helper.MapTimeHelper;
import com.student.xxc.etime.helper.PermissionHelper;
import com.student.xxc.etime.helper.PushService;
import com.student.xxc.etime.helper.SelectIconHelper;
import com.student.xxc.etime.adapter.TimeLineAdapter;
import com.student.xxc.etime.helper.SiteHelper;
import com.student.xxc.etime.helper.TimeCalculateHelper;
import com.student.xxc.etime.helper.TraceItemTouchHelper;
import com.student.xxc.etime.helper.TraceSQLiteOpenHelper;
import com.student.xxc.etime.helper.shareView;
import com.student.xxc.etime.impl.GetRxJavaTrace_Interface;
import com.student.xxc.etime.impl.TraceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import okhttp3.MediaType;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerView;
    private static List<Trace>traceList=new ArrayList<>();
    private TraceItemTouchHelper touchHelper;
    private TimeLineAdapter adapter;
    private String nowDate;  //用来限定今天时间
    private Boolean showFinished=false;//用来显示是否显示完成时间
    private Boolean useIntellectSort =false;//控制是否进行智能排序
    private LinearLayoutManager manager=new LinearLayoutManager(this);
    private static final int REQUEST_CODE_SELECT_PIC = 120;
    private ImageView imageView = null;
    private boolean titleType=false;//标题默认显示周几
    private PushService pushService=new PushService();
    private shareView  shareUnit;
    private Bitmap userHead = null;//头像缓存引用
    private   MyHandler myhandler = new MyHandler(this);
    private MapTimeHelper mapTimeHelper = new MapTimeHelper(this,myhandler);
    private static final int REQUEST_CODE_INIT_DATA = 666;
    ////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        initDate();//更新今天日期

        initAccount();//初始化账户  完善更新顺序2.1
        initData(null);

        askSiteImage(false);//询问获得经纬度

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAdd();
            }
        });


        FloatingActionButton fab_share = (FloatingActionButton)findViewById(R.id.fab_share);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTraces();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.personal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {//侧滑栏图标
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//侧滑栏初始化
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        /////////////////////////////////////////////////////////////
//        SharedPreferences sharedPreferences=getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
//        String imagePath = sharedPreferences.getString("selectedImagePath", "");
//        String user_name=sharedPreferences.getString("user_name","用户");
//        username.setOnClickListener(new View.OnClickListener() {   //取消侧滑栏改用户名  改到个人中心  1.29
//            @Override
//            public void onClick(View v) {
//                SelectIconHelper.showInputDialog(username,MainActivity.this);
//            }
//        });
//        imageView.setOnClickListener(new View.OnClickListener() {  //取消侧滑栏改头像  改到个人中心  1.29
//            @Override
//            public void onClick(View v) {
//                PermissionHelper.checkPermission(MainActivity.this);
//                selectPicture();
//            }
//        });

        updateUserImage();//统一归纳到一个函数 2.1

       initShareUnit();
    }

    public static class MyHandler extends Handler
    {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity)
        {
            mActivity = new WeakReference<MainActivity>(activity);
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
            if(response==REQUEST_CODE_INIT_DATA)
            {
                mActivity.get().initData(null);
            }
        }
    }



    private void selectPicture() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
    }

    private void askSiteImage(final boolean flag)//请求现在日程的经纬度和距离
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mapTimeHelper.askForTracePoint(getSiteTrace(),flag);//请求日程的地点经纬度
            }
        }).start();
    }

    private void initShareUnit()
    {
       // LayoutInflater inflater = LayoutInflater.from();
       // inflater.inflate(R.layout.list_item, parent, false);
      //  shareUnit = LayoutInflater.from(this).inflate(R.layout.time_line,null,true);
        //shareUnit.setMinimumWidth(50);
       // shareUnit.setMinimumHeight(50);
        shareUnit = findViewById(R.id.share_unit);
        shareUnit.setVisibility(View.INVISIBLE);
    }


/////////////////////////////////////////////////////////////////
    private void getSetTrace(Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            String time = bundle.getString("time");
            String event = bundle.getString("event");
            boolean finish = bundle.getBoolean("finish");
            int traceId = bundle.getInt("traceId");
            boolean isdelete = bundle.getBoolean("isdel");
            boolean hasESTime = bundle.getBoolean("hasESTime");
            boolean hasLETime = bundle.getBoolean("hasLETime");//新增关键字
            String ESTime = bundle.getString("ESTime");
            String LETime =bundle.getString("LETime");
            String date = bundle.getString("date");
            String siteId = bundle.getString("siteId");
            String siteText =bundle.getString("siteText");
            int priority = bundle.getInt("priority");


            int predict = bundle.getInt("predict");
            Log.i("set", "-----------------" + time + "  " + event + "  " + finish + "  " + traceId
                    +" "+isdelete+" "+hasESTime+" "+ESTime+"   "+hasLETime+"  "+LETime+" "+siteId+"  "+siteText+" "+predict+" "+priority);
            Trace one =new  Trace(traceId,time,event,date,hasESTime,hasLETime,ESTime,LETime,finish,siteId,siteText,predict,priority);
            if(isdelete)
            {
                TraceManager.deleteTrace(one);
            }
            else {
                TraceManager.updateTrace(one);
            }
        }
    }

    private  void initDataBase(Intent data)
    {
        TraceManager.setContext(this);
       // TraceManager.setTraceList(traceList);
         TraceManager.getDatabase();
//         TraceManager.setShowFinished(true);  //设置显示完成可见
      //  TraceManager.saveTraces();
          TraceManager.getTraces();   //其实是删库哒  //然而并不删库 1.31

        SiteHelper.setContext(this);
        SiteHelper.getDatabase(TraceManager.getHelper());//添加地点数据库


        if(data!=null)
            getSetTrace(data); //获得从设定来的数据
        traceList.clear();
        traceList.addAll(TraceManager.initialTraces(this.nowDate));

//        refreshInform();
//        traceList =TraceManager.initialTraces(this.nowDate);//11.14  初始化增加设置日期
//        adapter.notifyDataSetChanged();

      //  mapTimeHelper.askForPointDistance();

        //TraceManager.dropTable();
    }


    private List<String>  getSiteTrace()//获取日程中没有存储地点的siteId
    {
        List<String> list =new LinkedList<String>();
        for(int i=0;i<traceList.size();i++)
        {
            String siteId = traceList.get(i).getSiteId();
            if(siteId!=null) {
                String result = SiteHelper.getLanLonPoint(siteId);
                Log.i("siteHelper", "---------------------------" + siteId+"   "+result);
                if(list.indexOf(siteId)==-1) {
                        Log.i("siteHelper", "---------------------------add:" + siteId);
                        list.add(siteId);
                }

            }
        }

        return list;
    }


    private void initDate()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = Calendar.getInstance().getTime();
        String date = df.format(tempDate);  //新加时间
        nowDate = date;

        final TextView title=(TextView)findViewById(R.id.toolbar_title);
        title.setText(getDateTitle(titleType));
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(getDateTitle(!titleType));
                titleType=!titleType;
            }
        });
    }//11.14  初始化时间

    private void initView() {
        if(touchHelper==null)
            touchHelper = new TraceItemTouchHelper(new MyItemTouchHelperCallback(adapter));
        touchHelper.setEnableDrag(!this.showFinished);
        touchHelper.setEnableSwipe(!this.showFinished);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void initData(Intent data) {

        TraceManager.setShowFinished(this.showFinished);//设定设置

        initDataBase(data);//初始化数据库

        for(int i=0;i<traceList.size();i++)
            Log.i("trace"+i,traceList.get(i).getEvent());
        if(adapter==null) {
            adapter = new TimeLineAdapter(this, traceList);
        }
        else{
            adapter.notifyDataSetChanged();
        }
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        final AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setDuration(1000);
        alphaAdapter.setFirstOnly(true);
        recyclerView.setAdapter(alphaAdapter);

        initView();

        Log.i("MainActivity","--------------------OnCreate");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_lock) {
           // actionAdd();
//            touchHelper.setEnableDrag(this.showFinished);
            this.showFinished = !this.showFinished;//暂时把事件改成切换模式了
//            DragItemTouchHelper.setEnableDrag(!showFinished);
            initData(null);


        }

        return super.onOptionsItemSelected(item);
    }
    public void actionAdd(){
//        Date tempDate = Calendar.getInstance().getTime();
//        SimpleDateFormat df_hour = new SimpleDateFormat("HH:mm");
//        String time = df_hour.format(tempDate);
//        Log.i("hour",time);
        String time = TimeCalculateHelper.getTimeByHourAndMinute(); //冗余函数归并6.30
        int traceId = TraceManager.getTraceId();
        Intent intent =new Intent();
        intent.putExtra("traceId",traceId);
        intent.putExtra("time",time);
        intent.putExtra("date",nowDate);
        intent.setClass(this, SetTraceActivity.class);
        this.startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == 1){
            if(data!=null){
//                String event=(String)data.getSerializableExtra("event");
//                String time=(String) data.getSerializableExtra("time");
//                int traceId=data.getIntExtra("traceId",-1);
//                Boolean important = data.getBooleanExtra("isimportant",false);
//                Boolean urgent = data.getBooleanExtra("isurgent",false);
//                Boolean fix = data.getBooleanExtra("isfix",false);
//                int predict = data.getIntExtra("predict",30);
//                Boolean finish=data.getBooleanExtra("finish",false);
//                Log.i("onActivity","-----------------------------"+fix+"  "+predict);
//
//                SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
//                Date tempDate = Calendar.getInstance().getTime();
//
//                Log.i("Date",this.nowDate+"--------------------------------"); //修改新增日期错误问题 1.11 zyf
//                String date = df_date.format(tempDate);  //新加时间
//                Trace trace=new Trace(time,this.nowDate,event,traceId,finish,important,urgent,fix,predict);
                Bundle bundle = data.getExtras();
                String time = bundle.getString("time");
                String event = bundle.getString("event");
                boolean finish = bundle.getBoolean("finish");
                int traceId = bundle.getInt("traceId");
                boolean hasESTime = bundle.getBoolean("hasESTime");
                boolean hasLETime = bundle.getBoolean("hasLETime");//新增关键字
                String ESTime = bundle.getString("ESTime");
                String LETime =bundle.getString("LETime");
                String date = bundle.getString("date");
                String siteId = bundle.getString("siteId");
                String siteText =bundle.getString("siteText");

                int predict = bundle.getInt("predict");
                int priority =bundle.getInt("prioirty",1);

                Trace trace = new Trace(traceId, time, event, date, hasESTime, hasLETime,
                        ESTime, LETime, finish, siteId, siteText, predict,priority);

                adapter.addData(trace,0);//1->0
                adapter.MoveToPosition(manager,0);

                String t=trace.getDate()+" "+trace.getTime()+":00";//通知
                Log.i("t", "onActivityResult: "+t);
                addInform(t,"E_time",trace.getEvent());
            }
        }
        if(requestCode == 2 && resultCode == 1){
            if(data!=null){
                initData(data);
            }
            refreshInform();
        }
        if (requestCode == REQUEST_CODE_SELECT_PIC)
        {
            // 获取选择的图片
            if(data!=null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                // 获取到图片的路径
                String selectedImagePath = cursor.getString(columnIndex);
                SelectIconHelper.setIcon(imageView,selectedImagePath);
//                SharedPreferences sharedPreferences = getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("selectedImagePath", selectedImagePath);
//                editor.apply();
                Account.setUserLocalImagePath(selectedImagePath);
                updateUserImage(); //本地选择图片的弥补
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_date) {
            //日期设置选项
            showDailog();

        } else if (id == R.id.nav_lock) {
//            this.showFinished = !this.showFinished;//暂时把事件改成切换模式了
            initDate();
            initData(null);

        } else if (id == R.id.nav_about) {
            Intent intent=new Intent();
            intent.putExtra("mode",getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
            intent.setClass(this,AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            // 同样需要调用recreate方法使之生效
            Thread myThread=new Thread(){//创建子线程
                @Override
                public void run() {
                    try{
                        SharedPreferences preferences=getSharedPreferences("default_night",MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putInt("default_night",getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
                        editor.apply();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
            recreate();


        }else if (id==R.id.nav_sort) {  //添加侧栏
            if(this.useIntellectSort==false && traceList.size()==0)
            {
                Toast.makeText(this,"没有事件，无法使用排序！",Toast.LENGTH_SHORT).show();
            }
            else {
                if (!judgePrepared() && this.useIntellectSort == false) {
                    Toast.makeText(this, "有事件没设置地址或时间，无法使用！", Toast.LENGTH_SHORT).show();
                } else {
                    this.useIntellectSort = !this.useIntellectSort;
                    TraceManager.setUseIntellectSort(this.useIntellectSort);
                    if (!this.useIntellectSort) {//不用智能排序
                        this.initData(null);
                        refreshInform();
                    } else {//使用用回调
                        askSiteImage(true);
                    }
                }
            }
        }else if(id==R.id.nav_user) {//用户登陆功能1.21
            Intent intent = new Intent();
            intent.putExtra("mode",getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
            intent.setClass(this,UserSettingActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_community){
            Intent intent=new Intent();
            intent.putExtra("mode",getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
            intent.setClass(this,CommunityActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private boolean judgePrepared()//判断日程合法
    {
        for(int i=0;i<traceList.size();i++)
        {
            if(traceList.get(i).hasSite()==false)
            {
                return false;
            }
            if(!traceList.get(i).isHasLETime() || !traceList.get(i).isHasESTime())
            {
                return false;
            }
        }
        return true;
    }

    //////////////////////////////////////////////////////////////
 public String getDateTitle(boolean titleType) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
         try {
            cal.setTime(format.parse(nowDate));
         } catch (ParseException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
         int year=cal.get(Calendar.YEAR);
         int month=cal.get(Calendar.MONTH)+1;
         int day=cal.get(Calendar.DAY_OF_MONTH);
         int i = cal.get(Calendar.DAY_OF_WEEK);
        String part = year+"年"+month+"月"+day+"日";
        if(titleType)
            return part;
        else {
            switch (i) {
                case 1:
                    return "周日";
                case 2:
                    return "周一";
                case 3:
                    return "周二";
                case 4:
                    return "周三";
                case 5:
                    return "周四";
                case 6:
                    return "周五";
                case 7:
                    return "周六";
                default:
                    return "";
            }
        }
    }

    private void showDailog(){

        Calendar calendar=Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthofYear, int dayofMonth) {
                String time = String.valueOf(year)+ "-" ;
                if(monthofYear<10)
                {
                    time+="0";
                }
                time+=(monthofYear + 1) + "-" ;
                if(dayofMonth<10)
                {
                    time+="0";
                }
                time+= dayofMonth;
                nowDate=time;
                final TextView title=(TextView)findViewById(R.id.toolbar_title);
                title.setText(getDateTitle(titleType));
                initData(null);
                //Log.i("SatDateActivity","----------------------"+SetDateActivity.this.nowDate);
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
        //自动弹出键盘问题解决
//        datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void addInform(String servertime,String contentTitle,String contentText)
    {
        Date curDate = new Date(System.currentTimeMillis());//当前时间
        Date endDate=new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        endDate=PushService.parseServerTime(servertime,format);//将字符串转化为date
        long delaytime = endDate.getTime() - curDate.getTime();//得到设定时间与当前时间间隔多少毫米
        if (delaytime>0)
        pushService.addNotification(delaytime,contentTitle,contentText);//通过addNotification 添加service
        Log.e("addNotification", String.valueOf(delaytime));
    }

    public void refreshInform(){//更新通知
        PushService.cleanAllNotification();
        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                for(int i=0;i<traceList.size();i++){
                    String time=traceList.get(i).getDate()+" "+traceList.get(i).getTime()+":00";
                    addInform(time,"E_time",traceList.get(i).getEvent());
                }
            }
        };
        myThread.start();
    }
    private  void initAccount()
    {
//        SharedPreferences sharedPreferences=getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
//        String imagePath = sharedPreferences.getString("selectedImagePath", "");
//        String user_name=sharedPreferences.getString("user_name","用户");
//        String user_account = sharedPreferences.getString("user_account",null);
//        Account.setUserName(user_name);
//        Account.setUserImagePath(imagePath);
//        Account.setUserAccount(user_account);

        Account.setContext(this);
        Account.initAccount();
    }

    @Override
    protected void onRestart() {//作为点击back键回到主界面没有刷新用户信息的弥补
        super.onRestart();
        updateUserImage();
    }


    void updateUserImage()//刷新用户信息
    {
        String userName = Account.getUserName();
        String imagePath = Account.getUserImagePath();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView_user);//选头像
        final TextView textView_userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView);

        textView_userName.setText(userName);

        final ImageView imageView_userImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView_user);


        RequestListener mRequestListener = new RequestListener() {//用于监听Glide加载错误
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

        if (!this.isDestroyed()) {
            if (!imagePath.isEmpty()) {//网络图片路径存在
                //SelectIconHelper.setIcon(imageView, imagePath);  //放弃本地设置图片做法1.29


                Glide.with(this)//使用glide加载网络图片
                        .load(imagePath)
                        .listener(mRequestListener)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(imageView);

            } else {
                if ((new File(Account.getUserLocalImagePath()).exists())) //加载本地图片路径
                {
                    Glide.with(this)
                            .load(Account.getUserLocalImagePath())
                            .listener(mRequestListener)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .into(imageView);
                } else {
                    Glide.with(this)//使用初始照片
                            .load(R.mipmap.ic_launcher)
                            .listener(mRequestListener)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(imageView);
                }
            }

        }

    }

    public void shareTraces()//分享日程
    {
        final List<Bitmap>  bitmaps = new LinkedList<Bitmap>();
        if(recyclerView.getChildCount()==0)
        {
            Toast.makeText(this,"您还没有日程可以分享！", Toast.LENGTH_LONG).show();
            return;
        }


        Log.i("size:","-------------------------------"+traceList.size());
        int h  = 0;//总照片长度
        for (int i = 0; i < traceList.size(); i++)//统计组件的身高
            {
                    shareUnit.time.setTextColor(getResources().getColor(R.color.colorTime));
                    shareUnit.event.setTextColor(getResources().getColor(R.color.colorText));
                    shareUnit.tvDot.setBackgroundResource(R.drawable.ic_menu_send);
                    if (traceList.get(i).getFinish()) {
                        CardView cardView = shareUnit.findViewById(R.id.card);
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorFinish));
                    }
                    else
                    {
                        CardView cardView = shareUnit.findViewById(R.id.card);
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                    shareUnit.time.setText(traceList.get(i).getTime());
                    shareUnit.event.setText(traceList.get(i).getEvent());
                    h += shareUnit.getHeight();

                    Bitmap bm = Bitmap.createBitmap(shareUnit.getWidth(), shareUnit.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bm);
                    bm.eraseColor(Color.WHITE);//背景调整白色

                    shareUnit.draw(canvas);
                    bitmaps.add(bm);
            }

        //创建用户分享图片头

        int upOffset = shareUnit.getHeight()/10;//上部填充偏差
        h+=upOffset;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView_user);//选头像
        Bitmap userHeadBitmap = Bitmap.createBitmap(imageView.getWidth(),imageView.getHeight(),Bitmap.Config.ARGB_8888);//画用户头像
        userHeadBitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Canvas userHeadCanvas = new Canvas(userHeadBitmap);
        imageView.draw(userHeadCanvas);//imageview获得用户头像

        Bitmap userFitHeadBitmap = Bitmap.createBitmap(shareUnit.getHeight(),shareUnit.getHeight()+upOffset,Bitmap.Config.ARGB_8888);
        userFitHeadBitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Bitmap  userZoomedBitmap = BitmapTranslateHelper.zoomImage(userHeadBitmap,shareUnit.getHeight(),shareUnit.getHeight());//用户压缩头像
        Canvas userFitHeadCanvas = new Canvas(userFitHeadBitmap);
        userFitHeadCanvas.drawBitmap(userZoomedBitmap,0,upOffset,null);
        if(userHeadBitmap.isRecycled())
        {
            userHeadBitmap.recycle();
        }
        if(userZoomedBitmap.isRecycled())
        {
            userZoomedBitmap.recycle();
        }

        Log.i("userhead","--------------------------h:"+userHeadBitmap.getHeight()+"w:"+userHeadBitmap.getWidth());

        Bitmap headBitmap = Bitmap.createBitmap(shareUnit.getWidth()-shareUnit.getHeight(),shareUnit.getHeight()+upOffset,Bitmap.Config.ARGB_8888);
        Canvas headCanvas = new Canvas(headBitmap);
        headBitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Paint paint = new Paint();
        paint.setTextSize(shareUnit.getHeight()/4);
        paint.setColor(getResources().getColor(R.color.colorBg));
        paint.setFlags(1);
        paint.setStyle(Paint.Style.FILL);
        headCanvas.drawText(Account.getUserName()+"的日程分享",100,headBitmap.getHeight()/2-10,paint);
        h+=headBitmap.getHeight();


        //拼接成一张总图片

        if(h<shareUnit.getHeight()*6)//提高图片高度
        {
            h = shareUnit.getHeight()*6;
        }
        Bitmap bm = Bitmap.createBitmap(shareUnit.getWidth(),h, Bitmap.Config.ARGB_8888);
        bm.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bm);
        int tempHeight = 0;


       canvas.drawBitmap(userFitHeadBitmap,0,tempHeight,null);
       canvas.drawBitmap(headBitmap,shareUnit.getHeight(),tempHeight,null);
       tempHeight+= headBitmap.getHeight();

       if(headBitmap.isRecycled()) {
           headBitmap.recycle();
       }
       if(userFitHeadBitmap.isRecycled()){
           userFitHeadBitmap.recycle();
       }

        for(int i=0;i<bitmaps.size();i++) {
            Bitmap bitmap = bitmaps.get(i);
            canvas.drawBitmap(bitmap,0,tempHeight,null);
            tempHeight+=bitmap.getHeight();
            if (bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        PermissionHelper.checkPermission(this);//请求存储权限
        String filePath = MediaStore.Images.Media.insertImage(getContentResolver(),bm, null, null);
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(MainActivity.this, "生成图片失败！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.i("share", "--------------------------" + getRealPathFromURI(Uri.parse(filePath)));
        }

       // Toast.makeText(MainActivity.this, "分享成功!", Toast.LENGTH_LONG).show();

        if(bm.isRecycled())
        {
            bm.recycle();
        }


        //跳转到写帖子的界面
        Intent intent = getIntent();
        intent.putExtra("sharePhotoPath",filePath);
        intent.putExtra("request","share");
        intent.setClass(this,SetPostActivity.class);
        startActivity(intent);
    }


    private  String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null) {
                return "";
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
