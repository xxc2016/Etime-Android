package com.student.xxc.etime.entity;

public class User {//用户登陆和注册和其他通讯
     private String name;
     private String password;
     private String nickName;
     private String ImagePath;
     private int requestCode;
     private int responseCode;

     final public static  int  LOGIN_REQUEST = 0x00001;  //登陆请求
     final public static  int  REGISTER_REQUEST = 0x00002;//注册请求
     final public static  int  LOGIN_RESPONSE_SUCCESSED  =0x00003;//登陆成功
     final public static  int  LOGIN_RESPONSE_FAILED  =0x00004;//登陆失败
     final public static  int  REGISTER_RESPONSE_SUCCESSED  =0x00005;//注册成功
     final public static  int  REGISTER_RESPONSE_FAILED  =0x00006;//注册失败

     final public static int  IMAGE_STORE_REQUEST = 0x00007;//用户信息上传请求
     final public static int  IMAGE_STORE_RESPONSE_SUCCESSED = 0x00008;//用户信息上传成功
     final public static int  IMAGE_STORE_RESPONSE_FAILED =0x00009;//用户信息上传失败
     final public static int  IMAGE_DOWNLOAD_REQUEST= 0x00010;//用户信息下载请求
     final public static int  IMAGE_DOWNLOAD_RESPONSE_SUCCESSED = 0x00011;//用户信息下载成功
     final public static int  IMAGE_DOWNLOAD_RESPONSE_FAILED = 0x00012;//用户信息下载失败
     final public static  int  IMAGE_STORE_EXTRA_REQUEST = 0x00013;//用户信息上传请求(除头像)
     final public static  int  IMAGE_STORE_EXTRA_RESPONSE_SUCCESSED = 0x00014;//用户信息上传成功（除头像）
     final public static  int  IMAGE_STORE_EXTRA_RESPONSE_FAILED =0x00015;//用户信息上传失败（除头像）

     final public static int   UNKNOWN_ERROR = 0x00016;//未知错误

     public User(String nickName) {
          this.nickName = nickName;
     }

     public User() {
     }

     public String getName() {
          return name;
     }

     public int getResponseCode() {
          return responseCode;
     }

     public String getPassword() {
          return password;
     }

     public int getRequestCode() {
          return requestCode;
     }

     public void setName(String name) {
          this.name = name;
     }

     public void setPassword(String password) {
          this.password = password;
     }

     public void setRequestCode(int requestCode) {
          this.requestCode = requestCode;
     }

     public void setResponseCode(int responseCode) {
          this.responseCode = responseCode;
     }

     public String getNickName() {
          return nickName;
     }

     public void setNickName(String nickName) {
          this.nickName = nickName;
     }

     public String getImagePath() {
          return ImagePath;
     }

     public void setImagePath(String imagePath) {
          ImagePath = imagePath;
     }
}

