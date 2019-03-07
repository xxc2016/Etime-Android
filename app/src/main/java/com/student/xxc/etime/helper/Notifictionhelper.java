package com.student.xxc.etime.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.student.xxc.etime.view.MainActivity;
import com.student.xxc.etime.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notifictionhelper {

    Context context;

    public Notifictionhelper(Context context) {
        this.context = context;
    }

    public void init() {
        String channelId = "message";
        String channelName = "通知消息";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);//通信管道实例化

    }


    //建立通信管道
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = null;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    NOTIFICATION_SERVICE);
            channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //发送信息，Title为信息标题，Text为信息内容
    public void sendChatMsg( String Title, String Text) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        intent.setClass(context,MainActivity.class);//点击返回的Activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);//点击事件
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"message");
        mBuilder.setContentTitle(Title)//设置标题
                .setContentText(Text)//设置消息内容
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)//设置图标
                .setContentIntent(pendingIntent);  //设置点击事件
        Notification notification = mBuilder.build();
        manager.notify(1, notification);
    }
}