package com.student.xxc.etime.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.student.xxc.etime.entity.Trace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TraceManager {//用于管理trace的工具类
    static List<Trace> traceList;
    static TraceSQLiteOpenHelper helper;
    static Context context;
    static int traceId;
    static boolean showFinished=false;



    public static final String CREATE_DATABASE = "CREATE TABLE "+
            "userAction(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "time TEXT,event TEXT,date TEXT,traceId INTEGER,finish INTEGER)";
    public  static final String DROP_TABLE = "DROP TABLE "+
            "userAction";

    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    static public void getDatabase()
    {
       helper = new TraceSQLiteOpenHelper(context,"userAction",null,1);
       helper.getWritableDatabase();
    }

    static public void setTraceList(List<Trace> traceList)
    {
        TraceManager.traceList = traceList;
    }

    static public int  getTraceId()
    {
        traceId ++;
        TraceManager.traceList = traceList;
        SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("traceId",traceId);
//        editor.clear();
        editor.apply();
        Log.i("traceId","------------------------"+traceId);
        return traceId;
    }

    static public void setContext(Context context)
    {
        TraceManager.context = context;
        SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        traceId = sp.getInt("traceId",0);  //获得消息索引
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("traceId",traceId);
        editor.apply();
    }

    static public List<Trace> initialTraces()
    {
        traceList  = new ArrayList<Trace>();
        SQLiteDatabase db = helper.getWritableDatabase();

        String [] col = new String [1];
        col[0] = "date";

        String [] sel =new String [1];
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = Calendar.getInstance().getTime();
        sel[0] = df.format(tempDate);

        Cursor cursor  =db.query("userAction",null,"date = ?",
                sel,null,null,"time desc");
        int count = -1;
        if(cursor.moveToFirst())
        {
            do{
                count++;
                String time  =cursor.getString(cursor.getColumnIndex("time"));
                String event = cursor.getString(cursor.getColumnIndex("event"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int traceId = cursor.getInt(cursor.getColumnIndex("traceId"));
                int finishTemp= cursor.getInt(cursor.getColumnIndex("finish"));
                boolean finish;
                if(finishTemp==1)
                {
                    finish=true ;
                }
                else{
                    finish =false;
                }
                if(!finish  ||  showFinished)
                {
                    traceList.add(new Trace(time, date, event, traceId, finish));
                }
                Log.i("database","------------"+date+"   "+time+"  "+event+"  "+traceId+" "+finish);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return traceList;
    }

    static  public void deleteTable()
    {
        helper.getWritableDatabase().execSQL(DROP_TABLE);
    }

    static  public void getTraces()   //单纯读一遍
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_DATABASE);
        Cursor cursor  =db.query("userAction",null,null,
        null,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                //String index = cursor.getString(cursor.getColumnIndex("index"));
                String time  =cursor.getString(cursor.getColumnIndex("time"));
                String event = cursor.getString(cursor.getColumnIndex("event"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int traceId = cursor.getInt(cursor.getColumnIndex("traceId"));
                int finish = cursor.getInt(cursor.getColumnIndex("finish"));
                Log.i("database","------------"+date+"   "+time+"  "+event+"  "+traceId+" "+finish);
            }while (cursor.moveToNext());
            cursor.close();
        }
    }


    static public  void saveTraces()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_DATABASE);
        ContentValues cv = new ContentValues();
        for(int i=0;i<traceList.size();i++)
        {
            cv.put("time",traceList.get(i).getTime());
            cv.put("event",traceList.get(i).getEvent());
            cv.put("date",traceList.get(i).getDate());
            cv.put("traceId",traceList.get(i).getTraceId());
            cv.put("finish",traceList.get(i).getFinish_int());

            db.insert("userAction",null,cv);
            Log.i("input","------------"+traceList.get(i).getDate()+"   "+traceList.get(i).getTime()
                    +"  "+traceList.get(i).getEvent()+" "+traceList.get(i).getTraceId()+" "+traceList.get(i).getFinish_int());
        }
    }

    static  public void updateTrace(Trace e)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update userAction set time = '"+e.getTime()+"',event = '"+e.getEvent()+
                "',date = '"+e.getDate()+"'"+",finish = "+e.getFinish_int()+"  where traceId = "+e.getTraceId());
        Log.i("SqlUpdate","--------------------------"+"update userAction set time = '"+e.getTime()+"',event = '"+e.getEvent()+
                "',date = '"+e.getDate()+"'"+",finish = "+e.getFinish_int()+"  where traceId = "+e.getTraceId());

    }

    static  public  void addTrace(Trace e)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("time",e.getTime());
        cv.put("event",e.getEvent());
        cv.put("date",e.getDate());
        cv.put("traceId",e.getTraceId());
        cv.put("finish",e.getFinish_int());
        Log.i("SqlAdd","id:"+e.getTraceId());
        db.insert("userAction",null,cv);
    }

    static  public void deleteTrace(Trace e)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from userAction where traceId="+e.getTraceId());
    }

    static public void finishTrace(Trace e){
        e.setFinish(true);
        updateTrace(e);
    }
    static  public void setShowFinished(boolean show)
    {
        TraceManager.showFinished = show;
    }
}


