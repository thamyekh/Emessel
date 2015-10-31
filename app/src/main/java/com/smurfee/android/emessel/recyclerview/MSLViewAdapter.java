package com.smurfee.android.emessel.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smurfee on 12/10/2015.
 */
public class MSLViewAdapter extends RecyclerView.Adapter<MSLViewAdapter.ViewHolder> {

    private List<MSLRowView> mRows;
    Set<Long> mDeleteSet = new LinkedHashSet();
    private Cursor mCursor;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIdColumn;

    public MSLViewAdapter(Context context, Cursor cursor) {
        populate(cursor);
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex(MSLTable.COLUMN_ID) : -1;
        mDataSetObserver = new MSLDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void populate(Cursor cursor){
        mRows = new ArrayList<>();
        if (cursor != null && cursor.moveToPosition(0)) {
            MSLRowView mslItem;
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(cursor.getColumnIndex(MSLTable.COLUMN_ID));
                String item = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_ITEM));
                mslItem = new MSLRowView(id, item);
                mRows.add(mslItem);
                cursor.moveToNext();
            }
        }
//        cursor.close();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MSLRowView current = mRows.get(position);
        holder.item.setText(current.getItem());

        boolean isChecked = mRows.get(position).isChecked();

        if (isChecked) holder.itemView.setSelected(true);
        else holder.itemView.setSelected(false);
    }

    @Override
    public int getItemCount() {
        return mRows.size();
    }

    public void changeCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return;
        }

        // Making the old cursor redundant
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mCursor = cursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = cursor.getColumnIndexOrThrow(MSLTable.COLUMN_ID);
            mDataValid = true;
            int count = mCursor.getCount();
            //Add item
            if (mCursor.moveToPosition(count-1) && count > mRows.size()) {
                String itemStr = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_ITEM));
                long itemId = cursor.getLong(cursor.getColumnIndex(MSLTable.COLUMN_ID));
                MSLRowView item = new MSLRowView(itemId, itemStr);
                mRows.add(0, item);
                notifyItemInserted(0);
            } else { //Deleted item(s)
                populate(mCursor);
                notifyDataSetChanged();
            }
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        if (oldCursor != null) oldCursor.close();
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public void toggleChecked(View view, int position) {
        MSLRowView row = mRows.get(position);
        boolean checked = row.isChecked();

        if (checked) mDeleteSet.remove(row.getId());
        else mDeleteSet.add(row.getId());
        row.setChecked(!checked);
        notifyItemChanged(position);
    }

    public Set<Long> getSelectedRows() {
        return mDeleteSet;
    }

    public void setSelectedRows(Set<Long> newSet) {
        mDeleteSet = newSet;
    }

    private class MSLDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.label);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
}
