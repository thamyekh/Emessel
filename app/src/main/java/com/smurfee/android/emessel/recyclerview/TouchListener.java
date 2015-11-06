package com.smurfee.android.emessel.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Handles clicks and long presses for the MSLRecyclerView
 *
 * @author smurfee
 * @version 2015.11.6
 */
public class TouchListener implements RecyclerView.OnItemTouchListener {

    private static float mDensity;
    private ClickListener mClickListener;

    public interface ClickListener {

        int OFFSET_A = (int) ((150 * mDensity) + 0.5f); //Long click above an expanded row
        int OFFSET_B = (int) ((200 * mDensity) + 0.5f); //Long click below an expanded row

        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private GestureDetector mGestureDetector;

    public TouchListener(Context context, final RecyclerView recyclerView, ClickListener clickListener) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mClickListener != null) {
                    mClickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mClickListener != null && mGestureDetector.onTouchEvent(e)) {
            mClickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public static ClickListener newClickListener(final MSLViewAdapter mAdapter, final RecyclerView recyclerView) {

        return new TouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int expandPosition = mAdapter.getExpandedPosition();
                if (expandPosition >= 0 && expandPosition == position) {
//                    mAdapter.setExpandedPosition(-1);
//                    mAdapter.notifyItemChanged(position);
                    return;
                }
                mAdapter.toggleChecked(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                int expandedPosition = mAdapter.getExpandedPosition();
                if (expandedPosition >= 0) {
                    mAdapter.notifyItemChanged(expandedPosition); // collapse prev pos
                }

                int[] xy = new int[2];
                view.getLocationInWindow(xy);
                int offset = (expandedPosition >= 0 && position > expandedPosition) ? OFFSET_B : OFFSET_A;
                recyclerView.smoothScrollBy(0, (xy[1] - (offset)));

                mAdapter.setExpandedPosition(position);
                mAdapter.notifyItemChanged(position);
            }
        };
    }
}
