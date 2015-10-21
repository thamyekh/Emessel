package com.smurfee.android.emessel.recyclerview;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLItem;
import com.smurfee.android.emessel.db.MSLSQLiteHelper;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MSLRecyclerViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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

        List<MSLItem> dataset = initialiseList();
        mAdapter = new MSLViewAdapter(getActivity(), dataset);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        return layout;
    }

    private List<MSLItem> initialiseList() {
        // Connecting to SQLite
        MSLSQLiteHelper handler = new MSLSQLiteHelper(getActivity());
        // Access writable database
        SQLiteDatabase db = handler.getWritableDatabase();
        // Query all items from the database
        Cursor cursor = db.rawQuery("SELECT  * FROM " + MSLTable.TABLE_MSL, null);

        List<MSLItem> dataset = new ArrayList<MSLItem>();
        if (cursor != null && cursor.moveToFirst()) {
            MSLItem mslitem; //TODO create a dedicated MSLViewItem class
            while(!cursor.isAfterLast()){
                mslitem = new MSLItem();
                long id = cursor.getLong(cursor.getColumnIndex(MSLTable.COLUMN_ID));
                String item = cursor.getString(cursor.getColumnIndex(MSLTable.COLUMN_ITEM));

                mslitem.setId(id);
                mslitem.setItem(item);
                dataset.add(mslitem);
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return dataset;
    }

        public void addItem(Context context, String item) {
        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_ITEM, item);
        context.getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
//        mAdapter.notifyItemInserted(mAdapter.getItemCount()); //Doesn't work
//        MSLSQLiteHelper handler = new MSLSQLiteHelper(getActivity());
//        SQLiteDatabase db = handler.getWritableDatabase();
//        Cursor c = db.rawQuery("SELECT  * FROM " + MSLTable.TABLE_MSL, null);
//        mAdapter.swapCursor(c);
    }

    /**
     * When the delete checked item icon in the toolbar is checked this method is called to removed
     * the checked items
     */
    public void deleteItems() {
        //TODO: uncheck for later
//        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
//        ContentProviderOperation operation;
//        SparseBooleanArray selected = mAdapter.getSelectionArray();
//        SparseArray<Long> selectedId = mAdapter.getSelectedIdArray();
//        for (int i = 0; i < selected.size(); i++) {
//            if (selected.get(selected.keyAt(i))) {
//                String[] selectionArgs = new String[]{selectedId.get(selectedId.keyAt(i)).toString()};
//                operation = ContentProviderOperation
//                        .newDelete(MSLContentProvider.CONTENT_URI)
//                        .withSelection(MSLTable.COLUMN_ID + " = ?", selectionArgs)
//                        .build();
//                operations.add(operation);
//            }
//        }
//
//        try {
//            getActivity().getContentResolver().applyBatch(MSLContentProvider.AUTHORITY, operations);
//        } catch (RemoteException e) {
//        } catch (OperationApplicationException e) {
//        }
//
//        mAdapter.resetSelection();
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
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
