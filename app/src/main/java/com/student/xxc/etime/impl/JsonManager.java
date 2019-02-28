package com.student.xxc.etime.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.student.xxc.etime.bean.ImageBean;
import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.PostDetailBean;
import com.student.xxc.etime.bean.RemarkBean;
import com.student.xxc.etime.bean.TraceBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.User;

public class JsonManager {

    static public String UserToJson(User user)
    {
          Gson gson = new Gson();
          String json = gson.toJson(user);
          return json;
    }

    static public User JsonToUser(String json)
    {
        Gson gson = new Gson();
        User user = gson.fromJson(json,User.class);
        return user;
    }

    static public String TraceBeanToJson(TraceBean tracebean)
    {
        Gson gson = new Gson();
        String json = gson.toJson(tracebean);
        return json;
    }

    static  public TraceBean JsonToTraceBean(String json)
    {
        Gson gson =new Gson();
        java.lang.reflect.Type type = new TypeToken<TraceBean>(){}.getType();
        TraceBean  tracebean = gson.fromJson(json,type);
        return tracebean;
    }

    static  public String PostBeanToJson(PostBean postBean)
    {
        Gson gson = new Gson();
        String json = gson.toJson(postBean);
        return json;
    }

    static  public PostBean JsonToPostBean(String json)
    {
        Gson gson =new Gson();
        java.lang.reflect.Type type = new TypeToken<PostBean>(){}.getType();
        PostBean  postBean = gson.fromJson(json,type);
        return postBean;
    }

    static  public String PostDetailBeanToJson(PostDetailBean postDetailBean)
    {
        Gson gson = new Gson();
        String json = gson.toJson(postDetailBean);
        return json;
    }

    static  public PostDetailBean JsonToPostDetailBean(String json)
    {
        Gson gson =new Gson();
        java.lang.reflect.Type type = new TypeToken<PostDetailBean>(){}.getType();
        PostDetailBean  postDetailBean = gson.fromJson(json,type);
        return postDetailBean;
    }

    static  public String RemarkBeanToJson(RemarkBean remarkBean)
    {
        Gson gson = new Gson();
        String json = gson.toJson(remarkBean);
        return json;
    }

    static  public RemarkBean JsonToRemarkBean(String json)
    {
        Gson gson =new Gson();
        java.lang.reflect.Type type = new TypeToken<RemarkBean>(){}.getType();
        RemarkBean  remarkBean = gson.fromJson(json,type);
        return remarkBean;
    }

    static  public String UserBeanToJson(UserBean userBean)
    {
        Gson gson = new Gson();
        String json = gson.toJson(userBean);
        return json;
    }

    static  public UserBean JsonToUserBean(String json)
    {
        Gson gson =new Gson();
        java.lang.reflect.Type type = new TypeToken<UserBean>(){}.getType();
        UserBean  userBean = gson.fromJson(json,type);
        return userBean;
    }

    static  public String ImageBeanToJson(ImageBean imageBean)
    {
        Gson gson = new Gson();
        String json = gson.toJson(imageBean);
        return json;
    }

    static  public ImageBean JsonToImageBean(String json)
    {
        Gson gson =new Gson();
        java.lang.reflect.Type type = new TypeToken<ImageBean>(){}.getType();
        ImageBean  imageBean = gson.fromJson(json,type);
        return imageBean;
    }

}
