package com.student.xxc.etime.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.student.xxc.etime.bean.TraceBean;
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

}
