package com.student.xxc.etime.helper;

public class UrlHelper {//管理和后台连接ip和url  便于测试

    private static boolean isTest = false;

    static private String url_ip  ="47.101.49.74:8080";
//    static private String url_user = "http://47.101.49.74:8080//E_TimeServer/servlet/E_TimeServlet";
//    static private String url_user_image = "http://47.101.49.74:8080//E_TimeServer/servlet/E_TimeServlet";
//    static private String url_base = "http://47.101.49.74:8080//E_TimeServer";
      static private String url_user = "http://"+url_ip+"//E_TimeServer/servlet/E_TimeServlet";
      static private String url_user_image = "http://"+url_ip+"//E_TimeServer/StoreImageServlet";
      static private String url_base = "http://"+url_ip+"//E_TimeServer";



    static private String url_ip_test = "192.168.1.105:8080";
//    static private String url_user_test = "http://192.168.1.105:8080//E_TimeServer/servlet/E_TimeServlet";
//    static private String url_user_image_test = "http://192.168.1.105:8080//E_TimeServer/StoreImageServlet";
//    static private String url_base_test ="http://192.168.1.105:8080//E_TimeServer";
    static private String url_user_test = "http://"+url_ip_test+"//E_TimeServer/servlet/E_TimeServlet";
    static private String url_user_image_test = "http://"+url_ip_test+"//E_TimeServer/StoreImageServlet";
    static private String url_base_test ="http://"+url_ip_test+"//E_TimeServer";


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
}
