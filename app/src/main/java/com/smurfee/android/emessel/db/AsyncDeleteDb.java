package com.smurfee.android.emessel.db;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.smurfee.android.emessel.CheckboxAdapter;
import com.smurfee.android.emessel.R;

import java.io.File;

/**
 * Created by Yek on 24/05/2017.
 */

public class AsyncDeleteDb extends AsyncLoadDb {

    ArrayAdapter<String> loadAdapter;

    public AsyncDeleteDb(Context context) {
        super(context, null);
    }

    @Override
    protected void onPostExecute(final String[] result) {
        if (result == null) return;
        //TODO: databind the list_delete.xml and also mark checkbox when whole relative view is clicked
        loadAdapter = new CheckboxAdapter(context, R.layout.list_delete, R.id.delete_filename, R.id.checkbox_delete, result);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        shoppingLists.setAdapter(loadAdapter);
        builder.setTitle("Choose File");
        builder.setView(shoppingLists);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                boolean[] markedForDeletion = ((CheckboxAdapter)loadAdapter).getMarkedForDeletion();
                File dbPath = context.getDatabasePath("msl.db");

                for (int i = 0; i < result.length; i++) {
                    if(markedForDeletion[i]){
                        context.deleteDatabase(result[i]);
                    }
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        loadDialog = builder.create();
        loadDialog.show();

    }
}
