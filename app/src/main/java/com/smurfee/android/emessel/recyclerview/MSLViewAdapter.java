package com.smurfee.android.emessel.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLItem;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.List;

/**
 * Created by smurfee on 12/10/2015.
 */
public class MSLViewAdapter extends RecyclerView.Adapter<MSLViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<MSLItem> mDataset;
    private Cursor mCursor;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIdColumn;
    private Context mContext;

    public MSLViewAdapter(Context context, List<MSLItem> dataset) {
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex(MSLTable.COLUMN_ID) : -1;
        mDataSetObserver = new MSLDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MSLItem current = mDataset.get(position);
        holder.item.setText(current.getItem());
    }

    @Override
    public int getItemCount() {
//        return mDataset.size(); TODO remove if adding works
        if (mDataValid && mCursor != null) {
            return mCursor.getCount()+1;
        }
        return 0;
    }

    public void changeCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return;
        }

        // Making the old cursor redundant
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
            oldCursor.close();
        }

        mCursor = cursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = cursor.getColumnIndexOrThrow(MSLTable.COLUMN_ID);
            mDataValid = true;
            //Add item
            if (mCursor.moveToLast()) { //TODO: this condition will cause delete to fail
                MSLItem item = new MSLItem();
                String result = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_ITEM));
                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show(); // TODO: remove debug
                item.setItem(result);
                item.setId(cursor.getLong(cursor.getColumnIndex(MSLTable.COLUMN_ID)));
                mDataset.add(mDataset.size(), item);
                notifyItemInserted(mDataset.size()+1);
            }
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }

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
