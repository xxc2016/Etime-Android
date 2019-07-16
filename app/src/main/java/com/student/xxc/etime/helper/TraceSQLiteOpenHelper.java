package com.student.xxc.etime.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class TraceSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_DATABASE = "CREATE TABLE "+
            "userTrace(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "ESTime TEXT,LETime TEXT,time TEXT,event TEXT,date TEXT,traceId INTEGER,finish INTEGER," +
            "hasESTime INTEGER,hasLETime INTEGER,siteId TEXT,siteText Text,predict INTEGER,priority INTEGER)";

    public static final String CREATE_TABLE_LATLON_POINT = "CREATE TABLE " +
            "latlonPoint(siteId TEXT PRIMARY KEY,latitude REAL,longitude REAL)";

    public static final String CREATE_TABLE_DISTANCE = "CREATE TABLE " +
            "distance(siteId_a TEXT,siteId_b TEXT,dis REAL)";

    public  static final String DROP_TABLE = "DROP TABLE "+
            "userTrace";

    public static final String DB_NAME = "e_time1.db";

    private Context  context;

    public TraceSQLiteOpenHelper(Context context,SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context,DB_NAME,factory,version);
        this.context = context;
    }

    public  void setDropTable(SQLiteDatabase db)
    {
        db.execSQL(DROP_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
        db.execSQL(CREATE_TABLE_LATLON_POINT);
        db.execSQL(CREATE_TABLE_DISTANCE);
        //Toast.makeText(context,"数据库创建成功",Toast.LENGTH_LONG).show();
    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {

    }
}
