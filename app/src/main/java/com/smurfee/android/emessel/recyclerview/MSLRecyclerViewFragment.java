package com.smurfee.android.emessel.recyclerview;


import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLSQLiteHelper;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class MSLRecyclerViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private MSLViewAdapter mAdapter;

    public MSLRecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_mslrecycler_view, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_msl);

//      // Connecting to SQLite, get writable db and query all items
        MSLSQLiteHelper handler = new MSLSQLiteHelper(getActivity());
        SQLiteDatabase db = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + MSLTable.TABLE_MSL, null);

        mAdapter = new MSLViewAdapter(cursor);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new MSLTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mAdapter.toggleChecked(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Start activity to edit item details
                Toast.makeText(getActivity(), "long pos: " + position, Toast.LENGTH_SHORT).show();
            }
        }));
        getLoaderManager().initLoader(0, null, this);
        db.close();
        return layout;
    }

    public void addItemToDatabase(Context context, String item) {
        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_ITEM, item);
        context.getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
    }

    /**
     * When the delete checked item icon in the toolbar is checked this method is called to removed
     * the checked items
     */
    public void deleteItems(Context context) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation operation;

        Set<Long> deleteSet = mAdapter.getSelectedRows();

        for (long id : deleteSet) {
            String[] selectionArgs = new String[]{String.valueOf(id)};
            operation = ContentProviderOperation
                    .newDelete(MSLContentProvider.CONTENT_URI)
                    .withSelection(MSLTable.COLUMN_ID + " = ?", selectionArgs)
                    .build();
            operations.add(operation);
        }

        try {
            context.getContentResolver().applyBatch(MSLContentProvider.AUTHORITY, operations);
        } catch (RemoteException e) {
        } catch (OperationApplicationException e) {
        }

        mAdapter.setSelectedRows(new LinkedHashSet());
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MSLTable.COLUMN_ID, MSLTable.COLUMN_ITEM};
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                MSLContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        mRecyclerView.getLayoutManager().scrollToPosition(0);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    public static class MSLTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener mClickListener;
        private RecyclerView mRecyclerView; //TODO: for long press
        private GestureDetector mGestureDetector;

        public MSLTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            mClickListener = clickListener;
            mRecyclerView = recyclerView;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Log.d("test", "onSingleTapUp"  + e);
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
//                    View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && mClickListener != null && mGestureDetector.onTouchEvent(e)) {
//                        mClickListener.onLongClick(child, mRecyclerView.getChildAdapterPosition(child));
//                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && mClickListener != null && mGestureDetector.onTouchEvent(e)) {
                Log.d("test", "onInterceptTouch " + mGestureDetector.onTouchEvent(e)+ " " + e);
                mClickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
