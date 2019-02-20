package com.student.xxc.etime.entity;

import java.util.List;

public class Remark {
    private User user;
    private String content;
    private List<String> bitmapList;
    private String date;//添加属性2.19
    private String time;
    private int remarkId;
    private int postDetailId;


    public Remark(User user, String content, List<String> bitmapList) {
        this.user = user;
        this.content = content;
        this.bitmapList = bitmapList;
    }


    public Remark(User user, String content, List<String> bitmapList,String date,String time,int remarkId,int postDetailId) {
        this.user = user;
        this.content = content;
        this.bitmapList = bitmapList;
        this.date = date;
        this.time = time;
        this.remarkId = remarkId;
        this.postDetailId = postDetailId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<String> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPostDetailId(int postDetailId) {
        this.postDetailId = postDetailId;
    }

    public int getPostDetailId() {
        return postDetailId;
    }

    public void setRemarkId(int remarkId) {
        this.remarkId = remarkId;
    }

    public int getRemarkId() {
        return remarkId;
    }
}
