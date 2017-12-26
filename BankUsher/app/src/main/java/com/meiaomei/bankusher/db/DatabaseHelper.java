package com.meiaomei.bankusher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建city数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String name = "recent_city.db";
    public static DatabaseHelper instance = null;
    private static final int version = 1;

    public synchronized static DatabaseHelper getInstance(Context context){
        if (instance==null){
            instance=new DatabaseHelper(context);
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS recent_city (id integer primary key autoincrement, name varchar(40), date INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close() {
        if (instance != null) {
            instance.close();
        }
        super.close();
    }
}
