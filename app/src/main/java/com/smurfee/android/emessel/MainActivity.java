package com.smurfee.android.emessel;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private View viewContainer;
    private ListView lv;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listview);
        String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2"};
        ArrayAdapter<String> adapter = new MSLAdapter(this, values);
        viewContainer = findViewById(R.id.undobar);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                String item = (String) lv.getAdapter().getItem(position);
                Toast.makeText(getBaseContext(), item + " selected", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Deletion undone", Toast.LENGTH_LONG).show();
        viewContainer.setVisibility(View.GONE);
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
}