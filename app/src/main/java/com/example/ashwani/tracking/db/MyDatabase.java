package com.example.ashwani.tracking.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MyDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyDatabase";
    private static final int VERSION = 9;
    public static final String TABLE_NAME = "Location";
    public static final String LAT = "Latitude";
    public static final String LNG = "Longitude";
    public static final String TIME = "time";
    private SQLiteDatabase db;
    public static final String TAG = MyDatabase.class.getSimpleName();

    public MyDatabase(Context context){
        super(context,DB_NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"database");
        String sql = "create table "
                + TABLE_NAME + "(" + TIME
                + " int primary key, " + LAT
                + " double precision, " + LNG
                + " double precision);";
        Log.i("create", "created");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"upgrade version");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}