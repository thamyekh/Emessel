package com.smurfee.android.emessel.recyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.db.MSLContentProvider;
import com.smurfee.android.emessel.db.MSLTable;

/**
 * Handles clicks and long presses for the MSLRecyclerView
 *
 * @author smurfee
 * @version 2015.12.6
 */
public class MSLTouchListener implements RecyclerView.OnItemTouchListener {

    private static Context mContext;
    private boolean mDisallowIntercept;
    private ClickListener mClickListener;

    public interface ClickListener {

        int OFFSET_A = (int) (150 * mContext.getResources().getDisplayMetrics().density + 0.5f);
        int OFFSET_B = (int) (200 * mContext.getResources().getDisplayMetrics().density + 0.5f);

        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private GestureDetector mGestureDetector;

    public MSLTouchListener(Context context, final RecyclerView recyclerView, ClickListener clickListener) {
        mContext = context;
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mClickListener != null) {
                    mClickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mDisallowIntercept) return;
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mClickListener != null) {
                    mClickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
                mDisallowIntercept = true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        if (!mDisallowIntercept) {
            mGestureDetector.onTouchEvent(e);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        mDisallowIntercept = disallowIntercept;
    }

    public static ClickListener newClickListener(final MSLViewAdapter adapter, final RecyclerView recyclerView) {

        return new MSLTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int expandPosition = adapter.getExpandedPosition();
                if (expandPosition >= 0 && expandPosition == position) {
                    adapter.setExpandedPosition(-1);
                    adapter.notifyItemChanged(position);
                    return;
                }
                adapter.toggleChecked(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                int expandedPosition = adapter.getExpandedPosition();
                if (expandedPosition >= 0) { // collapse prev pos
                    adapter.notifyItemChanged(expandedPosition);
                }

                int[] xy = new int[2];
                view.getLocationInWindow(xy);
                int offset = (expandedPosition >= 0 && position > expandedPosition) ? OFFSET_B : OFFSET_A;
                recyclerView.smoothScrollBy(0, (xy[1] - (offset)));

                adapter.setExpandedPosition(position);
                adapter.notifyItemChanged(position);
            }
        };
    }


    public static View.OnClickListener collapseListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = (RecyclerView) view.getParent().getParent().getParent();
                View row = (View) view.getParent().getParent();
                int position = recyclerView.getChildAdapterPosition((View) view.getParent().getParent());
                MSLViewAdapter adapter = (MSLViewAdapter) recyclerView.getAdapter();

                ContentValues cv = new ContentValues();
                cv.put(MSLTable.COLUMN_ITEM, ((EditText) row.findViewById(R.id.edit_item)).getText().toString());

                mContext.getContentResolver().update(
                        MSLContentProvider.CONTENT_URI,
                        cv,
                        MSLTable.COLUMN_ID + " = " + adapter.getItemId(position),
                        null);
                adapter.setExpandedPosition(-1);
                adapter.notifyItemChanged(position);
                recyclerView.requestDisallowInterceptTouchEvent(false);
            }
        };
    }

    /**
     * Factory method for creating a {@link android.view.View.OnTouchListener} to attach to
     * ViewHolder members.
     *
     * @return
     */
    public static View.OnTouchListener newOnTouchListener() {

        return new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && mClickListener != null) {
//                        mClickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
//                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
//                    if (mDisallowIntercept) return;
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && mClickListener != null) {
//                        mClickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
//                    }
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                RecyclerView recyclerView = (RecyclerView) view.getParent().getParent();
//                int pos = recyclerView.getChildAdapterPosition((View) view.getParent());
//                int expos = ((MSLViewAdapter) recyclerView.getAdapter()).getExpandedPosition();
//                if (pos != expos) return false;
                gestureDetector.onTouchEvent(motionEvent);

//                    AlertDialog.Builder builder = newAlertDialogBuilder(view);
//                    builder.show();

                return false;
            }
        };
    }

    /**
     * This will be used for the price query feature.
     *
     * @param view
     * @return
     */
    private static AlertDialog.Builder newAlertDialogBuilder(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Edit Value");

        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView textView = (TextView) view;
                if (input.getText().equals(""))
                    dialog.cancel();
                textView.setText(input.getText().toString());
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
                dialog.cancel();
            }
        });
        return builder;
    }
}
