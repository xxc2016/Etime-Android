package com.student.xxc.etime.impl;

import android.util.Log;

import com.student.xxc.etime.bean.TraceBean;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.UrlHelper;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class HttpConnection {//用于和后台通讯接口

    public static void sendOkHttpRequest_register(String name,String password,Callback callback)
    {
        OkHttpClient okHttpClient = new OkHttpClient();


        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setRequestCode(User.REGISTER_REQUEST);


        String json = JsonManager.UserToJson(user);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_user()).post(requestBody).build();
       okHttpClient.newCall(request).enqueue(callback);
    }



    public static void sendOkHttpRequest_login(String name,String password,Callback callback)
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setRequestCode(User.LOGIN_REQUEST);


        String json = JsonManager.UserToJson(user);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_user()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);

    }


    public static void sendOkHttpRequest_sendUserImage(String userAccount,Callback callback,File file)//用户上传头像
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        RequestBody requestBody = builder.setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),fileBody)
                .addFormDataPart("userAccount",userAccount)
                .build();

        Request request = new Request.Builder().url(UrlHelper.getUrl_user_image()).post(requestBody).build();
        Log.i("POST","send file:"+file.getName());
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_image_downLoad(String userAccount,Callback callback)//用户下载头像url和其他信息
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        User user = new User();
        user.setName(userAccount);
        user.setRequestCode(User.IMAGE_DOWNLOAD_REQUEST);

        String json = JsonManager.UserToJson(user);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_user()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);

    }


    public static void sendOkHttpRequest_sendUserExtraImage(String userAccount,String userName,Callback callback)//上传用户信息除头像
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        User user = new User();
        user.setName(userAccount);
        user.setNickName(userName);
        user.setRequestCode(User.IMAGE_STORE_EXTRA_REQUEST);

        String json = JsonManager.UserToJson(user);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_user()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_sendTraces(TraceBean bean, Callback callback)//上传用户日程
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(TraceBean.UP_STORE_REQUEST);
        String json = JsonManager.TraceBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_user_trace()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpRequest_downLoadTraces(String userAccount,TraceBean bean, Callback callback)//下载用户日程
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(TraceBean.DOWN_LOAD_REQUEST);
        bean.setUserAccount(userAccount);
        String json = JsonManager.TraceBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_user_trace()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}
