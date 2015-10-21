package com.smurfee.android.emessel;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLCursorAdapter;
import com.smurfee.android.emessel.db.MSLSQLiteHelper;
import com.smurfee.android.emessel.db.MSLTable;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MSListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private OnFragmentInteractionListener mListener;
    private ListView mListView;
    private MSLCursorAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MSListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void fillData() {
        getLoaderManager().initLoader(0, null, this);
        // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
        MSLSQLiteHelper handler = new MSLSQLiteHelper(getActivity());
        // Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
        // Query for items from the database and get a cursor back
        Cursor c = db.rawQuery("SELECT  * FROM " + MSLTable.TABLE_MSL, null);
        mAdapter = new MSLCursorAdapter(getActivity(), c, 0);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mslitem, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setDividerHeight(2);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
//        mListView.setOnItemClickListener(this);
        fillData();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMSLItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onMSLItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("test");
//        if (null != mListener) {
            System.out.println("test2");
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
            mAdapter.setSelected(position, id);
            mAdapter.notifyDataSetChanged();
            //TODO: delete item on touch
//            Uri todoUri = Uri.parse(MSLContentProvider.CONTENT_URI + "/" + id);
//            getActivity().getContentResolver().delete(todoUri, null, null);


            //TODO: This section goes into the item details
//            Intent i = new Intent(getActivity(), MSLDetailActivity.class);
//            Uri todoUri = Uri.parse(MSLContentProvider.CONTENT_URI + "/" + id);
//            i.putExtra(MSLContentProvider.CONTENT_ITEM_TYPE, todoUri);
//            startActivity(i);
//        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * When the delete checked item icon in the toolbar is checked this method is called to removed
     * the checked items
     */
    public void deleteCheckedItems() {

        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation operation;
        SparseBooleanArray selected = mAdapter.getSelectionArray();
        SparseArray<Long> selectedId = mAdapter.getSelectedIdArray();
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(selected.keyAt(i))) {
                String[] selectionArgs = new String[]{selectedId.get(selectedId.keyAt(i)).toString()};
                operation = ContentProviderOperation
                        .newDelete(MSLContentProvider.CONTENT_URI)
                        .withSelection(MSLTable.COLUMN_ID + " = ?", selectionArgs)
                        .build();
                operations.add(operation);
            }
        }

        try {
            getActivity().getContentResolver().applyBatch(MSLContentProvider.AUTHORITY, operations);
        } catch (RemoteException e) {
        } catch (OperationApplicationException e) {
        }

        mAdapter.resetSelection();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MSLTable.COLUMN_ID, MSLTable.COLUMN_ITEM};
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                MSLContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        mAdapter.swapCursor(null);
    }
}
