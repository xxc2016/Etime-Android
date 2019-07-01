package com.student.xxc.etime.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.student.xxc.etime.bean.TraceBean;
import com.student.xxc.etime.entity.Account;
import com.student.xxc.etime.entity.Trace;
import com.student.xxc.etime.helper.TraceSQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TraceManager {//用于管理trace的工具类
    static List<Trace> traceList;
    static TraceSQLiteOpenHelper helper;
    static Context context;
    static int traceId;
    static boolean showFinished=false;
    static boolean useIntellectSort = false;



    public static final String CREATE_DATABASE = "CREATE TABLE "+
            "userTrace(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "ESTime TEXT,LETime TEXT,time TEXT,event TEXT,date TEXT,traceId INTEGER,finish INTEGER," +
            "hasESTime INTEGER,hasLETime INTEGER,siteId TEXT,siteText Text,predict INTEGER)";
    public  static final String DROP_TABLE = "DROP TABLE "+
            "userTrace";
    public  static final String DELETE_TABLE = "DELETE FROM "+ "userTrace";

    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    static public void getDatabase()
    {
        if(helper==null){
            helper = new TraceSQLiteOpenHelper(context,"userTrace",null,1);
        }
       helper.getWritableDatabase();
    }

    static public void setTraceList(List<Trace> traceList)
    {
        TraceManager.traceList = traceList;
    }

    static public int  getTraceId()
    {
        traceId ++;
//        TraceManager.traceList = traceList;
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
        TraceManager.context = context.getApplicationContext(); //同改正一个context的问题  1.31
        SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        traceId = sp.getInt("traceId",0);  //获得消息索引
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("traceId",traceId);
        editor.apply();
    }

    static public List<Trace> initialTraces(String  nowDate)
    {
        traceList  = new ArrayList<Trace>();
        SQLiteDatabase db = helper.getWritableDatabase();

        String [] col = new String [1];
        col[0] = "date";

         String [] sel =new String [1];
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Date tempDate = Calendar.getInstance().getTime();
//        sel[0] = df.format(tempDate);
          sel[0] = nowDate;

        Cursor cursor  =db.query("userTrace",null,"date = ?",
                sel,null,null,"time asc");
        int count = -1;
        if(cursor.moveToFirst())
        {
            do{
                count++;
                /*String time  =cursor.getString(cursor.getColumnIndex("time"));
                String event = cursor.getString(cursor.getColumnIndex("event"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int traceId = cursor.getInt(cursor.getColumnIndex("traceId"));
                int finishTemp= cursor.getInt(cursor.getColumnIndex("finish"));
                int importantTemp = cursor.getInt(cursor.getColumnIndex("important"));
                int urgentTemp = cursor.getInt(cursor.getColumnIndex("urgent"));
                int fixTemp = cursor.getInt(cursor.getColumnIndex("fix"));
                int predict = cursor.getInt(cursor.getColumnIndex("predict"));*/
                String ESTime  =cursor.getString(cursor.getColumnIndex("ESTime"));
                String LETime  =cursor.getString(cursor.getColumnIndex("LETime"));
                String time  =cursor.getString(cursor.getColumnIndex("time"));
                String event = cursor.getString(cursor.getColumnIndex("event"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String siteId  =cursor.getString(cursor.getColumnIndex("siteId"));
                String siteText  =cursor.getString(cursor.getColumnIndex("siteText"));
                int traceId = cursor.getInt(cursor.getColumnIndex("traceId"));
                int finishTemp= cursor.getInt(cursor.getColumnIndex("finish"));
                int hasEstTemp= cursor.getInt(cursor.getColumnIndex("hasESTime"));
                int hasLetTemp = cursor.getInt(cursor.getColumnIndex("hasLETime"));
                int predict = cursor.getInt(cursor.getColumnIndex("predict"));
                boolean finish;
                boolean hasESTime;
                boolean hasLETime;
                if(finishTemp==1)
                {
                    finish=true ;
                }
                else{
                    finish =false;
                }
                if(hasEstTemp ==1)
                {
                    hasESTime = true;
                }
                else
                {
                    hasESTime = false;
                }
                if(hasLetTemp==1)
                {
                    hasLETime = true;
                }
                else
                {
                    hasLETime = false;
                }

                if(!finish  ||  showFinished)
                {
                    traceList.add(new Trace(traceId,time,event ,date,hasESTime,hasLETime,
                            ESTime,LETime,finish,siteId,siteText,predict));
                }
                Log.i("database","------------"+date+"   "+time+"   "+ESTime+"  "+LETime+"  "+event+"  "+traceId+" "+finish+" "+hasESTime+
                        " "+hasLETime +" "+siteId+" "+siteText+"  "+predict);
            }while (cursor.moveToNext());
            cursor.close();
        }
        if(useIntellectSort)//如果开启智能排序
        {
            Log.i("useIntellectSort","-----------------------------------"+"智能排序");
           // return intellectSort(traceList);
            return traceList;//取消原来智能排序算法 6.29
        }
        Log.i("useIntellectSort","-----------------------------------"+"普通排序");
        return traceList;
    }

    /*static  private  boolean  judgeByLabel(Trace e1,Trace e2)//相当于大于
    {
        if(e1.getUrgent()== true)
        {
            if(e2.getUrgent()==false)
            {
                return true;
            }
            else
            {
                if(e1.getImportant()==true && e2.getImportant()==false)
                {
                    return true;
                }
            }
        }
        else
        {
            if(e1.getImportant()==true)
            {
                if(e2.getUrgent()==false && e2.getImportant()==false)
                {
                    return true;
                }
            }
        }
        return false;
    }

    static  private  List<Trace> sortByLabel(List<Trace> traceList)
    {
        for(int j=0;j<traceList.size();j++)
        {
            for(int i=0;i<traceList.size()-1;i++) {
                if (judgeByLabel(traceList.get(i+1), traceList.get(i)))
                {
                    Trace temp =traceList.get(i);
                    traceList.set(i,traceList.get(i+1));
                    traceList.set(i+1,temp);
                }
            }
        }
        return traceList;
    }

    static  private  ArrayList<Trace> sortByTime(ArrayList<Trace> traceList)
    {
        for(int j=0;j<traceList.size();j++)
        {
            for(int i=0;i<traceList.size()-1;i++) {
                if (traceList.get(i+1).getTime().compareTo(traceList.get(i).getTime())<0)//倒序
                {
                    Trace temp =traceList.get(i);
                    traceList.set(i,traceList.get(i+1));
                    traceList.set(i+1,temp);
                }
            }
        }
        return traceList;
    }

    static private  int getTimeInterval(int startHour,int startMinute,int endHour,int endMinute)
    {
        return (endHour-startHour)*60+endMinute-startMinute;
    }

   static  private  int getNowMinEvent_1(List<Trace> arrayList)//f返回当前优先级中花费最小的事件索引
   {
       int temp = 0;
       if(arrayList.size()==0)
       {
           return -1;
       }
       int tempInterval = arrayList.get(0).getPredict();
       for(int i=1;i<arrayList.size();i++)
       {
           Trace preTrace = arrayList.get(i-1);
           Trace nowTrace = arrayList.get(i);
           if(preTrace.getUrgent()==nowTrace.getUrgent() && preTrace.getImportant()==nowTrace.getImportant())
           {
               if(nowTrace.getPredict()<tempInterval)
               {
                   temp = i;
                   tempInterval = nowTrace.getPredict();
               }
           }
           else
           {
               break;
           }
       }
       return temp;
   }

   static private  int getNowMinEvent_2(List<Trace> arrayList,int interval)//返回小于给定时隙的最高优先级的最小花费事件
   {
       int temp=-1;
       int tempInterval = -1;
       if(arrayList.size()==0)
       {
           return temp;
       }
       if(arrayList.get(0).getPredict()<interval)
       {
           temp = 0;
           tempInterval = arrayList.get(0).getPredict();
       }
       for(int i=1;i<arrayList.size();i++)
       {
           Trace preTrace = arrayList.get(i-1);
           Trace nowTrace = arrayList.get(i);
           if(preTrace.getUrgent()!=nowTrace.getUrgent() || preTrace.getImportant()!=nowTrace.getImportant())
           {
               if(temp!=-1)
               {
                   break;
               }
           }
           if(nowTrace.getPredict()<tempInterval || (temp==-1 &&  nowTrace.getPredict()<interval))
           {
               temp = i;
               tempInterval = nowTrace.getPredict();
           }
       }
       return temp;
   }


    static private List<Trace> intellectSort(List<Trace> traceList)  //排序函数
    {
        ArrayList<Trace>  tempList = new ArrayList<Trace>();
        tempList.addAll(traceList);
        ArrayList<Trace>  fixList = new ArrayList<Trace>();
        ArrayList<Trace>  finishList = new ArrayList<Trace>();
        ArrayList<Trace>  SortedList = new ArrayList<Trace>();
        for(int i=0;i<traceList.size();i++)
        {
            if(traceList.get(i).getFix()==true && traceList.get(i).getFinish()==false)
            {
                fixList.add(traceList.get(i));
                traceList.remove(i);
                i--;
            }
        }
        for(int i=0;i<traceList.size();i++)
        {
            if(traceList.get(i).getFinish()==true)
            {
                finishList.add(traceList.get(i));
                traceList.remove(i);
                i--;
            }
        }
        traceList = sortByLabel(traceList);
        for(int i=0;i<traceList.size();i++)
        {
            Log.i("trace","______________________"+traceList.get(i).getEvent()
                    +" "+traceList.get(i).getUrgent()+" "+traceList.get(i).getImportant());
        }
        Date tempDate = Calendar.getInstance().getTime();
        SimpleDateFormat df_hour = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(df_hour.format(tempDate));
        SimpleDateFormat df_minute = new SimpleDateFormat("mm");
        int minute = Integer.parseInt(df_minute.format(tempDate));
        Log.i("TraceManager","-------------------------hour:"+hour+"   minute:"+minute);
        while(!traceList.isEmpty())
        {
            int fixHour = -1;
            int fixMinute =-1;//找到可以出入事件的固定事件
            int loc=-1;
            int tempHour = hour;
            int tempMinute = minute;
            for(int i=0;i<fixList.size();i++)
            {
                Trace tempTrace = fixList.get(i);
                if(i!=0)
                {
                    int tempHour_1 = fixList.get(i-1).getHour();
                    int tempMinute_1 =fixList.get(i-1).getMinute()+fixList.get(i-1).getPredict();
                    if(tempMinute_1>=60)
                    {
                        int t = (tempMinute_1-tempMinute_1%60)/60;
                        tempMinute_1%=60;
                        tempHour_1+=t;
                    }
                    if(getTimeInterval(tempHour,tempMinute,tempHour_1,tempHour_1)>0)
                    {
                        tempHour = tempHour_1;
                        tempMinute = tempMinute_1;
                    }
                }
                int interval =  getTimeInterval(tempHour,tempMinute,tempTrace.getHour(),tempTrace.getMinute());
                if(tempTrace.getFinish()==false && interval>0)//完成事件无效
                {
                    if(getNowMinEvent_2(traceList,interval)!=-1)
                    {
                        fixHour = tempTrace.getHour();
                        fixMinute = tempTrace.getMinute();
                        loc = i;
                        break;
                    }
                }
            }
            if(loc==-1)//m没有FIX节点约束
            {
                if(fixList.size()!=0)
                {
                    Trace tempTrace  =fixList.get(fixList.size()-1);
                    int tempLastHour = tempTrace.getHour();
                    int tempLastMinute = tempTrace.getMinute();
                    tempLastMinute+=tempTrace.getPredict();
                    if(tempLastMinute>=60)
                    {
                        int t = (tempLastMinute-tempLastMinute%60)/60;
                        tempLastMinute%=60;
                        tempLastHour+=t;
                    }
                    if(getTimeInterval(tempLastHour,tempLastMinute,hour,minute)<=0)
                    {
                        hour = tempLastHour;
                        minute = tempLastMinute;
                    }
                }
                Trace selectTrace = traceList.get(0);
                selectTrace.setTime(hour,minute);
                SortedList.add(selectTrace);
                traceList.remove(0);
                minute+=selectTrace.getPredict();
                if(minute>=60)
                {
                    int t = (minute-minute%60)/60;
                    minute%=60;
                    hour+=t;
                }
            }
            else {
                Trace tempFix = fixList.get(loc);
                int index = getNowMinEvent_2(traceList,getTimeInterval(hour,minute,tempFix.getHour(),tempFix.getMinute()));
                Trace selectTrace = traceList.get(index);
                selectTrace.setTime(hour,minute);
                SortedList.add(selectTrace);
                traceList.remove(index);
                minute = minute+selectTrace.getPredict();
                if(minute>=60)
                {
                    int t = (minute-minute%60)/60;
                    minute%=60;
                    hour+=t;
                }
            }
            Log.i("fixEvent","________________________fixHour:"+fixHour+"  fixMinute:"+fixMinute);
        }
        // return traceList;
        for(int i=0;i<SortedList.size();i++)
        {
            Trace tempTrace = SortedList.get(i);
            Log.i("Sorted","----------------------------------event:"+tempTrace.getEvent()
                    +"  time:"+tempTrace.getTime()+"   predict:"+tempTrace.getPredict()+
                    "-----------\n"+"urgent:"+tempTrace.getUrgent()+"  important:"+tempTrace.getImportant()+"   fix:"+tempTrace.getFix()
                    +"    i:"+i);
        }
        ArrayList<Trace> tempList_1 = new ArrayList<Trace>();
        tempList_1.addAll(SortedList);
        tempList_1.addAll(fixList);
        tempList_1.addAll(finishList);
        tempList_1 = sortByTime(tempList);
        for(int i=0;i<tempList_1.size();i++)
        {
            Trace tempTrace = tempList_1.get(i);
            Log.i("Sorted","----------------------------------event:"+tempTrace.getEvent()
                    +"  time:"+tempTrace.getTime()+"   predict:"+tempTrace.getPredict()+
                    "-----------\n"+"urgent:"+tempTrace.getUrgent()+"  important:"+tempTrace.getImportant()+"   fix:"+tempTrace.getFix()
                    +"    i:"+i);
        }

        return tempList_1;
    }*/

    static  public void dropTable()
    {
        helper.getWritableDatabase().execSQL(DROP_TABLE);
    }

    static  public void deleteTable()
    {
        helper.getWritableDatabase().execSQL(DELETE_TABLE);
    }

    static  public TraceBean  getTraces()   //单纯读一遍  //2.1  改成返回数据库TraceBean
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        //db.execSQL(DROP_TABLE);
        //db.execSQL(CREATE_DATABASE);
        Cursor cursor  =db.query("userTrace",null,null,
        null,null,null,null);
        ArrayList<TraceBean.Trace>  traces = new ArrayList<TraceBean.Trace>();
        TraceBean list = new TraceBean();
        list.setUserAccount(Account.getUserAccount());
        if(cursor.moveToFirst())
        {
            do{
                //String index = cursor.getString(cursor.getColumnIndex("index"));
                /*String time  =cursor.getString(cursor.getColumnIndex("time"));
                String event = cursor.getString(cursor.getColumnIndex("event"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int traceId = cursor.getInt(cursor.getColumnIndex("traceId"));
                int finish = cursor.getInt(cursor.getColumnIndex("finish"));
                int important= cursor.getInt(cursor.getColumnIndex("important"));
                int urgent = cursor.getInt(cursor.getColumnIndex("urgent"));
                int fix = cursor.getInt(cursor.getColumnIndex("fix"));
                int predict = cursor.getInt(cursor.getColumnIndex("predict"));*/
                String ESTime  =cursor.getString(cursor.getColumnIndex("ESTime"));
                String LETime  =cursor.getString(cursor.getColumnIndex("LETime"));
                String time  =cursor.getString(cursor.getColumnIndex("time"));
                String event = cursor.getString(cursor.getColumnIndex("event"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String siteId  =cursor.getString(cursor.getColumnIndex("siteId"));
                String siteText  =cursor.getString(cursor.getColumnIndex("siteText"));
                traceId = cursor.getInt(cursor.getColumnIndex("traceId"));
                int finish= cursor.getInt(cursor.getColumnIndex("finish"));
                int hasESTime= cursor.getInt(cursor.getColumnIndex("hasESTime"));
                int hasLETime= cursor.getInt(cursor.getColumnIndex("hasLETime"));
                int predict = cursor.getInt(cursor.getColumnIndex("predict"));

                Trace trace =new Trace(traceId,time,event ,date,hasESTime,hasLETime,
                        ESTime,LETime,finish,siteId,siteText,predict);
                TraceBean.Trace bean = new TraceBean.Trace(Account.getUserAccount(),ESTime,LETime,time,event,date,finish,traceId,
                        hasESTime,hasLETime,siteId,siteText,predict);
                traces.add(bean);
                Log.i("database","------------"+"------------"+date+"   "+time+"   "+ESTime+"  "+LETime+"  "+event+"  "+traceId+" "+finish+" "+hasESTime+
                        " "+hasLETime +" "+siteId+" "+siteText+"  "+predict+"  account"+Account.getUserAccount());
            }while (cursor.moveToNext());
            cursor.close();
        }
        list.setTraces(traces);
        Gson gson = new Gson();
        String json = gson.toJson(list);
        Log.i("json",json);

//        java.lang.reflect.Type type = new TypeToken<TraceBean>(){}.getType();
//        TraceBean  traceList = gson.fromJson(json,type);
//        Log.i("json",traceList.getUserAccount()+"  "+traceList.getTraces().get(1).toString());  //回归测试
        return list;
    }


    static public void  resetTraces(TraceBean traceBean)//通过traceBean 改数据库
    {
        deleteTable();
        List<TraceBean.Trace>  traces =  traceBean.getTraces();
        Iterator<TraceBean.Trace> it =  traces.iterator();
        while(it.hasNext())
        {
            TraceBean.Trace  trace = it.next();
            Trace  trace1= new Trace(trace.traceId,trace.time,trace.event ,trace.date,trace.hasESTime,trace.hasLETime,
                    trace.ESTime,trace.LETime,trace.finish,trace.siteId,trace.siteText,trace.predict);
            addTrace(trace1);
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
            cv.put("ESTime",traceList.get(i).getESTime());
            cv.put("LETime",traceList.get(i).getLETime());
            cv.put("time",traceList.get(i).getTime());
            cv.put("event",traceList.get(i).getEvent());
            cv.put("date",traceList.get(i).getDate());
            cv.put("siteId",traceList.get(i).getSiteId());
            cv.put("siteText",traceList.get(i).getSiteText());
            cv.put("traceId",traceList.get(i).getTraceId());
            cv.put("finish",traceList.get(i).getFinish_int());
            cv.put("hasESTime",traceList.get(i).get_hasEst_int());
            cv.put("hasLETime",traceList.get(i).get_hasLet_int());
            cv.put("predict",traceList.get(i).getPredict());

            db.insert("userTrace",null,cv);
            Log.i("input","------------"+traceList.get(i).getESTime()+"  "+traceList.get(i).getLETime()+"  "+traceList.get(i).getTime()
                    +traceList.get(i).getDate()+"   "+traceList.get(i).getEvent()+"  "+traceList.get(i).getTraceId()+"  "+traceList.get(i).getSiteText()
                    +"  "+traceList.get(i).getFinish_int()+" "+traceList.get(i).get_hasEst_int()+" "+traceList.get(i).get_hasLet_int()
                    +" "+traceList.get(i).getPredict());
        }
    }

    static  public void updateTrace(Trace e)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put("ESTime",e.getESTime());
        cv.put("LETime",e.getLETime());
        cv.put("time",e.getTime());
        cv.put("event",e.getEvent());
        cv.put("date",e.getDate());
        cv.put("siteText",e.getSiteText());
        cv.put("traceId",e.getTraceId());
        cv.put("finish",e.getFinish_int());
        cv.put("hasESTime",e.get_hasEst_int());
        cv.put("hasLETime",e.get_hasLet_int());
        cv.put("predict",e.getPredict());

        db.update("userTrace",cv,"traceId= ?",new String[]{String.valueOf(e.getTraceId())});

        Log.i("SqlUpdate","--------------------------"+"update userTrace set ESTime = '"+e.getESTime()+"',LETime = '"+e.getLETime()+"',event = '"+e.getEvent()+
                "',time='"+e.getTime()+"',date = '"+e.getDate()+"',siteText='"+e.getSiteText()+
                "',siteId='"+e.getSiteId()+"',finish = "+e.getFinish_int()+",hasESTime ="+e.get_hasEst_int()
                +",hasLETime = "+e.get_hasLet_int()+",predict="+e.getPredict()+"  where traceId = "+e.getTraceId());

    }

    static  public  void addTrace(Trace e)//数据库中添加日程
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        /*cv.put("time",e.getTime());
        cv.put("event",e.getEvent());
        cv.put("date",e.getDate());
        cv.put("traceId",e.getTraceId());
        cv.put("finish",e.getFinish_int());
        cv.put("important",e.getImportant_int());
        cv.put("urgent",e.getUrgent_int());
        cv.put("fix",e.getFix_int());
        cv.put("predict",e.getPredict());*/

        cv.put("ESTime",e.getESTime());
        cv.put("LETime",e.getLETime());
        cv.put("time",e.getTime());
        cv.put("event",e.getEvent());
        cv.put("date",e.getDate());
        cv.put("traceId",e.getTraceId());
        cv.put("hasESTime",e.get_hasEst_int());
        cv.put("hasLETime",e.get_hasLet_int());
        cv.put("finish",e.getFinish_int());
        cv.put("siteId",e.getSiteId());
        cv.put("siteText",e.getSiteText());
        cv.put("predict",e.getPredict());

        Log.i("SqlAdd","id:"+e.getTraceId());
        db.insert("userTrace",null,cv);
    }

    static  public void deleteTrace(Trace e)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from userTrace where traceId="+e.getTraceId());
    }

    static public void finishTrace(Trace e){
        e.setFinish(true);
        updateTrace(e);
    }
    static  public void setShowFinished(boolean show)
    {
        TraceManager.showFinished = show;
    }

    static  public void setUseIntellectSort(boolean open)
    {
        TraceManager.useIntellectSort= open;
    }

    static  public  void saveAllTraces(ArrayList<Trace> traceArrayList)//存入给入的事件串
    {
        for(int i=0;i<traceArrayList.size();i++)
        {
            updateTrace(traceArrayList.get(i));
        }
    }

}


