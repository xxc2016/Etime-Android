package com.student.xxc.etime;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.student.xxc.etime.entity.Trace;
import com.student.xxc.etime.helper.MyItemTouchHelperCallback;
import com.student.xxc.etime.helper.PermissionHelper;
import com.student.xxc.etime.helper.SelectIconHelper;
import com.student.xxc.etime.helper.TimeLineAdapter;
import com.student.xxc.etime.helper.TraceItemTouchHelper;
import com.student.xxc.etime.impl.TraceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

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

    ////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        initDate();//更新今天日期



        initData(null);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAdd();
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
        SharedPreferences sharedPreferences=getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("selectedImagePath", "");
        String user_name=sharedPreferences.getString("user_name","用户");

        imageView=(ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView_user);//选头像
        final TextView username=(TextView)navigationView.getHeaderView(0).findViewById(R.id.textView);
        username.setText(user_name);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectIconHelper.showInputDialog(username,MainActivity.this);
            }
        });
        if(!imagePath.isEmpty()) {
            SelectIconHelper.setIcon(imageView, imagePath);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.checkPermission(MainActivity.this);
                selectPicture();
            }
        });
    }

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
    }

/////////////////////////////////////////////////////////////////
    private void getSetTrace(Intent intent)
    {
//        Intent intent = this.getIntent();
        //setResult(0x000011,intent);
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            String time = bundle.getString("time");
            String event = bundle.getString("event");
            boolean finish = bundle.getBoolean("finish");
            int traceId = bundle.getInt("traceId");
            boolean isdelete = bundle.getBoolean("isdel");
            boolean isimportant = bundle.getBoolean("isimportant");
            boolean isurgent = bundle.getBoolean("isurgent");//新增关键字
            boolean isfix = bundle.getBoolean("isfix");
            int predict = bundle.getInt("predict");
            Log.i("set", "-----------------" + time + "  " + event + "  " + finish + "  " + traceId
                    +" "+isdelete+" "+isimportant+" "+isurgent+" "+isfix+" "+predict);
            Trace one =new  Trace(time,nowDate,event,traceId,finish,isimportant,isurgent,isfix,predict);
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
//        TraceManager.getTraces();   //其实是删库哒

        if(data!=null)
            getSetTrace(data); //获得从设定来的数据
        traceList.clear();
        traceList.addAll(TraceManager.initialTraces(this.nowDate));
//        traceList =TraceManager.initialTraces(this.nowDate);//11.14  初始化增加设置日期
//        adapter.notifyDataSetChanged();
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

    private void initData(Intent data) {

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

//        MyItemTouchHelperCallback callback = new MyItemTouchHelperCallback(adapter);
//        mItemTouchHelper = new WItemTouchHelperPlus(callback);
//        mItemTouchHelper.attachToRecyclerView(recyclerView);
        initView();
//        DragItemTouchHelper.setItemTouchHelper(alphaAdapter,traceList);
//        DragItemTouchHelper.getHelper().attachToRecyclerView(recyclerView);

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
        Date tempDate = Calendar.getInstance().getTime();
        SimpleDateFormat df_hour = new SimpleDateFormat("HH:mm");
        String time = df_hour.format(tempDate);
        Log.i("hour",time);
        int traceId = TraceManager.getTraceId();
        Intent intent =new Intent();
        intent.putExtra("traceId",traceId);
        intent.putExtra("time",time);
        intent.setClass(this, SetTraceActivity.class);
        this.startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == 1){
            if(data!=null){
                String event=(String)data.getSerializableExtra("event");
                String time=(String) data.getSerializableExtra("time");
                int traceId=data.getIntExtra("traceId",-1);
                Boolean important = data.getBooleanExtra("isimportant",false);
                Boolean urgent = data.getBooleanExtra("isurgent",false);
                Boolean fix = data.getBooleanExtra("isfix",false);
                int predict = data.getIntExtra("predict",30);
                Log.i("onActivity","-----------------------------"+fix+"  "+predict);

                SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
                Date tempDate = Calendar.getInstance().getTime();
                String date = df_date.format(tempDate);  //新加时间
                Trace trace=new Trace(time, date,event,traceId,false,important,urgent,fix,predict);
                adapter.addData(trace,0);//1->0
                adapter.MoveToPosition(manager,0);
            }
        }
        if(requestCode == 2 && resultCode == 1){
            if(data!=null){
                initData(data);
            }
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
                SharedPreferences sharedPreferences = getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedImagePath", selectedImagePath);
                editor.apply();
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
            this.useIntellectSort = !this.useIntellectSort;
            TraceManager.setUseIntellectSort( this.useIntellectSort);
            this.initData(null);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

}
