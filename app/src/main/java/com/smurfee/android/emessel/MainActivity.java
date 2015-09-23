package com.smurfee.android.emessel;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Comment;

import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private View viewContainer;
    private ListView listView;
    private ItemDataSource dataSource;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        dataSource = new ItemDataSource(this);
        dataSource.open();
        List<MSLItem> values = dataSource.getAllMSLItems();
        ArrayAdapter<MSLItem> adapter = new MSLAdapter(this, values);
        viewContainer = findViewById(R.id.undobar);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                String item = (String) listView.getAdapter().getItem(position);
//                Toast.makeText(getBaseContext(), item + " selected", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showUndo(viewContainer);
        return true;
    }

    public void onClick(View view) {
//        Toast.makeText(this, "Deletion undone", Toast.LENGTH_LONG).show();
//        viewContainer.setVisibility(View.GONE);
        ArrayAdapter<MSLItem> adapter = (ArrayAdapter<MSLItem>) listView.getAdapter();
        MSLItem item = null;
        switch (view.getId()) {
            case R.id.add:
                String[] items = new String[] { "Cool", "Very nice", "Hate it" };
                int nextInt = new Random().nextInt(3);
                // save the new comment to the database
                item = dataSource.createMSLItem(items[nextInt]);
                adapter.add(item);
                break;
            case R.id.delete:
                if (listView.getAdapter().getCount() > 0) {
                    item = (MSLItem) listView.getAdapter().getItem(0);
                    dataSource.deleteMSLItem(item);
                    adapter.remove(item);
                }
                break;
        }
        adapter.notifyDataSetChanged();
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
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}