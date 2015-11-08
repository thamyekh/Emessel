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
 * @version 2015.11.8
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
}
