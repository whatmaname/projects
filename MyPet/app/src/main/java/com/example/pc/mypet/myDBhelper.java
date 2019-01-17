package com.example.pc.mypet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDBhelper extends SQLiteOpenHelper {
    myDBhelper(Context context) {
        super(context, "LivestockDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS animal (num int primary key,kind TEXT , age double NOT NULL , " +
                    "kgs double NOT NULL , price integer,status TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS myWallet (num Integer primary key ,money String NOT NULL,lv integer not null)");
            db.execSQL("CREATE TABLE IF NOT EXISTS stuff (milk Integer ,wool integer,meat integer, egg integer)");
            db.execSQL("INSERT INTO myWallet VALUES(1 , '50000000',1)");
            db.execSQL("INSERT INTO stuff VALUES(0,0,0,0)");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
