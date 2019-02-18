package com.student.xxc.etime.bean;


import java.util.List;

public class PostDetailBean {
    public int detailId;
    public Remark.User user;
    public int postId;
    public String title;
    public String content;
    public List<Remark> remarkList;
    public List<String> bitmapPath;
    public String time;
    public String date;
    private int requestCode;//请求编号
    private int responseCode;//回应编号


    public final static int UNKNOWN_ERROR = 0x03001;//未知错误

    public final static int POST_DETAIL_UP_STORE_REQUEST = 0x03002;//详细帖子上传请求
    public final static int POST_DETAIL_UP_STORE_RESPONSE_SUCCESSED = 0x03003;//详细帖子上传成功
    public final static int POST_DETAIL_UP_STORE_RESPONSE_FAILED =0x03004;//详细帖子上传失败
    public final static int POST_DETAIL_DOWN_LOAD_REQUEST = 0x03005;//详细帖子下载请求
    public final static int POST_DETAIL_DOWN_LOAD_RESPONSE_SUCCESSED=0x03006;//详细帖子下载成功
    public final static int POST_DETAIL_DOWN_LOAD_RESPONSE_FAILED=0x03007;//详细帖子下载失败
    public final static int POST_DETAIL_DELETE_REQUEST = 0x03008;//详细帖子删除请求
    public final static int POST_DETAIL_DELETE_RESPONSE_SUCCESSED=0x03009;//详细帖子删除成功
    public final static int POST_DETAIL_DELETE_RESPONSE_FAILED=0x0300A;//详细帖子删除失败


    public static class Remark
    {
        public int remarkId;
        public User user;
        public int detailId;
        public String time;
        public String date;
        public String content;

        public static class User{
            public String account;
            public String head;
            public String nickName;
      }
    }

    public Remark.User getUser() {
        return user;
    }

    public void setUser(Remark.User user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public String getTitle() {
        return title;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getPostId() {
        return postId;
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

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public List<Remark> getRemarkList() {
        return remarkList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRemarkList(List<Remark> remarkList) {
        this.remarkList = remarkList;
    }

    public String getPic()  //根据content的内容找到第一个图片的url  保留接口 2.13
    {
        if(bitmapPath==null || bitmapPath.size()==0)
        {
            return null;
        }
        else
        {
            return bitmapPath.get(0);
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public List<String> getBitmapPath() {
        return bitmapPath;
    }

    public void setBitmapPath(List<String> bitmapPath) {
        this.bitmapPath = bitmapPath;
    }
}
