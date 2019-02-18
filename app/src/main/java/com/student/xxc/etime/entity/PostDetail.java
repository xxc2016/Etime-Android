package com.student.xxc.etime.entity;

import android.graphics.Bitmap;

import java.util.List;

public class PostDetail {
    private User user;
    private List<String> bitmapList;//改成路径
    private String content;
    private String title;//添加一些属性
    private String date;
    private String time;
    private int detailId;
    private int postId;

    public PostDetail(User user, List<String> bitmapList, String content) {
        this.user = user;
        this.bitmapList = bitmapList;
        this.content = content;
    }

    public PostDetail(User user,List<String> bitmapList,String content,String title,String date,String time,int detailId,int postId)
    {
        this.user = user;
        this.bitmapList = bitmapList;
        this.content =content;
        this.title = title;
        this.date = date;
        this.time  = time;
        this.detailId  = detailId;
        this.postId = postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<String> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostId() {
        return postId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getDetailId() {
        return detailId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
