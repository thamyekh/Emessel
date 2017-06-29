package com.smurfee.android.emessel.recyclerview;


import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.smurfee.android.emessel.MainActivity;
import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.databinding.FragmentMslViewBinding;
import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLTable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A {@link Fragment} class used to hold a RecyclerView to display Shopping List items.
 *
 * @author smurfee
 * @version 2017.4.3
 */
public class MSLViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private MSLViewAdapter mAdapter;
    private EditText mTxtItem;
    private ImageView mEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMslViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_msl_view, container, false);

        mRecyclerView = binding.recyclerMsl;
        mTxtItem = binding.txtAddItem;
        mEmptyView = binding.imgPointer;

        mTxtItem.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addItem();
                    mTxtItem.setText("");
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            mTxtItem.requestFocus();
                        }
                    });
                }
                return false;
            }
        });
        binding.btnAddItem.setOnClickListener(this);
        mAdapter = new MSLViewAdapter(getActivity(), null);
        binding.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new MSLTouchListener(getActivity(), mRecyclerView,
                MSLTouchListener.newClickListener(mAdapter, mRecyclerView)));
        getLoaderManager().initLoader(0, null, this);

        return binding.getRoot();
    }

    /**
     * Take input text, create a record and insert it into the database. The {@link CursorLoader}
     * will update the view on a background thread.
     */
    public void addItem() {
        String item = mTxtItem.getText().toString();
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

    public void deleteList(Context context) {
        context.getContentResolver().delete(MSLContentProvider.CONTENT_URI, null, null);
    }

    public void saveList(Context context) {
        /** TODO: CHECKS
         *  Don't save if the list is already empty
         *  Concat .db to filename if it doesn't exist
         *  ensure that you are not allowed to use the word jornal when saving
         */

        final EditText input = new EditText(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter filename:");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDBfilename = input.getText().toString();
                try {
                    File databaseFile = getActivity().getDatabasePath("msl.db");
                    File oldDatabaseFile = new File(databaseFile.getParentFile(), newDBfilename);
                    databaseFile.renameTo(oldDatabaseFile);
                } catch (Exception e) {
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

        mEmptyView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.insert_first_item));
        if (mAdapter.getItemCount() != 0) mEmptyView.clearAnimation();
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
                mTxtItem.getText().clear();
                mTxtItem.clearFocus();
                break;
        }
    }

    public void lockAddItem(boolean b) {
        mTxtItem.setEnabled(b);
    }
}