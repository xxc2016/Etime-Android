package com.student.xxc.etime.entity;

import android.graphics.Bitmap;

import java.util.List;

public class PostDetail {
    private User user;
    private List<Bitmap> bitmapList;
    private String content;

    public PostDetail(User user, List<Bitmap> bitmapList, String content) {
        this.user = user;
        this.bitmapList = bitmapList;
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
