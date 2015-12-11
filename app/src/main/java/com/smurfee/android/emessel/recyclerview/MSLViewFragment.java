package com.smurfee.android.emessel.recyclerview;


import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.smurfee.android.emessel.MainActivity;
import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A {@link Fragment} class used to hold a RecyclerView to display Shopping List items.
 *
 * @author smurfee
 * @version 2015.11.8
 */
public class MSLViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private MSLViewAdapter mAdapter;
    private EditText txtItem;

    public MSLViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mslrecycler_view, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_msl);
        txtItem = (EditText) layout.findViewById(R.id.txt_add_item);
        txtItem.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addItem();
                    txtItem.setText("");
                    txtItem.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtItem, 0);
                }
                return true;
            }
        });
        layout.findViewById(R.id.btn_add_item).setOnClickListener(this);
        mAdapter = new MSLViewAdapter(getActivity(), null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new MSLTouchListener(getActivity(), mRecyclerView,
                MSLTouchListener.newClickListener(mAdapter, mRecyclerView)));

        getLoaderManager().initLoader(0, null, this);
        return layout;
    }

    /**
     * Take input text, create a record and insert it into the database. The {@link CursorLoader}
     * will update the view on a background thread.
     */
    public void addItem() {
        String item = txtItem.getText().toString();
        if (item.isEmpty()) return; // Don't add nothing

        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_LABEL, item);
        getActivity().getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
    }

    /**
     * When the delete checked label icon in the toolbar is checked this method is called to removed
     * the checked items.
     *
     * @param context Parent Activity used to get a Content Resolver.
     */
    public void deleteItems(Context context) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
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
        } catch (RemoteException | OperationApplicationException ignored) {
        }

        mAdapter.setSelectedRows(new LinkedHashSet<Long>());
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MSLTable.COLUMN_ID,
                MSLTable.COLUMN_LABEL,
                MSLTable.COLUMN_NOTE,
                MSLTable.COLUMN_PRICE};
        return new CursorLoader(getActivity(),
                MSLContentProvider.CONTENT_URI, projection, null, null, null);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_item:
                addItem();
                // close keyboard
                ((MainActivity) getActivity()).hideKeyboard();
                txtItem.getText().clear();
                txtItem.clearFocus();
                break;
        }
    }

    public void lockAddItem(boolean b) {
        txtItem.setEnabled(b);
    }
}
