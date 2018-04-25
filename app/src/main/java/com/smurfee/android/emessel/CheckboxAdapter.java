package com.smurfee.android.emessel;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Yek on 24/06/2017.
 */

public class CheckboxAdapter extends ArrayAdapter {

    private Context context;
    private int listLayout;
    private int textViewId;
    private int checkBoxId;
    private String[] dbFiles;
    private boolean[] markedForDeletion;

    public CheckboxAdapter(Context context, int resource, int textViewResourceId, int checkboxResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.listLayout = resource;
        this.textViewId = textViewResourceId;
        this.checkBoxId = checkboxResourceId;
        this.dbFiles = objects;
        markedForDeletion = new boolean[objects.length];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(listLayout, parent, false);
        TextView textView = (TextView) convertView.findViewById(textViewId);

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) finalConvertView.findViewById(checkBoxId);
                markedForDeletion[position] = !markedForDeletion[position];
                cb.setChecked(markedForDeletion[position]);
            }
        });
        textView.setText(dbFiles[position]);
        return convertView;
    }

    public boolean[] getMarkedForDeletion() {
        return markedForDeletion;
    }
}
