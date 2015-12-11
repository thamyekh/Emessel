package com.smurfee.android.emessel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.smurfee.android.emessel.recyclerview.MSLViewFragment;

/**
 * TODO(s):
 * webscrape
 * sms shopping list
 * calculate total shopping list cost
 */

/**
 * @author smurfee
 * @version 2015.12.11
 */

public class MainActivity extends AppCompatActivity {

    private MSLViewFragment fragment;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        FragmentManager fm = getSupportFragmentManager();
        fragment = (MSLViewFragment) fm.findFragmentById(R.id.fragment_recycler_msl);
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

    public void removeItems() {
        if (fragment != null && fragment.isInLayout()) fragment.deleteItems(this);
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}