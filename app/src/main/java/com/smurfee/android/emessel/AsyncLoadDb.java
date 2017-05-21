package com.smurfee.android.emessel;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.smurfee.android.emessel.db.MSLContentProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by smurfee on 23/04/2017.
 */

public class AsyncLoadDb extends AsyncTask<String, Void, String[]> {

    private Context context;
    private ListView shoppingLists;
    private AlertDialog loadDialog;

    public AsyncLoadDb(Context context) {
        this.context = context;
        this.shoppingLists = new ListView(context);
    }

    @Override
    protected String[] doInBackground(String... params) {
        File data = context.getDatabasePath("msl.db");

        File currentDB = new File(data.getParent());
        List<String> fileNames = new ArrayList<>(Arrays.asList(currentDB.list()));

        Pattern p = Pattern.compile("msl.db|.*journal$");
        for (int i = 0; i < fileNames.size(); i++) {
            if (p.matcher(fileNames.get(i)).matches()) {
                fileNames.remove(i);
                i--;
            }
        }

        return fileNames.toArray(new String[0]);
    }

    protected void onPostExecute(final String[] result) {
        if (result == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ArrayAdapter<String> loadAdapter = new ArrayAdapter<>(context, R.layout.list_load, R.id.load_filename, result);

        shoppingLists.setAdapter(loadAdapter);
        builder.setTitle("Choose File");
        builder.setView(shoppingLists);
        loadDialog = builder.create();
        loadDialog.show();

        shoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Load the database into msl.db
                // access MSLContentProvider to access MSLSQLiteHelper instance field
                String inputFileName = ((TextView) view.findViewById(R.id.load_filename)).getText().toString();
                File dbPath = context.getDatabasePath("msl.db");
                try {
                    InputStream mInput = new FileInputStream(new File(dbPath.getParentFile(), inputFileName));
                    OutputStream mOutput = new FileOutputStream(context.getDatabasePath("msl.db"));
                    byte[] mBuffer = new byte[1024];
                    int mLength;
                    while ((mLength = mInput.read(mBuffer)) > 0) {
                        mOutput.write(mBuffer, 0, mLength);
                    }
                    mOutput.flush();
                    mOutput.close();
                    mInput.close();
                    // may need to query
                } catch (Exception e) {
                }
                //TODO Close builder refresh sql
                loadDialog.dismiss();
                context.getContentResolver().notifyChange(MSLContentProvider.CONTENT_URI, null);
            }
        });
    }
}
