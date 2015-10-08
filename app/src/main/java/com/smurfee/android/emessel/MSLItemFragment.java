package com.smurfee.android.emessel;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MSLItemFragment extends Fragment implements ListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView with
     * Views.
     */
    private MSLCursorAdapter mAdapter;

    // TODO: Rename and change types of parameters
//    public static MSLItemFragment newInstance(String param1, String param2) {
//        MSLItemFragment fragment = new MSLItemFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MSLItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    private void fillData() {
        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{MSLTable.COLUMN_ITEM};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.label};
        getLoaderManager().initLoader(0, null, this);
//        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_row, null, from,
//                to, 0);
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
        mListView.setOnItemClickListener(this);
        fillData();
        return view;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
            mAdapter.setSelected(position);
            mAdapter.notifyDataSetChanged();
            //TODO: delete item on touch
//            Uri todoUri = Uri.parse(MSLContentProvider.CONTENT_URI + "/" + id);
//            getActivity().getContentResolver().delete(todoUri, null, null);


            //TODO: This section goes into the item details
//            Intent i = new Intent(getActivity(), MSLDetailActivity.class);
//            Uri todoUri = Uri.parse(MSLContentProvider.CONTENT_URI + "/" + id);
//            i.putExtra(MSLContentProvider.CONTENT_ITEM_TYPE, todoUri);
//            startActivity(i);
        }
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
