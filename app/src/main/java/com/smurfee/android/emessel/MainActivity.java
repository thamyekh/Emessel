package com.smurfee.android.emessel;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
Commit comment
Established color theme
 */

public class MainActivity extends AppCompatActivity implements MSListFragment.OnFragmentInteractionListener {

    private EditText txtItem;
    private Toolbar toolbar;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        txtItem = (EditText) findViewById(R.id.txt_add_item);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add menu items to toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_checked:
                removeItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: change to creating item on main activity
    private void addItem() {
        Intent i = new Intent(this, MSLDetailActivity.class);
        startActivity(i);
    }

    public void removeItems() {
        MSListFragment listFragment = (MSListFragment) getFragmentManager().findFragmentById(R.id.listFragment);
        if (null != listFragment && listFragment.isInLayout()) {
            listFragment.deleteCheckedItems();
        }
    }

    public void onClickAdd(View view) {
        String item = txtItem.getText().toString();
        if (item.isEmpty()) return; // Don't add nothing
        ContentValues values = new ContentValues();
        values.put(MSLTable.COLUMN_ITEM, item);
        getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
        txtItem.setText("");
    }

    @Override
    public void onFragmentInteraction(String id) {
        // i dunno lol
    }
}