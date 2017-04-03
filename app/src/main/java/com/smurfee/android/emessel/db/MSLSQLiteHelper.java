package com.smurfee.android.emessel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by smurfee on 20/09/2015.
 *
 * @author smurfee
 * @version 2017.4.3
 */
public class MSLSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "msl.db";
    public static final int DATABASE_VERSION = 1;

    public MSLSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        MSLTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MSLTable.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Use in the case of resetting a database.
     * @param context
     */
    public void delete(Context context){
        close();
        context.deleteDatabase(DATABASE_NAME);
    }
}
