package com.huijimuhei.beacon.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class DatabaseManager extends SQLiteOpenHelper {
    public static String DB_PATH = Environment.getExternalStorageDirectory() +  "/caotang/beacon.db";

    private static DatabaseManager singleton = null;
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private DatabaseManager(Context context) {
        super(context, DB_PATH, null, 1);
        this.myContext = context;
        try {
            open();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void init(Context context) {
        if (singleton == null) {
            synchronized (DatabaseManager.class) {
                singleton = new DatabaseManager(context);
            }
        }
    }

    public static DatabaseManager getInstance() {
        if (singleton == null) {
            throw new IllegalArgumentException("DatabaseManager must init");
        }
        return singleton;
    }

    public static boolean exist() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            return false;
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public void open() {
        String myPath = DB_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Cursor rawQuery(String sql) {
        return myDataBase.rawQuery(sql, null);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
