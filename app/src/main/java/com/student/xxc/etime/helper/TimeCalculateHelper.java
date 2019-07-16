package com.student.xxc.etime.helper;

import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class TimeCalculateHelper {//计算时间帮助类


    static public String getTimeByHourAndMinute()
    {
        Date tempDate = Calendar.getInstance().getTime();
        SimpleDateFormat df_hour = new SimpleDateFormat("HH:mm");
        String time = df_hour.format(tempDate);
        return time;
    }

    static public int getMinuteGap(String time)
    {
        String now = getTimeByHourAndMinute();
        String [] now_t = now.split(":");
        int nowHour = Integer.parseInt(now_t[0]);
        int nowMinute = Integer.parseInt(now_t[1]);

        String [] time_t = time.split(":");
        int Hour = Integer.parseInt(time_t[0]);
        int Minute = Integer.parseInt(time_t[1]);
        int ans = (Hour - nowHour)*60 + (Minute - nowMinute);
        Log.i("timeGap","------------------------------------"+ans);
        return ans;
    }

    static  public  String getTimeGap(String date,String time)
    {
        SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = Calendar.getInstance().getTime();
        String date_now = df_date.format(tempDate);
        SimpleDateFormat df_time = new SimpleDateFormat("HH:mm");
        String time_now = df_time.format(tempDate);

        //Log.i("timeGap",date_now+","+time_now+","+date+","+time);

        String [] nowDate = date_now.split("-");
        String [] nowTime = time_now.split(":");
        String [] getDate = date.split("-");
        String [] getTime = time.split(":");

        //Log.i("timeGap","length:"+nowDate.length+""+nowDate[0]+"   "+nowDate[1]+"   "+nowDate[2]);

        int nowYear = Integer.parseInt(nowDate[0]);
        int nowMonth = Integer.parseInt(nowDate[1]);
        int nowDay = Integer.parseInt(nowDate[2]);
        int nowHour = Integer.parseInt(nowTime[0]);
        int nowMinute = Integer.parseInt(nowTime[1]);
        //int nowSecond = Integer.getInteger(nowTime[2]);

        int getYear = Integer.parseInt(getDate[0]);
        int getMonth = Integer.parseInt(getDate[1]);
        int getDay = Integer.parseInt(getDate[2]);
        int getHour = Integer.parseInt(getTime[0]);
        int getMinute = Integer.parseInt(getTime[1]);
        //int getSecond = Integer.getInteger(getTime[2]);


//        int totalGapSecond = (nowYear-getYear)*365*24*3600 + (nowMonth-getMonth)*30*24*3600+(nowDay-getDay)*24*3600
//                +(nowHour-getHour)*3600+(nowMinute-getMinute)*60+(nowSecond-getSecond);

        int totalGapSecond = (nowYear-getYear)*365*24*3600 + (nowMonth-getMonth)*30*24*3600+(nowDay-getDay)*24*3600
                +(nowHour-getHour)*3600+(nowMinute-getMinute)*60;

        Log.i("timeGap",""+totalGapSecond);
        int temp = totalGapSecond;

        int YearGap =  (temp-temp%(365*24*3600))/(365*24*3600);
        temp = temp%(365*24*3600);
        int MonthGap = (temp-temp%(30*24*3600))/(30*24*3600);
        temp = temp%(30*24*3600);
        int DayGap = (temp-temp%(24*3600))/(24*3600);
        temp = temp%(24*3600);
        int HourGap  =(temp-temp%(3600))/3600;
        temp = temp%3600;
        int MinuteGap =(temp-temp%60)/60;
//        temp = temp%60;
//        int SecondGap = (temp)%60;
        Log.i("timeGap","year:"+YearGap+" month:"+MonthGap+" day:"+DayGap+" hour:"+HourGap+" minute:"+MinuteGap);
        if(YearGap!=0)
        {
            return YearGap+"年前";
        }

        if(MonthGap!=0)
        {
            return MonthGap+"个月前";
        }

        if(DayGap!=0)
        {
            return DayGap+"天前";
        }

        if(HourGap!=0)
        {
            return HourGap+"小时前";
        }

        return MinuteGap+"分钟前";

//        if(MinuteGap!=0)
//        {
//            return MinuteGap+"分钟前";
//        }
//
//        return SecondGap+"秒前";
    }
}
