package com.student.xxc.etime.entity;

import java.io.Serializable;

public class Trace implements Serializable{//6.28  日程类重写  尝试添加序列化接口
    private int traceId;//每个的唯一索引 在traceManager中保存计数
    private String time;//推荐开始时间 默认为插入时间
    private String event;//事件
    private String date;//日期
    private boolean hasESTime;//有最早开始时间
    private boolean hasLETime;//有最晚开始时间
    private String ESTime;//最早开始时间
    private String LETime;//最晚结束时间
    private boolean finish;//结束标识
    private String siteId;//地址标识号
    private String siteText;//地址文字
    private int predict;//持续时间

    public Trace(int traceId,String time,String event ,String date,boolean hasESTime,boolean hasLETime
            ,String ESTime,String LETime,boolean finish,String siteId,String siteText) {//构造函数除了预计持续时间
        this.traceId = traceId;
        this.time = time;
        this.date = date;
        this.event = event;

        this.hasESTime = hasESTime;
        this.hasLETime = hasLETime;
        this.ESTime = ESTime;
        this.LETime=  LETime;

        this.finish = finish;
        this.siteId = siteId;
        this.siteText  =siteText;
        this.predict = 30;
    }

    public Trace(int traceId,String time,String event ,String date,boolean hasESTime,boolean hasLETime
            ,String ESTime,String LETime,boolean finish,String siteId,String siteText,int predict) {//构造函数完整
        this.traceId = traceId;
        this.date = date;
        this.event = event;
        this.time = time;

        this.hasESTime = hasESTime;
        this.hasLETime = hasLETime;
        this.ESTime = ESTime;
        this.LETime=  LETime;

        this.finish = finish;
        this.siteId = siteId;
        this.siteText  =siteText;
        this.predict = predict;
    }

    public Trace(int traceId,String time,String event ,String date,int hasESTime,int hasLETime
            ,String ESTime,String LETime,int finish,String siteId,String siteText,int predict) {//构造函数 布尔类型自动转换
        this.ESTime = ESTime;
        this.LETime=  LETime;
        this.hasESTime = judge_hasEst_boolean(hasESTime);
        this.hasLETime = judge_hasLet_boolean(hasLETime);
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = judgeFinish_boolean(finish);
        this.siteId = siteId;
        this.siteText  =siteText;
        this.time = time;
        this.predict = predict;
    }

//    public Trace(String time,String date, String event,int traceId,boolean finish) {
//        this.time = time;
//        this.date = date;
//        this.event = event;
//        this.traceId =traceId;
//        this.finish  =finish;
//        this.urgent =false;
//        this.important = false;//自动默认
//        this.fix = false;
//        this.predict = 30;
//    }
//
//    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent) {
//        this.time = time;
//        this.date = date;
//        this.event = event;
//        this.traceId = traceId;
//        this.finish = finish;
//        this.important = important;
//        this.urgent = urgent;
//        this.fix =false;
//        this.predict =30;
//    }
//
//
//    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent,boolean fix) {
//        this.time = time;
//        this.date = date;
//        this.event = event;
//        this.traceId = traceId;
//        this.finish = finish;
//        this.important = important;
//        this.urgent = urgent;
//        this.fix =fix;
//        this.predict =30;
//    }
//
//    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent,boolean fix,int predict) {
//        this.time = time;
//        this.date = date;
//        this.event = event;
//        this.traceId = traceId;
//        this.finish = finish;
//        this.important = important;
//        this.urgent = urgent;
//        this.fix =fix;
//        this.predict =predict;
//    }
//
//    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent,boolean fix,int predict,int imageType) {
//        this.time = time;
//        this.date = date;
//        this.event = event;
//        this.traceId = traceId;
//        this.finish = finish;
//        this.important = important;
//        this.urgent = urgent;
//        this.fix =fix;
//        this.predict =predict;
//        this.imageType = imageType;
//    }
//
//
//    public int getImportant_int(){
//        if(this.important==true)
//        {
//            return 1;
//        }
//        else
//        {
//            return 0;
//        }
//    }
//
//    public static boolean judgeImportant_boolean(int important)
//    {
//        if(important==1)
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
//
//
//    public int getUrgent_int()
//    {
//        if(this.urgent==true)
//        {
//            return 1;
//        }
//        else
//        {
//            return 0;
//        }
//    }
//
//    public static boolean judgeUrgent_boolean(int urgent)
//    {
//        if(urgent==1)
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
//
//
//    public  boolean getUrgent()
//    {
//        return this.urgent;
//    }
//
    public  int getFinish_int()
    {
        if(true == finish)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static boolean judgeFinish_boolean(int finish)
    {
        if(finish==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean judge_hasEst_boolean(int hasESTime)
    {
        if(hasESTime==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int get_hasEst_int()
    {
        if(true == hasESTime)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static boolean judge_hasLet_boolean(int hasLETime)
    {
        if(hasLETime==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int get_hasLet_int()
    {
        if(true == hasLETime)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }


    public void setPredict(int predictTime) {
        this.predict = predictTime;
    }
//
//    public  void setImportant(boolean important){
//        this.important = important;
//    }
//
//    public  void setUrgent(boolean urgent){
//        this.urgent = urgent;
//    }
//
//
//    public  int  getHour() {
//        String tempHour =this.time.charAt(0)+""+this.time.charAt(1);
//        return  Integer.parseInt(tempHour);
//    }
//
//    public int getMinute(){
//        String tempMinute = this.time.charAt(3)+""+this.time.charAt(4);
//        return  Integer.parseInt(tempMinute);
//    }
//
//
//    public  void setTime(int hour,int minute)
//    {
//        String tempTime ="";
//        if(hour<10)
//        {
//            tempTime+="0";
//        }
//        tempTime+=hour;
//        tempTime+=":";
//        if(minute<10)
//        {
//            tempTime+="0";
//        }
//        tempTime+=minute;
//        this.time = tempTime;
//    }


    public void setSiteText(String siteText) {
        this.siteText = siteText;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setLETime(String LETime) {
        this.LETime = LETime;
    }

    public void setHasLETime(boolean hasLETime) {
        this.hasLETime = hasLETime;
    }

    public void setHasESTime(boolean hasESTime) {
        this.hasESTime = hasESTime;
    }

    public void setESTime(String ESTime) {
        this.ESTime = ESTime;
    }

    public String getSiteText() {
        return siteText;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getLETime() {
        return LETime;
    }

    public String getESTime() {
        return ESTime;
    }

    public boolean isHasLETime() {
        return hasLETime;
    }

    public int getPredict() {
        return predict;
    }

    public boolean isHasESTime() {
        return hasESTime;
    }

    public int getTraceId() {
        return traceId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public void setTraceId(int traceId) {
        this.traceId = traceId;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getFinish()
    {
        return this.finish;
    }

    public boolean hasSite()//判断是否有地点
    {
        if("".equals(this.siteId) || this.siteId==null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
