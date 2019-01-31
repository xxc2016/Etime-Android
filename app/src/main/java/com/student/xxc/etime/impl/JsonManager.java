package com.student.xxc.etime.impl;

import com.google.gson.Gson;
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
}
