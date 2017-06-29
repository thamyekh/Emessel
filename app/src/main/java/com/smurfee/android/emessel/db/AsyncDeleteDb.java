package com.smurfee.android.emessel.db;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
        super(context);

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
                    Log.d("deleting", result[i] + " " + markedForDeletion[i]);
                    if(markedForDeletion[i]){
                        context.deleteDatabase(result[i]);
                        Log.d("deleted", result[i]);
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

//        shoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // Load the database into msl.db
//                // access MSLContentProvider to access MSLSQLiteHelper instance field
//                String inputFileName = ((TextView) view.findViewById(R.id.load_filename)).getText().toString();
//                File dbPath = context.getDatabasePath("msl.db");
//                try {
//                    InputStream mInput = new FileInputStream(new File(dbPath.getParentFile(), inputFileName));
//                    OutputStream mOutput = new FileOutputStream(context.getDatabasePath("msl.db"));
//                    byte[] mBuffer = new byte[1024];
//                    int mLength;
//                    while ((mLength = mInput.read(mBuffer)) > 0) {
//                        mOutput.write(mBuffer, 0, mLength);
//                    }
//                    mOutput.flush();
//                    mOutput.close();
//                    mInput.close();
//                    // may need to query
//                } catch (Exception e) {
//                }
//
//                loadDialog.dismiss();
//                context.getContentResolver().notifyChange(MSLContentProvider.CONTENT_URI, null);
//            }
//        });
    }
}
