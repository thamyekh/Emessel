package com.smurfee.android.emessel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLTable;

/*
TODO:
fix layout_width for add items xml
FEATURES TO IMPLEMENT:
create new shopping lists
swipe to remove item
Undo
total cost
item priority
 */

public class MainActivity extends Activity implements MSLItemFragment.OnFragmentInteractionListener {

    private EditText txtItem;
    private static final int DELETE_ID = Menu.FIRST + 1; //TODO:refer to onContextItemSelected in this class

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        txtItem = (EditText) findViewById(R.id.txt_add_item);

        //TODO: update actionbar to toolbar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ic_done_black_18dp);
        // add the custom view to the action bar
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_view, null);
        actionBar.setCustomView(v);
//        registerForContextMenu(listView); //TODO:refer to onContextItemSelected in this class
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.add(0, DELETE_ID, 0, R.string.delete_item);
//    }

    public void onClickAdd(View view){
        String item = txtItem.getText().toString();
        if (item.isEmpty()) return; // Don't add nothing
        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_ITEM, item);
        getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
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
    public void onFragmentInteraction(String id) {
        // i dunno
    }
}