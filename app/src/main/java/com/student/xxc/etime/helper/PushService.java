package com.student.xxc.etime.helper;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.student.xxc.etime.util.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class PushService extends Service {

    static Timer timer = null;

    //清除通知
    public static void cleanAllNotification() {
        NotificationManager mn= (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        if (mn != null) {
            mn.cancelAll();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //添加通知
    public static void addNotification(long delayTime,String contentTitle,String contentText)
    {
        Intent intent = new Intent(MyApplication.getContext(), PushService.class);
        intent.putExtra("delayTime", delayTime);
        intent.putExtra("contentTitle", contentTitle);
        intent.putExtra("contentText", contentText);
        MyApplication.getContext().startService(intent);
    }

    public void onCreate() {
        Log.e("addNotification", "===========create=======");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e("addNotification", "===========onStartCommand=======");
        long delay=intent.getLongExtra("delayTime",0);
        if (null == timer ) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            //service里的函数
            public void run() {

                Notifictionhelper notifictionhelper=new Notifictionhelper(MyApplication.getContext());
                notifictionhelper.init();
                notifictionhelper.sendChatMsg(intent.getStringExtra("contentTitle"),intent.getStringExtra("contentText"));

            }
        },delay);

        return START_STICKY;
    }

    public static Date parseServerTime(String serverTime, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
        }
        return date;
    }

    @Override
    public void onDestroy(){
        Log.e("addNotification", "===========destroy=======");
        super.onDestroy();
    }
}

