package com.student.xxc.etime.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SiteHelper {//用于存放和获取地点信息缓存

    static TraceSQLiteOpenHelper siteHelper;
    static TraceSQLiteOpenHelper distanceHelper;
    static Context context;

    public static final String CREATE_TABLE_LATLON_POINT = "CREATE TABLE " +
            "latlonPoint(siteId TEXT PRIMARY KEY,latitude REAL,longitude REAL)";

    public static final String CREATE_TABLE_DISTANCE = "CREATE TABLE " +
            "distance(siteId_a TEXT,siteId_b TEXT,dis REAL)";

    public static final String DELETE_TABLE_LATLON_POINT = "DELETE FROM " +"latlonPoint";

    public static final String DELETE_TABLE_DISTANCE = "DELETE FROM "+" distance";

    static public void getDatabase(TraceSQLiteOpenHelper helper)
    {
        distanceHelper = helper;
        siteHelper = helper;
    }


    static public void setContext(Context context)
    {
        context = context.getApplicationContext(); //同改正一个context的问题
    }

    static public String  getLanLonPoint(String siteId)
    {
        String [] sel =new String [1];
        sel[0] = siteId;
        SQLiteDatabase db = siteHelper.getWritableDatabase();
        Cursor cursor  =db.query("latlonPoint",null,"siteId = ? ",
                sel,null,null,null);

        if(cursor.moveToNext())
        {
            double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double lon = cursor.getDouble(cursor.getColumnIndex("longitude"));

            return lon+","+lat;
        }
        else
        {
            return null;
        }
    }

    static public void  setLanLonPoint(String siteId,double lat,double lon)
    {
        if(getLanLonPoint(siteId)==null) {//保证不会重复加
            SQLiteDatabase db = siteHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("siteId", siteId);
            cv.put("latitude", lat);
            cv.put("longitude", lon);
            db.insert("latlonPoint", null, cv);
        }
    }

    static public double  getDistance(String siteId_a,String siteId_b)
    {
        String [] sel =new String [2];
        sel[0] = siteId_a;
        sel[1] = siteId_b;
        SQLiteDatabase db = distanceHelper.getWritableDatabase();
        Cursor cursor  =db.query("distance",null,"siteId_a = ? and siteId_b = ?",
                sel,null,null,null);

        if(cursor.moveToNext())
        {
            double dis = cursor.getDouble(cursor.getColumnIndex("dis"));
            return dis;
        }
        else
        {
            return -1;
        }
    }

    static public void  setDistance(String siteId_a,String siteId_b,double dis)
    {
        if(getDistance(siteId_a,siteId_b)==-1) {
            SQLiteDatabase db = distanceHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("siteId_a", siteId_a);
            cv.put("siteId_b", siteId_b);
            cv.put("dis", dis);
            db.insert("distance", null, cv);
        }
    }


}
