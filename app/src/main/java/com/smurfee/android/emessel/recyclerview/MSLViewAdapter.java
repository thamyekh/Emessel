package com.smurfee.android.emessel.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLCursorAdapter;

/**
 * Created by smurfee on 12/10/2015.
 */
public class MSLViewAdapter extends RecyclerView.Adapter<MSLViewAdapter.MSLViewHolder> {

    private LayoutInflater mInflater;
    private MSLCursorAdapter mCursorAdapter;
    private Context mContext;

    public MSLViewAdapter(Context context, Cursor c) {
        mInflater = LayoutInflater.from(context);
        mCursorAdapter = new MSLCursorAdapter(context, c, 0);
        mContext = context;
    }

    @Override
    public MSLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_row, parent, false);
        return new MSLViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MSLViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MSLViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        ImageView icon;

        public MSLViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.label);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }

    }
}
