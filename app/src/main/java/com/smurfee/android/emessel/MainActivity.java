package com.smurfee.android.emessel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.smurfee.android.emessel.recyclerview.MSLRecyclerViewFragment;

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

public class MainActivity extends AppCompatActivity /*implements MSListFragment.OnFragmentInteractionListener*/ {

    private EditText txtItem;
    private Toolbar toolbar;
    private MSLRecyclerViewFragment fragment;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        txtItem = (EditText) findViewById(R.id.txt_add_item);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        FragmentManager fm = getSupportFragmentManager();
        fragment = (MSLRecyclerViewFragment) fm.findFragmentById(R.id.recycler_msl);
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

    //TODO: change to long pause to edit item on main activity
    private void editItem() {
        Intent i = new Intent(this, MSLDetailActivity.class);
        startActivity(i);
    }

    public void removeItems() {
//        MSListFragment listFragment = (MSListFragment) getFragmentManager().findFragmentById(R.id.listFragment);
//        if (null != listFragment && listFragment.isInLayout()) {
//            listFragment.deleteItems();
//        }
        if (null != fragment && fragment.isInLayout()) {
            fragment.deleteItems();
        }
    }

    public void onClickAdd(View view) {
        String item = txtItem.getText().toString();
        if (item.isEmpty()) return; // Don't add nothing
        fragment.addItem(this, item);
        txtItem.setText("");
    }

//    @Override
//    public void onFragmentInteraction(String id) {
//        // i dunno lol
//    }
}