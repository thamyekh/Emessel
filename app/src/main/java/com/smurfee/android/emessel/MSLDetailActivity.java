package com.smurfee.android.emessel;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MSLDetailActivity extends Activity {

    private Spinner mCategory;
    private EditText mTitleText;

    private Uri MSLUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCategory = (Spinner) findViewById(R.id.category);
        mTitleText = (EditText) findViewById(R.id.edit_title);
        Button confirmButton = (Button) findViewById(R.id.edit_button);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        MSLUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(MSLContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            MSLUri = extras
                    .getParcelable(MSLContentProvider.CONTENT_ITEM_TYPE);

            fillData(MSLUri);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "You didn't add anything...", Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
    }

    private void fillData(Uri uri) {
        String[] projection = { MSLTable.COLUMN_ITEM};
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            //TODO: relatese to priority
//            String category = cursor.getString(cursor
//                    .getColumnIndexOrThrow(MSLTable.COLUMN_CATEGORY));

//            for (int i = 0; i < mCategory.getCount(); i++) {
//                String s = (String) mCategory.getItemAtPosition(i);
//                if (s.equalsIgnoreCase(category)) {
//                    mCategory.setSelection(i);
//                }
//            }

            mTitleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(MSLTable.COLUMN_ITEM)));
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MSLContentProvider.CONTENT_ITEM_TYPE, MSLUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String category = (String) mCategory.getSelectedItem();
        String title = mTitleText.getText().toString();

        // only save if either summary or description
        // is available

        if (/*description.length() == 0 && */title.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
//        values.put(MSLTable.COLUMN_CATEGORY, category);
        values.put(MSLTable.COLUMN_ITEM, title);

        if (MSLUri == null) {
            // New todo
            MSLUri = getContentResolver().insert(MSLContentProvider.CONTENT_URI, values);
        } else {
            // Update todo
            getContentResolver().update(MSLUri, values, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
