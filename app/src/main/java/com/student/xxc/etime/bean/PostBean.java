package com.student.xxc.etime.bean;

import java.util.List;

public class PostBean {

    private List<Post> posts;//帖子详细内容
    private List<Integer> requestPostList;//需求帖子序号列表
    private List<Integer> responsePostList;//回应帖子序号列表
    private String source;
    private int requestCode;
    private int responseCode;


    public static class Post
    {
        public int PostId;
        public int detailId;
        public User user;
        public String title;
        public String pic;
        public String time;
        public String date;
        public int watch;
        public int remark;

        public Post(int PostId,int detailId,User user,String title,String pic,String time,int watch,int remark,String date)
        {
            this.detailId = detailId;
            this.PostId = PostId;
            this.user = user;
            this.title  =title;
            this.pic = pic;
            this.time = time;
            this.watch = watch;
            this.remark =  remark;
            this.date = date ;
        }

        public Post()
        {

        }

        public static class User{
            public String account;
            public String head;
            public String nickName;
        }

    }

    final public static int UNKNOWN_ERROR = 0x02001;//未知错误

    final public static int POST_UP_STORE_REQUEST = 0x02002;//帖子上传请求
    final public static int POST_UP_STORE_RESPONSE_SUCCESSED = 0x02003;//帖子上传成功
    final public static int POST_UP_STORE_RESPONSE_FAILED =0x02004;//帖子上传失败

    final public static int POST_DOWN_LOAD_COMMUNITY_ALL_REQUEST = 0x02005;//社区全部帖子下载请求
    final public static int POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_SUCCESSED=0x02006;//社区全部帖子下载成功
    final public static int POST_DOWN_LOAD_COMMUNITY_ALL_RESPONSE_FAILED=0x02007;//社区全部帖子下载失败

    final public static int POST_DOWN_LOAD_LIST_REQUEST = 0x02008;//按照序号帖子下载请求
    final public static int POST_DOWN_LOAD_LIST_RESPONSE_SUCCESSED=0x02009;//按照列表帖子下载成功
    final public static int POST_DOWN_LOAD_LIST_RESPONSE_FAILED=0x0200A;//按照列表帖子下载失败

    final public static int POST_COMMUNITY_GET_LIST_REQUEST = 0x0200B;//请求基准帖下面若干数量的帖子
    final public static int POST_COMMUNITY_GET_LIST_RESPONSE_SUCCESSED=0x0200C;//请求基准帖下面若干数量的帖子下载成功
    final public static int POST_COMMUNITY_GET_LIST_RESPONSE_FAILED=0x0200D;//请求基准帖下面若干数量的帖子下载失败

    final public static int POST_DOWN_LOAD_LIST_TOP = 0x0200E; //最新帖子为基准号

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
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

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Integer> getRequestPostList() {
        return requestPostList;
    }

    public void setRequestPostList(List<Integer> requestPostList) {
        this.requestPostList = requestPostList;
    }

    public List<Integer> getResponsePostList() {
        return responsePostList;
    }

    public void setResponsePostList(List<Integer> responsePostList) {
        this.responsePostList = responsePostList;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
