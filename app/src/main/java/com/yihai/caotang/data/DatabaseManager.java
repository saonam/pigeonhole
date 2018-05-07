package com.yihai.caotang.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class DatabaseManager extends SQLiteOpenHelper {
    public static String DB_PATH = FileUtils.SDPATH;
    private static String DB_NAME = "caotang.db";

    private static DatabaseManager singleton = null;
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private DatabaseManager(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        open();
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
            String myPath = DB_PATH + DB_NAME;
            loadDateBase();
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
        String myPath = DB_PATH + DB_NAME;
        try {
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch (Exception e){
            return ;
        }
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

    public static void loadDateBase() {
        AssetManager assetManager = AppContext.getInstance().getAssets();
        try {
            InputStream inputStream = assetManager.open(DB_NAME);
            File filestr = new File(DB_PATH);
            File file = new File(filestr.getAbsolutePath(), DB_NAME);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int b = 0;
            while ((b = inputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, b);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
