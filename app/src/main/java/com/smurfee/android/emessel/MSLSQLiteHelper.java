package com.smurfee.android.emessel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by smurfee on 20/09/2015.
 */
public class MSLSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_MSL = "MSL";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM = "item";

    private static final String DATABASE_NAME = "msl.db";
    private static final int DATABASE_VERSION = 1;

    //create database
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_MSL + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ITEM
            + " text not null);";

    public MSLSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MSLSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MSL);
        onCreate(db);
    }

}
