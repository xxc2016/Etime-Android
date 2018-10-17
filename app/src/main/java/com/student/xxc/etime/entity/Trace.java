package com.student.xxc.etime.entity;

public class Trace {
    private String time;//时间
    private String event;//事件
    private int imageType;

    public Trace(String time, String event, int imageType) {
        this.time = time;
        this.event = event;
        this.imageType = imageType;
    }

    public Trace(String time, String event) {
        this.time = time;
        this.event = event;
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
}
