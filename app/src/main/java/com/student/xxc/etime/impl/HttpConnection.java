package com.student.xxc.etime.impl;

import android.util.Log;

import com.student.xxc.etime.bean.PostBean;
import com.student.xxc.etime.bean.PostDetailBean;
import com.student.xxc.etime.bean.RemarkBean;
import com.student.xxc.etime.bean.TraceBean;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.entity.User;
import com.student.xxc.etime.helper.UrlHelper;

import java.io.File;
import java.util.List;

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

    public static void sendOkHttpRequest_sendPostDetail(PostDetailBean bean, Callback callback)//请求上传详细帖子类没有图片
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostDetailBean.POST_DETAIL_UP_STORE_REQUEST);
        String json = JsonManager.PostDetailBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_post_detail()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_sendPostDetail(PostDetailBean bean,Callback callback,List<File> files)//用户上传详细帖子类
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostDetailBean.POST_DETAIL_UP_STORE_REQUEST);
        String json = JsonManager.PostDetailBeanToJson(bean);
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);

        multipartBodyBuilder.addFormDataPart("json",json);
        multipartBodyBuilder.addFormDataPart("jsonType","postDetailBean");
        Log.i("json",json);
        if(files!=null) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                multipartBodyBuilder.addFormDataPart("file"+i,file.getName(),fileBody);
                Log.i("file"+i,file.getName());
            }
            multipartBodyBuilder.addFormDataPart("picNumber",""+files.size());
        }else
        {
            multipartBodyBuilder.addFormDataPart("picNumber",""+0);
        }
        RequestBody requestBody = multipartBodyBuilder.build();
        Request request = new Request.Builder().url(UrlHelper.getUrl_image()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_downLoadPostByList(PostBean bean, Callback callback)//按照序号请求用户帖子
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostBean.POST_DOWN_LOAD_LIST_REQUEST);
        String json = JsonManager.PostBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_post()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_downLoadPostALL(PostBean bean, Callback callback)//请求所有帖子
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostBean.POST_DOWN_LOAD_COMMUNITY_ALL_REQUEST);
        String json = JsonManager.PostBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_post()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_downLoadPostListByLine(PostBean bean, Callback callback)//请求基准帖后面若干帖子序号
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostBean.POST_COMMUNITY_GET_LIST_REQUEST);
        String json = JsonManager.PostBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_post()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_downLoadPostDetail(PostDetailBean bean, Callback callback)//根据detailId请求下载详细帖子类
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostDetailBean.POST_DETAIL_DOWN_LOAD_REQUEST);
        String json = JsonManager.PostDetailBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_post_detail()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public  static  void sendOkHttpRequest_sendUserBean_FollowList(UserBean bean, Callback callback)//添加好友
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        bean.setRequestCode(UserBean.USER_UP_FOLLOWLIST_REQUEST);
        String json = JsonManager.UserBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_userBean()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public  static  void sendOkHttpRequest_deleteUserBean_FollowList(UserBean bean,Callback callback)//删除好友
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        bean.setRequestCode(UserBean.USER_DELETE_FOLLOWLIST_REQUEST);
        String json = JsonManager.UserBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_userBean()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpRequest_deleteRemarkBean(RemarkBean bean, Callback callback)//删除评论类
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(RemarkBean.REMARK_DELETE_REQUEST);
        String json = JsonManager.RemarkBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_remark()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_deletePostDetail(PostDetailBean bean, Callback callback)//请求删除详细帖子类
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        bean.setRequestCode(PostDetailBean.POST_DETAIL_DELETE_REQUEST);
        String json = JsonManager.PostDetailBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_post_detail()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequest_downLoadUserBean(UserBean bean,Callback callback)//通过userId下载用户信息
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        bean.setRequestCode(UserBean.USER_DOWN_LOAD_REQUEST);
        String json = JsonManager.UserBeanToJson(bean);
        Log.i("json",""+json);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody= RequestBody.create(JSON,json);
        Request request =new Request.Builder().url(UrlHelper.getUrl_userBean()).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }




}
