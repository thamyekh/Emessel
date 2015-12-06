package com.smurfee.android.emessel.recyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom RecyclerView Adapter that handles checked items that are marked for deletion.
 * Derived from skyfishjy's CursorRecyclerViewAdapter.
 *
 * @author smurfee
 * @version 2015.12.6
 */


public class MSLViewAdapter extends RecyclerView.Adapter<MSLViewAdapter.ViewHolder> {

    private static Context mContext;
    private Cursor mCursor;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIdColumn;
    private int mExpandedPosition = -1;

    private List<MSLRowView> mRows = new ArrayList<>();
    private Set<Long> mDeleteSet = new LinkedHashSet<>();

    public MSLViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex(MSLTable.COLUMN_ID) : -1;
        mDataSetObserver = new MSLDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
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

        if (position == mExpandedPosition) {
            holder.expand(current);
        } else {
            holder.collapse();
        }

        holder.item.setText(current.getItem());

        // (Un)marks row for deletion
        boolean isChecked = mRows.get(position).isChecked();
        if (isChecked) holder.itemView.setSelected(true);
        else holder.itemView.setSelected(false);
    }

    @Override
    public int getItemCount() {
        return mRows.size();
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemId(int)
     */
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

    /**
     * Populates the Recycler View with all rows in the cursor from scratch.
     *
     * @param cursor Cursor containing all records queried from the database
     */
    public void populate(Cursor cursor) {
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
    }

    /**
     * Marks the row at position as checked in the Recycler View. Only checked rows can be removed.
     *
     * @param position Clicked position.
     */
    public void toggleChecked(int position) {
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

    /**
     * Change the current cursor to a new one and close the old one.
     *
     * @param cursor Updated cursor
     */
    public void changeCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return;
        }

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
            if (mCursor.moveToPosition(count - 1) && count == (mRows.size() + 1)) {
                // Add item
                String itemStr = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_ITEM));
                long itemId = cursor.getLong(cursor.getColumnIndex(MSLTable.COLUMN_ID));
                MSLRowView item = new MSLRowView(itemId, itemStr);
                mRows.add(0, item);
                notifyItemInserted(0);
            } else {
                // Deleted item(s)
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

    public int getExpandedPosition() {
        return mExpandedPosition;
    }

    public void setExpandedPosition(int expandedPosition) {
        mExpandedPosition = expandedPosition;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        ImageView icon;
        View expanded;
        ImageView close;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.label);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            expanded = itemView.findViewById(R.id.expanded);
            expanded.setOnTouchListener(MSLTouchListener.newOnTouchListener());
            close = (ImageView) expanded.findViewById(R.id.close);
            close.setOnClickListener(MSLTouchListener.collapseListener());
        }

        public void expand(MSLRowView current) {
            item.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            expanded.setVisibility(View.VISIBLE);
            EditText editItem = (EditText) expanded.findViewById(R.id.edit_item);
            editItem.setText(current.getItem());
            close.setVisibility(View.VISIBLE);
        }

        public void collapse() {
            item.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);
            expanded.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
        }
    }
}
