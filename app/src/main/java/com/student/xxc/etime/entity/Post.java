package com.student.xxc.etime.entity;

public class Post {
    private String head;
    private String username;
    private String pic;
    private String postTime;
    private String title;//帖子预览添加主题2.16
    private int watch;
    private int remark;
    private int postId;
    private int postDetailId;//添加两个序号2.17

    public Post(String head, String username, String  pic, String postTime, int watch, int remark,String title,int postId,int postDetailId) {
        this.head = head;
        this.username = username;
        this.pic = pic;
        this.postTime = postTime;
        this.watch = watch;
        this.remark = remark;
        this.title =title;
        this.postId = postId;
        this.postDetailId = postDetailId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public int getWatch() {
        return watch;
    }

    public void setWatch(int watch) {
        this.watch = watch;
    }

    public int getRemark() {
        return remark;
    }

    public void setRemark(int remark) {
        this.remark = remark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostDetailId() {
        return postDetailId;
    }

    public void setPostDetailId(int postDetailId) {
        this.postDetailId = postDetailId;
    }
}
