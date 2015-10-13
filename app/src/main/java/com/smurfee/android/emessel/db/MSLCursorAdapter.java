package com.smurfee.android.emessel.db;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.smurfee.android.emessel.R;

/**
 * Created by smurfee on 4/10/2015.
 */

//TODO: include view caching/ViewHolder
public class MSLCursorAdapter extends CursorAdapter {

    SparseBooleanArray selectionArray = new SparseBooleanArray();
    SparseArray<Long> selectedIdArray = new SparseArray<Long>();

    // Method to mark items in selection
    public void setSelected(int position, long id) {
        selectionArray.put(position, !selectionArray.get(position));
        selectedIdArray.put(position, id);
    }

    public MSLCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (selectionArray.get(cursor.getPosition()))
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.checked));
        else view.setBackgroundColor(ContextCompat.getColor(context, R.color.window_background));

        // Find fields to populate in inflated template
        TextView itemText = (TextView) view.findViewById(R.id.label);
        String item = cursor.getString(cursor.getColumnIndexOrThrow(MSLTable.COLUMN_ITEM));
        itemText.setText(item);
    }

    public void resetSelection() {
        selectionArray = new SparseBooleanArray();
        selectedIdArray = new SparseArray<Long>();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectionArray() {
        return selectionArray;
    }

    public SparseArray<Long> getSelectedIdArray() {
        return selectedIdArray;
    }
}