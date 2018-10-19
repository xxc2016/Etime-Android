package com.student.xxc.etime.entity;

public class Trace {
    private String time;//时间
    private String event;//事件
    private String date;
    private boolean finish;
    private int traceId;//每个的唯一索引 在traceManager中保存计数
    private int imageType;

    public Trace(String time,String date ,String event,int traceId,boolean finish, int imageType) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId = traceId;
        this.finish = finish;
        this.imageType = imageType;
    }

    public Trace(String time,String date, String event,int traceId,boolean finish) {
        this.time = time;
        this.date = date;
        this.event = event;
        this.traceId =traceId;
        this.finish  =finish;
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


}
