package com.wf.gu.udpchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SA on 29/10/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "WhatFriend";
    private static SQLiteDatabase ldb = null;


    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CONTACTS(ID INTEGER PRIMARY KEY AUTOINCREMENT, UID INTEGER NOT NULL UNIQUE, UNAME VARCHAR(90), UPLACE VARCHAR(120), USTATUS VARCHAR(360),HIMAGE INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE CHAT(ID INTEGER PRIMARY KEY AUTOINCREMENT, UID INTEGER NOT NULL, MESSAGE VARCHAR(560), TIME VARCHAR(20), UNAME VARCHAR(90),HIMAGE VARCHAR(60) DEFAULT 'user_files/download.svg', IS_NEW INTEGER DEFAULT 0,IS_WHOM INTEGER DEFAULT 0,IS_SENT INTEGER DEFAULT 0  )");
        ldb = db;
    }


    public static SQLiteDatabase getDB(){
        return ldb;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {

    }
}
