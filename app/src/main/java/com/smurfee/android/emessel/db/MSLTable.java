package com.smurfee.android.emessel.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smurfee.android.emessel.MSLSQLiteHelper;

/**
 * Created by smurfee on 23/09/2015.
 */
public class MSLTable {

    public static final String TABLE_MSL = "MSL";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM = "item";

    //create database
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_MSL + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ITEM
            + " text not null);";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MSLSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MSL);
        onCreate(db);
    }
}
