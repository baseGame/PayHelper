package com.tools.payhelper.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context arg4) {
        super(arg4, "trade.db", null, 7);
    }

    public void onCreate(SQLiteDatabase arg2) {
        arg2.execSQL("CREATE TABLE IF NOT EXISTS qrcode(_id INTEGER PRIMARY KEY AUTOINCREMENT, money varchar, mark varchar, type varchar, payurl varchar, dt varchar)");

        arg2.execSQL("CREATE TABLE IF NOT EXISTS unionpayorder(_id INTEGER PRIMARY KEY AUTOINCREMENT, tradeno varchar, money varchar,ordertime varchar, " +
                "mark varchar ,payaccountno varchar,payaccountname varchar,result varchar)");

        arg2.execSQL("CREATE TABLE IF NOT EXISTS xposorder(_id INTEGER PRIMARY KEY AUTOINCREMENT,money varchar, type varchar,dt varchar,business varchar,result varchar)");

    }

    public void onUpgrade(SQLiteDatabase arg1, int arg2, int arg3) {
    }
}

