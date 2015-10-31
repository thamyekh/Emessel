package com.smurfee.android.emessel.unused;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smurfee.android.emessel.R;

import java.util.List;

/**
 * Created by smurfee on 20/09/2015.
 */

public class MSLAdapter extends ArrayAdapter<MSLItem> {
    private final Context context;
    private final List<MSLItem> values;

    public MSLAdapter(Context context, List<MSLItem> values) {
        super(context, R.layout.list_row, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_row, null);
            // configure view holder
                ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.label);
            viewHolder.image = (ImageView) rowView
                    .findViewById(R.id.icon);
            rowView.setTag(viewHolder);
        }

        return rowView;
    }
}
