package com.smurfee.android.emessel;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
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
        for (int i = 0; i < fileNames.size(); i ++) {
            if(p.matcher(fileNames.get(i)).matches()){
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
        AlertDialog loadDialog = builder.create();
        loadDialog.show();
    }
}
