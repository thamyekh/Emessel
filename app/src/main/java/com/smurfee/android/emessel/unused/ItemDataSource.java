package com.smurfee.android.emessel.unused;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.smurfee.android.emessel.db.MSLItem;
import com.smurfee.android.emessel.db.MSLSQLiteHelper;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YekHong on 21/09/2015.
 */
public class ItemDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MSLSQLiteHelper dbHelper;
    private String[] allColumns = {MSLTable.COLUMN_ID,
            MSLTable.COLUMN_ITEM};

    public ItemDataSource(Context context) {
        dbHelper = new MSLSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MSLItem createMSLItem(String item) {
        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_ITEM, item);
        long insertId = database.insert(MSLTable.TABLE_MSL, null, values);
        Cursor cursor = database.query(MSLTable.TABLE_MSL,
                allColumns, MSLTable.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MSLItem newMSLItem = cursorToMSLItem(cursor);
        cursor.close();
        return newMSLItem;
    }

    public void deleteMSLItem(MSLItem item) {
        long id = item.getId();
        System.out.println("MSLItem deleted with id: " + id);
        database.delete(MSLTable.TABLE_MSL, MSLTable.COLUMN_ID
                + " = " + id, null);
    }

    public List<MSLItem> getAllMSLItems() {
        List<MSLItem> items = new ArrayList<MSLItem>();

        Cursor cursor = database.query(MSLTable.TABLE_MSL,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MSLItem item = cursorToMSLItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    private MSLItem cursorToMSLItem(Cursor cursor) {
        MSLItem item = new MSLItem();
        item.setId(cursor.getLong(0));
        item.setItem(cursor.getString(1));
        return item;
    }
}
