package com.smurfee.android.emessel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.smurfee.android.emessel.db.MSLSQLiteHelper;
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
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        FragmentManager fm = getSupportFragmentManager();
        fragment = (MSLViewFragment) fm.findFragmentById(R.id.fragment_recycler_msl);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_new:
                        deleteDatabase(MSLSQLiteHelper.DATABASE_NAME);
                        fragment.deleteList(MainActivity.this);
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this, "hope you didn't regret doing that", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_save:
                        fragment.saveList(MainActivity.this);
                        Toast.makeText(MainActivity.this, "Save Successful.", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_open:
                        fragment.loadList(MainActivity.this);
                        Toast.makeText(MainActivity.this, "not implemented yet...", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_delete:
                        Toast.makeText(MainActivity.this, "not implemented yet...", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_sms:
                        Toast.makeText(MainActivity.this, "not implemented yet...", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_settings:
                        Toast.makeText(MainActivity.this, "not implemented yet...", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        return true;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
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