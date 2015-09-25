package com.smurfee.android.emessel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

/*
TODO:
Convert list to ListFragment
fix layout_width for add items xml
FEATURES TO IMPLEMENT:
create new shopping lists
swipe to remove item
Undo
total cost
item priority
 */

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

//    private ListView listView;
    private EditText txtItem;
//    private ItemDataSource dataSource;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private SimpleCursorAdapter adapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
//        listView = (ListView) findViewById(R.id.listview); //TODO: move to fragment
        txtItem = (EditText) findViewById(R.id.txt_add_item);
        //Using DAO as oppose to ContentProvider
//        dataSource = new ItemDataSource(this);
//        dataSource.open();
//        List<MSLItem> values = dataSource.getAllMSLItems();
//        ArrayAdapter<MSLItem> adapter = new MSLAdapter(this, values);
//        listView.setAdapter(adapter);

        //TODO: move comment block to fragment
//        listView.setDividerHeight(2);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                onListClick(parent, v, position, id);
//            }
//        });
//        fillData();
//        registerForContextMenu(listView); //TODO:refer to onContextItemSelected in this class
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                addItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case DELETE_ID:
//                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
//                        .getMenuInfo();
//                Uri uri = Uri.parse(MSLContentProvider.CONTENT_URI + "/"
//                        + info.id);
//                getContentResolver().delete(uri, null, null);
//                fillData();
//                return true;
//        }
//        return super.onContextItemSelected(item);
//    }

    //TODO: change to creating item on main activity
    private void addItem() {
        Intent i = new Intent(this, MSLDetailActivity.class);
        startActivity(i);
    }

    //TODO: moved to fragment
//    private void fillData() {
//        // Fields from the database (projection)
//        // Must include the _id column for the adapter to work
//        String[] from = new String[]{MSLTable.COLUMN_ITEM};
//        // Fields on the UI to which we map
//        int[] to = new int[]{R.id.label};
//        getLoaderManager().initLoader(0, null, this);
//        adapter = new SimpleCursorAdapter(this, R.layout.list_row, null, from,
//                to, 0);
//        listView.setAdapter(adapter);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.delete_item);
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MSLTable.COLUMN_ID, MSLTable.COLUMN_ITEM};
        CursorLoader cursorLoader = new CursorLoader(this,
                MSLContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    public void onClickAdd(View view){
        String item = txtItem.getText().toString();
        if (item.isEmpty()) return; // Don't add nothing
        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_ITEM, item);
        getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
    }
    //TODO:remove?
    public void onListClick(AdapterView<?> parent, View v, int position, long id) {
        Intent i = new Intent(getBaseContext(), MSLDetailActivity.class);
        Uri todoUri = Uri.parse(MSLContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(MSLContentProvider.CONTENT_ITEM_TYPE, todoUri);
        startActivity(i);
////        viewContainer.setVisibility(View.GONE);
//        ArrayAdapter<MSLItem> adapter = (ArrayAdapter<MSLItem>) listView.getAdapter();
//        MSLItem item = null;
//        switch (view.getId()) {
//            case R.id.add:
//                String[] items = new String[] { "Cool", "Very nice", "Hate it" };
//                int nextInt = new Random().nextInt(3);
//                // save the new comment to the database
//                item = dataSource.createMSLItem(items[nextInt]);
//                adapter.add(item);
//                break;
//            case R.id.delete:
//                if (listView.getAdapter().getCount() > 0) {
//                    item = (MSLItem) listView.getAdapter().getItem(0);
//                    dataSource.deleteMSLItem(item);
//                    adapter.remove(item);
//                }
//                break;
//        }
//        adapter.notifyDataSetChanged();
    }

    public static void showUndo(final View viewContainer) {
        viewContainer.setVisibility(View.VISIBLE);
        viewContainer.setAlpha(1);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            viewContainer.animate().alpha(0.4f).setDuration(5000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            viewContainer.setVisibility(View.GONE);
                        }
                    });
        } else {
            viewContainer.animate().alpha(0.4f).setDuration(5000)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            viewContainer.setVisibility(View.GONE);
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
//        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        dataSource.close();
        super.onPause();
    }
}