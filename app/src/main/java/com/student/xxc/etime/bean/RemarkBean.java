package com.student.xxc.etime.bean;

import java.util.List;

public class RemarkBean {
    public int remarkId;
    public User user;
    public int detailId;
    public String time;
    public String date;
    public String content;
    public List<String> bitmapPath;//评论图片
    private int requestCode;
    private int responseCode;

    static  public class User{
        public String account;
        public String head;
        public String nickName;
    }

   public final static int UNKNOWN_ERROR = 0x04001;//未知错误

   public final static int REMARK_UP_STORE_REQUEST = 0x04002;//评论上传请求
   public final static int REMARK_UP_STORE_RESPONSE_SUCCESSED = 0x04003;//评论上传成功
   public final static int REMARK_UP_STORE_RESPONSE_FAILED =0x04004;//评论上传失败
   public final static int REMARK_DOWN_LOAD_REQUEST = 0x04005;//评论下载请求
   public final static int REMARK_DOWN_LOAD_RESPONSE_SUCCESSED=0x04006;//评论下载成功
   public final static int REMARK_DOWN_LOAD_RESPONSE_FAILED=0x04007;//评论下载失败
    public final static int REMARK_DELETE_REQUEST = 0x04008;//评论删除请求
    public final static int REMARK_DELETE_RESPONSE_SUCCESSED=0x04009;//评论删除成功
    public final static int REMARK_DELETE_RESPONSE_FAILED=0x0400A;//评论删除失败

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public int getDetailId() {
        return detailId;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public int getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(int remarkId) {
        this.remarkId = remarkId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
