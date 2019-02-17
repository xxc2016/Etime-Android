package com.student.xxc.etime.entity;

public class Trace {
    private String time;//时间
    private String event;//事件
    private String date;
    private boolean finish;
    private int traceId;//每个的唯一索引 在traceManager中保存计数
    private int imageType;
    private boolean important;
    private boolean urgent;
    private boolean fix;//设为固定
    private int predict;//预计时间

    public Trace(String time,String date ,String event,int traceId,boolean finish, int imageType,boolean important,boolean urgent) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = finish;
        this.imageType = imageType;
        this.important = important;
        this.urgent = urgent;
        this.fix = false;//自动默认
        this.predict = 30;
    }

    public Trace(String time,String date, String event,int traceId,boolean finish) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId =traceId;
        this.finish  =finish;
        this.urgent =false;
        this.important = false;//自动默认
        this.fix = false;
        this.predict = 30;
    }

    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = finish;
        this.important = important;
        this.urgent = urgent;
        this.fix =false;
        this.predict =30;
    }


    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent,boolean fix) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = finish;
        this.important = important;
        this.urgent = urgent;
        this.fix =fix;
        this.predict =30;
    }

    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent,boolean fix,int predict) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = finish;
        this.important = important;
        this.urgent = urgent;
        this.fix =fix;
        this.predict =predict;
    }

    public Trace(String time,String date ,String event,int traceId,boolean finish,boolean important,boolean urgent,boolean fix,int predict,int imageType) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = finish;
        this.important = important;
        this.urgent = urgent;
        this.fix =fix;
        this.predict =predict;
        this.imageType = imageType;
    }




    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getDate()
    {
        return date ;
    }

    public  int  getTraceId()
    {
        return this.traceId;
    }

    public  void setFinish(boolean finish)
    {
        this.finish = finish;
    }

    public boolean getFinish()
    {
        return this.finish;
    }

    public boolean getImportant(){
        return this.important;
    }

    public int getImportant_int(){
        if(this.important==true)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static boolean judgeImportant_boolean(int important)
    {
        if(important==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public int getUrgent_int()
    {
        if(this.urgent==true)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static boolean judgeUrgent_boolean(int urgent)
    {
        if(urgent==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public  boolean getUrgent()
    {
        return this.urgent;
    }

    public  int getFinish_int()
    {
        if(finish ==  true)
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


    public  int getFix_int()
    {
        if(fix ==  true)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static boolean judgeFix_boolean(int fix)
    {
        if(fix==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public  boolean getFix()
    {
        return this.fix;
    }

    public void setFix(boolean fix) {
        this.fix = fix;
    }

    public int getPredict()
    {
        return this.predict;
    }

    public void setPredict(int predictTime) {
        this.predict = predictTime;
    }

    public  void setImportant(boolean important){
        this.important = important;
    }

    public  void setUrgent(boolean urgent){
        this.urgent = urgent;
    }


    public  int  getHour() {
        String tempHour =this.time.charAt(0)+""+this.time.charAt(1);
        return  Integer.parseInt(tempHour);
    }

    public int getMinute(){
        String tempMinute = this.time.charAt(3)+""+this.time.charAt(4);
        return  Integer.parseInt(tempMinute);
    }


    public  void setTime(int hour,int minute)
    {
        String tempTime ="";
        if(hour<10)
        {
            tempTime+="0";
        }
        tempTime+=hour;
        tempTime+=":";
        if(minute<10)
        {
            tempTime+="0";
        }
        tempTime+=minute;
        this.time = tempTime;
    }






}
