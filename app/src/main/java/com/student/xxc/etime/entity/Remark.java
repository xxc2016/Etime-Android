package com.student.xxc.etime.entity;

import java.util.List;

public class Remark {
    private User user;
    private String content;
    private List<String> bitmapList;

    public Remark(User user, String content, List<String> bitmapList) {
        this.user = user;
        this.content = content;
        this.bitmapList = bitmapList;
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
}
