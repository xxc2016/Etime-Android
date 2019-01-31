package com.student.xxc.etime.entity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

public class Account {//账户类 用于管理用户账户信息


    static private String userName = null;
    static private String userAccount = null;
    static private String userImagePath  = null ;
    static private Context context;//对于应用Context的引用
    static private int State;//账户状态

    static  public final int  ACCOUNT_OFFLINE = 0x000001;
    static  public final int  ACCOUNT_ONLINE = 0x000002;

    static  public void initAccount()//初始化账户 必须在设置context后使用
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("selectedImagePath", "");
        String user_name=sharedPreferences.getString("user_name","用户");
        String user_account = sharedPreferences.getString("user_account","");
        int account_state =sharedPreferences.getInt("account_state",Account.ACCOUNT_OFFLINE);

        Account.userName = user_name;
        Account.userAccount = user_account;
        Account.userImagePath = imagePath;
        Account.State = account_state;
    }

    public static void setContext(Context context)
    {
        Account.context = context.getApplicationContext();//获得应用context的引用
    }

    public static String getUserName()
    {
        return userName;
    }

    public static String getUserAccount()
    {
        return userAccount;
    }

    public static String getUserImagePath() {
        return userImagePath;
    }


    public static int getState() {
        return State;
    }

    public static void setUserName(String userName)
    {
        if(userName == null || userName=="") //特殊情况的处理
        {
            userName = "用户";
        }
        Account.userName = userName;
        SharedPreferences sharedPreferences = context.getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", userName);
        editor.apply();
    }

    public  static void setUserAccount(String userAccount) {
        Account.userAccount = userAccount;
        SharedPreferences sharedPreferences = context.getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_account", userAccount);
        editor.apply();
    }

    public  static void setUserImagePath(String userImagePath) {
        if(userImagePath==null)
        {
            userImagePath = "";
        }
        Account.userImagePath = userImagePath;
        SharedPreferences sharedPreferences = context.getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedImagePath", userImagePath);
        editor.apply();
    }

    public static void setState(int state) {
        Account.State = state;
        SharedPreferences sharedPreferences = context.getSharedPreferences("photo_Path", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("account_state", Account.State);
        editor.apply();
    }
}
