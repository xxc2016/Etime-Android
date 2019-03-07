package com.student.xxc.etime.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.student.xxc.etime.R;
import com.student.xxc.etime.bean.ImageBean;
import com.student.xxc.etime.adapter.PictureViewAdapter;
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

public class PictureViewActivity extends AppCompatActivity {//用于查看图片

    private ViewPager viewPager = null;
    private List<String> list_url = null;
    private List<String> list_url_origin = null;//原来缩略图
    private MyHandler myhandler =new MyHandler(this);
    private int position;//显示图片位置
    private PagerAdapter pagerAdapter = null;

    private static class MyHandler extends Handler {
        private final WeakReference<PictureViewActivity> mActivity;

        public MyHandler(PictureViewActivity activity) {
            mActivity = new WeakReference<PictureViewActivity>(activity);
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

            if(response == ImageBean.IMAGE_GET_SOURCE_RESPONSE_SUCCESSED)
            {
                String json = bundle.getString("json");
                ImageBean imageBean = JsonManager.JsonToImageBean(json);
                mActivity.get().updateViewPageSource(imageBean);
            }
        }
    }

    private void updateToast(int response) {

        if(response == ImageBean.IMAGE_GET_SOURCE_RESPONSE_SUCCESSED)
        {
            Toast.makeText(this,"加载原图",Toast.LENGTH_SHORT).show();
        }
        if(response == ImageBean.IMAGE_GET_SOURCE_RESPONSE_FAILED)
        {
            Toast.makeText(this,"加载原图失败",Toast.LENGTH_SHORT).show();
        }
        if(response == ImageBean.UNKNOWN_ERROR)
        {
            Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        SharedPreferences preferences=getSharedPreferences("default_night", MODE_PRIVATE);
        int currentNightMode = preferences.getInt("default_night",getResources().getConfiguration().uiMode);
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);


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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<String> pic = null;

        position =0;//初始化位置
        if(bundle!=null) {
            pic = bundle.getStringArrayList("pic");
            position = bundle.getInt("position",0);
        }

        viewPager = (ViewPager) findViewById(R.id.ViewPager_view_picture);
        LayoutInflater inflater = getLayoutInflater();

        list_url = new ArrayList<String>();
        list_url_origin = new ArrayList<String>();


        if(pic!=null) {
           for(int i=0;i<pic.size();i++)
           {
               list_url.add(pic.get(i));
               list_url_origin.add(pic.get(i));
           }
        }



        TextView  textView_number = (TextView)this.findViewById(R.id.textView_picture_view_number);
        textView_number.setText((position+1)+"/"+list_url.size());

        pagerAdapter = new PictureViewAdapter(this, list_url);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(position);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i("PageSelected","position:"+position);
                position = i;
                TextView  textView_number = (TextView)PictureViewActivity.this.findViewById(R.id.textView_picture_view_number);
                textView_number.setText((position+1)+"/"+list_url.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        TextView textView_ask_source = (TextView)this.findViewById(R.id.textView_picture_view_ask_source);
        textView_ask_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageBean imageBean =new ImageBean();
                List<String> list = new LinkedList<String>();
                String url = list_url_origin.get(position);
                if(url.lastIndexOf(UrlHelper.getUrl_base())!=-1) {
                    url = url.substring(url.lastIndexOf(UrlHelper.getUrl_base()) + UrlHelper.getUrl_base().length());//图片处理
                }
                list.add(url);
                List<Integer>  list_position = new LinkedList<Integer>();
                list_position.add(position);
                imageBean.setImagePath(list);
                imageBean.setImagePosition(list_position);

                PictureViewActivity.this.downLoad_ImageBean(imageBean);
            }
        });
    }


    private void downLoad_ImageBean(final ImageBean imageBean)
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

                        ImageBean askImageBean = null;
                        try {
                            askImageBean = JsonManager.JsonToImageBean(json);
                        }catch (Exception e)
                        {
                            Log.i("jsonError",e+"");
                            Looper.prepare();
                            Bundle  bundle = new Bundle();
                            bundle.putInt("response",ImageBean.UNKNOWN_ERROR);
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Looper.loop();
                            return ;
                        }

                        if(askImageBean!=null) {
                            Looper.prepare();
                            Bundle bundle = new Bundle();
                            bundle.putInt("response", askImageBean.getResponseCode());
                            bundle.putString("json",JsonManager.ImageBeanToJson(askImageBean));
                            Message message = myhandler.obtainMessage();
                            message.setData(bundle);
                            message.sendToTarget();
                            Log.i("thread", "-----------------------------send message");
                            Looper.loop();
                        }
                    }
                };

                HttpConnection.sendOkHttpRequest_downLoadImnageBean(imageBean,callback);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void updateViewPageSource(ImageBean imageBean)
    {
        if(pagerAdapter==null || list_url==null) return;

         List<String>  pic_source = imageBean.getImagePath_src();
         List<String>  pic_compress =imageBean.getImagePath();
         List<Integer> pic_position = imageBean.getImagePosition();
         if(pic_position!=null && pic_source!=null && pic_position.size()==pic_source.size())
         {
             for(int i=0;i<pic_position.size();i++)
             {
                 int position  = pic_position.get(i);
                 String source =UrlHelper.getUrl_base()+pic_source.get(i);//路径补全
                 list_url.set(position, source);
                 Log.i("page","c:"+pic_compress.get(i)+" s:"+source);
             }
             pagerAdapter.notifyDataSetChanged();
         }
    }

}
