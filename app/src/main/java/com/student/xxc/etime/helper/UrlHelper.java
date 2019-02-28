package com.student.xxc.etime.helper;


public class UrlHelper {//管理和后台连接ip和url  便于测试

    private static boolean isTest = false;

    static private String url_ip  ="47.100.106.208:8080";
    //    static private String url_user = "http://47.101.49.74:8080//E_TimeServer/servlet/E_TimeServlet";
//    static private String url_user_image = "http://47.101.49.74:8080//E_TimeServer/servlet/E_TimeServlet";
//    static private String url_base = "http://47.101.49.74:8080//E_TimeServer";
    static private String url_user = "http://"+url_ip+"//E_TimeServer/servlet/E_TimeServlet";
    static private String url_user_image = "http://"+url_ip+"//E_TimeServer/StoreImageServlet";
    static private String url_base = "http://"+url_ip+"//E_TimeServer";
    static private String url_user_trace="http://"+url_ip+"//E_TimeServer/TracesServlet";
    static private String url_post="http://"+url_ip+"//E_TimeServer/PostServlet";
    static private String url_post_detail="http://"+url_ip+"//E_TimeServer/PostDetailServlet";
    static private String url_remark="http://"+url_ip+"//E_TimeServer/RemarkServlet";
    static private String url_userBean = "http://"+url_ip+"//E_TimeServer/UserBeanServlet";
    static private String url_post_detail_image = "http://"+url_ip+"//E_TimeServer/PostDetailImageServlet";
    static private String url_image = "http://"+url_ip+"//E_TimeServer/ImageServlet";
    static private String url_image_source = "http://"+url_ip+"//E_TimeServer/ImageBeanServlet";



    static private String url_ip_test = "192.168.43.179:8080";
    //    static private String url_user_test = "http://192.168.1.105:8080//E_TimeServer/servlet/E_TimeServlet";
//    static private String url_user_image_test = "http://192.168.1.105:8080//E_TimeServer/StoreImageServlet";
//    static private String url_base_test ="http://192.168.1.105:8080//E_TimeServer";
    static private String url_user_test = "http://"+url_ip_test+"//E_TimeServer/servlet/E_TimeServlet";
    static private String url_user_image_test = "http://"+url_ip_test+"//E_TimeServer/StoreImageServlet";
    static private String url_base_test ="http://"+url_ip_test+"//E_TimeServer";
    static private String url_user_trace_test="http://"+url_ip_test+"//E_TimeServer/TracesServlet";
    static private String url_post_test="http://"+url_ip_test+"//E_TimeServer/PostServlet";
    static private String url_post_detail_test="http://"+url_ip_test+"//E_TimeServer/PostDetailServlet";
    static private String url_remark_test="http://"+url_ip_test+"//E_TimeServer/RemarkServlet";
    static private String url_userBean_test = "http://"+url_ip_test+"//E_TimeServer/UserBeanServlet";
    static private String url_post_detail_image_test = "http://"+url_ip_test+"//E_TimeServer/PostDetailImageServlet";
    static private String url_image_test = "http://"+url_ip_test+"//E_TimeServer/ImageServlet";
    static private String url_image_source_test = "http://"+url_ip_test+"//E_TimeServer/ImageBeanServlet";


    public static String getUrl_user() {
        if(!isTest) {
            return url_user;
        }
        else
        {
            return url_user_test;
        }
    }

    public static String getUrl_user_image() {
        if(!isTest)
        {
            return url_user_image;
        }
        else
        {
            return url_user_image_test;
        }
    }

    public static String getUrl_base() {
        if(!isTest) {
            return url_base;
        }else {
            return url_base_test;
        }
    }

    public static String getUrl_user_trace() {
        if(!isTest)
        {
            return url_user_trace;
        }
        else
        {
            return url_user_trace_test;
        }
    }

    public static String getUrl_post() {
        if(!isTest) {
            return url_post;
        }
        else
        {
            return url_post_test;
        }
    }

    public static String getUrl_post_detail() {
        if(!isTest) {
            return url_post_detail;
        }
        else
        {
            return url_post_detail_test;
        }
    }

    public static String getUrl_remark()
    {
        if(!isTest) {
            return url_remark;
        }
        else
        {
            return url_remark_test;
        }
    }

    public static String getUrl_userBean() {
        if(!isTest)
        {
            return url_userBean;
        }
        else {
            return url_userBean_test;
        }
    }

    public static String getUrl_post_detail_image() {
        if(!isTest) {
              return url_post_detail_image;
        }else {
            return url_post_detail_image_test;
        }
    }

    public static String getUrl_image() {
        if(!isTest) {
            return url_image;
        }else {
            return url_image_test;
        }

    }

    public static String getUrl_image_source() {
        if(!isTest) {
            return url_image_source;
        }else {
          return url_image_source_test;
        }
    }
}
