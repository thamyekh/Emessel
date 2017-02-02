package com.smurfee.android.emessel.recyclerview;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.databinding.ObservableInt;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smurfee.android.emessel.MainActivity;
import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLTable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

    private Context mContext;
    private Cursor mCursor;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIdColumn;
    private int mExpandedPosition = -1;
    private MSLViewFragment mFragment;

    public final ObservableInt something = new ObservableInt();

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
        mFragment = (MSLViewFragment) ((MainActivity) mContext)
                .getSupportFragmentManager().findFragmentById(R.id.fragment_recycler_msl);
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
            holder.displayOptionalDetails(current);
        }

        holder.label.setText(current.getLabel());

        // (Un)marks row for deletion
        boolean isChecked = mRows.get(position).isChecked();
        if (isChecked) {
            holder.itemView.setSelected(true);
            holder.icon.setImageResource(R.drawable.ic_check_circle_black_48dp);
        } else {
            holder.itemView.setSelected(false);
            holder.icon.setImageResource(R.drawable.ic_priority_light_blue_300_48dp);
        }
    }

    @Override
    public int getItemCount() {
        something.set(mRows.size());
        return something.get();
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
//        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
//            return mCursor.getLong(mRowIdColumn);
//        }
//        return 0;
        return mRows.get(position).getId();
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
                String item = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_LABEL));
                mslItem = new MSLRowView(id, item);

                //TODO may need to handle null for note and/or price
                if (!cursor.isNull(cursor.getColumnIndex(MSLTable.COLUMN_NOTE))) {
                    String note = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_NOTE));
                    mslItem.setNote(note);
                }
                if (!cursor.isNull(cursor.getColumnIndex(MSLTable.COLUMN_PRICE))) {
                    String price = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_PRICE));
                    mslItem.setPrice(price);
                }

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
     * Change the current cursor to a new one and btnDone the old one.
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
                // Add label
                String itemStr = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_LABEL));
                long itemId = cursor.getLong(cursor.getColumnIndex(MSLTable.COLUMN_ID));
                MSLRowView item = new MSLRowView(itemId, itemStr);
                mRows.add(0, item);
                notifyItemInserted(0);
            } else {
                // Deleted label(s)
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

    public MSLViewAdapter getAdapter() {
        return MSLViewAdapter.this;
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
        TextView label;
        ImageView icon;
        TextView note;
        TextView price;
        View expanded;
        Button btnFind;
        Button btnDone;
        Button btnCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            label = (TextView) itemView.findViewById(R.id.label);
            note = (TextView) itemView.findViewById(R.id.note);
            price = (TextView) itemView.findViewById(R.id.price);

            expanded = itemView.findViewById(R.id.expanded);
            expanded.setOnTouchListener(MSLTouchListener.newOnTouchListener());

            btnFind = (Button) expanded.findViewById(R.id.find);
            btnFind.setOnClickListener(findClickListener());
            btnDone = (Button) expanded.findViewById(R.id.done);
            btnDone.setOnClickListener(doneClickListener());
            btnCancel = (Button) expanded.findViewById(R.id.cancel);

        }

        private View.OnClickListener findClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new PriceFinder().execute();
                }
            };
        }

        public View.OnClickListener doneClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String label = ((EditText) itemView.findViewById(R.id.edit_label)).getText().toString();
                    if (label.equals("")) {
                        Toast.makeText(mContext, "Label cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String note = ((EditText) itemView.findViewById(R.id.edit_notes)).getText().toString();
                    String price = ((EditText) itemView.findViewById(R.id.edit_price)).getText().toString();

                    int position = getAdapterPosition();
                    MSLViewAdapter adapter = getAdapter();
                    ContentValues cv = new ContentValues();
                    cv.put(MSLTable.COLUMN_LABEL, label);
                    cv.put(MSLTable.COLUMN_NOTE, note);
                    cv.put(MSLTable.COLUMN_PRICE, price);

                    mContext.getContentResolver().update(
                            MSLContentProvider.CONTENT_URI,
                            cv,
                            MSLTable.COLUMN_ID + " = " + adapter.getItemId(position),
                            null);

                    adapter.setExpandedPosition(RecyclerView.NO_POSITION);
                    adapter.notifyItemChanged(position);
                    ((RecyclerView) ((Activity) mContext).findViewById(R.id.recycler_msl))
                            .requestDisallowInterceptTouchEvent(false);

                    ((MainActivity) mContext).hideKeyboard();
                }
            };
        }

        public void collapse() {
            label.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);
            expanded.setVisibility(View.GONE);
            mFragment.lockAddItem(true);
        }

        public void expand(MSLRowView current) {
            icon.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
            price.setVisibility(View.GONE);

            expanded.setVisibility(View.VISIBLE);
            ((EditText) expanded.findViewById(R.id.edit_label)).setText(current.getLabel());
            if (current.getNote() != null)
                ((EditText) expanded.findViewById(R.id.edit_notes)).setText(current.getNote());
            if (current.getPrice() != null)
                ((EditText) expanded.findViewById(R.id.edit_price)).setText(current.getPrice().toPlainString());
            mFragment.lockAddItem(false);
        }

        public void displayOptionalDetails(MSLRowView current) {
            if (current.getNote() == null || current.getNote().equals("")) {
                note.setVisibility(View.GONE);
                ((RelativeLayout.LayoutParams) label.getLayoutParams())
                        .addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            } else {
                note.setVisibility(View.VISIBLE);
                note.setText(current.getNote());
                ((RelativeLayout.LayoutParams) label.getLayoutParams())
                        .addRule(RelativeLayout.CENTER_VERTICAL, 0);
            }
            if (current.getPrice() == null)
                price.setVisibility(View.GONE);
            else {
                price.setVisibility(View.VISIBLE);
                price.setText("$" + current.getPrice().toPlainString());
            }
        }
    }

    private class PriceFinder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String keyword = "grapes"; //TODO replace with textview value
                Log.d("JSoup", "attempting to connect");
                Document doc = Jsoup.connect("https://shop.countdown.co.nz/Shop/SearchProducts?search=" + keyword).get();
                Elements product = doc.select("#product-list .details-container.row-fluid.mrow-fluid");

                print("\nItem Name: (%d)", product.size());
                for (Element cls : product) {
                    String productName = trim(cls.select(".description.span12.mspan8").text(), 35);
                    //TODO: must consider club price and non club price
                    String productPrice = cls.select(".din-medium").first().text().split(" ", 2)[0];
                    print(" * %s. @ %s", productName, productPrice);
                }
            } catch (IOException e) {
                Log.d("JSoup", e.toString());
            }
            return null;
        }

        private String trim(String s, int width) {
            if (s.length() > width)
                return s.substring(0, width - 1) + ".";
            else
                return s + ".";
        }

        private void print(String msg, Object... args) {
            Log.d("JSoup", String.format(msg, args));
        }
    }
}
